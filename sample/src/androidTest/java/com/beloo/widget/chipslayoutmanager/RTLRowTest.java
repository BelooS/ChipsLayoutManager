package com.beloo.widget.chipslayoutmanager;

import android.support.test.espresso.ViewAction;
import android.support.test.espresso.ViewInteraction;
import android.support.v4.view.ViewCompat;
import com.beloo.chipslayoutmanager.sample.R;

import org.junit.Before;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

public class RTLRowTest extends RowTest {

    @Override
    public void onSetUp() throws Exception {
        resetToInitialAfterRotate();
        ViewAction action = actionsFactory.actionDelegate((uiController, view) -> ViewCompat.setLayoutDirection(view, ViewCompat.LAYOUT_DIRECTION_RTL));
        recyclerView.perform(action);
    }
}
