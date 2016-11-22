package com.beloo.widget.chipslayoutmanager.anchor;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.beloo.widget.chipslayoutmanager.layouter.ICanvas;

abstract class AbstractAnchorFactory implements IAnchorFactory {
    RecyclerView.LayoutManager lm;
    private ICanvas canvas;

    RecyclerView.Recycler recycler;

    AbstractAnchorFactory(RecyclerView.LayoutManager lm, ICanvas canvas) {
        this.lm = lm;
        this.canvas = canvas;
    }

    public void setRecycler(RecyclerView.Recycler recycler) {
        this.recycler = recycler;
    }

    public Rect getCanvasRect() {
        return canvas.getCanvasRect();
    }

    @Override
    public AnchorViewState createAnchorState(View view) {
        int left = lm.getDecoratedLeft(view);
        int top = lm.getDecoratedTop(view);
        int right = lm.getDecoratedRight(view);
        int bottom = lm.getDecoratedBottom(view);
        Rect viewRect = new Rect(left, top, right, bottom);
        return new AnchorViewState(lm.getPosition(view), viewRect);
    }

    @Override
    public AnchorViewState createNotFound() {
        return AnchorViewState.getNotFoundState();
    }
}
