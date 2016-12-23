package com.beloo.widget.chipslayoutmanager;

import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.ViewInteraction;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import com.beloo.chipslayoutmanager.sample.R;
import com.beloo.widget.chipslayoutmanager.util.Action;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

public class RTLRowTest extends ChipsLayoutManagerRowTest {

    @Override
    protected ChipsLayoutManager getLayoutManager() {
        ViewInteraction recyclerView = onView(withId(R.id.rvTest)).check(matches(isDisplayed()));

        ViewAction action = new Action<RecyclerView>() {
            @Override
            public void performAction(UiController uiController, RecyclerView view) {
                ViewCompat.setLayoutDirection(view, ViewCompat.LAYOUT_DIRECTION_RTL);
            }
        };

        recyclerView.perform(action);

        return ChipsLayoutManager.newBuilder(activityTestRule.getActivity())
                .setOrientation(ChipsLayoutManager.HORIZONTAL)
                .build();
    }
}
