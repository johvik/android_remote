package android.remote.mouse;

import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;

public class MouseController implements GestureDetector.OnGestureListener {
    private MouseModel mMouseModel;
    private MouseView mMouseView;
    private boolean mLeftButtonLock = false;
    private boolean mRightButtonLock = false;

    public MouseController(MouseModel mouseModel, MouseView mouseView) {
        mMouseModel = mouseModel;
        mMouseView = mouseView;
    }

    private void toggleButtonLock(MotionEvent e) {
        if (mMouseView.isLeftSide(e.getX())) {
            if (mLeftButtonLock) {
                mMouseModel.setLeftButtonDown(false);
            } else {
                mMouseModel.setLeftButtonDown(true);
            }
            mLeftButtonLock = !mLeftButtonLock; // Toggle
            Log.d("MouseController", "toggleLeftButtonLock " + mMouseModel.isLeftButtonDown());
        } else {
            if (mRightButtonLock) {
                mMouseModel.setRightButtonDown(false);
            } else {
                mMouseModel.setRightButtonDown(true);
            }
            mRightButtonLock = !mRightButtonLock; // Toggle
            Log.d("MouseController", "toggleRightButtonLock " + mMouseModel.isRightButtonDown());
        }
    }

    private void buttonClick(MotionEvent e) {
        if (mMouseView.isLeftSide(e.getX())) {
            if (!mLeftButtonLock) {
                mMouseModel.setLeftButtonDown(true);
                mMouseModel.setLeftButtonDown(false);
                Log.d("MouseController", "leftClick");
            }
        } else {
            if (!mRightButtonLock) {
                mMouseModel.setRightButtonDown(true);
                mMouseModel.setRightButtonDown(false);
                Log.d("MouseController", "rightClick");
            }
        }
    }

    /**
     * Notified when a tap occurs with the down {@link android.view.MotionEvent}
     * that triggered it. This will be triggered immediately for
     * every down event. All other events should be preceded by this.
     *
     * @param e The down motion event.
     */
    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    /**
     * The user has performed a down {@link android.view.MotionEvent} and not performed
     * a move or up yet. This event is commonly used to provide visual
     * feedback to the user to let them know that their action has been
     * recognized i.e. highlight an element.
     *
     * @param e The down motion event
     */
    @Override
    public void onShowPress(MotionEvent e) {
    }

    /**
     * Notified when a tap occurs with the up {@link android.view.MotionEvent}
     * that triggered it.
     *
     * @param e The up motion event that completed the first tap
     * @return true if the event is consumed, else false
     */
    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        buttonClick(e);
        return true;
    }

    /**
     * Notified when a scroll occurs with the initial on down {@link android.view.MotionEvent}
     * and the
     * current move {@link android.view.MotionEvent}. The distance in x and y is also supplied for
     * convenience.
     *
     * @param e1        The first down motion event that started the scrolling.
     * @param e2        The move motion event that triggered the current onScroll.
     * @param distanceX The distance along the X axis that has been scrolled since the last
     *                  call to onScroll. This is NOT the distance between {@code e1}
     *                  and {@code e2}.
     * @param distanceY The distance along the Y axis that has been scrolled since the last
     *                  call to onScroll. This is NOT the distance between {@code e1}
     *                  and {@code e2}.
     * @return true if the event is consumed, else false
     */
    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        Log.d("MouseController", "onScroll " + distanceX + " " + distanceY);
        // TODO
        return true;
    }

    /**
     * Notified when a long press occurs with the initial on down {@link android.view.MotionEvent}
     * that trigged it.
     *
     * @param e The initial on down motion event that started the longpress.
     */
    @Override
    public void onLongPress(MotionEvent e) {
        toggleButtonLock(e);
        mMouseView.performLongClick();
    }

    /**
     * Notified of a fling event when it occurs with the initial on down {@link android.view
     * .MotionEvent}
     * and the matching up {@link android.view.MotionEvent}. The calculated velocity is supplied
     * along
     * the x and y axis in pixels per second.
     *
     * @param e1        The first down motion event that started the fling.
     * @param e2        The move motion event that triggered the current onFling.
     * @param velocityX The velocity of this fling measured in pixels per second
     *                  along the x axis.
     * @param velocityY The velocity of this fling measured in pixels per second
     *                  along the y axis.
     * @return true if the event is consumed, else false
     */
    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }
}
