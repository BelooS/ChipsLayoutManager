package com.beloo.widget.chipslayoutmanager.layouter;

import android.graphics.Rect;

import com.beloo.widget.chipslayoutmanager.ChipsLayoutManager;
import com.beloo.widget.chipslayoutmanager.breaker.IRowBreaker;
import com.beloo.widget.chipslayoutmanager.cache.IViewCacheStorage;

public class RTLLayouterFactory extends AbstractLayouterFactory {

    public RTLLayouterFactory(ChipsLayoutManager layoutManager, IViewCacheStorage cacheStorage, IRowBreaker breaker) {
        super(layoutManager, cacheStorage, breaker);
    }

    //---- up layouter below
    @Override
    Rect createOffsetRectForUpLayouter(Rect anchorRect) {
        return new Rect(
                //we shouldn't include anchor view here, so anchorLeft is a rightOffset
                anchorRect == null ? layoutManager.getPaddingLeft() : anchorRect.right,
                anchorRect == null ? layoutManager.getPaddingTop() : anchorRect.top,
                0,
                anchorRect == null ? layoutManager.getPaddingBottom() : anchorRect.bottom);
    }

    @Override
    AbstractLayouter.Builder createUpBuilder() {
        return RTLUpLayouter.newBuilder();
    }

    //---- down layouter below

    @Override
    AbstractLayouter.Builder createDownBuilder() {
        return RTLDownLayouter.newBuilder();
    }

    @Override
    Rect createOffsetRectForDownLayouter(Rect anchorRect) {
        return new Rect(
                0,
                anchorRect == null ? layoutManager.getPaddingTop() : anchorRect.top,
                anchorRect == null ? layoutManager.getPaddingRight() : anchorRect.right,
                anchorRect == null ? layoutManager.getPaddingBottom() : anchorRect.bottom);
    }
}
