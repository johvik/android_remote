package android.remote.mousebutton;

import android.util.Log;
import android.view.View;

public class MouseButtonController implements View.OnClickListener, View.OnLongClickListener {
    private MouseButtonModel mMouseButtonModel;
    private boolean mButtonLock = false;

    public MouseButtonController(MouseButtonModel mouseButtonModel) {
        mMouseButtonModel = mouseButtonModel;
    }

    @Override
    public void onClick(View view) {
        if (!mButtonLock) {
            mMouseButtonModel.setButtonDown(true);
            mMouseButtonModel.setButtonDown(false);
            Log.d("MouseButtonController", "Click");
        }
    }

    @Override
    public boolean onLongClick(View view) {
        if (mButtonLock) {
            mMouseButtonModel.setButtonDown(false);
        } else {
            mMouseButtonModel.setButtonDown(true);
        }
        Log.d("MouseButtonController", "LongClick " + mMouseButtonModel.isButtonDown());
        mButtonLock = !mButtonLock; // Toggle
        return true;
    }
}
