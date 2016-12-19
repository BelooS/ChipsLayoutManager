package com.beloo.widget.chipslayoutmanager.layouter;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import static org.junit.Assert.*;

@RunWith(JUnit4.class)
public class IncrementalPositionIteratorTest extends AbstractPositionIteratorTest {

    @Override
    AbstractPositionIterator providePositionIterator(int maxPosition) {
        return new IncrementalPositionIterator(maxPosition);
    }

    @Test
    public void nextShouldReturnCurrentAndThanIncreasedValue() {
        AbstractPositionIterator iterator = providePositionIterator(5);
        int result = iterator.next();
        assertTrue(result == 0);
        result = iterator.next();
        assertTrue(result == 1);
        result = iterator.next();
        assertTrue(result == 2);
    }

    @Test
    public void hasNextShouldReturnTrueIfMaxCountNotReached() {
        AbstractPositionIterator iteratorEmpty = providePositionIterator(0);
        AbstractPositionIterator iterator = providePositionIterator(2);
        assertTrue(iterator.hasNext());
        assertFalse(iteratorEmpty.hasNext());
        iterator.next();
        assertTrue(iterator.hasNext());
        iterator.next();
        assertFalse(iterator.hasNext());
    }

    @Test(expected = IllegalStateException.class)
    public void nextWhenMaxCountReachedShouldThrowException() {
        AbstractPositionIterator iterator = providePositionIterator(2);
        int result = iterator.next();
        assertTrue(result == 0);
        result = iterator.next();
        assertTrue(result == 1);
        iterator.next();
    }

}
