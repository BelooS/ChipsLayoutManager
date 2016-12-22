package com.beloo.widget.chipslayoutmanager.util;

import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import org.hamcrest.Matcher;

import java.util.Locale;

import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static org.hamcrest.Matchers.allOf;

public class RecyclerViewActionsFactory {
    public ViewAction scrollBy(int x, int y) {
        return new ScrollByRecyclerViewAction(x, y);
    }

    private static final class ScrollByRecyclerViewAction implements ViewAction {
        private final int x;
        private final int y;

        private ScrollByRecyclerViewAction(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public Matcher<View> getConstraints() {
            return allOf(isAssignableFrom(RecyclerView.class), isDisplayed());
        }

        @Override
        public String getDescription() {
            return String.format(Locale.getDefault(), "scroll RecyclerView with offsets: x = %d, y = %d ", x, y);
        }

        @Override
        public void perform(UiController uiController, View view) {
            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.scrollBy(x, y);
        }
    }
}
