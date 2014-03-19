package android.remote.mouse;

import junit.framework.TestCase;

public class MouseModelTest extends TestCase {
    private MouseModel mMouseModel;

    protected void setUp() throws Exception {
        mMouseModel = new MouseModel();
    }

    public void testMouseModel() {
        assertEquals(false, mMouseModel.isLeftButtonDown());
        assertEquals(false, mMouseModel.isRightButtonDown());
    }
}
