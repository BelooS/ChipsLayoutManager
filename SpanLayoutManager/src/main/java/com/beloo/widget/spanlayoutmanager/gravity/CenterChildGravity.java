package com.beloo.widget.spanlayoutmanager.gravity;

import android.view.Gravity;

import com.beloo.widget.spanlayoutmanager.SpanLayoutChildGravity;

public class CenterChildGravity implements IChildGravityResolver {
    @Override
    @SpanLayoutChildGravity
    public int getItemGravity(int position) {
        return Gravity.CENTER_VERTICAL;
    }
}
