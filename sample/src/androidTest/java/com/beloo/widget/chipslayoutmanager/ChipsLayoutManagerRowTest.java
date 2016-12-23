package com.beloo.widget.chipslayoutmanager;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.beloo.chipslayoutmanager.sample.ui.LayoutManagerFactory;
import com.beloo.chipslayoutmanager.sample.ui.ChipsFacade;
import com.beloo.chipslayoutmanager.sample.ui.TestActivity;
import com.beloo.chipslayoutmanager.sample.ui.adapter.ChipsAdapter;
import com.beloo.widget.chipslayoutmanager.util.Action;
import com.beloo.widget.chipslayoutmanager.util.InstrumentalUtil;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.beloo.chipslayoutmanager.sample.R;
import com.beloo.widget.chipslayoutmanager.util.RecyclerViewActionFactory;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

/**
 */
public abstract class ChipsLayoutManagerRowTest {

    private static RecyclerViewActionFactory actionsFactory;

    static {
        actionsFactory = new RecyclerViewActionFactory();
        TestActivity.setItemsFactory(new ChipsFacade());
        TestActivity.isInitializeOutside = true;
    }

    @Rule
    public ActivityTestRule<TestActivity> activityTestRule = new ActivityTestRule<>(TestActivity.class);

    @Mock
    LayoutManagerFactory layoutManagerFactory;

    private ChipsLayoutManager layoutManager;

    @Before
    public void setUp() throws Throwable {
        MockitoAnnotations.initMocks(this);

        layoutManager = getLayoutManager();

        doReturn(layoutManager).when(layoutManagerFactory).layoutManager(activityTestRule.getActivity());

        TestActivity.setLmFactory(layoutManagerFactory);

        activityTestRule.getActivity().initialize();

    }

    protected abstract ChipsLayoutManager getLayoutManager();

    @Test
    public void layouting_ScrollForwardAndBackward_VerifyCorrectOrder () throws Exception {
        //arrange
        ViewInteraction recyclerView = onView(withId(R.id.rvTest)).check(matches(isDisplayed()));
        InstrumentalUtil.waitForIdle();
        //act
        recyclerView.perform(actionsFactory.scrollBy(0, 300));
        recyclerView.perform(actionsFactory.scrollBy(0, -300));
        InstrumentalUtil.waitForIdle();
        //assert
        recyclerView.check(matches(actionsFactory.correctOrder()));
    }

    @Test
    public void scrollBy_LMInInitialStateAndScrollForward_CorrectFirstCompletelyVisibleItem() throws Exception {
        //arrange
        ViewInteraction recyclerView = onView(withId(R.id.rvTest)).check(matches(isDisplayed()));
        InstrumentalUtil.waitForIdle();
        //act
        recyclerView.perform(actionsFactory.scrollBy(0, 300));
        int actual = layoutManager.findFirstCompletelyVisibleItemPosition();
        //assert
        assertEquals(4, actual);
    }

    @Test
    public void scrollBy_ScrolledForwardScrollBackward_CorrectFirstCompletelyVisibleItem() throws Exception {
        //arrange
        ViewInteraction recyclerView = onView(withId(R.id.rvTest)).check(matches(isDisplayed()));

        InstrumentalUtil.waitForIdle();
        recyclerView.perform(actionsFactory.scrollBy(0, 300));
        //act
        recyclerView.perform(actionsFactory.scrollBy(0, -300));
        int actual = layoutManager.findFirstCompletelyVisibleItemPosition();
        //assert
        assertEquals(0, actual);
    }

    @Test
    public void scrollToPosition_LMInInitialState_FirstVisiblePositionsEqualsScrollingTarget() throws Exception {
        //arrange
        ViewInteraction recyclerView = onView(withId(R.id.rvTest)).check(matches(isDisplayed()));

        //act
        recyclerView.perform(RecyclerViewActions.scrollToPosition(8));
        InstrumentalUtil.waitForIdle();

        //assert
        int actual = layoutManager.findFirstCompletelyVisibleItemPosition();
        assertEquals(8, actual);
    }

    @Test
    public synchronized void smoothScrollToPosition_LMInInitialState_FirstVisiblePositionsEqualsScrollingTarget() throws Exception {
        //arrange
        ViewInteraction recyclerView = onView(withId(R.id.rvTest)).check(matches(isDisplayed()));
        InstrumentalUtil.waitForIdle();

        //act
        ViewAction scrollAction = actionsFactory.smoothScrollToPosition(8);
        //noinspection SynchronizationOnLocalVariableOrMethodParameter
        synchronized (scrollAction) {
            recyclerView.perform(scrollAction);
            //wait for completion of SmoothScrollAction
            scrollAction.wait();
        }

        //assert
        int actual = layoutManager.findFirstCompletelyVisibleItemPosition();
        assertEquals(8, actual);
    }

    @Test
    public void findFirstVisibleItem_scrolledCompletelyToItemInTheMiddle_resultCorrect() throws Exception {
        //arrange
        ViewInteraction recyclerView = onView(withId(R.id.rvTest)).check(matches(isDisplayed()));
        recyclerView.perform(RecyclerViewActions.scrollToPosition(7));
        InstrumentalUtil.waitForIdle();

        //act
        int actual = layoutManager.findFirstVisibleItemPosition();

        //assert
        assertEquals(6, actual);
    }

