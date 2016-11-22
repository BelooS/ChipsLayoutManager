package com.beloo.widget.chipslayoutmanager;

import android.content.Context;
import android.graphics.PointF;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.beloo.widget.chipslayoutmanager.anchor.AnchorViewState;

public class VerticalScrollingController implements IScrollingController {

    private RecyclerView.LayoutManager lm;

    public VerticalScrollingController(RecyclerView.LayoutManager lm) {
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
                return new PointF(0, position > visiblePosition ? 1 : -1);
            }

            @Override
            protected void onTargetFound(View targetView, RecyclerView.State state, Action action) {
                super.onTargetFound(targetView, state, action);
                int desiredTop = lm.getPaddingTop();
                int currentTop = lm.getDecoratedTop(targetView);

                int dy = currentTop - desiredTop;

                //perform fit animation to move target view at top of layout
                action.update(0, dy, timeMs, new LinearInterpolator());
            }
        };
    }
}
