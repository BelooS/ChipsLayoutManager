package com.beloo.widget.spanlayoutmanager.gravity;

import android.test.suitebuilder.annotation.SmallTest;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;

public class Testable {

    public void doSmth1(A param1, B param2, C param3){}

    public void doSmth2(D param1, E param2){}

    public void doSmth3(E param1){}

}

@RunWith(Parameterized.class)
@SmallTest
class TestableTest {

    @Parameterized.Parameters
    public static Iterable<Object[]> data() {
        return Arrays.asList(new Object[][]{
            {new A(1), new B(1), new C(1)},
            {new A(2), new B(2), new C(2)},
        });
    }

    private A a;
    private B b;
    private C c;

    public TestableTest(A a, B b, C c) {
        this.a = a;
        this.b = b;
        this.c = c;
    }

    @Test
    public void testSmth() {
        //this test will run for each set of @Parameters and i'm able to test doSmth1 method
    }

    @Test
    public void testSmth2( ) {
        Arrays.asList(Arrays.asList(new D(1), new E(1)), Arrays.asList(new D(2), new E(2)));

    }
}

