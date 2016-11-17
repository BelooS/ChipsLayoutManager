package com.beloo.widget.chipslayoutmanager.layouter;

import android.graphics.Rect;
import android.view.View;

public class VerticalForwardLayouter extends AbstractLayouter {

    VerticalForwardLayouter(Builder builder) {
        super(builder);
    }

    @Override
    Rect createViewRect(View view) {
        return null;
    }

    @Override
    void onPreLayout() {

    }

    @Override
    void onAfterLayout() {

    }

    @Override
    boolean isAttachedViewFromNewRow(View view) {
        return false;
    }

    @Override
    AbstractPositionIterator createPositionIterator() {
        return new IncrementalPositionIterator(getLayoutManager().getItemCount());
    }

    @Override
    void onInterceptAttachView(View view) {

    }
}
