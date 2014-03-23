package android.remote.mouse;

import android.remote.connection.ConnectionThread;
import android.view.GestureDetector;
import android.view.MotionEvent;

import remote.api.commands.MouseMove;
import remote.api.commands.MousePress;
import remote.api.commands.MouseRelease;
import remote.api.commands.TextInput;

public class MouseController implements GestureDetector.OnGestureListener {
    private static final int LEFT_BUTTON_MASK = 1024; // BUTTON1_DOWN_MASK
    private static final int RIGHT_BUTTON_MASK = 4096; // BUTTON3_DOWN_MASK

    private MouseModel mMouseModel;
    private MouseView mMouseView;
    private boolean mLeftButtonLock = false;
    private boolean mRightButtonLock = false;
    private ConnectionThread mConnectionThread = null;

    public MouseController(MouseModel mouseModel, MouseView mouseView) {
        mMouseModel = mouseModel;
        mMouseView = mouseView;
    }

    private void leftButtonUp() {
        mMouseModel.setLeftButtonDown(false);
        if (mConnectionThread != null) {
            mConnectionThread.commandRequest(new MouseRelease(LEFT_BUTTON_MASK));
        }
    }

    private void leftButtonDown() {
        mMouseModel.setLeftButtonDown(true);
        if (mConnectionThread != null) {
            mConnectionThread.commandRequest(new MousePress(LEFT_BUTTON_MASK));
        }
    }

    private void rightButtonUp() {
        mMouseModel.setRightButtonDown(false);
        if (mConnectionThread != null) {
            mConnectionThread.commandRequest(new MouseRelease(RIGHT_BUTTON_MASK));
        }
    }

    private void rightButtonDown() {
        mMouseModel.setRightButtonDown(true);
        if (mConnectionThread != null) {
            mConnectionThread.commandRequest(new MousePress(RIGHT_BUTTON_MASK));
        }
    }

    private void toggleButtonLock(MotionEvent e) {
        if (mMouseView.isLeftSide(e.getX())) {
            if (mLeftButtonLock) {
                leftButtonUp();
            } else {
                leftButtonDown();
            }
            mLeftButtonLock = !mLeftButtonLock; // Toggle
        } else {
            // Right side
            if (mRightButtonLock) {
                rightButtonUp();
            } else {
                rightButtonDown();
            }
            mRightButtonLock = !mRightButtonLock; // Toggle
        }
    }

    private void buttonClick(MotionEvent e) {
        if (mMouseView.isLeftSide(e.getX())) {
            if (!mLeftButtonLock) {
                leftButtonDown();
                leftButtonUp();
            }
        } else {
            // Right side
            if (!mRightButtonLock) {
                rightButtonDown();
                rightButtonUp();
            }
        }
    }

    private void mouseMove(float distanceX, float distanceY) {
        if (mConnectionThread != null) {
            // Coordinate system is reversed...
            short x = (short) -distanceX;
            short y = (short) -distanceY;
            if (x != 0 || y != 0) { // No point in sending just zeros
                mConnectionThread.commandRequest(new MouseMove(x, y));
            }
        }
    }

    public void setConnectionThread(ConnectionThread connectionThread) {
        mConnectionThread = connectionThread;
    }

    public void onKeyboardInput(String input) {
        if (mConnectionThread != null) {
            mConnectionThread.commandRequest(new TextInput(input));
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
        return true;
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
        mouseMove(distanceX, distanceY);
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
        return true;
    }
}
