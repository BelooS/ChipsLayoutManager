package com.beloo.widget.chipslayoutmanager;

import android.support.annotation.IntRange;

import com.beloo.widget.chipslayoutmanager.layouter.breaker.IRowBreaker;

interface IChipsLayoutManagerContract extends IPositionsContract, IScrollingContract {
    /** use it to strictly disable scrolling.
     * If scrolling enabled it would be disabled in case all items fit on the screen */
    void setScrollingEnabledContract(boolean isEnabled);
    /**
     * change max count of row views in runtime
     */
    void setMaxViewsInRow(@IntRange(from = 1) Integer maxViewsInRow);

    /** retrieve max views in row settings*/
    Integer getMaxViewsInRow();

    /** retrieve instantiated row breaker*/
    IRowBreaker getRowBreaker();

    /** retrieve row strategy type*/
    @RowStrategy
    int getRowStrategyType();

    @Orientation
    /** orientation type of layout manager*/
    int layoutOrientation();

    /** whether or not scrolling disabled outside*/
    boolean isScrollingEnabledContract();
}
