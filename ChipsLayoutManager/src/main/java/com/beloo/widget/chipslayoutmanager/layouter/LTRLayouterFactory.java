package com.beloo.widget.chipslayoutmanager.layouter;

import android.graphics.Rect;
import android.support.annotation.Nullable;

import com.beloo.widget.chipslayoutmanager.ChipsLayoutManager;
import com.beloo.widget.chipslayoutmanager.cache.IViewCacheStorage;
import com.beloo.widget.chipslayoutmanager.layouter.criteria.CriteriaAdditionalRow;
import com.beloo.widget.chipslayoutmanager.layouter.criteria.EmtpyCriteria;
import com.beloo.widget.chipslayoutmanager.layouter.placer.DisappearingViewBottomPlacer;

public class LTRLayouterFactory extends AbstractLayouterFactory {

    public LTRLayouterFactory(ChipsLayoutManager layoutManager, IViewCacheStorage cacheStorage) {
        super(cacheStorage, layoutManager);
    }

    private Rect createOffsetRectForUpLayouter(Rect anchorRect) {
        return new Rect(
                0,
                anchorRect == null? layoutManager.getPaddingTop() : anchorRect.top,
                //we shouldn't include anchor view here, so anchorLeft is a rightOffset
                anchorRect == null ? layoutManager.getPaddingRight() : anchorRect.left,
                anchorRect == null? layoutManager.getPaddingBottom() : anchorRect.bottom);
    }

    private Rect createOffsetRectForDownLayouter(Rect anchorRect) {
        return new Rect(
                //we should include anchor view here, so anchorLeft is a leftOffset
                anchorRect == null ? layoutManager.getPaddingLeft() : anchorRect.left,
                anchorRect == null? layoutManager.getPaddingTop() : anchorRect.top,
                0,
                anchorRect == null? layoutManager.getPaddingBottom() : anchorRect.bottom);
    }

    @Override
    AbstractLayouter.Builder createUpBuilder(Rect anchorRect) {
        return LTRUpLayouter.newBuilder()
                .offsetRect(createOffsetRectForUpLayouter(anchorRect));
    }

    @Override
    AbstractLayouter.Builder createDownBuilder(Rect anchorRect) {
        return LTRDownLayouter.newBuilder()
                .offsetRect(createOffsetRectForDownLayouter(anchorRect));
    }
}
