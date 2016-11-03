package com.beloo.widget.chipslayoutmanager.anchor;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class AnchorFactory implements IAnchorFactory {

    private RecyclerView.LayoutManager lm;

    public AnchorFactory(RecyclerView.LayoutManager lm) {
        this.lm = lm;
    }

    @Override
    public AnchorViewState getTopLeftAnchor() {
        int childCount = lm.getChildCount();
        AnchorViewState topLeft = AnchorViewState.getNotFoundState();

        Rect mainRect = new Rect(lm.getPaddingLeft(),
                lm.getPaddingTop(),
                lm.getWidth() - lm.getPaddingRight(),
                lm.getHeight() - lm.getPaddingBottom());
//        Rect mainRect = new Rect(0, 0, getWidth(), getHeight());
        int minTop = Integer.MAX_VALUE;
        for (int i = 0; i < childCount; i++) {
            View view = lm.getChildAt(i);
            AnchorViewState anchorViewState = createAnchorState(view);
            //intersection changes rect!!!
            Rect viewRect = new Rect(anchorViewState.getAnchorViewRect());
            boolean intersect = viewRect.intersect(mainRect);
            if (intersect && !anchorViewState.isRemoving()) {
                if (topLeft.isNotFoundState()) {
                    topLeft = anchorViewState;
                }
                minTop = Math.min(minTop, anchorViewState.getAnchorViewRect().top);
            }
        }

        if (!topLeft.isNotFoundState()) {
            assert topLeft.getAnchorViewRect() != null;
            topLeft.getAnchorViewRect().top = minTop;
        }

        return topLeft;
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
