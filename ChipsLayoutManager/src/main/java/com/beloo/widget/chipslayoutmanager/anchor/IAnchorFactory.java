package com.beloo.widget.chipslayoutmanager.anchor;

import android.view.View;

public interface IAnchorFactory {
    /** find the view in a higher row which is closest to the left border*/
    AnchorViewState getTopLeftAnchor();

    AnchorViewState createAnchorState(View view);

    AnchorViewState createNotFound();
}
