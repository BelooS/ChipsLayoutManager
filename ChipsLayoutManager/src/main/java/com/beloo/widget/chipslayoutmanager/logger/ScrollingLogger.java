package com.beloo.widget.chipslayoutmanager.logger;

import timber.log.Timber;

public class ScrollingLogger implements IScrollingLogger {
    @Override
    public void logChildCount(int childCount) {

    }

    @Override
    public void logUpScrollingNormalizationDistance(int distance) {
        Timber.d("scrollUp, distance = " + distance);
    }
}
