package com.beloo.widget.chipslayoutmanager.layouter;

import android.graphics.Rect;
import android.view.View;

import com.beloo.widget.chipslayoutmanager.ChipsLayoutManager;
import com.beloo.widget.chipslayoutmanager.cache.IViewCacheStorage;
import com.beloo.widget.chipslayoutmanager.gravity.IChildGravityResolver;
import com.beloo.widget.chipslayoutmanager.layouter.criteria.IFinishingCriteria;
import com.beloo.widget.chipslayoutmanager.layouter.placer.IPlacer;

class RTLDownLayouter extends AbstractLayouter {

    private int viewRight;

    RTLDownLayouter(ChipsLayoutManager layoutManager,
                    ICanvas canvas,
                    IChildGravityResolver childGravityResolver,
                    IViewCacheStorage cacheStorage,
                    Rect offsetRect,
                    IFinishingCriteria finishingCriteria,
                    IPlacer placer) {
        super(layoutManager, canvas, offsetRect, cacheStorage, childGravityResolver, finishingCriteria, placer);
        viewRight = offsetRect.right;
    }

    @Override
    void onPreLayout() {
        getCacheStorage().storeRow(rowViews);
    }

    @Override
    void onAfterLayout() {
        //go to next row, increase top coordinate, reset left
        viewRight = getCanvasRightBorder();
        rowTop = rowBottom;
    }

    @Override
    public boolean canNotBePlacedInCurrentRow() {
        return super.canNotBePlacedInCurrentRow() || (viewRight < getCanvasRightBorder() && viewRight - currentViewWidth < getCanvasLeftBorder());
    }

    @Override
    public AbstractPositionIterator positionIterator() {
        return new IncrementalPositionIterator(getLayoutManager().getItemCount());
    }

    @Override
    Rect createViewRect(View view) {
        Rect viewRect = new Rect(viewRight - currentViewWidth, rowTop, viewRight, rowTop + currentViewHeight);
        viewRight = viewRect.left;
        rowBottom = Math.max(rowBottom, viewRect.bottom);
        return viewRect;
    }

    @Override
    public boolean onAttachView(View view) {
        rowTop = getLayoutManager().getDecoratedTop(view);
        viewRight = getLayoutManager().getDecoratedLeft(view);

        rowBottom = Math.max(rowBottom, getLayoutManager().getDecoratedBottom(view));

        return super.onAttachView(view);
    }

}
