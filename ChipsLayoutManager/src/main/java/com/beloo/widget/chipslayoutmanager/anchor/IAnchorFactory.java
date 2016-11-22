package com.beloo.widget.chipslayoutmanager.anchor;

import android.support.v7.widget.RecyclerView;
import android.view.View;

public interface IAnchorFactory {
    /** find the view in a higher row which is closest to the left border*/
    AnchorViewState getAnchor();

    AnchorViewState createAnchorState(View view);

    AnchorViewState createNotFound();

    boolean normalize(AnchorViewState anchor);

    /** modify anchorView state according to pre-layout state */
    void onPreLayout(AnchorViewState anchorView, RecyclerView.Recycler recycler);

    void setRecycler(RecyclerView.Recycler recycler);
}
