package com.beloo.widget.spanlayoutmanager.layouter;

import com.beloo.widget.spanlayoutmanager.ChipsLayoutManager;
import com.beloo.widget.spanlayoutmanager.cache.IViewCacheStorage;

public class LayouterFactory {
    private ChipsLayoutManager layoutManager;
    private IViewCacheStorage cacheStorage;

    public LayouterFactory(ChipsLayoutManager layoutManager, IViewCacheStorage cacheStorage) {
        this.layoutManager = layoutManager;
        this.cacheStorage = cacheStorage;
    }

    public ILayouter getUpLayouter(int anchorTop, int anchorLeft, int anchorBottom, int anchorRight, boolean isRTL) {
        return isRTL ?
                new RTLUpLayouter(layoutManager, layoutManager.getChildGravityResolver(), cacheStorage, anchorTop, anchorRight, anchorBottom) :
                //we shouldn't include anchor view here, so anchorLeft is a rightOffset
                new LTRUpLayouter(layoutManager, layoutManager.getChildGravityResolver(), cacheStorage, anchorTop, anchorBottom, anchorLeft);
    }

    public ILayouter getDownLayouter(int anchorTop, int anchorLeft, int anchorBottom, int anchorRight, boolean isRTL) {
        return isRTL ?
                //down layouting should start from left point of anchor view to left point of container
                new RTLDownLayouter(layoutManager, layoutManager.getChildGravityResolver(), cacheStorage, anchorTop, anchorBottom, anchorRight) :
                //down layouting should start from right point of anchor view to right point of container
                //we should include anchor view here, so anchorLeft is a leftOffset
                //todo not working removing zero item
                new LTRDownLayouter(layoutManager, layoutManager.getChildGravityResolver(), cacheStorage, anchorTop, anchorLeft, anchorBottom);
    }
}
