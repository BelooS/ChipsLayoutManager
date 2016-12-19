package com.beloo.chipslayoumanager.sample.ui;

import android.content.Context;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.widget.RecyclerView;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

/**
 * test for {@link TestActivity}
 */
@RunWith(AndroidJUnit4.class)
public class ChipsLayoutManagerTest {

    @Rule
    public ActivityTestRule<TestActivity> activityTestRule = new ActivityTestRule<>(TestActivity.class);

    @Mock
    LayoutManagerFactory lmFactory;

    private RecyclerView.LayoutManager chipsLayoutManager;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        TestActivity.setLmFactory(lmFactory);

        LayoutManagerFactory layoutManagerFactory = new LayoutManagerFactory();
        this.chipsLayoutManager = layoutManagerFactory.layoutManager(activityTestRule.getActivity());
        this.chipsLayoutManager = spy(chipsLayoutManager);

        when(lmFactory.layoutManager(activityTestRule.getActivity())).thenReturn(chipsLayoutManager);
    }

    @Test
    public void onLayoutCallLimited() throws Exception {
        //arrange
        //act
        //assert
        verify(chipsLayoutManager).onLayoutChildren(any(RecyclerView.Recycler.class), any(RecyclerView.State.class));
    }
}