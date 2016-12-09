package com.beloo.widget.chipslayoutmanager.anchor;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.beloo.widget.chipslayoutmanager.layouter.ICanvas;

abstract class AbstractAnchorFactory implements IAnchorFactory {
    RecyclerView.LayoutManager lm;
    private ICanvas canvas;

    AbstractAnchorFactory(RecyclerView.LayoutManager lm, ICanvas canvas) {
        this.lm = lm;
        this.canvas = canvas;
    }

    ICanvas getCanvas() {
        return canvas;
    }

    AnchorViewState createAnchorState(View view) {
        return new AnchorViewState(lm.getPosition(view), canvas.getViewRect(view));
    }

    @Override
    public AnchorViewState createNotFound() {
        return AnchorViewState.getNotFoundState();
    }

}
