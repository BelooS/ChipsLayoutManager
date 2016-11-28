package com.beloo.widget.chipslayoutmanager.gravity;

import android.graphics.Rect;
import android.support.annotation.CallSuper;

import com.beloo.widget.chipslayoutmanager.layouter.AbstractLayouter;

abstract class GravityDecorator implements IGravityModifier {

    private IGravityModifier gravityModifier;

    GravityDecorator(IGravityModifier gravityModifier) {
        this.gravityModifier = gravityModifier;
    }

    @Override
    @CallSuper
    public void modifyChildRect(AbstractLayouter abstractLayouter, Rect childRect) {
        gravityModifier.modifyChildRect(abstractLayouter, childRect);
    }
}
