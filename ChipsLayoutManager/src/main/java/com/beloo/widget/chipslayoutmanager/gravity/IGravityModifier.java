package com.beloo.widget.chipslayoutmanager.gravity;

import android.graphics.Rect;
import android.support.annotation.IntRange;

public interface IGravityModifier {
    /** @return created rect based on modified input rect due to concrete gravity modifier.
     * @param childRect input rect. Immutable*/
    Rect modifyChildRect(@IntRange(from = 0) int minTop, @IntRange(from = 0) int maxBottom, Rect childRect);
}
