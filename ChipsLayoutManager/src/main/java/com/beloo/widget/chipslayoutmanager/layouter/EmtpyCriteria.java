package com.beloo.widget.chipslayoutmanager.layouter;

public class EmtpyCriteria implements IFinishingCriteria {
    @Override
    public boolean isFinishedLayouting(AbstractLayouter abstractLayouter) {
        return true;
    }

    @Override
    public boolean isFinishedLayouting() {
        return true;
    }
}
