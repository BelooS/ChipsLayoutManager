package com.beloo.widget.chipslayoutmanager.logger;

import android.util.Log;

import com.beloo.widget.chipslayoutmanager.anchor.AnchorViewState;

import timber.log.Timber;

public class StartPositionLogger implements IFillLogger {
    @Override
    public void onStartLayouter(int startPosition) {
        Timber.d("fillWithLayouter. " + "start position = " + startPosition);
    }

    @Override
    public void onItemRequested() {

    }

    @Override
    public void onItemRecycled() {

    }

    @Override
    public void onFinishedLayouter() {

    }

    @Override
    public void onAfterLayouter() {

    }

    @Override
    public void onRemovedAndRecycled(int position) {

    }

    @Override
    public void onAfterRemovingViews() {

    }

    @Override
    public void onBeforeLayouter(AnchorViewState state) {

    }
}
