package com.beloo.widget.chipslayoutmanager;

public class LTRRowTest extends RowTest {

    @Override
    protected ChipsLayoutManager getLayoutManager() {
        return ChipsLayoutManager.newBuilder(activityTestRule.getActivity())
                .setOrientation(ChipsLayoutManager.HORIZONTAL)
                .build();
    }
}
