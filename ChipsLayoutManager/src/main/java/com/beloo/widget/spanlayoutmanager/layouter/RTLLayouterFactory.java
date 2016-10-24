package com.beloo.widget.spanlayoutmanager.layouter;

import com.beloo.widget.spanlayoutmanager.ChipsLayoutManager;
import com.beloo.widget.spanlayoutmanager.cache.IViewCacheStorage;

public class RTLLayouterFactory extends AbstractLayouterFactory {

    public RTLLayouterFactory(ChipsLayoutManager layoutManager, IViewCacheStorage cacheStorage) {
        super(cacheStorage, layoutManager);
    }

    public ILayouter getUpLayouter(int anchorTop, int anchorLeft, int anchorBottom, int anchorRight) {
        //we shouldn't include anchor view here, so anchorLeft is a rightOffset
        AbstractLayouter layouter = new RTLUpLayouter(layoutManager, layoutManager.getChildGravityResolver(), cacheStorage, anchorTop, anchorRight, anchorBottom);
        layouter.setMaxViewsInRow(getMaxViewsInRow());
        return layouter;
    }

    public ILayouter getDownLayouter(int anchorTop, int anchorLeft, int anchorBottom, int anchorRight) {
          //down layouting should start from left point of anchor view to left point of container
        AbstractLayouter layouter = new RTLDownLayouter(layoutManager, layoutManager.getChildGravityResolver(), cacheStorage, anchorTop, anchorRight, anchorBottom) ;
        layouter.setMaxViewsInRow(getMaxViewsInRow());
        return layouter;
    }

}
