package com.beloo.widget.chipslayoutmanager;

import android.content.Context;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.widget.RecyclerView;

import com.beloo.chipslayoumanager.sample.ui.FewChipsFacade;
import com.beloo.chipslayoumanager.sample.ui.LayoutManagerFactory;
import com.beloo.chipslayoumanager.sample.ui.TestActivity;
import com.beloo.widget.chipslayoutmanager.util.testing.ISpy;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.atMost;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * test for {@link TestActivity}
 */
@RunWith(AndroidJUnit4.class)
public class ChipsLayoutManagerLoopingTest {

    static {
        TestActivity.isInitializeOutside = true;
        TestActivity.setItemsFactory(new FewChipsFacade());
    }

    @Rule
    public ActivityTestRule<TestActivity> activityTestRule = new ActivityTestRule<>(TestActivity.class);

    @Mock
    ISpy spy;

    @Mock
    LayoutManagerFactory layoutManagerFactory;

    @Before
    public void setUp() throws Throwable {
        MockitoAnnotations.initMocks(this);

        ChipsLayoutManager layoutManager = ChipsLayoutManager.newBuilder(activityTestRule.getActivity())
                .setOrientation(ChipsLayoutManager.HORIZONTAL)
                .build();

        layoutManager.setSpy(spy);

        doReturn(layoutManager).when(layoutManagerFactory).layoutManager(activityTestRule.getActivity());

        TestActivity.setLmFactory(layoutManagerFactory);

        activityTestRule.getActivity().initialize();
    }

    /** test, that {@link android.support.v7.widget.LinearLayoutManager#onLayoutChildren} isn't called infinitely */
    @Test
    public void onLayoutChildren_afterActivityStarted_onLayoutCallLimited() throws Exception {
        //arrange

        //act
        Thread.sleep(700);

        //assert
        verify(spy, atMost(6)).onLayoutChildren(any(RecyclerView.Recycler.class), any(RecyclerView.State.class));
    }
}