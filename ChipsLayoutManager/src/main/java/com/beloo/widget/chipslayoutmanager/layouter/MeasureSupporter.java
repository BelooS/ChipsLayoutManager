package com.beloo.widget.chipslayoutmanager.layouter;

import android.support.v7.widget.RecyclerView;

abstract class MeasureSupporter extends RecyclerView.AdapterDataObserver implements IMeasureSupporter {

    RecyclerView.LayoutManager lm;

    MeasureSupporter(RecyclerView.LayoutManager lm) {
        this.lm = lm;
    }

    @Override
    public void onItemsRemoved(final RecyclerView recyclerView){
        //subscribe to next animations tick
        lm.postOnAnimation(new Runnable() {
            @Override
            public void run() {
                //listen removing animation
                recyclerView.getItemAnimator().isRunning(new RecyclerView.ItemAnimator.ItemAnimatorFinishedListener() {
                    @Override
                    public void onAnimationsFinished() {
                        //when removing animation finished return auto-measuring back
                        lm.setAutoMeasureEnabled(true);
                        // and process onMeasure again
                        lm.requestLayout();
                    }
                });
            }
        });
    }

}
