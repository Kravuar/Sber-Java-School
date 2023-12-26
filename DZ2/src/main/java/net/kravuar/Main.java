package net.kravuar;

import java.util.Arrays;
import java.util.HashSet;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        var words = Arrays.asList(
                "w1", "w2", "w3", "w4",
                "w1", "w1", "w3", "w5",
                "w6", "w2", "w7", "w8"
        );

        System.out.println("DATA:");
        System.out.println(words);

        System.out.println("DISTINCT:");
        System.out.println(new HashSet<>(words));

        System.out.println("COUNTS:");
        System.out.println(words.parallelStream()
                .collect(Collectors.groupingBy(
                        Function.identity(),
                        Collectors.counting()
                ))
        );

        System.out.println();
        System.out.println("================================");
        System.out.println();

        var phoneBook = new PhoneBook();
        phoneBook.add("Бебебе", "1");
        phoneBook.add("Бабаба", "2");
        phoneBook.add("Бубубу", "3");
        phoneBook.add("Быбыбы", "4");
        phoneBook.add("Бабаба", "5");
        phoneBook.add("Бибиби", "6");
        phoneBook.add("Бебебе", "7");
        phoneBook.add("Бебебе", "7");
        phoneBook.add("Бебебе", "7");
        phoneBook.add("Бъбъбъ", "8");
        phoneBook.add("Бебебе", "9");
        phoneBook.add("Бъбъбъ", "10");

        var byBebebe = phoneBook.get("Бебебе");
        assert (byBebebe.size() == 3);
        System.out.println("Бебебе");
        System.out.println(byBebebe);

        var byBababa = phoneBook.get("Бабаба");
        assert (byBababa.size() == 2);
        System.out.println("Бабаба");
        System.out.println(byBababa);

        var byBibibi = phoneBook.get("Бибиби");
        assert (byBibibi.size() == 1);
        System.out.println("Бибиби");
        System.out.println(byBibibi);
    }
}