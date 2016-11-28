package com.beloo.widget.chipslayoutmanager.gravity;

import android.view.Gravity;

import com.beloo.widget.chipslayoutmanager.SpanLayoutChildGravity;

abstract class RowGravityModifiersFactory implements IGravityModifiersFactory {

    RowGravityModifiersFactory() {}

    public final IGravityModifier getGravityModifier(@SpanLayoutChildGravity int gravity) {

        IGravityModifier gravityModifier = new CenterInRowGravityModifier();

        if ((gravity & Gravity.TOP) == Gravity.TOP) {
            gravityModifier = new TopGravityModifier();
        } else if ((gravity & Gravity.BOTTOM) == Gravity.BOTTOM) {
            gravityModifier = new BottomGravityModifier();
        }

        //fill gravity could set together with other types
        if ((gravity & Gravity.FILL_HORIZONTAL) == Gravity.FILL_HORIZONTAL) {
            gravityModifier = new RTLRowFillGravityModifier(gravityModifier);
        }

        return gravityModifier;
    }

    public abstract IGravityModifier createFillModifier(IGravityModifier gravityModifier);
}
