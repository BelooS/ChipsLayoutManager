package com.beloo.widget.chipslayoutmanager.gravity;

import android.graphics.Rect;

import com.beloo.widget.chipslayoutmanager.layouter.AbstractLayouter;

public interface IGravityModifier {
    void modifyChildRect(int minStart, int maxEnd, Rect childRect);
}
