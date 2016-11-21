package com.beloo.widget.chipslayoutmanager.layouter;

import android.graphics.Rect;

public interface ICanvas {
    int getCanvasRightBorder();

    int getCanvasBottomBorder();

    int getCanvasLeftBorder();

    int getCanvasTopBorder();

    Rect getCanvasRect();
}
