package com.beloo.widget.chipslayoutmanager.gravity;

import android.graphics.Rect;
import android.support.annotation.CallSuper;

import com.beloo.widget.chipslayoutmanager.layouter.AbstractLayouter;

abstract class PerpendicularItemGravity implements IGravityModifier {

    int minStart;
    int maxEnd;

    @Override
    @CallSuper
    public void modifyChildRect(AbstractLayouter abstractLayouter, Rect childRect) {
        minStart = abstractLayouter.getStartRowBorder();
        maxEnd = abstractLayouter.getEndRowBorder();
    }
}
