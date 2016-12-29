package com.beloo.widget.chipslayoutmanager;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.beloo.chipslayoutmanager.sample.R;
import com.beloo.chipslayoutmanager.sample.entity.ChipsEntity;
import com.beloo.chipslayoutmanager.sample.ui.ChipsFacade;
import com.beloo.chipslayoutmanager.sample.ui.LayoutManagerFactory;
import com.beloo.chipslayoutmanager.sample.ui.TestActivity;
import com.beloo.chipslayoutmanager.sample.ui.adapter.ChipsAdapter;
import com.beloo.widget.chipslayoutmanager.util.InstrumentalUtil;
import com.beloo.test.util.RecyclerViewEspressoFactory;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;
import static com.beloo.test.util.RecyclerViewEspressoFactory.*;

/**
 */
@RunWith(AndroidJUnit4.class)
public class ColumnTest {

    static {
        TestActivity.isInitializeOutside = true;
    }

    @Rule
    public ActivityTestRule<TestActivity> activityTestRule = new ActivityTestRule<>(TestActivity.class);
    private TestActivity activity;

    private List<ChipsEntity> items;

    private ChipsLayoutManager layoutManager;
    private ViewInteraction recyclerView;

    @Before
    public void setUp() throws Throwable {
        MockitoAnnotations.initMocks(this);
        activity = activityTestRule.getActivity();
        recyclerView = onView(withId(R.id.rvTest)).check(matches(isDisplayed()));

        layoutManager = getLayoutManager();

        LayoutManagerFactory layoutManagerFactory = new LayoutManagerFactory() {
            @Override
            public RecyclerView.LayoutManager layoutManager(Context context) {
                //we need clean layout manager for each request
                return retrieveLayoutManager();
            }
        };

        //set items
        ChipsFacade chipsFacade = spy(new ChipsFacade());
        items = chipsFacade.getItems();
        when(chipsFacade.getItems()).thenReturn(items);
        TestActivity.setItemsFactory(chipsFacade);

        TestActivity.setLmFactory(layoutManagerFactory);

        activity.runOnUiThread(() -> activity.initialize());
    }

    private ChipsLayoutManager retrieveLayoutManager() {
        this.layoutManager = getLayoutManager();
        return layoutManager;
    }

    private ChipsLayoutManager getLayoutManager() {
        if (activityTestRule.getActivity() == null) return null;
        return ChipsLayoutManager.newBuilder(activityTestRule.getActivity())
                .setOrientation(ChipsLayoutManager.VERTICAL)
                .build();
    }

    @Test
    public void layouting_ScrollForwardAndBackward_VerifyCorrectOrder () throws Exception {
        //arrange
        InstrumentalUtil.waitForIdle();

        //act
        recyclerView.perform(scrollBy(1000, 0));
        recyclerView.perform(scrollBy(-1000, 0));

        //assert
        recyclerView.check(matches(incrementOrder()));
    }

    ///////////////////////////////////////////////////////////////////////////
    // scrolling
    ///////////////////////////////////////////////////////////////////////////

    @Test
    public void scrollBy_LMInInitialStateAndScrollForward_CorrectFirstCompletelyVisibleItem() throws Exception {
        //arrange
        InstrumentalUtil.waitForIdle();
        //act
        recyclerView.perform(scrollBy(300, 0));
        int actual = layoutManager.findFirstCompletelyVisibleItemPosition();
        //assert
        assertEquals(9, actual);
    }

    @Test
    public void scrollBy_ScrolledForwardScrollBackward_CorrectFirstCompletelyVisibleItem() throws Exception {
        //arrange
        InstrumentalUtil.waitForIdle();
        recyclerView.perform(scrollBy(1000, 0));

        //act
        recyclerView.perform(scrollBy(-1000, 0));
        int actual = layoutManager.findFirstCompletelyVisibleItemPosition();

        //assert
        assertEquals(0, actual);
    }

    @Test
    public void scrollBy_LastItemInLastRowHasSmallSize_scrolledCompletelyToBiggestItemSize() throws Exception {
        //arrange

        //act
        recyclerView.perform(RecyclerViewActions.scrollToPosition(36),
                scrollBy(0, -200),
                scrollBy(0, 200));


        //assert
        recyclerView.check(matches(atPosition(39, rvPaddingMatcher())));
    }

