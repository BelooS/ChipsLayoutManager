package com.beloo.widget.chipslayoutmanager.logger;

import com.beloo.widget.chipslayoutmanager.anchor.AnchorViewState;

public interface IScrollingLogger {
    void logChildCount(int childCount);
    void logUpScrollingNormalizationDistance(int distance);
    void logAnchorView(AnchorViewState anchorViewState);
}
