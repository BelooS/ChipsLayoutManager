package com.beloo.widget.spanlayoutmanager;

interface IChildGravityResolver {
    @SpanLayoutChildGravity
    int getItemGravity(int position);
}
