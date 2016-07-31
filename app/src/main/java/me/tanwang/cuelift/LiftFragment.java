package me.tanwang.cuelift;

import android.app.Activity;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.content.SharedPreferencesCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

public class LiftFragment extends Fragment {

    private static final String TAG = "LiftFragment";

    private LiftFragmentCallbacks callbacks;
    private LiftManager liftManager;

    private ImageButton liftIconImageButton;
    private EditText liftNameEditText;
    private EditText addCueEditText;
    private FloatingActionButton addCueFab;

    private Lift lift;
    // TODO list the stuff in fragment_lift here

    public interface LiftFragmentCallbacks {
        void onLiftUpdated(Lift lift);
    }

    public static LiftFragment newInstance(Lift lift) {
        Log.i(TAG, "Creating new LiftFragment with lift: " + lift.toString());
        Bundle args = new Bundle();
        args.putSerializable(Lift.EXTRA_LIFT, lift);
        LiftFragment liftFragment = new LiftFragment();
        liftFragment.setArguments(args);
        return liftFragment;
    }

    /*
    // see http://stackoverflow.com/questions/32083053/android-fragment-onattach-deprecated
    @Override
    public void onAttach(Context hostActivity) {
        super.onAttach(hostActivity);
        callbacks = (LiftFragmentCallbacks) hostActivity;
        Log.i(TAG, "Callbacks added through onAttach(Context)");
    }
    */

    @Override
    @SuppressWarnings("deprecation")
    public void onAttach(Activity hostActivity) {
        super.onAttach(hostActivity);
        Log.i(TAG, "onAttach(Activity) called");
        Log.i(TAG, "Callbacks added through onAttach(Activity)");
        callbacks = (LiftFragmentCallbacks) hostActivity;
        liftManager = LiftManager.get(hostActivity.getApplicationContext());
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

        liftIconImageButton = (ImageButton) view.findViewById(R.id.lift_icon_image_button);
        liftNameEditText = (EditText) view.findViewById(R.id.lift_name_edit_text);
        addCueEditText = (EditText) view.findViewById(R.id.add_cue_edit_text);
        addCueFab = (FloatingActionButton) view.findViewById(R.id.add_cue_fab);

        liftIconImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "This fragment holds: " + lift.toString());
                Toast.makeText(getActivity(), "This will bring up a file browser to find a new icon", Toast.LENGTH_LONG).show();
            }
        });

        liftNameEditText.setText(lift.getDisplayName());
        liftNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                lift.setDisplayName(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        addCueFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cue = addCueEditText.getText().toString();

            }
        });


        // TODO if today's lifts/last day's lifts don't exist, hide that section
        // TODO if no lifts exist at all, hide the PR section

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_lift_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(getActivity());
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onPause() {
        Log.i(TAG, "LiftFragment onPause is calling updateLift");
        updateLift();
        super.onPause();
    }

    public void updateLift() {
        liftManager.updateLift(lift);
    }
}

