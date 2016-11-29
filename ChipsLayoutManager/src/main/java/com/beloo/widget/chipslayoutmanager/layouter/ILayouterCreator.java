package com.beloo.widget.chipslayoutmanager.layouter;

import android.graphics.Rect;

interface ILayouterCreator {
    //---- up layouter below
    Rect createOffsetRectForBackwardLayouter(Rect anchorRect);

    AbstractLayouter.Builder createBackwardBuilder();

    AbstractLayouter.Builder createForwardBuilder();

    Rect createOffsetRectForForwardLayouter(Rect anchorRect);
}
