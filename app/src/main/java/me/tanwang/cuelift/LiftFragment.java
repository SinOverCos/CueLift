package me.tanwang.cuelift;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class LiftFragment extends Fragment {

    private static final String TAG = "LiftFragment";

    private LiftFragmentCallbacks callbacks;

    private Lift lift;
    // TODO list the stuff in fragment_lift here

    public interface LiftFragmentCallbacks {
        void onLiftUpdated(Lift lift);
    }

    public static LiftFragment newInstance(Lift lift) {
        Bundle args = new Bundle();
        args.putSerializable(Lift.EXTRA_LIFT, lift);
        LiftFragment liftFragment = new LiftFragment();
        liftFragment.setArguments(args);
        return liftFragment;
    }

    // see http://stackoverflow.com/questions/32083053/android-fragment-onattach-deprecated
    @Override
    public void onAttach(Context hostActivity) {
        super.onAttach(hostActivity);
        callbacks = (LiftFragmentCallbacks) hostActivity;
        Log.i(TAG, "Callbacks added through onAttach(Context)");
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onAttach(Activity hostActivity) {
        super.onAttach(hostActivity);
        Log.i(TAG, "onAttach(Activity) called");
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            Log.i(TAG, "Callbacks added through onAttach(Activity)");
            callbacks = (LiftFragmentCallbacks) hostActivity;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

        lift = (Lift) getArguments().getSerializable(Lift.EXTRA_LIFT);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lift, parent, false);

        // TODO if today's lifts/last day's lifts don't exist, hide that section
        // TODO if no lifts exist at all, hide the PR section

        return view;
    }

}








































