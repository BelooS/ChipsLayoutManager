package com.beloo.widget.chipslayoutmanager.anchor;

public interface IAnchorFactory {
    /** find the view in a higher row which is closest to the left border*/
    AnchorViewState getAnchor();

    AnchorViewState createNotFound();

    /** modify anchorView state according to pre-layout state */
    void resetRowCoordinates(AnchorViewState anchorView);

    /** method in case deleting longest items from the first row*/
    boolean normalize(AnchorViewState anchorView);
}
