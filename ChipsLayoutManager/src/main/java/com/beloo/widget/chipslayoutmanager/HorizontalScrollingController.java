package com.beloo.widget.chipslayoutmanager;

import android.content.Context;
import android.graphics.PointF;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.beloo.widget.chipslayoutmanager.anchor.AnchorViewState;
import com.beloo.widget.chipslayoutmanager.layouter.IStateFactory;

class HorizontalScrollingController extends ScrollingController implements IScrollingController {

    private ChipsLayoutManager layoutManager;

    HorizontalScrollingController(ChipsLayoutManager layoutManager, IStateFactory stateFactory, IScrollerListener scrollerListener) {
        super(layoutManager, stateFactory, scrollerListener);
        this.layoutManager = layoutManager;
    }

    @Override
    public RecyclerView.SmoothScroller createSmoothScroller(@NonNull Context context, final int position, final int timeMs, final AnchorViewState anchor) {
        return new LinearSmoothScroller(context) {
            /*
             * LinearSmoothScroller, at a minimum, just need to know the vector
             * (x/y distance) to travel in order to get from the current positioning
             * to the target.
             */
            @Override
            public PointF computeScrollVectorForPosition(int targetPosition) {
                int visiblePosition = anchor.getPosition();
                //determine scroll up or scroll down needed
                return new PointF(position > visiblePosition ? 1 : -1, 0);
            }

            @Override
            protected void onTargetFound(View targetView, RecyclerView.State state, Action action) {
                super.onTargetFound(targetView, state, action);
                int currentLeft = layoutManager.getPaddingLeft();
                int desiredLeft = layoutManager.getDecoratedLeft(targetView);

                int dx = desiredLeft - currentLeft;

                //perform fit animation to move target view at top of layoutX
                action.update(dx, 0, timeMs, new LinearInterpolator());
            }
        };
    }

    @Override
    public boolean canScrollVertically() {
        return false;
    }

    @Override
    public boolean canScrollHorizontally() {
        canvas.findBorderViews();
        if (layoutManager.getChildCount() > 0) {
            int left = layoutManager.getDecoratedLeft(canvas.getLeftView());
            int right = layoutManager.getDecoratedRight(canvas.getRightView());

            if (canvas.getMinPositionOnScreen() == 0
                    && canvas.getMaxPositionOnScreen() == layoutManager.getItemCount() - 1
                    && left >= layoutManager.getPaddingLeft()
                    && right <= layoutManager.getWidth() - layoutManager.getPaddingRight()) {
                return false;
            }
        } else {
            return false;
        }

        return layoutManager.isScrollingEnabledContract();
    }

    @Override
    void offsetChildren(int d) {
        layoutManager.offsetChildrenHorizontal(d);
    }

}
