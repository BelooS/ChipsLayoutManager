package com.beloo.widget.chipslayoutmanager;

import android.graphics.Rect;
import android.view.View;

public interface ICanvas {
    int getCanvasRightBorder();

    int getCanvasBottomBorder();

    int getCanvasLeftBorder();

    int getCanvasTopBorder();

    Rect getCanvasRect();

    Rect getViewRect(View view);

    boolean isInside(Rect rectCandidate);

    boolean isInside(View viewCandidate);
}
