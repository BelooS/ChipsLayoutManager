package com.beloo.widget.spanlayoutmanager.logger;

import com.beloo.widget.spanlayoutmanager.AnchorViewState;

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
