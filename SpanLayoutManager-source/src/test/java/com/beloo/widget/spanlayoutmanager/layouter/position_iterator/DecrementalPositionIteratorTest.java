package com.beloo.widget.spanlayoutmanager.layouter.position_iterator;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.*;

@RunWith(JUnit4.class)
public class DecrementalPositionIteratorTest extends AbstractPositionIteratorTest {

    @Override
    AbstractPositionIterator providePositionIterator(int maxPosition) {
        return factory.getDecrementalPositionIterator(maxPosition);
    }

    @Test(expected = IllegalStateException.class)
    public void callingNextWhenNegativePositionReachedShouldThrowException() {
        AbstractPositionIterator iterator = factory.getDecrementalPositionIterator(5);
        assertTrue(iterator.next() == 0);
        iterator.next();
    }

    @Test
    public void nextShouldDecreaseResultPosition() {
        AbstractPositionIterator iterator = factory.getDecrementalPositionIterator(5);
        iterator.move(3);
        assertTrue(iterator.next() == 3);
        assertTrue(iterator.next() == 2);
    }

    @Test
    public void hasNextShouldReturnTrueIfZeroPositionIsNotPrevious() {
        AbstractPositionIterator iterator = factory.getDecrementalPositionIterator(2);
        iterator.move(1);
        assertTrue(iterator.hasNext());
        iterator.next();
        assertTrue(iterator.hasNext());
        iterator.next();
        assertFalse(iterator.hasNext());
    }
}
