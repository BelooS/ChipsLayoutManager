package com.beloo.widget.spanlayoutmanager.gravity;

import android.graphics.Rect;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(JUnitParamsRunner.class)
public class CenterGravityModifierTest {

    private CenterGravityModifier gravityModifier = new CenterGravityModifier();

    public static Object[] data() {
        return new Object[][] {
                {0, 100, new Rect(0, 20, 0, 100), new Rect(0, 10, 0, 90)},
                {0, 100, new Rect(0, 20, 0, 80), new Rect(0, 20, 0, 80)},
                {0, 100, new Rect(0, 0, 0, 40), new Rect(0, 30, 0, 70)},
                {0, 100, new Rect(0, 10, 0, 20), new Rect(0, 45, 0, 55)},
                {0, 100, new Rect(0, 0, 0, 100), new Rect(0, 0, 0, 100)}
        };
    }

    @Test(expected = IllegalArgumentException.class)
    @Parameters(source = GravityDataProvider.class, method = "invalidData")
    public void invalidDataForCenterModifierShouldThrowIllegalArgumentException(int minTop, int maxBottom, Rect childRect) {
        gravityModifier.modifyChildRect(minTop, maxBottom, childRect);
    }

    @Test
    @Parameters(method = "data")
    public void modifierShouldAlignInputRectToCenter(int minTop, int maxBottom, Rect childRect, Rect testRect) {
        Rect resultRect = gravityModifier.modifyChildRect(minTop, maxBottom, childRect);
        assertTrue(Mockito.verify(testRect).equals(resultRect));
    }

}
