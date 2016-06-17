package com.beloo.widget.spanlayoutmanager;

public interface IChildGravityResolver {
    @SpanLayoutChildGravity
    int getItemGravity(int position);
}
