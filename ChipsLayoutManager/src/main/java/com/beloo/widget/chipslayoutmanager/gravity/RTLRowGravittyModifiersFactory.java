package com.beloo.widget.chipslayoutmanager.gravity;

public class RTLRowGravittyModifiersFactory extends RowGravityModifiersFactory implements IGravityModifiersFactory {

    @Override
    public IGravityModifier createFillModifier(IGravityModifier gravityModifier) {
        return new RTLRowFillGravityModifier(gravityModifier);
    }
}
