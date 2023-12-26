package net.kravuar;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

public class CollectionUtils {
    public static <T> void addAll(List<? extends T> source, List<? super T> destination) {
        destination.addAll(source);
    }

    public static <T> List<T> newArrayList() {
        return new ArrayList<>();
    }

    public static <T> int indexOf(List<? extends T> source, T o) {
        return source.indexOf(o);
    }

    public static <T> List<T> limit(List<? extends T> source, int size) {
        return source.stream()
                .limit(size)
                .collect(Collectors.toList());
    }

    // В задании сигнатура void add(List source, Object o). Непонятно, что он должен делать
    public static <T> void add(List<? super T> destination, T o) {
        destination.add(o); // возможно, это?
    }

    public static <T> void removeAll(List<? super T> removeFrom, List<? extends T> c2) {
        removeFrom.removeAll(c2);
    }

    public static <T> boolean containsAll(List<? super T> c1, List<? extends T> c2) {
        return new HashSet<>(c1).containsAll(c2);
    }

    public static <T> boolean containsAny(List<? super T> c1, List<? extends T> c2) {
        return new HashSet<>(c2).stream()
                .anyMatch(c1::contains);
    }

    public static <T extends Comparable<T>> List<T> range(List<? extends T> list, T min, T max) {
        return list.stream()
                .filter(element -> element.compareTo(min) >= 0 && element.compareTo(max) < 0)
                .collect(Collectors.toList());
    }

    public static <T> List<T> range(List<? extends T> list, T min, T max, Comparator<? super T> comparator) {
        return list.stream()
                .filter(element -> comparator.compare(element, min) >= 0 && comparator.compare(element, max) < 0)
                .collect(Collectors.toList());
    }
}
