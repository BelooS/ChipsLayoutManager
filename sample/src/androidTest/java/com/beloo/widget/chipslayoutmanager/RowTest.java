package com.beloo.widget.chipslayoutmanager;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.support.annotation.UiThread;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.beloo.chipslayoutmanager.sample.entity.ChipsEntity;
import com.beloo.chipslayoutmanager.sample.ui.LayoutManagerFactory;
import com.beloo.chipslayoutmanager.sample.ui.ChipsFacade;
import com.beloo.chipslayoutmanager.sample.ui.TestActivity;
import com.beloo.chipslayoutmanager.sample.ui.adapter.ChipsAdapter;
import com.beloo.widget.chipslayoutmanager.support.BiConsumer;
import com.beloo.widget.chipslayoutmanager.util.InstrumentalUtil;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;

import com.beloo.chipslayoutmanager.sample.R;
import com.beloo.test.util.RecyclerViewEspressoFactory;

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
public class RowTest {

    static {
        TestActivity.isInitializeOutside = true;
    }

    @Rule
    public ActivityTestRule<TestActivity> activityTestRule = new ActivityTestRule<TestActivity>(TestActivity.class) {
        @Override
        protected void afterActivityLaunched() {
            super.afterActivityLaunched();

        }
    };

    private ChipsLayoutManager layoutManager;
    private TestActivity activity;

    private List<ChipsEntity> items;

    private ViewInteraction recyclerView;

    @Before
    public final void setUp() throws Throwable {
        MockitoAnnotations.initMocks(this);
        activity = activityTestRule.getActivity();

        LayoutManagerFactory layoutManagerFactory = new LayoutManagerFactory() {
            @Override
            public RecyclerView.LayoutManager layoutManager(Context context) {
                return retrieveLayoutManager();
            }
        };

        RecyclerView rvTest = (RecyclerView) activityTestRule.getActivity().findViewById(R.id.rvTest);
        //disable all animations
        rvTest.setItemAnimator(null);

        //set items
        ChipsFacade chipsFacade = spy(new ChipsFacade());
        items = chipsFacade.getItems();
        when(chipsFacade.getItems()).thenReturn(items);
        TestActivity.setItemsFactory(chipsFacade);

        TestActivity.setLmFactory(layoutManagerFactory);

        recyclerView = onView(withId(R.id.rvTest)).check(matches(isDisplayed()));

        onSetUp();

        activity.runOnUiThread(() -> activity.initialize());
    }

    public void onSetUp() throws Exception {}

    private ChipsLayoutManager retrieveLayoutManager() {
        layoutManager = getLayoutManager();
        return layoutManager;
    }

    @UiThread
    protected ChipsLayoutManager getLayoutManager() {
        if (activityTestRule.getActivity() == null) return null;
        return ChipsLayoutManager.newBuilder(activityTestRule.getActivity())
                .setOrientation(ChipsLayoutManager.HORIZONTAL)
                .build();
    }

    ///////////////////////////////////////////////////////////////////////////
    // layouting, main feature
    ///////////////////////////////////////////////////////////////////////////

    @Test
    public void layouting_ScrollForwardOffScreenAndBackward_ItemsStayOnASamePlace() throws Exception {
        InstrumentalUtil.waitForIdle();

        //arrange
        RecyclerView rvTest = (RecyclerView) activityTestRule.getActivity().findViewById(R.id.rvTest);
        View child = getViewForPosition(rvTest, 7);
        Rect expectedViewRect = layoutManager.getCanvas().getViewRect(child);

        //act
        recyclerView.perform(scrollBy(0, 1000), scrollBy(0, -1000));
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
        recyclerView.perform(scrollBy(0, 250), scrollBy(0, -250));
        Rect resultViewRect = layoutManager.getCanvas().getViewRect(child);

        //assert
        assertEquals(expectedViewRect, resultViewRect);
    }

    @Test
    public void layouting_ScrollForwardAndBackward_VerifyCorrectOrder () throws Exception {
        //arrange
        //act
        recyclerView.perform(scrollBy(0, 300));
        recyclerView.perform(scrollBy(0, -300));
        InstrumentalUtil.waitForIdle();
        //assert
        recyclerView.check(matches(incrementOrder()));
    }

