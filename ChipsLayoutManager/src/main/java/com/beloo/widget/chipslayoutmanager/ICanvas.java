package com.beloo.widget.chipslayoutmanager;

import android.graphics.Rect;
import android.view.View;

public interface ICanvas extends IBorder{
    Rect getCanvasRect();

    Rect getViewRect(View view);

    boolean isInside(Rect rectCandidate);

    boolean isInside(View viewCandidate);

    boolean isFullyVisible(View view);

    boolean isFullyVisible(Rect rect);
}
