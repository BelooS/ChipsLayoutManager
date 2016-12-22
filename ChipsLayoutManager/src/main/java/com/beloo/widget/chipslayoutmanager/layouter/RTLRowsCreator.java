package com.beloo.widget.chipslayoutmanager.layouter;

import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;

import com.beloo.widget.chipslayoutmanager.ChipsLayoutManager;
import com.beloo.widget.chipslayoutmanager.gravity.IGravityModifiersFactory;
import com.beloo.widget.chipslayoutmanager.gravity.IRowStrategy;
import com.beloo.widget.chipslayoutmanager.layouter.breaker.IBreakerFactory;
import com.beloo.widget.chipslayoutmanager.cache.IViewCacheStorage;
import com.beloo.widget.chipslayoutmanager.layouter.criteria.ICriteriaFactory;
import com.beloo.widget.chipslayoutmanager.layouter.placer.IPlacerFactory;

class RTLRowsCreator implements ILayouterCreator {

    private RecyclerView.LayoutManager layoutManager;

    RTLRowsCreator(RecyclerView.LayoutManager layoutManager) {
        this.layoutManager = layoutManager;
    }

    //---- up layouter below
    @Override
    public Rect createOffsetRectForBackwardLayouter(@NonNull Rect anchorRect) {
        return new Rect(
                //we shouldn't include anchor view here, so anchorLeft is a rightOffset
                anchorRect.right,
                anchorRect.top,
                0,
                anchorRect.bottom);
    }

    @Override
    public AbstractLayouter.Builder createBackwardBuilder() {
        return RTLUpLayouter.newBuilder();
    }

    //---- down layouter below

    @Override
    public AbstractLayouter.Builder createForwardBuilder() {
        return RTLDownLayouter.newBuilder();
    }

    @Override
    public Rect createOffsetRectForForwardLayouter(Rect anchorRect) {
        return new Rect(
                0,
                anchorRect == null ? layoutManager.getPaddingTop() : anchorRect.top,
                anchorRect == null ? layoutManager.getPaddingRight() : anchorRect.right,
                anchorRect == null ? layoutManager.getPaddingBottom() : anchorRect.bottom);
    }
}
