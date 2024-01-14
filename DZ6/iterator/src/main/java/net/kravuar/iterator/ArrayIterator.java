package net.kravuar.iterator;

import lombok.RequiredArgsConstructor;

import java.util.Iterator;
import java.util.NoSuchElementException;

@RequiredArgsConstructor
public class ArrayIterator<E> implements Iterator<E> {
    private final E[] backedArray;
    private int idx = 0;
    private boolean lastRemoved = false;

    @Override
    public boolean hasNext() {
        return idx < backedArray.length;
    }

    @Override
    public E next() {
        if (idx >= backedArray.length)
            throw new NoSuchElementException("IDX: " + idx);
        E e = backedArray[idx++];
        lastRemoved = false;
        return e;
    }

    @Override
    public void remove() {
        if (idx == 0 || lastRemoved)
            throw new IllegalStateException();
        backedArray[idx - 1] = null;
        lastRemoved = true;
    }
}
