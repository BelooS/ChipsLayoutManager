package com.beloo.widget.spanlayoutmanager.layouter.position_iterator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.*;

@RunWith(JUnit4.class)
public class IncrementalPositionIteratorTest {

    private PositionIteratorFactory factory;

    @Before
    public void setUp() {
        factory = new PositionIteratorFactory();
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

    @Test
    public void moveShouldSetCurrentPosition() {
        AbstractPositionIterator iterator = factory.getIncrementalPositionIterator(20);
        iterator.move(10);
        assertTrue(iterator.next() == 10);

        iterator.move(0);
        assertTrue(iterator.next() == 0);

        iterator.move(19);
        assertTrue(iterator.next() == 19);
    }

    @Test(expected = IllegalArgumentException.class)
    public void negativeCountInitializationShouldThrowException() {
        //arrange
        factory.getIncrementalPositionIterator(-5);
    }

    @Test(expected = IllegalArgumentException.class)
    public void negativePositionMoveShouldThrowException() {
        //arrange
        AbstractPositionIterator incrementalIterator = factory.getIncrementalPositionIterator(5);
        incrementalIterator.move(-5);
    }

    @Test(expected = IllegalArgumentException.class)
    public void movementEqualMaxCountShouldThrowException() {
        AbstractPositionIterator iterator = factory.getIncrementalPositionIterator(5);
        iterator.move(5);
    }

    @Test(expected = IllegalArgumentException.class)
    public void movementAboveMaxCountShouldThrowException() {
        AbstractPositionIterator iterator = factory.getIncrementalPositionIterator(5);
        iterator.move(7);
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

    @Test(expected = Exception.class)
    public void testRemoveNotSupported() {
        AbstractPositionIterator iterator = factory.getIncrementalPositionIterator(5);
        iterator.remove();
    }

}
