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

    /** calculate border state of layout manager after filling children*/
    void findBorderViews();

    View getTopView();

    View getBottomView();

    View getLeftView();

    View getRightView();

    Integer getMinPositionOnScreen();

    Integer getMaxPositionOnScreen();

    boolean isFirstItemAdded();
}
