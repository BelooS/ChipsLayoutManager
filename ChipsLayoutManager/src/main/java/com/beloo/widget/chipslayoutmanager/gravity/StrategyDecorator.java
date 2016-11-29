package com.beloo.widget.chipslayoutmanager.gravity;

import android.support.annotation.NonNull;

import com.beloo.widget.chipslayoutmanager.layouter.AbstractLayouter;
import com.beloo.widget.chipslayoutmanager.layouter.Item;

import java.util.List;

public class StrategyDecorator implements IRowStrategy {

    @NonNull
    private IRowStrategy rowStrategy;

    public StrategyDecorator(@NonNull IRowStrategy rowStrategy) {
        this.rowStrategy = rowStrategy;
    }

    @Override
    public void applyStrategy(AbstractLayouter abstractLayouter, List<Item> row) {
        rowStrategy.applyStrategy(abstractLayouter, row);
    }
}
