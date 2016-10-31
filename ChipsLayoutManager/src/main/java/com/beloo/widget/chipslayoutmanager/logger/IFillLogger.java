package com.beloo.widget.chipslayoutmanager.logger;

import com.beloo.widget.chipslayoutmanager.AnchorViewState;

public interface IFillLogger {


    void onStartLayouter();

    void onItemRequested();

    void onItemRecycled();

    void onFinishedLayouter();

    void onAfterLayouter();

    void onRemovedAndRecycled(int position);

    void onAfterRemovingViews();

    void onBeforeLayouter(AnchorViewState state);
}
