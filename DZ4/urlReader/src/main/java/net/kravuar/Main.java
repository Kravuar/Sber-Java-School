package net.kravuar;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        var scanner = new Scanner(System.in);

        for(;;) {
            try {
                System.out.println("Enter URL to read:");
                readContent(scanner.next());
            } catch (URLReadingException e) {
                System.out.println("There was an error during URL reading process. Want to try again? Yes[Y]/No[Other].");
                var choice = scanner.next();
                if (choice.equals("Y"))
                    continue;
            }
            return;
        }
    }

    /**
     * Prints content from provided URL into {@code System.out}
     *
     * @throws URLReadingException upon errors in reading process.
     */
    public static void readContent(String url) throws URLReadingException {
        try {
            var reader = new BufferedReader(new InputStreamReader(
                    URI.create(url).toURL().openStream()
            ));

            String line;
            while ((line = reader.readLine()) != null)
                System.out.println(line);
        } catch (IllegalArgumentException | IOException e) {
            throw new URLReadingException(e);
        }
    }
}