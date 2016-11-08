package com.beloo.widget.chipslayoutmanager;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class SpacingItemDecoration extends RecyclerView.ItemDecoration {
    private int horizontalSpacing;
    private int verticalSpacing;

    public SpacingItemDecoration(int horizontalSpacing, int verticalSpacing) {
        this.horizontalSpacing = horizontalSpacing;
        this.verticalSpacing = verticalSpacing;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.left = horizontalSpacing / 2;
        outRect.right = horizontalSpacing / 2;
        outRect.top = verticalSpacing / 2;
        outRect.bottom = verticalSpacing /2;
    }
}
