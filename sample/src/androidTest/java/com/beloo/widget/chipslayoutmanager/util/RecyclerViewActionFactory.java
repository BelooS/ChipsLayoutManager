package com.beloo.widget.chipslayoutmanager.util;

import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.beloo.widget.chipslayoutmanager.ChildViewsIterable;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import java.util.Locale;

import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static org.hamcrest.Matchers.allOf;

public class RecyclerViewActionFactory {
    ///////////////////////////////////////////////////////////////////////////
    // Actions factory
    ///////////////////////////////////////////////////////////////////////////

    public ViewAction scrollBy(int x, int y) {
        return new ScrollByRecyclerViewAction(x, y);
    }

    public ViewAction smoothScrollToPosition(int position) {
        return new SmoothScrollToPositionRecyclerViewAction(position);
    }

    public ViewAction setAdapter(RecyclerView.Adapter<? extends RecyclerView.ViewHolder> adapter) {
        return new SetAdapterAction(adapter);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Matcher factory
    ///////////////////////////////////////////////////////////////////////////

    public Matcher<View> incrementOrder() {
        return orderMatcher();
    }

    ///////////////////////////////////////////////////////////////////////////
    // Actions
    ///////////////////////////////////////////////////////////////////////////

    private static final class SetAdapterAction extends RecyclerViewAction {
        private final RecyclerView.Adapter<? extends RecyclerView.ViewHolder> adapter;

        private SetAdapterAction(RecyclerView.Adapter<? extends RecyclerView.ViewHolder> adapter) {
            this.adapter = adapter;
        }

        @Override
        public String getDescription() {
            return"set adapter to recycler view";
        }

        @Override
        public void perform(UiController uiController, View view) {
            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setAdapter(adapter);
        }
    }

    private static final class SmoothScrollToPositionRecyclerViewAction extends RecyclerViewAction {
        private final int position;

        private SmoothScrollToPositionRecyclerViewAction(int position) {
            this.position = position;
        }

        @Override
        public String getDescription() {
            return String.format(Locale.getDefault(), "smooth scroll RecyclerView to position %d", position);
        }

        @Override
        public void perform(UiController uiController, View view) {
            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    SmoothScrollToPositionRecyclerViewAction.this.onScrollStateChanged(recyclerView, newState);
                }
            });
            recyclerView.smoothScrollToPosition(position);
        }

        private synchronized void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                notify();
            }
        }
    }

    private static final class ScrollByRecyclerViewAction extends RecyclerViewAction {
        private final int x;
        private final int y;

        private ScrollByRecyclerViewAction(int x, int y) {
            this.x = x;
            this.y = y;
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

    private abstract static class RecyclerViewAction implements ViewAction {

        @Override
        public Matcher<View> getConstraints() {
            return allOf(isAssignableFrom(RecyclerView.class), isDisplayed());
        }

        @Override
        public String getDescription() {
            return "RecyclerView action " + this.getClass().getSimpleName();
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // Matcher
    ///////////////////////////////////////////////////////////////////////////

    private Matcher<View> orderMatcher() {
        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("with correct position order");
            }

            @Override
            public boolean matchesSafely(View v) {
                RecyclerView view = (RecyclerView) v;
                if (view.getLayoutManager() == null) return false;
                ChildViewsIterable childViews = new ChildViewsIterable(view.getLayoutManager());
                int pos = view.getChildAdapterPosition(childViews.iterator().next());
                for (View child : childViews) {
                    if (pos != view.getChildAdapterPosition(child)) {
                        return false;
                    }
                    pos ++;
                }
                return true;
            }
        };
    }
}
