package com.beloo.widget.chipslayoutmanager.gravity;

import com.beloo.widget.chipslayoutmanager.SpanLayoutChildGravity;

public interface IChildGravityResolver {
    @SpanLayoutChildGravity
    int getItemGravity(int position);
}
