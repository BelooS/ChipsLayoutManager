package com.beloo.widget.spanlayoutmanager.logger;

public interface IFillWithLayouterLogger {
    void onStart();

    void onItemRequested();

    void onItemRecycled();

    void onFinishedLayouting();
}
