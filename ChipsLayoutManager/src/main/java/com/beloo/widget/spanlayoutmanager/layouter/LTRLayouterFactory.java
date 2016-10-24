package com.beloo.widget.spanlayoutmanager.layouter;

import android.graphics.Rect;
import android.support.annotation.Nullable;

import com.beloo.widget.spanlayoutmanager.ChipsLayoutManager;
import com.beloo.widget.spanlayoutmanager.cache.IViewCacheStorage;

public class LTRLayouterFactory extends AbstractLayouterFactory {

    public LTRLayouterFactory(ChipsLayoutManager layoutManager, IViewCacheStorage cacheStorage) {
        super(cacheStorage, layoutManager);
    }

    public ILayouter getUpLayouter(@Nullable Rect anchorRect) {
        AbstractLayouter layouter = new LTRUpLayouter(layoutManager, layoutManager.getChildGravityResolver(), cacheStorage,
                anchorRect == null? layoutManager.getPaddingTop() : anchorRect.top,
                //we shouldn't include anchor view here, so anchorLeft is a rightOffset
                anchorRect == null ? layoutManager.getPaddingRight() : anchorRect.left,
                anchorRect == null? layoutManager.getPaddingBottom() : anchorRect.bottom);

        layouter.setMaxViewsInRow(getMaxViewsInRow());
        return layouter;
    }

    public ILayouter getDownLayouter(@Nullable Rect anchorRect) {
        //down layouting should start from right point of anchor view to right point of container
        AbstractLayouter layouter = new LTRDownLayouter(layoutManager, layoutManager.getChildGravityResolver(), cacheStorage,
                anchorRect == null? layoutManager.getPaddingTop() : anchorRect.top,
                //we should include anchor view here, so anchorLeft is a leftOffset
                anchorRect == null ? layoutManager.getPaddingLeft() : anchorRect.left,
                anchorRect == null? layoutManager.getPaddingBottom() : anchorRect.bottom);
        layouter.setMaxViewsInRow(getMaxViewsInRow());
        return layouter;
    }

}
