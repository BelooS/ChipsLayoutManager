package com.beloo.widget.chipslayoutmanager;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.beloo.chipslayoumanager.sample.ui.FewChipsFacade;
import com.beloo.chipslayoumanager.sample.ui.LayoutManagerFactory;
import com.beloo.chipslayoumanager.sample.ui.TestActivity;
import com.beloo.widget.chipslayoutmanager.util.InstrumentalUtil;
import com.beloo.widget.chipslayoutmanager.util.testing.ISpy;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.concurrent.CountDownLatch;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

/**
 * test for {@link TestActivity}
 */
@RunWith(AndroidJUnit4.class)
public class ChipsLayoutManagerTest {

    static {
        TestActivity.setItemsFactory(new FewChipsFacade());
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

        layoutManager = ChipsLayoutManager.newBuilder(activityTestRule.getActivity())
                .setOrientation(ChipsLayoutManager.HORIZONTAL)
                .build();

        doReturn(layoutManager).when(layoutManagerFactory).layoutManager(activityTestRule.getActivity());

        TestActivity.setLmFactory(layoutManagerFactory);

        activityTestRule.getActivity().initialize();
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

    /** verify that orientation change is performed successfully */
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
    public void deleteItem_ItemHasMaximumHeight_SamePadding() throws Exception {
        //arrange
        //act
        //assert
    }
}