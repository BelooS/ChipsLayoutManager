package com.beloo.widget.chipslayoutmanager.anchor;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.beloo.widget.chipslayoutmanager.ChildViewsIterable;
import com.beloo.widget.chipslayoutmanager.layouter.ICanvas;

public class ColumnsAnchorFactory extends AbstractAnchorFactory {

    private ChildViewsIterable childViews;

    public ColumnsAnchorFactory(RecyclerView.LayoutManager lm, ICanvas canvas) {
        super(lm, canvas);
        childViews = new ChildViewsIterable(lm);
    }

    /** get the closest views to left border. The highest view will be picked from it. */
    @Override
    public AnchorViewState getAnchor() {

        AnchorViewState minPosView = AnchorViewState.getNotFoundState();

        int minPosition = Integer.MAX_VALUE;
        int minLeft = Integer.MAX_VALUE;
        int maxRight = Integer.MIN_VALUE;

        for (View view : childViews) {
            AnchorViewState anchorViewState = createAnchorState(view);
            int pos = lm.getPosition(view);
            int left = lm.getDecoratedLeft(view);
            int right = lm.getDecoratedRight(view);

            Rect viewRect = new Rect(anchorViewState.getAnchorViewRect());

            if (getCanvas().isInside(viewRect) && !anchorViewState.isRemoving()) {
                if (minPosition > pos) {
                    minPosition = pos;
                    minPosView = anchorViewState;
                }

                if (minLeft > left) {
                    minLeft = left;
                    maxRight = right;
                } else if (minLeft == left) {
                    maxRight = Math.max(maxRight, right);
                }

            }
        }

        if (!minPosView.isNotFoundState()) {
            minPosView.getAnchorViewRect().left = minLeft;
            minPosView.getAnchorViewRect().right = maxRight;

            minPosView.setPosition(minPosition);
        }

        return minPosView;
    }

    @Override
    public void resetRowCoordinates(AnchorViewState anchorView) {
        if (!anchorView.isNotFoundState()) {
            Rect rect = anchorView.getAnchorViewRect();
            rect.top = getCanvas().getCanvasTopBorder();
            rect.bottom = getCanvas().getCanvasBottomBorder();
        }
    }
}
