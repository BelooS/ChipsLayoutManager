package com.beloo.widget.chipslayoutmanager.gravity;

public class LTRRowGravittyModifiersFactory extends RowGravityModifiersFactory implements IGravityModifiersFactory {

    @Override
    public IGravityModifier createFillModifier(IGravityModifier gravityModifier) {
        return new LTRRowFillGravityModifier(gravityModifier);
    }
}
