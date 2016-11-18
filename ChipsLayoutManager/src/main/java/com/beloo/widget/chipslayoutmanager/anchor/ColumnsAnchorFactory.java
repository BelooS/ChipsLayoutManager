package com.beloo.widget.chipslayoutmanager.anchor;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class ColumnsAnchorFactory extends AbstractAnchorFactory {

    public ColumnsAnchorFactory(RecyclerView.LayoutManager lm) {
        super(lm);
    }

    /** get the closest views to left border. The highest view will be picked from it. */
    @Override
    public AnchorViewState getAnchor() {

        int childCount = lm.getChildCount();
        AnchorViewState topLeft = AnchorViewState.getNotFoundState();

        Rect mainRect = new Rect(lm.getPaddingLeft(),
                lm.getPaddingTop(),
                lm.getWidth() - lm.getPaddingRight(),
                lm.getHeight() - lm.getPaddingBottom());

        int minLeft = Integer.MAX_VALUE;
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
                minLeft = Math.min(minLeft, anchorViewState.getAnchorViewRect().left);
            }
        }

        if (!topLeft.isNotFoundState()) {
            assert topLeft.getAnchorViewRect() != null;
            topLeft.getAnchorViewRect().left = minLeft;
            /* we don't need right coordinate for layouter
            also this helps to normalize column properly when anchor deleted and was the longest view in a column
            */
            topLeft.getAnchorViewRect().right = 0;
        }

        return topLeft;
    }
}
