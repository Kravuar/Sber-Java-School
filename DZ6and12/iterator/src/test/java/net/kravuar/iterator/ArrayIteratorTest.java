package net.kravuar.iterator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class ArrayIteratorTest {

    private Integer[] testArray;
    private ArrayIterator<Integer> arrayIterator;

    @BeforeEach
    void setUp() {
        testArray = new Integer[]{1, 2, 3, 4, 5};
        arrayIterator = new ArrayIterator<>(testArray);
    }

    @Test
    void hasNextReturnsTrueWhenElementsAvailable() {
        assertTrue(arrayIterator.hasNext());
    }

    @Test
    void hasNextReturnsFalseWhenNoMoreElements() {
        for (var i = 0; i < testArray.length; ++i)
            arrayIterator.next();
        assertFalse(arrayIterator.hasNext());
    }

    @Test
    void nextReturnsCorrectElement() {
        assertEquals(1, arrayIterator.next());
        assertEquals(2, arrayIterator.next());
        assertEquals(3, arrayIterator.next());
    }

    @Test
    void nextThrowsExceptionWhenNoMoreElements() {
        while (arrayIterator.hasNext())
            arrayIterator.next();
        assertThrows(NoSuchElementException.class, arrayIterator::next);
    }

    @Test
    void removeRemovesLastReturnedElement() {
        arrayIterator.next();
        arrayIterator.next();
        arrayIterator.remove();
        assertArrayEquals(new Integer[]{1, null, 3, 4, 5}, testArray);
    }

    @Test
    void removeThrowsExceptionIfCalledBeforeNext() {
        assertThrows(IllegalStateException.class, arrayIterator::remove);
    }

    @Test
    void removeThrowsExceptionIfCalledTwiceInARow() {
        arrayIterator.next();
        arrayIterator.remove();
        assertThrows(IllegalStateException.class, arrayIterator::remove);
    }
}
