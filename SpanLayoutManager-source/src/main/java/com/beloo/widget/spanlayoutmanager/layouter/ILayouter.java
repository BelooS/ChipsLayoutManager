package com.beloo.widget.spanlayoutmanager.layouter;

import android.view.View;

import com.beloo.widget.spanlayoutmanager.layouter.position_iterator.AbstractPositionIterator;

public interface ILayouter {
    void calculateView(View view);

    void layoutRow();
    void placeView(View view);
    void onAttachView(View view);

    boolean isFinishedLayouting();

    /** check if we can not add current view to row*/
    boolean canNotBePlacedInCurrentRow();

    int getPreviousRowSize();

    AbstractPositionIterator positionIterator();
}
