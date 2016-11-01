package com.beloo.widget.chipslayoutmanager.layouter;

import android.graphics.Rect;
import android.support.annotation.IntRange;
import android.support.annotation.Nullable;

import com.beloo.widget.chipslayoutmanager.ChipsLayoutManager;
import com.beloo.widget.chipslayoutmanager.cache.IViewCacheStorage;

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

    public ILayouter getUpLayouter(@Nullable Rect anchorRect) {
        Rect offsetRect = createOffsetRectForUpLayouter(anchorRect);

        AbstractLayouter layouter = new LTRUpLayouter(layoutManager,
                layoutManager.getChildGravityResolver(),
                cacheStorage,
                offsetRect,
                new CriteriaAdditionalRow(new CriteriaUpLayouterFinished(), getAdditionalRowsCount()));

        layouter.setMaxViewsInRow(getMaxViewsInRow());
        layouter.addLayouterListener(getLayouterListener());
        return layouter;
    }

    private Rect createOffsetRectForDownLayouter(Rect anchorRect) {
        return new Rect(
                //we should include anchor view here, so anchorLeft is a leftOffset
                anchorRect == null ? layoutManager.getPaddingLeft() : anchorRect.left,
                anchorRect == null? layoutManager.getPaddingTop() : anchorRect.top,
                0,
                anchorRect == null? layoutManager.getPaddingBottom() : anchorRect.bottom);
    }

    public ILayouter getDownLayouter(@Nullable Rect anchorRect) {
        Rect offsetRect = createOffsetRectForDownLayouter(anchorRect);

        //down layouting should start from right point of anchor view to right point of container
        AbstractLayouter layouter = new LTRDownLayouter(layoutManager,
                layoutManager.getChildGravityResolver(),
                cacheStorage,
                offsetRect,
                new CriteriaAdditionalHeight(
                        new CriteriaAdditionalRow(
                                new CriteriaDownLayouterFinished(),
                                getAdditionalRowsCount()),
                        getAdditionalHeight()));

        layouter.setMaxViewsInRow(getMaxViewsInRow());
        layouter.addLayouterListener(getLayouterListener());
        return layouter;
    }

}
