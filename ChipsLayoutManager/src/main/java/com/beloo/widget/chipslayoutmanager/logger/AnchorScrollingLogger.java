package com.beloo.widget.chipslayoutmanager.logger;

import android.util.Log;

import com.beloo.widget.chipslayoutmanager.anchor.AnchorViewState;

public class AnchorScrollingLogger implements IScrollingLogger {
    @Override
    public void logChildCount(int childCount) {

    }

    @Override
    public void logUpScrollingNormalizationDistance(int distance) {

    }

    @Override
    public void logAnchorView(AnchorViewState anchorViewState) {
        Log.d("onScroll", "anchorPos = " + anchorViewState.getPosition());
    }
}
