package com.beloo.widget.chipslayoutmanager.gravity;

import android.util.SparseArray;
import android.view.Gravity;

import com.beloo.widget.chipslayoutmanager.SpanLayoutChildGravity;

public class GravityModifiersFactory {

    private SparseArray<IGravityModifier> gravityModifierMap;

    public GravityModifiersFactory() {
        gravityModifierMap = new SparseArray<>();

        CenterGravityModifier centerGravityModifier = new CenterGravityModifier();
        TopGravityModifier topGravityModifier = new TopGravityModifier();
        BottomGravityModifier bottomGravityModifier = new BottomGravityModifier();

        gravityModifierMap.put(Gravity.TOP, topGravityModifier);
        gravityModifierMap.put(Gravity.BOTTOM, bottomGravityModifier);
        gravityModifierMap.put(Gravity.CENTER, centerGravityModifier);
        gravityModifierMap.put(Gravity.CENTER_VERTICAL, centerGravityModifier);
    }

    public IGravityModifier getGravityModifier(@SpanLayoutChildGravity int gravity) {
        IGravityModifier gravityModifier = gravityModifierMap.get(gravity);
        if (gravityModifier == null) {
            gravityModifier = gravityModifierMap.get(Gravity.CENTER_VERTICAL);
        }
        return gravityModifier;
    }

}
