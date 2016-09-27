package com.beloo.widget.spanlayoutmanager.gravity;

import com.beloo.widget.spanlayoutmanager.SpanLayoutChildGravity;

public interface IChildGravityResolver {
    @SpanLayoutChildGravity
    int getItemGravity(int position);
}
