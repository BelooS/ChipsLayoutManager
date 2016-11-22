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

        Rect mainRect = getCanvasRect();

        int minPosition = Integer.MAX_VALUE;
        int minLeft = Integer.MAX_VALUE;
        int maxRight = Integer.MIN_VALUE;

        for (View view : childViews) {
            AnchorViewState anchorViewState = createAnchorState(view);
            int pos = lm.getPosition(view);
            int left = lm.getDecoratedLeft(view);
            int right = lm.getDecoratedRight(view);

            //intersection changes rect!!!
            Rect viewRect = new Rect(anchorViewState.getAnchorViewRect());
            boolean intersect = viewRect.intersect(mainRect);

            if (intersect && !anchorViewState.isRemoving()) {
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
    public boolean normalize(AnchorViewState anchorView) {
        if (!anchorView.isNotFoundState() && anchorView.getAnchorViewRect().left > lm.getPaddingLeft()) {
            if (!anchorView.isNotFoundState()) {
                int d = anchorView.getAnchorViewRect().left - lm.getPaddingLeft();
                anchorView.getAnchorViewRect().left -= d;
                anchorView.getAnchorViewRect().right -= d;
            }
            return true;
        }
        return false;
    }

    @Override
    public void onPreLayout(AnchorViewState anchorView, RecyclerView.Recycler recycler) {
//        if (!anchorView.isNotFoundState() && recycler.convertPreLayoutPositionToPostLayout(anchorView.getPosition()) == -1) {
//            //view going to remove
//            anchorView.getAnchorViewRect().right = 0;
//        }
    }
}
