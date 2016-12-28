package com.beloo.widget.chipslayoutmanager;

import android.support.annotation.UiThread;
import android.support.v4.view.ViewCompat;
import android.view.View;

import com.beloo.chipslayoutmanager.sample.R;

public class RTLRowTest extends RowTest {

    @UiThread
    @Override
    protected ChipsLayoutManager getLayoutManager() {
        ChipsLayoutManager layoutManager = super.getLayoutManager();
        if (activityTestRule.getActivity() != null) {
            View recyclerView = activityTestRule.getActivity().findViewById(R.id.rvTest);
            ViewCompat.setLayoutDirection(recyclerView, ViewCompat.LAYOUT_DIRECTION_RTL);
        }
        return layoutManager;
    }
}