    private ViewHolderMatcher<RecyclerView.ViewHolder> rvPaddingMatcher() {
        return new RecyclerViewEspressoFactory.ViewHolderMatcher<RecyclerView.ViewHolder>() {

            @Override
            public boolean matches(RecyclerView parent, View itemView, RecyclerView.ViewHolder viewHolder) {
                int expectedPadding = parent.getPaddingRight();
                int right = layoutManager.getDecoratedRight(itemView);
                int parentRight = parent.getRight();
                int padding = parentRight - right;
                assertEquals("padding of RecyclerView item doesn't equal expected padding" ,expectedPadding, padding);
                return true;
            }
        };
    }

    @Test
    public void scrollToPosition_ScrollItemIsNotVisible_FirstVisiblePositionsEqualsScrollingTarget() throws Exception {
        //arrange
        recyclerView.perform(RecyclerViewActions.scrollToPosition(0));
        InstrumentalUtil.waitForIdle();
        //act
        recyclerView.perform(RecyclerViewActions.scrollToPosition(18));
        InstrumentalUtil.waitForIdle();

        //assert
        int actual = layoutManager.findFirstCompletelyVisibleItemPosition();
        assertEquals(18, actual);
    }

    @Test
    public synchronized void smoothScrollToPosition_ScrollItemIsNotVisible_FirstVisiblePositionsEqualsScrollingTarget() throws Exception {
        //arrange
        InstrumentalUtil.waitForIdle();

        //act
        ViewAction scrollAction = smoothScrollToPosition(18);
        //noinspection SynchronizationOnLocalVariableOrMethodParameter
        synchronized (scrollAction) {
            recyclerView.perform(scrollAction);
            //wait for completion of SmoothScrollAction
            scrollAction.wait();
        }

        //assert
        int actual = layoutManager.findFirstCompletelyVisibleItemPosition();
        assertEquals(18, actual);
    }

    @Test
    public synchronized void smoothScrollToPosition_ScrollItemIsVisible_ScrollItemDockedToStartBorder() throws Exception {
        //arrange
        InstrumentalUtil.waitForIdle();

        //act
        ViewAction scrollAction = smoothScrollToPosition(3);
        //noinspection SynchronizationOnLocalVariableOrMethodParameter
        synchronized (scrollAction) {
            recyclerView.perform(scrollAction);
            //wait for completion of SmoothScrollAction
            scrollAction.wait();
        }

        //assert
        int actual = layoutManager.findFirstCompletelyVisibleItemPosition();
        assertEquals(3, actual);
    }

    ///////////////////////////////////////////////////////////////////////////
    // find visible item
    ///////////////////////////////////////////////////////////////////////////

    @Test
    public void findFirstVisibleItem_scrolledCompletelyToItemInTheMiddle_resultCorrect() throws Exception {
        //arrange
        recyclerView.perform(RecyclerViewActions.scrollToPosition(19));
        InstrumentalUtil.waitForIdle();

        //act
        int actual = layoutManager.findFirstVisibleItemPosition();

        //assert
        assertEquals(18, actual);
    }

    @Test
    public void findLastVisibleItem_scrolledCompletelyToItemInTheMiddle_resultCorrect() throws Exception {
        //arrange
        recyclerView.perform(RecyclerViewActions.scrollToPosition(18));
        InstrumentalUtil.waitForIdle();

        //act
        int actual = layoutManager.findLastVisibleItemPosition();

        //assert
        assertEquals(35, actual);
    }

    @Test
    public void findLastCompletelyVisibleItem_scrolledCompletelyToItemInTheMiddle_resultCorrect() throws Exception {
        //arrange
        recyclerView.perform(RecyclerViewActions.scrollToPosition(18));
        InstrumentalUtil.waitForIdle();

        //act
        int actual = layoutManager.findLastCompletelyVisibleItemPosition();

        //assert
        assertEquals(26, actual);
    }

