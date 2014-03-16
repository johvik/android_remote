package android.remote.mousebutton;

import java.util.Observable;

public class MouseButtonModel extends Observable {
    private boolean mButtonDown = false;

    public void setButtonDown(boolean buttonDown) {
        if (mButtonDown != buttonDown) {
            mButtonDown = buttonDown;
            setChanged();
            notifyObservers();
        }
    }

    public boolean isButtonDown() {
        return mButtonDown;
    }
}
