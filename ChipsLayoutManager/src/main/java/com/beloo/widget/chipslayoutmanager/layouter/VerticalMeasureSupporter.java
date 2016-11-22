package com.beloo.widget.chipslayoutmanager.layouter;

import android.support.v7.widget.RecyclerView;
import android.view.View;

class VerticalMeasureSupporter extends MeasureSupporter {

    /**
     * height of RecyclerView before removing item
     */
    private Integer beforeRemovingHeight = null;

    /**
     * height which we receive after {@link RecyclerView.LayoutManager#onLayoutChildren} method finished.
     * Contains correct height after auto-measuring
     */
    private int autoMeasureHeight = 0;

    VerticalMeasureSupporter(RecyclerView.LayoutManager lm) {
        super(lm);
    }

    @Override
    public void afterOnLayoutChildren() {
        autoMeasureHeight = lm.getHeight();
    }

    @Override
    public void onMeasure(RecyclerView.Recycler recycler, RecyclerView.State state, int widthSpec, int heightSpec) {
        if (!lm.isAutoMeasureEnabled()) {
            // we should perform measuring manually
            // so request animations
            lm.requestSimpleAnimationsInNextLayout();
            //keep height until remove animation will be completed
            lm.setMeasuredDimension(View.MeasureSpec.getSize(widthSpec), beforeRemovingHeight);
        }
    }

    @Override
    public void onItemRangeRemoved(int positionStart, int itemCount) {
        super.onItemRangeRemoved(positionStart, itemCount);
        beforeRemovingHeight = autoMeasureHeight;
        /** we detected removing event, so should process measuring manually
         * @see <a href="http://stackoverflow.com/questions/40242011/custom-recyclerviews-layoutmanager-automeasuring-after-animation-finished-on-i">Stack Overflow issue</a>
         */
        lm.setAutoMeasureEnabled(false);
    }
}
