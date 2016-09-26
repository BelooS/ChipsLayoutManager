package com.beloo.widget.spanlayoutmanager.gravity;

import android.graphics.Rect;

import com.beloo.widget.spanlayoutmanager.ParamsType;

import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static org.junit.Assert.assertEquals;
import static com.beloo.widget.spanlayoutmanager.ParamsType.*;

@RunWith(Parameterized.class)
abstract class GravityModifierTest {

    private IGravityModifier gravityModifier;

    @Before
    public void setUp() {
        gravityModifier = getGravityModifier();
    }

    protected abstract IGravityModifier getGravityModifier();

    private ParamsType paramsType;
    private int minTop;
    private int maxBottom;
    private Rect testRect;
    private Rect expectedResultRect;

    GravityModifierTest(ParamsType paramsType, int minTop, int maxBottom, Rect testRect, Rect expectedResultRect) {
        this.paramsType = paramsType;
        this.minTop = minTop;
        this.maxBottom = maxBottom;
        this.testRect = testRect;
        this.expectedResultRect = expectedResultRect;
    }

    @Test(expected = IllegalArgumentException.class)
    public void invalidDataForBottomModifierShouldThrowIllegalArgumentException() {
        Assume.assumeTrue(paramsType.equals(INVALID));
        gravityModifier.modifyChildRect(minTop, maxBottom, testRect);
    }

    @Test
    public void modifierShouldAlignInputRectToMaxBottom() {
        Assume.assumeTrue(paramsType.equals(VALID));
        Rect resultRect = gravityModifier.modifyChildRect(minTop, maxBottom, testRect);
        assertEquals(expectedResultRect, resultRect);
    }

}