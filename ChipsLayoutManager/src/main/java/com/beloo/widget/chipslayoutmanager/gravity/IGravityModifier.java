package com.beloo.widget.chipslayoutmanager.gravity;

import android.graphics.Rect;

public interface IGravityModifier {
    void modifyChildRect(int minStart, int maxEnd, Rect childRect);
}
