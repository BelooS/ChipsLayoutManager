package com.beloo.widget.spanlayoutmanager.layouter.position_iterator;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import static org.junit.Assert.*;

@RunWith(JUnit4.class)
public class IncrementalPositionIteratorTest extends AbstractPositionIteratorTest {

    @Override
    AbstractPositionIterator providePositionIterator(int maxPosition) {
        return factory.getIncrementalPositionIterator(maxPosition);
    }

    @Test
    public void nextShouldReturnCurrentAndThanIncreasedValue() {
        AbstractPositionIterator iterator = factory.getIncrementalPositionIterator(5);
        int result = iterator.next();
        assertTrue(result == 0);
        result = iterator.next();
        assertTrue(result == 1);
        result = iterator.next();
        assertTrue(result == 2);
    }

    @Test
    public void hasNextShouldReturnTrueIfMaxCountNotReached() {
        AbstractPositionIterator iteratorEmpty = factory.getIncrementalPositionIterator(0);
        AbstractPositionIterator iterator = factory.getIncrementalPositionIterator(2);
        assertTrue(iterator.hasNext());
        assertFalse(iteratorEmpty.hasNext());
        iterator.next();
        assertTrue(iterator.hasNext());
        iterator.next();
        assertFalse(iterator.hasNext());
    }

    @Test(expected = IllegalStateException.class)
    public void nextWhenMaxCountReachedShouldThrowException() {
        AbstractPositionIterator iterator = factory.getIncrementalPositionIterator(2);
        int result = iterator.next();
        assertTrue(result == 0);
        result = iterator.next();
        assertTrue(result == 1);
        iterator.next();
    }

}
