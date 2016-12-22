package com.beloo.widget.chipslayoutmanager.gravity;

import android.graphics.Rect;

import java.util.Arrays;
import java.util.Collection;

import static com.beloo.widget.chipslayoutmanager.ParamsType.INVALID;

class GravityDataProvider {
    static Collection<Object[]> getInvalidGravityModifierParams() {
        return Arrays.asList(new Object[][]{
                //start lower than minStart
                {INVALID, 0, 100, new Rect(0, -50, 0, 0), new Rect(0, 0, 0, 0)},
                //start lower than minStart
                {INVALID, 20, 100, new Rect(0, 10, 0, 0), new Rect(0, 0, 0, 0)},
                //end bigger than maxEnd
                {INVALID, 20, 100, new Rect(0, 20, 0, 120), new Rect(0, 0, 0, 0)}
        });
    }
}
