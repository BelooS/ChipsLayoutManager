package com.beloo.widget.chipslayoutmanager.gravity;

import android.graphics.Rect;
import android.support.annotation.CallSuper;

class ValidGravityModifier implements IGravityModifier {

    /** performs validation of input parameters */
    @CallSuper
    @Override
    public Rect modifyChildRect(int minStart, int maxEnd, Rect childRect) {
        Rect returnRect = new Rect(childRect);
        if (childRect.top < minStart) {
            throw new IllegalArgumentException("top point of input rect can't be lower than minTop");
        }
        if (childRect.bottom > maxEnd) {
            throw new IllegalArgumentException("bottom point of input rect can't be bigger than maxTop");
        }
        if (minStart < 0 || maxEnd < 0) {
            throw new IllegalArgumentException("minTop and maxBottom can't be negative");
        }
        if (childRect.top < 0 || childRect.bottom < 0) {
            throw new IllegalArgumentException("Rect values can't be negative");
        }
        return returnRect;
    }
}
