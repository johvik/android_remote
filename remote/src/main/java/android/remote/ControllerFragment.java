package android.remote;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.remote.connection.ConnectionThread;
import android.remote.mouse.MouseController;
import android.remote.mouse.MouseModel;
import android.remote.mouse.MouseView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

public class ControllerFragment extends Fragment implements View.OnTouchListener {
    private MouseController mMouseController;
    private ControllerFragmentListener mControllerFragmentListener;
    private GestureDetector mGestureDetector = null;

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
            MouseView mouseView = (MouseView) view.findViewById(R.id.mouseView);
            MouseModel mouseModel = mControllerFragmentListener.getMouseModel();
            mouseView.setMouseModel(mouseModel);
            mMouseController = new MouseController(mouseModel, mouseView);

            mGestureDetector = new GestureDetector(getActivity(), mMouseController);
            mControllerFragmentListener.setGestureDetector(mGestureDetector);
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
        mControllerFragmentListener.setGestureDetector(null);
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

    public interface ControllerFragmentListener {
        /**
         * Get the mouse model. This has to remain the same within the lifetime of the activity.
         *
         * @return The mouse model.
         */
        public MouseModel getMouseModel();

        public ConnectionThread getConnectionThread();

        public void setGestureDetector(GestureDetector gestureDetector);
    }
}
