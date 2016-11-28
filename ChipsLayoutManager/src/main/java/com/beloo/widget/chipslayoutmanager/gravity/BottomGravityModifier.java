package com.beloo.widget.chipslayoutmanager.gravity;

import android.graphics.Rect;

import com.beloo.widget.chipslayoutmanager.layouter.AbstractLayouter;

class BottomGravityModifier extends PerpendicularItemGravity implements IGravityModifier {
    @Override
    public void modifyChildRect(AbstractLayouter abstractLayouter, Rect childRect) {
        super.modifyChildRect(abstractLayouter, childRect);
        if (childRect.bottom < maxEnd) {
            childRect.top += maxEnd - childRect.bottom;
            childRect.bottom = maxEnd;
        }
    }
}
