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
import static org.mockito.Mockito.verify;

/**
 * test for {@link TestActivity}
 */
@RunWith(AndroidJUnit4.class)
public class ChipsLayoutManagerLoopingTest {

    @Rule
    public ActivityTestRule<TestActivity> activityTestRule = new ActivityTestRule<TestActivity>(TestActivity.class) {
        @Override
        protected void beforeActivityLaunched() {
            super.beforeActivityLaunched();
            TestActivity.isInitializeOutside = true;
        }
    };

    @Mock
    ISpy spy;

    private ChipsLayoutManager chipsLayoutManager;

    @Before
    public void setUp() throws Throwable {
        MockitoAnnotations.initMocks(this);

        LayoutManagerFactory layoutManagerFactory = new LayoutManagerFactory() {
            @Override
            public RecyclerView.LayoutManager layoutManager(Context context) {
                chipsLayoutManager = (ChipsLayoutManager) super.layoutManager(context);
                chipsLayoutManager.setSpy(spy);
                return chipsLayoutManager;
            }
        };

        TestActivity.setLmFactory(layoutManagerFactory);
        TestActivity.setItemsFactory(new FewChipsFacade());

        activityTestRule.getActivity().initialize();
    }

    @Test
    public void onLayoutCallLimited() throws Exception {
        //arrange

        //act
        Thread.sleep(500);

        //assert
        verify(spy, atMost(3)).onLayoutChildren(any(RecyclerView.Recycler.class), any(RecyclerView.State.class));
    }
}