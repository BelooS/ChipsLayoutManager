package com.beloo.widget.spanlayoutmanager.layouter;

import com.beloo.widget.spanlayoutmanager.ChipsLayoutManager;
import com.beloo.widget.spanlayoutmanager.cache.IViewCacheStorage;

public class LTRLayouterFactory extends AbstractLayouterFactory {

    public LTRLayouterFactory(ChipsLayoutManager layoutManager, IViewCacheStorage cacheStorage) {
        super(cacheStorage, layoutManager);
    }

    public ILayouter getUpLayouter(int anchorTop, int anchorLeft, int anchorBottom, int anchorRight) {
        //we shouldn't include anchor view here, so anchorLeft is a rightOffset
        AbstractLayouter layouter = new LTRUpLayouter(layoutManager, layoutManager.getChildGravityResolver(), cacheStorage, anchorTop, anchorLeft, anchorBottom);
        layouter.setMaxViewsInRow(getMaxViewsInRow());
        return layouter;
    }

    public ILayouter getDownLayouter(int anchorTop, int anchorLeft, int anchorBottom, int anchorRight) {
        //down layouting should start from right point of anchor view to right point of container
        //we should include anchor view here, so anchorLeft is a leftOffset
        AbstractLayouter layouter = new LTRDownLayouter(layoutManager, layoutManager.getChildGravityResolver(), cacheStorage, anchorTop, anchorLeft, anchorBottom);
        layouter.setMaxViewsInRow(getMaxViewsInRow());
        return layouter;
    }

}
