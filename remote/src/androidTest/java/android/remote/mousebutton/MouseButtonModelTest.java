package android.remote.mousebutton;

import junit.framework.TestCase;

public class MouseButtonModelTest extends TestCase {
    public void testMouseButtonModel() {
        assertEquals(false, new MouseButtonModel().isButtonDown());
    }
}
