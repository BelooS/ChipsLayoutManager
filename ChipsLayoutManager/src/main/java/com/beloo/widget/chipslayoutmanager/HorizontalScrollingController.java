package com.beloo.widget.chipslayoutmanager;

import android.content.Context;
import android.graphics.PointF;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.beloo.widget.chipslayoutmanager.anchor.AnchorViewState;

public class HorizontalScrollingController implements IScrollingController {

    private RecyclerView.LayoutManager lm;

    public HorizontalScrollingController(RecyclerView.LayoutManager lm) {
        this.lm = lm;
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
                int currentLeft = lm.getPaddingLeft();
                int desiredLeft = lm.getDecoratedLeft(targetView);

                int dx = desiredLeft - currentLeft;

                //perform fit animation to move target view at top of layout
                action.update(dx, 0, timeMs, new LinearInterpolator());
            }
        };
    }
}
