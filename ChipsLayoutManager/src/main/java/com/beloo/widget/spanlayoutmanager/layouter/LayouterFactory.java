package com.beloo.widget.spanlayoutmanager.layouter;

import android.support.annotation.Nullable;

import com.beloo.widget.spanlayoutmanager.ChipsLayoutManager;
import com.beloo.widget.spanlayoutmanager.cache.IViewCacheStorage;

public class LayouterFactory {
    private ChipsLayoutManager layoutManager;
    private IViewCacheStorage cacheStorage;
    @Nullable
    private Integer maxViewsInRow = null;

    public LayouterFactory(ChipsLayoutManager layoutManager, IViewCacheStorage cacheStorage, @Nullable Integer maxViewsInRow) {
        this.layoutManager = layoutManager;
        this.cacheStorage = cacheStorage;
        this.maxViewsInRow = maxViewsInRow;
    }

    public ILayouter getUpLayouter(int anchorTop, int anchorLeft, int anchorBottom, int anchorRight, boolean isRTL) {
        AbstractLayouter layouter = isRTL ?
                new RTLUpLayouter(layoutManager, layoutManager.getChildGravityResolver(), cacheStorage, anchorTop, anchorRight, anchorBottom) :
                //we shouldn't include anchor view here, so anchorLeft is a rightOffset
                new LTRUpLayouter(layoutManager, layoutManager.getChildGravityResolver(), cacheStorage, anchorTop, anchorBottom, anchorLeft);
        layouter.setMaxViewsInRow(maxViewsInRow);
        return layouter;
    }

    public ILayouter getDownLayouter(int anchorTop, int anchorLeft, int anchorBottom, int anchorRight, boolean isRTL) {
        AbstractLayouter layouter = isRTL ?
                //down layouting should start from left point of anchor view to left point of container
                new RTLDownLayouter(layoutManager, layoutManager.getChildGravityResolver(), cacheStorage, anchorTop, anchorBottom, anchorRight) :
                //down layouting should start from right point of anchor view to right point of container
                //we should include anchor view here, so anchorLeft is a leftOffset
                //todo not working removing zero item
                new LTRDownLayouter(layoutManager, layoutManager.getChildGravityResolver(), cacheStorage, anchorTop, anchorLeft, anchorBottom);

        layouter.setMaxViewsInRow(maxViewsInRow);
        return layouter;
    }
}