    @Test
    public void findLastVisibleItem_scrolledCompletelyToItemInTheMiddle_resultCorrect() throws Exception {
        //arrange
        ViewInteraction recyclerView = onView(withId(R.id.rvTest)).check(matches(isDisplayed()));
        recyclerView.perform(RecyclerViewActions.scrollToPosition(7));
        InstrumentalUtil.waitForIdle();

        //act
        int actual = layoutManager.findLastVisibleItemPosition();

        //assert
        assertEquals(19, actual);
    }

    @Test
    public void findLastCompletelyVisibleItem_scrolledCompletelyToItemInTheMiddle_resultCorrect() throws Exception {
        //arrange
        ViewInteraction recyclerView = onView(withId(R.id.rvTest)).check(matches(isDisplayed()));
        recyclerView.perform(RecyclerViewActions.scrollToPosition(7));
        InstrumentalUtil.waitForIdle();

        //act
        int actual = layoutManager.findLastCompletelyVisibleItemPosition();

        //assert
        assertEquals(17, actual);
    }

    private void performOrientationChangeAndWaitIdle() throws Exception {
        //arrange

        final int orientation = InstrumentationRegistry.getTargetContext()
                .getResources()
                .getConfiguration()
                .orientation;

        //act
        activityTestRule.getActivity().setRequestedOrientation(
                orientation == Configuration.ORIENTATION_PORTRAIT ?
                        ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE :
                        ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        InstrumentalUtil.waitForIdle();

        //verify no exceptions
    }


    /**
     * verify that orientation change is performed successfully
     */
    @Test
    public void changeOrientation_LMBuiltFirstTime_NoExceptions() throws Exception {
        //arrange
        //act
        performOrientationChangeAndWaitIdle();
        //assert
    }

    @Test
    public void changeOrientation_LMHasItems_firstItemNotChanged() throws Exception {
        //arrange
        ViewInteraction recyclerView = onView(withId(R.id.rvTest)).check(matches(isDisplayed()));
        recyclerView.perform(RecyclerViewActions.scrollToPosition(7));
        InstrumentalUtil.waitForIdle();

        int expected = layoutManager.findFirstVisibleItemPosition();
        //act
        performOrientationChangeAndWaitIdle();
        int actual = layoutManager.findFirstVisibleItemPosition();
        //assert
        assertNotEquals(-1, expected);
        assertNotEquals(-1, actual);
        assertEquals("first visible positions before and after rotation doesn't match", expected, actual);
        System.out.println("first visible position = " + actual);
    }

    @Test
    public void setAdapterTwice_ChipsLayoutManagerHaveSetToRecyclerView_NoException() throws Exception {
        //arrange
        ViewInteraction recyclerView = onView(withId(R.id.rvTest)).check(matches(isDisplayed()));

        ChipsFacade chipsFacade = spy(new ChipsFacade());
        ChipsAdapter chipsAdapter = new ChipsAdapter(chipsFacade.getItems(), null);

        //act
        recyclerView.perform(actionsFactory.setAdapter(chipsAdapter));
        InstrumentalUtil.waitForIdle();
        recyclerView.perform(actionsFactory.setAdapter(chipsAdapter));
        InstrumentalUtil.waitForIdle();

        //assert
        int pos = layoutManager.findFirstVisibleItemPosition();
        assertNotEquals(RecyclerView.NO_POSITION, pos);
    }

    @Test
    public void clipToPadding_IsTrue_paddingStaySame() throws Exception {
        //arrange
        RecyclerView rvTest = (RecyclerView) activityTestRule.getActivity().findViewById(R.id.rvTest);
        ViewInteraction recyclerView = onView(withId(R.id.rvTest)).check(matches(isDisplayed()));

        ViewAction viewAction = new Action<RecyclerView>() {
            @Override
            public void performAction(UiController uiController, RecyclerView view) {
                view.setClipToPadding(true);
                view.setPadding(150, 150, 150, 150);
                view.requestLayout();
            }
        };

        //act
        recyclerView.perform(viewAction);
        InstrumentalUtil.waitForIdle();
        recyclerView.perform(actionsFactory.scrollBy(0, 200));
        InstrumentalUtil.waitForIdle();

        //assert
        View view = layoutManager.getChildAt(0);
        double padding = view.getX() - rvTest.getX();
        assertTrue(padding >= 150);
    }


    @Test
    public void clipToPadding_IsFalse_paddingOfScrolledViewIsLowerThanInitial() throws Exception {
        //arrange
        ViewInteraction recyclerView = onView(withId(R.id.rvTest)).check(matches(isDisplayed()));

        ViewAction viewAction = new Action<RecyclerView>() {
            @Override
            public void performAction(UiController uiController, RecyclerView view) {
                view.setClipToPadding(false);
                view.setPadding(150, 150, 150, 150);
                view.requestLayout();
            }
        };

        //act
        recyclerView.perform(viewAction);
        recyclerView.perform(actionsFactory.scrollBy(0, 200));
        InstrumentalUtil.waitForIdle();

        //assert
        View view = layoutManager.getChildAt(0);
        int padding = layoutManager.getDecoratedTop(view);
        assertTrue(padding < 0);
    }

    @Ignore
    @Test
    public void deleteItem_ItemHasMaximumHeight_SamePadding() throws Exception {
        //arrange
        //act
        //assert
    }
}