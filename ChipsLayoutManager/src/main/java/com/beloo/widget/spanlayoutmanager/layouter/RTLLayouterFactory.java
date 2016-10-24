package com.beloo.widget.spanlayoutmanager.layouter;

import android.graphics.Rect;
import android.support.annotation.Nullable;

import com.beloo.widget.spanlayoutmanager.ChipsLayoutManager;
import com.beloo.widget.spanlayoutmanager.cache.IViewCacheStorage;

public class RTLLayouterFactory extends AbstractLayouterFactory {

    public RTLLayouterFactory(ChipsLayoutManager layoutManager, IViewCacheStorage cacheStorage) {
        super(cacheStorage, layoutManager);
    }

    public ILayouter getUpLayouter(@Nullable Rect anchorRect) {
        //we shouldn't include anchor view here, so anchorLeft is a rightOffset
//        AbstractLayouter layouter = new RTLUpLayouter(layoutManager, layoutManager.getChildGravityResolver(), cacheStorage, anchorTop, anchorRight, anchorBottom);
//        layouter.setMaxViewsInRow(getMaxViewsInRow());
//        return layouter;
        return null;
    }

    public ILayouter getDownLayouter(@Nullable Rect anchorRect) {
          //down layouting should start from left point of anchor view to left point of container
//        AbstractLayouter layouter = new RTLDownLayouter(layoutManager, layoutManager.getChildGravityResolver(), cacheStorage, anchorTop, anchorRight, anchorBottom) ;
//        layouter.setMaxViewsInRow(getMaxViewsInRow());
//        return layouter;
        return null;
    }

}
