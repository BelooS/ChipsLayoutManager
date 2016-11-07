package com.beloo.widget.chipslayoutmanager.layouter;

import android.graphics.Rect;
import android.support.annotation.Nullable;

import com.beloo.widget.chipslayoutmanager.ChipsLayoutManager;
import com.beloo.widget.chipslayoutmanager.cache.IViewCacheStorage;
import com.beloo.widget.chipslayoutmanager.layouter.criteria.CriteriaAdditionalRow;
import com.beloo.widget.chipslayoutmanager.layouter.criteria.EmtpyCriteria;
import com.beloo.widget.chipslayoutmanager.layouter.placer.DisappearingViewBottomPlacer;

public class RTLLayouterFactory extends AbstractLayouterFactory {

    public RTLLayouterFactory(ChipsLayoutManager layoutManager, IViewCacheStorage cacheStorage) {
        super(cacheStorage, layoutManager);
    }

    //---- up layouter below

    private Rect createOffsetRectForUpLayouter(Rect anchorRect) {
        return new Rect(
                //we shouldn't include anchor view here, so anchorLeft is a rightOffset
                anchorRect == null ? layoutManager.getPaddingLeft() : anchorRect.right,
                anchorRect == null ? layoutManager.getPaddingTop() : anchorRect.top,
                0,
                anchorRect == null ? layoutManager.getPaddingBottom() : anchorRect.bottom);
    }

    public ILayouter getUpLayouter(@Nullable Rect anchorRect) {
        Rect offsetRect = createOffsetRectForUpLayouter(anchorRect);

        AbstractLayouter layouter = new RTLUpLayouter(layoutManager,
                new Square(layoutManager),
                layoutManager.getChildGravityResolver(),
                cacheStorage,
                offsetRect,
                getUpFinishingCriteria(),
                getTopPlacer());

        layouter.setMaxViewsInRow(getMaxViewsInRow());
        layouter.addLayouterListener(getLayouterListener());
        return layouter;
    }

    //---- down layouter below

    private Rect createOffsetRectForDownLayouter(Rect anchorRect) {
        return new Rect(
                0,
                anchorRect == null ? layoutManager.getPaddingTop() : anchorRect.top,
                anchorRect == null ? layoutManager.getPaddingRight() : anchorRect.right,
                anchorRect == null ? layoutManager.getPaddingBottom() : anchorRect.bottom);
    }

    public ILayouter getDownLayouter(@Nullable Rect anchorRect) {
        Rect offsetRect = createOffsetRectForDownLayouter(anchorRect);

        AbstractLayouter layouter = new RTLDownLayouter(layoutManager,
                new Square(layoutManager),
                layoutManager.getChildGravityResolver(),
                cacheStorage,
                offsetRect,
                getDownFinishingCriteria(),
                getBottomPlacer());

        layouter.setMaxViewsInRow(getMaxViewsInRow());
        layouter.addLayouterListener(getLayouterListener());
        return layouter;
    }

    @Override
    public ILayouter getDisappearingDownLayouter(@Nullable Rect anchorRect) {
        Rect offsetRect = createOffsetRectForDownLayouter(anchorRect);

        AbstractLayouter layouter = new RTLDownLayouter(layoutManager,
                new Square(layoutManager),
                layoutManager.getChildGravityResolver(),
                cacheStorage,
                offsetRect,
                new CriteriaAdditionalRow(new EmtpyCriteria(), getAdditionalRowsCount()),
                new DisappearingViewBottomPlacer(layoutManager));

        layouter.setMaxViewsInRow(getMaxViewsInRow());
        layouter.addLayouterListener(getLayouterListener());
        return layouter;
    }

}
