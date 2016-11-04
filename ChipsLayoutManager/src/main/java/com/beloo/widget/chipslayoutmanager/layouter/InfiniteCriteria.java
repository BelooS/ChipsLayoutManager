package com.beloo.widget.chipslayoutmanager.layouter;

/** when using this criteria {@link AbstractLayouter} doesn't able to finish himself, you should only stop calling placeView outside */
public class InfiniteCriteria implements IFinishingCriteria {
    @Override
    public boolean isFinishedLayouting(AbstractLayouter abstractLayouter) {
        return false;
    }

    @Override
    public boolean isFinishedLayouting() {
        return false;
    }
}
