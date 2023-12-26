package net.kravuar;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class CountMapImpl<E> implements CountMap<E> {
    private final Map<E, Integer> counter = new HashMap<>();

    @Override
    public void add(E o) {
        counter.merge(o, 1, Integer::sum);
    }

    @Override
    public int getCount(E o) {
        return counter.getOrDefault(o, 0);
    }

    @Override
    public int remove(E o) {
        var oldValue = getCount(o);
        if (oldValue != 0)
            if (oldValue == 1)
                counter.remove(o);
            else
                counter.merge(o, -1, Integer::sum);
        return oldValue;
    }

    @Override
    public int size() {
        return counter.values().stream()
                .mapToInt(Integer::intValue)
                .sum();
    }

    @Override
    public void addAll(CountMap<? extends E> source) {
        source.toMap(counter);
    }

    @Override
    public Map<E, Integer> toMap() {
        return Collections.unmodifiableMap(counter);
    }

    @Override
    public void toMap(Map<? super E, Integer> destination) {
        counter.forEach((key, value) -> destination.merge(key, value, Integer::sum));
    }
}
