package com.beloo.widget.chipslayoutmanager.gravity;

import com.beloo.widget.chipslayoutmanager.SpanLayoutChildGravity;

public interface IGravityModifiersFactory {
    IGravityModifier getGravityModifier(@SpanLayoutChildGravity int gravity);
}
