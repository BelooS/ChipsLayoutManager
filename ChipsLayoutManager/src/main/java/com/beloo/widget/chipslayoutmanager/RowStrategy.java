package com.beloo.widget.chipslayoutmanager;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@IntDef({ChipsLayoutManager.STRATEGY_DEFAULT,
        ChipsLayoutManager.STRATEGY_FILL_SPACE,
        ChipsLayoutManager.STRATEGY_FILL_VIEW,
        ChipsLayoutManager.STRATEGY_CENTER,
        ChipsLayoutManager.STRATEGY_CENTER_DENSE
})
@Retention(RetentionPolicy.SOURCE)
public @interface RowStrategy {}
