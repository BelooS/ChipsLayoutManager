package com.beloo.widget.spanlayoutmanager.gravity;

import android.graphics.Rect;

import com.beloo.widget.spanlayoutmanager.ParamsType;

import org.junit.runners.Parameterized;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.beloo.widget.spanlayoutmanager.ParamsType.*;

public class TopGravityModifierTest extends GravityModifierTest {

    @Parameterized.Parameters
    public static Iterable<Object[]> data() {
        List<Object[]> data = new ArrayList<>();
        data.addAll(Arrays.asList(new Object[][] {
                {VALID, 0, 100, new Rect(0, 20, 0, 100), new Rect(0, 0, 0, 80)},
                {VALID, 0, 100, new Rect(0, 20, 0, 80), new Rect(0, 0, 0, 60)},
                {VALID, 0, 100, new Rect(0, 0, 0, 40), new Rect(0, 0, 0, 40)},
                {VALID, 0, 100, new Rect(0, 10, 0, 20), new Rect(0, 0, 0, 10)},
                {VALID, 0, 100, new Rect(0, 0, 0, 100), new Rect(0, 0, 0, 100)}
        }));
        data.addAll(GravityDataProvider.getInvalidGravityModifierParams());
        return data;
    }

    public TopGravityModifierTest(ParamsType paramsType, int minTop, int maxBottom, Rect testRect, Rect expectedResultRect) {
        super(paramsType, minTop, maxBottom, testRect, expectedResultRect);
    }

    @Override
    protected IGravityModifier getGravityModifier() {
        return new TopGravityModifier();
    }

}
