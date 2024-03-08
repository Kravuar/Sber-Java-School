package net.kravuar.spring;

import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
class FilesDownloader {
    private static final Logger log = LogManager.getLogger(FilesDownloader.class);
    private final TimeLimitRebalancer rebalancer;

    List<Future<Long>> download(Path directory, Path fileList, int bytesPerSecondLimit, int nThreads) throws IOException, InterruptedException {
        if (!Files.exists(directory) || !Files.isDirectory(directory))
            throw new IllegalArgumentException();

        try (ExecutorService threadPoolExecutor = Executors.newFixedThreadPool(nThreads);
             ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor()
        ) {
            Collection<URL> urls = getUniqueURLs(fileList);
            int nUrls = urls.size();
            long initialBPS = bytesPerSecondLimit / nUrls;

            Map<String, ManagedBucket> bucketsByFile = urls.stream()
                    .collect(Collectors.toMap(
                            URL::getFile,
                            url -> new ManagedBucket(
                                    initialBPS,
                                    Refill.intervally(initialBPS, Duration.ofSeconds(1))
                            )
                    ));

            // Schedule rebalancing each second
            scheduler.scheduleWithFixedDelay(
                    () -> rebalance(bucketsByFile.values(), bytesPerSecondLimit),
                    1,
                    1,
                    TimeUnit.SECONDS
            );

            // Form list of tasks to start them at the same time
            // Just calling the transfer method and some logging
            List<Callable<Long>> tasks = urls.stream()
                    .map(url -> (Callable<Long>) () -> {
                        try {
                            log.info("File: {} started", url.getFile());
                            long size = transfer(directory, url, bucketsByFile.get(url.getFile()));
                            log.info("File: {} ended", url.getFile());
                            return size;
                        } catch (IOException | InterruptedException e) {
                            log.info("File: {} failed", url.getFile());
                            throw new RuntimeException(e);
                        } finally {
                            // Remove completed/failed, so that it does not affect rebalance anymore
                            bucketsByFile.remove(url.getFile());
                        }
                    }).toList();

            // Submit tasks and return futures
            return threadPoolExecutor.invokeAll(tasks);
        }
    }

    private void rebalance(Collection<ManagedBucket> buckets, long bandwidth) {
        rebalancer.rebalance(buckets, bandwidth);

        log.info("Buckets speed distribution: {}", buckets.stream()
                .map(bucket -> String.format("[%d | %d]", bucket.getMeanConsumed(), bucket.getBucketSize()))
                .collect(Collectors.joining(", "))
        );
    }

    private static long transfer(Path directory, URL url, ManagedBucket managedBucket) throws IOException, InterruptedException {
        // Loading files using ByteBuffers

        File destination = directory.resolve(url.getFile().substring(1)).toFile(); // combine paths, substring to remove trailing slash
        try (FileOutputStream fileOutputStream = new FileOutputStream(destination)) {
            ReadableByteChannel readableByteChannel = Channels.newChannel(url.openStream());
            FileChannel fileChannel = fileOutputStream.getChannel();

            long bytesRead;
            long offset = 0;
            Bucket bucket = managedBucket.getBucket();
            do {
                // On each iteration (chunk) we read bucketSize (as it might have changed due to rebalance)
                // Try to consume that amount (max) and restore non-consumed tokens to bucket
                long bucketSize = managedBucket.getBucketSize();
                bucket.asBlocking().consume(bucketSize);
                bytesRead = fileChannel.transferFrom(readableByteChannel, offset, bucketSize);

                long delta = bucketSize - bytesRead;
                if (delta > 0)
                    bucket.addTokens(delta); // return non-consumed back

                managedBucket.addObservation(bytesRead); // update mean statistics for rebalancing
                offset += bytesRead;
            } while (bytesRead != 0);

            return fileChannel.size();
        } catch (FileNotFoundException e) {
            throw new IOException(e);
        }
    }

    private static Collection<URL> getUniqueURLs(Path path) throws IOException {
        // Simple file scanning line by line

        Map<String, URL> fileToURL = new HashMap<>();
        try (Scanner rowScanner = new Scanner(path)) {
            while (rowScanner.hasNext()) {
                String line = rowScanner.nextLine();
                URL url = URI.create(line).toURL();
                fileToURL.put(url.getFile(), url);
            }
        }
        return fileToURL.values();
    }
}
