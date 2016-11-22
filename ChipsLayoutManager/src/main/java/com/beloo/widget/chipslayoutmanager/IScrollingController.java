package com.beloo.widget.chipslayoutmanager;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;

import com.beloo.widget.chipslayoutmanager.anchor.AnchorViewState;

public interface IScrollingController {

    RecyclerView.SmoothScroller createSmoothScroller(@NonNull Context context, int position, int timeMs, AnchorViewState anchor);
}