    ///////////////////////////////////////////////////////////////////////////
    // scroll
    ///////////////////////////////////////////////////////////////////////////

    @Test
    public void scrollBy_LMInInitialStateAndScrollForward_CorrectFirstCompletelyVisibleItem() throws Exception {
        //arrange
        InstrumentalUtil.waitForIdle();
        //act
        recyclerView.perform(scrollBy(0, 300));
        int actual = layoutManager.findFirstCompletelyVisibleItemPosition();
        //assert
        assertEquals(4, actual);
    }

    @Test
    public void scrollBy_ScrolledForwardScrollBackward_CorrectFirstCompletelyVisibleItem() throws Exception {
        //arrange
        InstrumentalUtil.waitForIdle();
        recyclerView.perform(scrollBy(0, 300));

        //act
        recyclerView.perform(scrollBy(0, -300));
        int actual = layoutManager.findFirstCompletelyVisibleItemPosition();

        //assert
        assertEquals(0, actual);
    }

    @Test
    public void scrollBy_LastItemInLastRowHasSmallSize_scrolledCompletelyToBiggestItemSize() throws Exception {
        //arrange
        {
            items.remove(39);
            items.remove(37);
        }
        activity.runOnUiThread(() -> activity.initialize());
        InstrumentalUtil.waitForIdle();

        //act
        recyclerView.perform(RecyclerViewActions.scrollToPosition(37),
                scrollBy(0, -200),
                scrollBy(0, 200));


        //assert
        recyclerView.check(matches(atPosition(36, rvPaddingMatcher())));
    }

