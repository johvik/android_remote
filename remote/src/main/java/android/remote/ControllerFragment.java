package android.remote;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.remote.connection.ConnectionThread;
import android.remote.mouse.MouseController;
import android.remote.mouse.MouseModel;
import android.remote.mouse.MouseView;
import android.text.Editable;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

public class ControllerFragment extends Fragment implements View.OnTouchListener,
        TextView.OnEditorActionListener {
    private MouseController mMouseController;
    private ControllerFragmentListener mControllerFragmentListener;
    private GestureDetector mGestureDetector = null;
    private MouseView mMouseView;
    private EditText mEditTextKeyboard;

    public ControllerFragment() {
        // Required empty public constructor
    }

    public static ControllerFragment newInstance() {
        return new ControllerFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_controller, container, false);

        if (view != null) {
            mEditTextKeyboard = (EditText) view.findViewById(R.id.editTextKeyboard);
            mEditTextKeyboard.setOnEditorActionListener(this);

            mMouseView = (MouseView) view.findViewById(R.id.mouseView);
            MouseModel mouseModel = mControllerFragmentListener.getMouseModel();
            mMouseView.setMouseModel(mouseModel);
            mMouseController = new MouseController(mouseModel, mMouseView);

            mGestureDetector = new GestureDetector(getActivity(), mMouseController);
            mMouseView.setOnTouchListener(this);
        }
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mMouseController.setConnectionThread(mControllerFragmentListener.getConnectionThread());
    }

    @Override
    public void onPause() {
        super.onPause();
        mMouseController.setConnectionThread(null);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mControllerFragmentListener = (ControllerFragmentListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity + " must implement ControllerFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mControllerFragmentListener = null;
    }

    /**
     * Called when a touch event is dispatched to a view. This allows listeners to
     * get a chance to respond before the target view.
     *
     * @param v     The view the touch event has been dispatched to.
     * @param event The MotionEvent object containing full information about
     *              the event.
     * @return True if the listener has consumed the event, false otherwise.
     */
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return mGestureDetector != null && mGestureDetector.onTouchEvent(event);
    }

    /**
     * Called when an action is being performed.
     *
     * @param v        The view that was clicked.
     * @param actionId Identifier of the action.  This will be either the
     *                 identifier you supplied, or {@link EditorInfo#IME_NULL
     *                 EditorInfo.IME_NULL} if being called due to the enter key
     *                 being pressed.
     * @param event    If triggered by an enter key, this is the event;
     *                 otherwise, this is null.
     * @return Return true if you have consumed the action, else false.
     */
    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            Editable editable = mEditTextKeyboard.getText();
            if (editable != null) {
                String keyboardInput = editable.toString();
                mMouseController.onKeyboardInput(keyboardInput);
                editable.clear();
            }
            // Hack to hide the keyboard
            mEditTextKeyboard.setEnabled(false);
            mEditTextKeyboard.setEnabled(true);
            mMouseView.requestFocus();
            return true;
        }
        return false;
    }

    public interface ControllerFragmentListener {
        /**
         * Get the mouse model. This has to remain the same within the lifetime of the activity.
         *
         * @return The mouse model.
         */
        public MouseModel getMouseModel();

        public ConnectionThread getConnectionThread();
    }
}
