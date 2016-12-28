package com.beloo.widget.chipslayoutmanager.util;

import android.support.annotation.NonNull;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

import static android.support.test.espresso.core.deps.guava.base.Preconditions.checkNotNull;

public class RecyclerViewMatcher {

    public static Matcher<View> atPosition(final int position, @NonNull final Matcher<View> itemMatcher) {
        checkNotNull(itemMatcher);
        return new BoundedMatcher<View, RecyclerView>(RecyclerView.class) {
            @Override
            public void describeTo(Description description) {
                description.appendText("has item at position " + position + ":\n");
                itemMatcher.describeTo(description);
            }

            @Override
            protected boolean matchesSafely(final RecyclerView view) {
                RecyclerView.ViewHolder viewHolder = view.findViewHolderForAdapterPosition(position);
                return viewHolder != null && itemMatcher.matches(viewHolder.itemView);
            }
        };
    }

    public static <T extends RecyclerView.ViewHolder> Matcher<View> atPosition(final int position, @NonNull final ViewHolderMatcher<T> itemMatcher) {
        checkNotNull(itemMatcher);
        return new BoundedMatcher<View, RecyclerView>(RecyclerView.class) {
            @Override
            public void describeTo(Description description) {
                description.appendText("has item at position " + position + ":\n");
                itemMatcher.describeTo(description);
            }

            @Override
            protected boolean matchesSafely(final RecyclerView view) {
                RecyclerView.ViewHolder viewHolder = view.findViewHolderForAdapterPosition(position);
                return viewHolder != null && itemMatcher.matches(viewHolder);
            }
        };
    }

    public abstract static class ViewHolderMatcher<VH extends RecyclerView.ViewHolder> extends BaseMatcher<VH> {

        @Override
        public boolean matches(Object item) {
            VH viewHolder = (VH) item;
            RecyclerView recyclerView = (RecyclerView) viewHolder.itemView.getParent();
            return matches(recyclerView, viewHolder.itemView, viewHolder);
        }

        @Override
        public void describeTo(Description description) {

        }

        public abstract boolean matches(RecyclerView parent, View itemView, RecyclerView.ViewHolder viewHolder);
    }
}