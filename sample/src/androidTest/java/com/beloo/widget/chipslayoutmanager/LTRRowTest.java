package com.beloo.widget.chipslayoutmanager;

public class LTRRowTest extends ChipsLayoutManagerRowTest {

    @Override
    protected ChipsLayoutManager getLayoutManager() {
        return ChipsLayoutManager.newBuilder(activityTestRule.getActivity())
                .setOrientation(ChipsLayoutManager.HORIZONTAL)
                .build();
    }
}
