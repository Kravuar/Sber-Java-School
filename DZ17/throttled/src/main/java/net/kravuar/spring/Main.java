package net.kravuar.spring;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

public class Main {
    public static void main(String[] args) {
        // CONFIGURE
        final ApplicationContext ctx = new AnnotationConfigApplicationContext(Config.class);
        FilesDownloader filesDownloader = ctx.getBean(FilesDownloader.class);
        // CONFIGURE


        // CLI
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("Enter the directory path:");
            String directoryPath = scanner.nextLine();
            Path directory = Paths.get(directoryPath);

            System.out.println("Enter the file list path:");
            String fileListPath = scanner.nextLine();
            Path fileList = Paths.get(fileListPath);

            System.out.println("Enter the bytes per second limit (default is 2 MB - enter '0'):");
            int bytesPerSecondLimit = scanner.nextInt();
            scanner.nextLine();

            if (bytesPerSecondLimit <= 0)
                bytesPerSecondLimit = 1024 * 1024 * 2; // 2 MB

            System.out.println("Enter the number of threads:");
            int nThreads = scanner.nextInt();
            scanner.nextLine();

            try {
                var futures = filesDownloader.download(directory, fileList, bytesPerSecondLimit, nThreads);
                for (var future : futures) {
                    try {
                        future.get();
                    } catch (ExecutionException e) {
                        System.out.println("Error for file: " + e.getMessage());
                    }
                }
                System.out.println("Download completed.");
                System.out.println("Do you want to download again? (yes/no)");
                String again = scanner.nextLine().toLowerCase();
                if (!again.equals("yes"))
                    break;
            } catch (IOException | InterruptedException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
        scanner.close();
    }
}