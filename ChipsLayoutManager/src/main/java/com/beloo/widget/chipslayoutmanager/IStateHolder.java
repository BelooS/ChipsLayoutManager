package com.beloo.widget.chipslayoutmanager;

public interface IStateHolder {
    int ROWS = 1;
    int COLUMNS = 2;

    boolean isLayoutRTL();

    @Orientation
    int orientation();

}
