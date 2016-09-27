package com.beloo.widget.spanlayoutmanager.gravity;

import com.beloo.widget.spanlayoutmanager.SpanLayoutChildGravity;

public class CustomGravityResolver implements IChildGravityResolver {

    @SpanLayoutChildGravity
    private int gravity;

    public CustomGravityResolver(int gravity) {
        this.gravity = gravity;
    }

    @Override
    @SpanLayoutChildGravity
    public int getItemGravity(int position) {
        return gravity;
    }
}
