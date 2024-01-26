package net.kravuar;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.math.BigInteger;
import java.util.Scanner;
import java.util.concurrent.Executors;

public class Factorial {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);

        while (true) {
            try {
                System.out.println("Enter file path:");
                var path = in.nextLine();

                Scanner fs = new Scanner(new FileReader(path));

                System.out.println("Enter delimiter:");
                var delimiter = in.nextLine();

                fs.useDelimiter(delimiter);

                try (var threadPool = Executors.newThreadPerTaskExecutor(Thread::new)) {
                    fs.tokens()
                            .mapToInt(Integer::valueOf)
                            .forEach(number -> threadPool.submit(
                                            () -> System.out.printf("fact(%d) = %d%n", number, factorial(number))
                                    )
                            );
                    return;
                }
            } catch (FileNotFoundException e) {
                System.out.println("Invalid path. Try again.");
            }
        }
    }

    public static BigInteger factorial(int number) {
        if (number <= 0)
            throw new IllegalArgumentException("Number isn't natural.");
        BigInteger r = BigInteger.valueOf(1);
        for (int i = 2; i <= number; ++i)
            r = r.multiply(BigInteger.valueOf(i));
        return r;
    }
}