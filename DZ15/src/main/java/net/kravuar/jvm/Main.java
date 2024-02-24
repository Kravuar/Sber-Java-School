package net.kravuar.jvm;

import java.util.*;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static final int OBJECTS_PER_CYCLE = 1_000_000;

    public static void main(String[] args) {
        while(true) {
            switch (menu()) {
                case 0:
                    return;
                case 1:
                    GC();
                    break;
                case 2:
                    JIT();
                    break;
                default:
                    break;
            }
        }
    }

    public static int menu() {
        while (true) {
            System.out.println("0 - exit");
            System.out.println("1 - GC");
            System.out.println("2 - JIT");
            System.out.println();
            var input = scanner.nextLine();
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input.");
            }
        }
    }

    private static void GC() {
        List<Integer> ints = new ArrayList<>();

        while (true) {
            switch (GCMenu()) {
                case 0:
                    return;
                case 1:
                    for (int i = 0; i < OBJECTS_PER_CYCLE; ++i)
                        ints.add(i);
                    break;
                case 2:
                    ints = new ArrayList<>();
                    break;
                default:
                    break;
            }
            System.out.printf("Objects: %d%n", ints.size());
            System.out.println("================================================");
            System.out.println();
        }
    }

    public static int GCMenu() {
        while (true) {
            System.out.println("0 - exit");
            System.out.println("1 - add objects");
            System.out.println("2 - clear objects");
            System.out.println();
            var input = scanner.nextLine();
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input.");
            }
        }
    }

    private static void JIT() {
        int size = 100_000;

        Map<Integer, Integer> map = new HashMap<>(size);
        for (int i = 0; i < size; ++i)
            map.put(i, i);
        for (int i = 0; i < size; ++i)
            map.remove(i);
    }
}