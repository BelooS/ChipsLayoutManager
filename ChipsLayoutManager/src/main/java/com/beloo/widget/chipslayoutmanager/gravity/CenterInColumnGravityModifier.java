package com.beloo.widget.chipslayoutmanager.gravity;

import android.graphics.Rect;

import com.beloo.widget.chipslayoutmanager.layouter.AbstractLayouter;

class CenterInColumnGravityModifier extends PerpendicularItemGravity implements IGravityModifier {

    @Override
    public void modifyChildRect(AbstractLayouter layouter, Rect childRect) {
        super.modifyChildRect(layouter, childRect);
        int placeWidth = maxEnd - minStart;

        int rectWidth = childRect.right - childRect.left;
        int halfOffset = (placeWidth - rectWidth)/2;

        childRect.left = minStart + halfOffset;
        childRect.right = maxEnd - halfOffset;
    }
}
