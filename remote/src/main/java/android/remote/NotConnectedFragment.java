package android.remote;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class NotConnectedFragment extends Fragment {

    public NotConnectedFragment() {
        // Required empty public constructor
    }

    public static NotConnectedFragment newInstance() {
        return new NotConnectedFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_not_connected, container, false);
    }
}
