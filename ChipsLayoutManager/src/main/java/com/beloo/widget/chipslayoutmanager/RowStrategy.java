package com.beloo.widget.chipslayoutmanager;

import android.support.annotation.IntDef;
import android.view.Gravity;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@IntDef({ChipsLayoutManager.STRATEGY_DEFAULT,
        ChipsLayoutManager.STRATEGY_FILL_SPACE,
        ChipsLayoutManager.STRATEGY_FILL_VIEW
})
@Retention(RetentionPolicy.SOURCE)
public @interface RowStrategy {}
