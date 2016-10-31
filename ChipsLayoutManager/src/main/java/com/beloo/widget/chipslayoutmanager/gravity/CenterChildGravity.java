package com.beloo.widget.chipslayoutmanager.gravity;

import android.view.Gravity;

import com.beloo.widget.chipslayoutmanager.SpanLayoutChildGravity;

public class CenterChildGravity implements IChildGravityResolver {
    @Override
    @SpanLayoutChildGravity
    public int getItemGravity(int position) {
        return Gravity.CENTER_VERTICAL;
    }
}
