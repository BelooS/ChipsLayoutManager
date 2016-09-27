package com.beloo.widget.spanlayoutmanager.gravity;

import android.graphics.Rect;
import android.support.annotation.CallSuper;

class AbstractGravityModifier implements IGravityModifier {

    @CallSuper
    @Override
    public Rect modifyChildRect(int minTop, int maxBottom, Rect childRect) {
        Rect returnRect = new Rect(childRect);
        if (childRect.top < minTop) {
            throw new IllegalArgumentException("top point of input rect can't be lower than minTop");
        }
        if (childRect.bottom > maxBottom) {
            throw new IllegalArgumentException("bottom point of input rect can't be bigger than maxTop");
        }
        if (minTop < 0 || maxBottom < 0) {
            throw new IllegalArgumentException("minTop and maxBottom can't be negative");
        }
        if (childRect.top < 0 || childRect.bottom < 0) {
            throw new IllegalArgumentException("Rect values can't be negative");
        }
        return returnRect;
    }
}
