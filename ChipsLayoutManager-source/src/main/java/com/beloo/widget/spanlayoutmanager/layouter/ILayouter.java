package com.beloo.widget.spanlayoutmanager.layouter;

import android.support.v7.widget.RecyclerView;
import android.view.View;

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
