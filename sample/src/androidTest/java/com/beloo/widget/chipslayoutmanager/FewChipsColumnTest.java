package com.beloo.widget.chipslayoutmanager;

import android.support.test.espresso.ViewAction;
import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.beloo.chipslayoutmanager.sample.R;
import com.beloo.chipslayoutmanager.sample.entity.ChipsEntity;
import com.beloo.chipslayoutmanager.sample.ui.ChipsFacade;
import com.beloo.chipslayoutmanager.sample.ui.LayoutManagerFactory;
import com.beloo.chipslayoutmanager.sample.ui.TestActivity;
import com.beloo.widget.chipslayoutmanager.util.InstrumentalUtil;
import com.beloo.widget.chipslayoutmanager.util.RecyclerViewActionFactory;
import com.beloo.widget.chipslayoutmanager.util.testing.ISpy;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Locale;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assume.assumeTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.atMost;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * test for {@link TestActivity}
 */
@RunWith(AndroidJUnit4.class)
public class FewChipsColumnTest {

    private static RecyclerViewActionFactory actionFactory;

    static {
        TestActivity.isInitializeOutside = true;
        FewChipsColumnTest.actionFactory = new RecyclerViewActionFactory();
    }

    @Rule
    public ActivityTestRule<TestActivity> activityTestRule = new ActivityTestRule<>(TestActivity.class);

    @Mock
    ISpy spy;

    @Mock
    LayoutManagerFactory layoutManagerFactory;

    private ChipsLayoutManager layoutManager;

    private List<ChipsEntity> items;

    private TestActivity activity;

    @Before
    public void setUp() throws Throwable {
        MockitoAnnotations.initMocks(this);
        activity = activityTestRule.getActivity();

        layoutManager = getLayoutManager();

        doReturn(layoutManager).when(layoutManagerFactory).layoutManager(any());

        RecyclerView rvTest = (RecyclerView) activityTestRule.getActivity().findViewById(R.id.rvTest);
        //disable all animations
        rvTest.setItemAnimator(null);

        //set items
        ChipsFacade chipsFacade = spy(new ChipsFacade());
        items = chipsFacade.getItems();
        when(chipsFacade.getItems()).thenReturn(items);
        TestActivity.setItemsFactory(chipsFacade);

        TestActivity.setLmFactory(layoutManagerFactory);
    }

    protected ChipsLayoutManager getLayoutManager() {
        return ChipsLayoutManager.newBuilder(activityTestRule.getActivity())
                .setOrientation(ChipsLayoutManager.VERTICAL)
                .build();
    }

    /**
     * test, that {@link android.support.v7.widget.LinearLayoutManager#onLayoutChildren} isn't called infinitely
     */
    @Test
    public void onLayoutChildren_afterActivityStarted_onLayoutCallLimited() throws Exception {
        //arrange
        activity.runOnUiThread(() -> activity.initialize());

        //act
        //we can't wait for idle, coz in case of error it won't be achieved. So just approximate time here.
        Thread.sleep(700);

        //assert
        verify(spy, atMost(6)).onLayoutChildren(any(RecyclerView.Recycler.class), any(RecyclerView.State.class));
    }

    @Test
    public void wrapContent_HeightIsWrapContent_DeletedLastItemInLastRowCauseHeightToDecrease() throws Exception {
        //arrange
        activity.runOnUiThread(() -> activity.initialize());
        final RecyclerView[] rvTest = new RecyclerView[1];

        ViewInteraction recyclerView = onView(withId(R.id.rvTest)).check(matches(isDisplayed()));
        ViewAction viewAction = actionFactory.actionDelegate(((uiController, view) -> {
            rvTest[0] = view;
            view.getLayoutParams().width = ViewGroup.LayoutParams.WRAP_CONTENT;
            view.requestLayout();
        }));

        recyclerView.perform(viewAction);

        int startWidth = rvTest[0].getHeight();

        //act
        recyclerView.perform(
                actionFactory.actionDelegate(((uiController, view) -> items.remove(9))),
                actionFactory.notifyItemRemovedAction(9));
        InstrumentalUtil.waitForIdle();

        //assert
        int endWidth = rvTest[0].getWidth();
        System.out.println(String.format(Locale.getDefault(), "start height = %d, end height = %d", startWidth, endWidth));
        assertTrue(endWidth < startWidth);
    }

    @Test
    public void deleteItemInTheFirstLine_ItemHasMaximumWidth_SameStartPadding() throws Exception {
        //arrange
        {
            //just adapt input items list to required start values
            items.remove(1);
            items.remove(9);
            ChipsEntity longItem = items.remove(8);
            items.add(1, longItem);
        }

        activity.runOnUiThread(() -> activity.initialize());
        ViewInteraction recyclerView = onView(withId(R.id.rvTest)).check(matches(isDisplayed()));
        //just adapt input items list to required start values

        InstrumentalUtil.waitForIdle();

        View second = layoutManager.getChildAt(1);
        double expectedX = second.getX();

        //act
        recyclerView.perform(
                actionFactory.actionDelegate(((uiController, view) -> items.remove(1))),
                actionFactory.notifyItemRemovedAction(1));

        InstrumentalUtil.waitForIdle();

        second = layoutManager.getChildAt(5);
        double resultX = second.getX();

        //assert
        assertNotEquals(0, expectedX, 0.01);
        assertNotEquals(0, resultX, 0.01);
        assertEquals(resultX, expectedX, 0.01);
    }
}