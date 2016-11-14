package com.beloo.widget.chipslayoutmanager.logger;

import android.util.Log;

public class ScrollingLogger implements IScrollingLogger {
    @Override
    public void logChildCount(int childCount) {

    }

    @Override
    public void logUpScrollingNormalizationDistance(int distance) {
        Log.d("scrollUp", "distance = " + distance);
    }
}
