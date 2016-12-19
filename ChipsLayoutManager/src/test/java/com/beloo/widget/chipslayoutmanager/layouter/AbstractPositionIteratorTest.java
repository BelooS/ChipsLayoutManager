package com.beloo.widget.chipslayoutmanager.layouter;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.assertTrue;

@RunWith(JUnit4.class)
abstract class AbstractPositionIteratorTest {

    abstract AbstractPositionIterator providePositionIterator(int maxPosition);

    @Before
    public void setUp() {}

    @Test
    public void moveShouldSetCurrentPosition() {
        AbstractPositionIterator iterator = providePositionIterator(20);
        iterator.move(10);
        assertTrue(iterator.pos == 10);

        iterator.move(0);
        assertTrue(iterator.pos == 0);

        iterator.move(19);
        assertTrue(iterator.pos == 19);
    }

    @Test
    public void nextAfterMoveShouldReturnTargetMovePosition() {
        AbstractPositionIterator iterator = providePositionIterator(20);
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
        providePositionIterator(-5);
    }

    @Test(expected = IllegalArgumentException.class)
    public void negativePositionMoveShouldThrowException() {
        //arrange
        AbstractPositionIterator incrementalIterator = providePositionIterator(5);
        incrementalIterator.move(-5);
    }

    @Test(expected = IllegalArgumentException.class)
    public void movementEqualMaxCountShouldThrowException() {
        AbstractPositionIterator iterator = providePositionIterator(5);
        iterator.move(5);
    }

    @Test(expected = IllegalArgumentException.class)
    public void movementAboveMaxCountShouldThrowException() {
        AbstractPositionIterator iterator = providePositionIterator(5);
        iterator.move(7);
    }

    @Test(expected = Exception.class)
    public void testRemoveNotSupported() {
        AbstractPositionIterator iterator = providePositionIterator(5);
        iterator.remove();
    }


}
