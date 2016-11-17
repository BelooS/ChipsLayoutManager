package com.beloo.widget.chipslayoutmanager.layouter;

import android.graphics.Rect;

import com.beloo.widget.chipslayoutmanager.ChipsLayoutManager;
import com.beloo.widget.chipslayoutmanager.cache.IViewCacheStorage;
import com.beloo.widget.chipslayoutmanager.layouter.breaker.IBreakerFactory;

public class HorizontalLayouterFactory extends AbstractLayouterFactory {

    HorizontalLayouterFactory(ChipsLayoutManager layoutManager, IViewCacheStorage cacheStorage, IBreakerFactory breakerFactory) {
        super(layoutManager, cacheStorage, breakerFactory);
    }

    @Override
    AbstractLayouter.Builder createBackwardBuilder() {
        return LeftLayouter.newBuilder();
    }

    @Override
    AbstractLayouter.Builder createForwardBuilder() {
        return RightLayouter.newBuilder();
    }

    @Override
    Rect createOffsetRectForBackwardLayouter(Rect anchorRect) {
        return new Rect(
                anchorRect == null ? layoutManager.getPaddingLeft() : anchorRect.left,
                0,
                anchorRect == null ? layoutManager.getPaddingRight() : anchorRect.right,
                //we shouldn't include anchor view here, so anchorTop is a bottomOffset
                anchorRect == null ? layoutManager.getPaddingTop() : anchorRect.top);
    }

    @Override
    Rect createOffsetRectForForwardLayouter(Rect anchorRect) {
        return new Rect(
                anchorRect == null ? layoutManager.getPaddingLeft() : anchorRect.left,
                //we should include anchor view here, so anchorTop is a topOffset
                anchorRect == null? layoutManager.getPaddingTop() : anchorRect.top,
                anchorRect == null ? layoutManager.getPaddingRight() : anchorRect.right,
                0);
    }
}
