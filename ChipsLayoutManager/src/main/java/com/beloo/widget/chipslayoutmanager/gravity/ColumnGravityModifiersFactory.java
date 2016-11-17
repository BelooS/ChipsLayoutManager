package com.beloo.widget.chipslayoutmanager.gravity;

import android.util.SparseArray;
import android.view.Gravity;

import com.beloo.widget.chipslayoutmanager.SpanLayoutChildGravity;

public class ColumnGravityModifiersFactory implements IGravityModifiersFactory {

    private SparseArray<IGravityModifier> gravityModifierMap;

    public ColumnGravityModifiersFactory() {
        gravityModifierMap = new SparseArray<>();

        CenterInColumnGravityModifier centerGravityModifier = new CenterInColumnGravityModifier();

        gravityModifierMap.put(Gravity.CENTER, centerGravityModifier);
        gravityModifierMap.put(Gravity.CENTER_HORIZONTAL, centerGravityModifier);
    }

    public IGravityModifier getGravityModifier(@SpanLayoutChildGravity int gravity) {
        IGravityModifier gravityModifier = gravityModifierMap.get(gravity);
        if (gravityModifier == null) {
            gravityModifier = gravityModifierMap.get(Gravity.CENTER_HORIZONTAL);
        }
        return gravityModifier;
    }

}
