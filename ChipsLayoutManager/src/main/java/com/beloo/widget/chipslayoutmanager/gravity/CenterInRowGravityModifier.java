package com.beloo.widget.chipslayoutmanager.gravity;

import android.graphics.Rect;

import com.beloo.widget.chipslayoutmanager.layouter.AbstractLayouter;

class CenterInRowGravityModifier extends PerpendicularItemGravity implements IGravityModifier {
    @Override
    public void modifyChildRect(AbstractLayouter layouter, Rect childRect) {
        super.modifyChildRect(layouter, childRect);
        int placeHeight = maxEnd - minStart;
        int rectHeight = childRect.bottom - childRect.top;
        //calculate needed offset
        int halfOffset = (placeHeight - rectHeight)/2;

        childRect.top = minStart + halfOffset;
        childRect.bottom = maxEnd - halfOffset;
    }
}
