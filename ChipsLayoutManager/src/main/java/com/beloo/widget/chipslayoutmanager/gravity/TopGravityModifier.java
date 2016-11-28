package com.beloo.widget.chipslayoutmanager.gravity;

import android.graphics.Rect;

import com.beloo.widget.chipslayoutmanager.layouter.AbstractLayouter;

class TopGravityModifier extends PerpendicularItemGravity implements IGravityModifier {

    @Override
    public void modifyChildRect(AbstractLayouter layouter, Rect childRect) {
        super.modifyChildRect(layouter, childRect);
        if (childRect.top > minStart) {
            childRect.bottom -= (childRect.top - minStart);
            childRect.top = minStart;
        }
    }
}