    @Test
    public void scrollToPosition_LMInInitialState_FirstVisiblePositionsEqualsScrollingTarget() throws Exception {
        //arrange

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
        InstrumentalUtil.waitForIdle();

        //act
        ViewAction scrollAction = smoothScrollToPosition(8);
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

    ///////////////////////////////////////////////////////////////////////////
    // find visible item
    ///////////////////////////////////////////////////////////////////////////

    @Test
    public void findFirstVisibleItem_scrolledCompletelyToItemInTheMiddle_resultCorrect() throws Exception {
        //arrange
        recyclerView.perform(RecyclerViewActions.scrollToPosition(7),
                scrollBy(0, 50));

        //act
        int actual = layoutManager.findFirstVisibleItemPosition();

        //assert
        assertEquals(6, actual);
    }

    @Test
    public void findFirstCompletelyVisibleItem_scrolledCompletelyToItemInTheMiddle_resultCorrect() throws Exception {
        //arrange
        recyclerView.perform(RecyclerViewActions.scrollToPosition(7),
                scrollBy(0, 50));

        //act
        int actual = layoutManager.findFirstCompletelyVisibleItemPosition();

        //assert
        assertEquals(7, actual);
    }

    @Test
    public void findLastVisibleItem_scrolledCompletelyToItemInTheMiddle_resultCorrect() throws Exception {
        //arrange
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
        recyclerView.perform(RecyclerViewActions.scrollToPosition(7));
        InstrumentalUtil.waitForIdle();

        //act
        int actual = layoutManager.findLastCompletelyVisibleItemPosition();

        //assert
        assertEquals(17, actual);
    }

    ///////////////////////////////////////////////////////////////////////////
    // rotate
    ///////////////////////////////////////////////////////////////////////////

    void rotateAndWaitIdle() throws Exception {
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

    void resetToInitialAfterRotate() throws Exception {
        activityTestRule.launchActivity(new Intent(activity, TestActivity.class));
        InstrumentalUtil.waitForIdle();
    }

    @Test
    public void rotate_LMHasItems_firstItemNotChanged() throws Exception {
        //arrange
        recyclerView.perform(RecyclerViewActions.scrollToPosition(7));
        InstrumentalUtil.waitForIdle();

        int expected = layoutManager.findFirstVisibleItemPosition();
        //act
        rotateAndWaitIdle();
        int actual = layoutManager.findFirstVisibleItemPosition();
        resetToInitialAfterRotate();

        //assert
        assertNotEquals(-1, expected);
        assertNotEquals(-1, actual);
        assertEquals("first visible positions before and after rotation doesn't match", expected, actual);
        System.out.println("first visible position = " + actual);
    }

    @Test
    public void rotate_ScrolledToEndOfItems_BottomPaddingStaySame() throws Exception {
        InstrumentalUtil.waitForIdle();
        //arrange
        RecyclerView rvTest = (RecyclerView) activityTestRule.getActivity().findViewById(R.id.rvTest);
        recyclerView.perform(RecyclerViewActions.scrollToPosition(layoutManager.getItemCount() - 1));

        InstrumentalUtil.waitForIdle();
        Thread.sleep(200);

        View child = getViewForPosition(rvTest, layoutManager.findLastVisibleItemPosition());
        double bottom = (rvTest.getY() + rvTest.getHeight()) - (child.getY() + child.getHeight());

        Log.println(Log.ASSERT, "rotateTest", "expected child padding = " + bottom);

        //act
        rotateAndWaitIdle();
        recyclerView.perform(RecyclerViewActions.scrollToPosition(layoutManager.getItemCount() - 1));
        rotateAndWaitIdle();
        rvTest = (RecyclerView) activityTestRule.getActivity().findViewById(R.id.rvTest);
        child = getViewForPosition(rvTest, layoutManager.findLastVisibleItemPosition());
        double result = (rvTest.getY() + rvTest.getHeight()) - (child.getY() + child.getHeight());
        Log.println(Log.ASSERT, "rotateTest", "result child padding = " + result);
        //reset
        resetToInitialAfterRotate();

        //assert
        assertNotEquals(0.0d, bottom, 0.01);
        assertNotEquals(0.0d, result, 0.01);
        assertEquals(bottom, result, 0.01);
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

    ///////////////////////////////////////////////////////////////////////////
    // padding
    ///////////////////////////////////////////////////////////////////////////

    @Test
    public void clipToPadding_IsTrue_paddingStaySame() throws Exception {
        //arrange
        RecyclerView rvTest = (RecyclerView) activityTestRule.getActivity().findViewById(R.id.rvTest);
        ViewAction initAction = actionDelegate((uiController, view) ->  {
            view.setClipToPadding(true);
            view.setPadding(150, 150, 150, 150);
            view.requestLayout();
        });
        recyclerView.perform(initAction);

        //act
        recyclerView.perform(RecyclerViewActions.scrollToPosition(8));

        //assert
        View view = layoutManager.getChildAt(0);
        double padding = view.getY() - rvTest.getY();
        assertTrue(padding >= 150);
    }


    @Test
    public void clipToPadding_IsFalse_paddingOfScrolledViewIsLowerThanInitial() throws Exception {
        //arrange
        ViewAction arrangeAction = actionDelegate((uiController, view) -> {
            view.setClipToPadding(false);
            view.setPadding(150, 150, 150, 150);
            view.requestLayout();
        });
        recyclerView.perform(arrangeAction);

        //act
        recyclerView.perform(RecyclerViewActions.scrollToPosition(8),
                scrollBy(0, 200));

        //assert
        View view = layoutManager.getChildAt(0);
        int padding = layoutManager.getDecoratedTop(view);
        assertTrue(padding < 0);
    }

    private ViewHolderMatcher<RecyclerView.ViewHolder> rvPaddingMatcher() {
        return new ViewHolderMatcher<RecyclerView.ViewHolder>() {
            @Override
            public boolean matches(RecyclerView parent, View itemView, RecyclerView.ViewHolder viewHolder) {
                int expectedPadding = parent.getPaddingBottom();
                int bottom = layoutManager.getDecoratedBottom(itemView);
                int parentBottom = parent.getBottom();
                int padding = parentBottom - bottom;
                assertEquals("padding of RecyclerView item doesn't equal expected padding" ,expectedPadding, padding);
                return true;
            }
        };
    }

    @Test
    public void gapsNormalization_OnLastRowDeleted_PaddingStaySame() throws Exception {
        //arrange
        recyclerView.perform(RecyclerViewActions.scrollToPosition(39));
        //act
        recyclerView.perform(actionDelegate((uiController, recyclerView) -> items.remove(39)),
                notifyItemRemovedAction(39));
        //assert
        recyclerView.check(matches(atPosition(38, rvPaddingMatcher())));
    }

    private View getViewForPosition(RecyclerView recyclerView, int position) {
        return recyclerView.findViewHolderForAdapterPosition(position).itemView;
    }
}