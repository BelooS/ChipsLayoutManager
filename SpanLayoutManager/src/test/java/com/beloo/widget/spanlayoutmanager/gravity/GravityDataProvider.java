package com.beloo.widget.spanlayoutmanager.gravity;

import android.graphics.Rect;

class GravityDataProvider {

    public static Object[] invalidData() {
        return new Object[][] {
                {-20, 100, new Rect(0,0,0,0)},
                {0, -100, new Rect(0,0,0,0)},
                {0, 100, new Rect(0,-50,0,0)},
                {0, 100, new Rect(0,0,0,-50)},
                {20, 100, new Rect(0,10,0,0)},
                {20, 100, new Rect(0,20,0,120)}
        };
    }

}
