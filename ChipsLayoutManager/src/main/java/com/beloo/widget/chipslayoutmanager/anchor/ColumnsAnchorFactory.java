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
                if (minLeft > anchorViewState.getAnchorViewRect().left) {
                    topLeft = anchorViewState;
                    minLeft = anchorViewState.getAnchorViewRect().left;
                }
            }
        }

        return topLeft;
    }
}