    private void rotateAndWaitIdle() throws Exception {
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
    public void rotate_LMBuiltFirstTime_NoExceptions() throws Exception {
        //arrange
        //act
        rotateAndWaitIdle();
        //assert
    }

    @Test
    public void rotate_LMHasItems_firstItemNotChanged() throws Exception {
        //arrange
        recyclerView.perform(RecyclerViewActions.scrollToPosition(18));
        InstrumentalUtil.waitForIdle();

        int expected = layoutManager.findFirstVisibleItemPosition();
        //act
        rotateAndWaitIdle();
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
        ChipsFacade chipsFacade = spy(new ChipsFacade());
        ChipsAdapter chipsAdapter = new ChipsAdapter(chipsFacade.getItems(), null);

        //act
        recyclerView.perform(setAdapter(chipsAdapter));
        recyclerView.perform(setAdapter(chipsAdapter));
        InstrumentalUtil.waitForIdle();

        //assert
        int pos = layoutManager.findFirstVisibleItemPosition();
        assertNotEquals(RecyclerView.NO_POSITION, pos);
    }

    @Test
    public void clipToPadding_IsTrue_paddingStaySame() throws Exception {
        //arrange
        RecyclerView rvTest = (RecyclerView) activityTestRule.getActivity().findViewById(R.id.rvTest);
        ViewAction viewAction = actionDelegate((uiController, view) -> {
            view.setClipToPadding(true);
            view.setPadding(150, 150, 150, 150);
            view.requestLayout();
        });

        //act
        recyclerView.perform(viewAction);
        recyclerView.perform(RecyclerViewActions.scrollToPosition(18));

        //assert
        View view = layoutManager.getChildAt(0);
        double padding = view.getX() - rvTest.getX();
        assertTrue(padding >= 150);
    }


    @Test
    public void clipToPadding_IsFalse_paddingOfScrolledViewIsLowerThanInitial() throws Exception {
        //arrange
        ViewAction viewAction = actionDelegate((uiController, view) -> {
            view.setClipToPadding(false);
            view.setPadding(150, 150, 150, 150);
            view.requestLayout();
        });

        //act
        recyclerView.perform(viewAction,
                RecyclerViewActions.scrollToPosition(18),
                scrollBy(200, 0));

        //assert
        View view = layoutManager.getChildAt(0);
        int padding = layoutManager.getDecoratedLeft(view);
        assertTrue(padding < 0);
    }

    private View getViewForPosition(RecyclerView recyclerView, int position) {
        return recyclerView.findViewHolderForAdapterPosition(position).itemView;
    }

    @Test
    public void layouting_ScrollForwardOffScreenAndBackward_ItemsStayOnASamePlace() throws Exception {
        InstrumentalUtil.waitForIdle();

        //arrange
        RecyclerView rvTest = (RecyclerView) activityTestRule.getActivity().findViewById(R.id.rvTest);
        View child = getViewForPosition(rvTest, 7);
        Rect expectedViewRect = layoutManager.getCanvas().getViewRect(child);

        //act
        recyclerView.perform(scrollBy(2000, 0), scrollBy(-2000, 0));
        Rect resultViewRect = layoutManager.getCanvas().getViewRect(child);

        //assert
        assertEquals(expectedViewRect, resultViewRect);
    }

    @Test
    public void layouting_ScrollForwardOnScreenAndBackward_ItemsStayOnASamePlace() throws Exception {
        InstrumentalUtil.waitForIdle();

        //arrange
        RecyclerView rvTest = (RecyclerView) activityTestRule.getActivity().findViewById(R.id.rvTest);
        View child = getViewForPosition(rvTest, 6);
        Rect expectedViewRect = layoutManager.getCanvas().getViewRect(child);

        //act
        recyclerView.perform(scrollBy(500, 0), scrollBy(-500, 0));
        Rect resultViewRect = layoutManager.getCanvas().getViewRect(child);

        //assert
        assertEquals(expectedViewRect, resultViewRect);
    }

    @Test
    public void gapsNormalization_OnLastRowDeleted_PaddingStaySame() throws Exception {
        //arrange
        {
            items.remove(39);
            items.remove(38);
            items.remove(37);
            activity.runOnUiThread(() -> activity.initialize());
            InstrumentalUtil.waitForIdle();
        }
        recyclerView.perform(RecyclerViewActions.scrollToPosition(36));
        //act
        recyclerView.perform(actionDelegate((uiController, recyclerView) -> items.remove(36)),
                notifyItemRemovedAction(36));
        //assert
        recyclerView.check(matches(atPosition(29, rvPaddingMatcher())));
    }
}