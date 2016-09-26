package com.beloo.widget.spanlayoutmanager.gravity;

import android.graphics.Rect;

import java.util.Arrays;
import java.util.Collection;

import static com.beloo.widget.spanlayoutmanager.ParamsType.INVALID;

public class GravityDataProvider {
    public static Collection<Object[]> getInvalidGravityModifierParams() {
        return Arrays.asList(new Object[][]{
                {INVALID, -20, 100, new Rect(0, 0, 0, 0), new Rect(0, 0, 0, 0)},
                {INVALID, 0, -100, new Rect(0, 0, 0, 0), new Rect(0, 0, 0, 0)},
                {INVALID, 0, 100, new Rect(0, -50, 0, 0), new Rect(0, 0, 0, 0)},
                {INVALID, 0, 100, new Rect(0, 0, 0, -50), new Rect(0, 0, 0, 0)},
                {INVALID, 20, 100, new Rect(0, 10, 0, 0), new Rect(0, 0, 0, 0)},
                {INVALID, 20, 100, new Rect(0, 20, 0, 120), new Rect(0, 0, 0, 0)}
        });
    }
}
