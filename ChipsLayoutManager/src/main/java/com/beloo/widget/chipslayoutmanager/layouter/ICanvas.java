package com.beloo.widget.chipslayoutmanager.layouter;

import android.graphics.Rect;
import android.view.View;

import com.beloo.widget.chipslayoutmanager.IBorder;

public interface ICanvas extends IBorder {
    Rect getCanvasRect();

    Rect getViewRect(View view);

    boolean isInside(Rect rectCandidate);

    boolean isInside(View viewCandidate);

    boolean isFullyVisible(View view);

    boolean isFullyVisible(Rect rect);

    int getEnd();

    int getEndAfterPadding();

    int getStart();

    int getStartAfterPadding();
}
