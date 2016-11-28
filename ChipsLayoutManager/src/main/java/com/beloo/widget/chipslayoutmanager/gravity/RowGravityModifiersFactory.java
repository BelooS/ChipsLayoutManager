package com.beloo.widget.chipslayoutmanager.gravity;

import android.view.Gravity;

import com.beloo.widget.chipslayoutmanager.SpanLayoutChildGravity;

public class RowGravityModifiersFactory implements IGravityModifiersFactory {


    public RowGravityModifiersFactory() {}

    public IGravityModifier getGravityModifier(@SpanLayoutChildGravity int gravity) {

        IGravityModifier gravityModifier = new CenterInRowGravityModifier();

        if ((gravity & Gravity.TOP) == Gravity.TOP) {
            gravityModifier = new TopGravityModifier();
        } else if ((gravity & Gravity.BOTTOM) == Gravity.BOTTOM) {
            gravityModifier = new BottomGravityModifier();
        }

        //fill gravity could set together with other types
        if ((gravity & Gravity.FILL) == Gravity.FILL) {
            gravityModifier = new RowFillGravityModifier(gravityModifier);
        }

        return gravityModifier;
    }

}
