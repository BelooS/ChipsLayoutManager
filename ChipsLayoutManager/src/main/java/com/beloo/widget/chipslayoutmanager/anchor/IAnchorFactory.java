package com.beloo.widget.chipslayoutmanager.anchor;

import android.support.v7.widget.RecyclerView;
import android.view.View;

public interface IAnchorFactory {
    /** find the view in a higher row which is closest to the left border*/
    AnchorViewState getAnchor();

    AnchorViewState createAnchorState(View view);

    AnchorViewState createNotFound();

    /** modify anchorView state according to pre-layout state */
    void afterPreLayout(AnchorViewState anchorView, RecyclerView.Recycler recycler);
}
