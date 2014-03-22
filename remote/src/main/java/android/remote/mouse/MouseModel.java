package android.remote.mouse;

import java.util.Observable;

public class MouseModel extends Observable {
    private boolean mLeftButtonDown = false;
    private boolean mRightButtonDown = false;

    public boolean isLeftButtonDown() {
        return mLeftButtonDown;
    }

    public void setLeftButtonDown(boolean leftButtonDown) {
        if (mLeftButtonDown != leftButtonDown) {
            mLeftButtonDown = leftButtonDown;
            setChanged();
            notifyObservers();
        }
    }

    public boolean isRightButtonDown() {
        return mRightButtonDown;
    }

    public void setRightButtonDown(boolean rightButtonDown) {
        if (mRightButtonDown != rightButtonDown) {
            mRightButtonDown = rightButtonDown;
            setChanged();
            notifyObservers();
        }
    }

    public void reset() {
        setLeftButtonDown(false);
        setRightButtonDown(false);
    }
}
