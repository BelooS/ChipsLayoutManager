package com.beloo.widget.chipslayoutmanager.gravity;

import com.beloo.widget.chipslayoutmanager.RowStrategy;

public interface IRowStrategyFactory {
    IRowStrategy createRowStrategy(@RowStrategy int rowStrategy);
}
