package android.remote.mouse;

import android.util.Log;
import android.view.View;

public class MouseController implements View.OnClickListener, View.OnLongClickListener {
    private MouseModel mMouseModel;
    private boolean mButtonLock = false;

    public MouseController(MouseModel mouseModel) {
        mMouseModel = mouseModel;
    }

    private void toggleLeftButtonLock() {
        if (mButtonLock) {
            mMouseModel.setLeftButtonDown(false);
        } else {
            mMouseModel.setLeftButtonDown(true);
        }
        mButtonLock = !mButtonLock; // Toggle
    }

    @Override
    public void onClick(View view) {
        if (!mButtonLock) {
            mMouseModel.setLeftButtonDown(true);
            mMouseModel.setLeftButtonDown(false);
            Log.d("MouseController", "Click");
        }
    }

    @Override
    public boolean onLongClick(View view) {
        toggleLeftButtonLock();
        Log.d("MouseController", "LongClick " + mMouseModel.isLeftButtonDown());
        return true;
    }
}
