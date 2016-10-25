package com.beloo.widget.spanlayoutmanager.logger;

public interface IFillLogger {


    void onStartLayouter();

    void onItemRequested();

    void onItemRecycled();

    void onFinishedLayouter();

    void onAfterLayouter();

    void onRemovedAndRecycled(int position);

    void onAfterRemovingViews();
}
