package com.beloo.widget.chipslayoutmanager.layouter.placer;

import android.support.annotation.NonNull;
import android.view.View;

import com.beloo.widget.chipslayoutmanager.layouter.AbstractLayouter;
import com.beloo.widget.chipslayoutmanager.layouter.IFinishingCriteria;

public class DisappearingViewBottomPlacer extends AbstractPlacer {

    @NonNull
    private IFinishingCriteria behaviourCriteria;
    @NonNull
    private AbstractLayouter abstractLayouter;

    public DisappearingViewBottomPlacer(@NonNull AbstractLayouter abstractLayouter, @NonNull IFinishingCriteria behaviourCriteria) {
        super(abstractLayouter.getLayoutManager());
        this.behaviourCriteria = behaviourCriteria;
        this.abstractLayouter = abstractLayouter;
    }

    @Override
    public void addView(View view) {
        if (!behaviourCriteria.isFinishedLayouting(abstractLayouter)) {
            getLayoutManager().addView(view);
        } else {
            getLayoutManager().addDisappearingView(view);
        }
    }
}
