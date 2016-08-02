package me.tanwang.cuelift;

import android.app.Activity;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.support.design.widget.FloatingActionButton;
import android.os.Bundle;
import android.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.IllegalFormatException;
import java.util.Locale;

public class LiftFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = "LiftFragment";
    private static final String CUE_LIFT_ID = "me.tanwang.cuelift.cue_lift_id";
    private static final int ID_LOAD_CUES = 1;
    private static final int REQUEST_DATE = 10;

    private LiftFragmentCallbacks callbacks;
    private LiftManager liftManager;
    private LiftDatabaseHelper.CueCursor cueCursor;

    private LinearLayout cueLinearLayout;

    private TextView datePickerTextView;
    private TextView repPickerTextView;
    private TextView weightPickerTextView;

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

    public String formatDate(Date date) {
        String formatted = new SimpleDateFormat("dd MMMM yyyy", Locale.CANADA).format(date);
        Log.i(TAG, "Formatted date: " + formatted);
        return formatted;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lift, parent, false);

        liftIconImageButton = (ImageButton) view.findViewById(R.id.lift_icon_image_button);
        liftNameEditText = (EditText) view.findViewById(R.id.lift_name_edit_text);

        cueLinearLayout = (LinearLayout) view.findViewById(R.id.cue_list);
        addCueEditText = (EditText) view.findViewById(R.id.add_cue_edit_text);
        addCueFab = (FloatingActionButton) view.findViewById(R.id.add_cue_fab);

        datePickerTextView = (TextView) view.findViewById(R.id.date_picker);
        repPickerTextView = (TextView) view.findViewById(R.id.rep_picker);
        weightPickerTextView = (TextView) view.findViewById(R.id.weight_picker);

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
                Cue cue = new Cue(addCueEditText.getText().toString(), lift.getId());
                if (cue.getCue().equals("")) return;
                addCueEditText.setText("");
                addCueEditText.clearFocus();
                liftManager.insertCue(cue);
                reloadCues();
            }
        });

        loadCues();

        datePickerTextView.setText(formatDate(new Date()));
        datePickerTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getActivity().getFragmentManager();
                Date date;
                try {
                    date = new SimpleDateFormat("dd MMMM yyyy", Locale.CANADA).parse(datePickerTextView.getText().toString());
                } catch (ParseException e) {
                    date = new Date();
                }
                DateDialogFragment dateDialogFragment = DateDialogFragment.newInstance(date);
                dateDialogFragment.setTargetFragment(LiftFragment.this, REQUEST_DATE);
                dateDialogFragment.show(fragmentManager, getResources().getString(R.string.set_date));
            }
        });

        // TODO if today's lifts/last day's lifts don't exist, hide that section
        // TODO if no lifts exist at all, hide the PR section

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "LiftFragment#onResume called");
        if (getUserVisibleHint()) {
            reloadCues();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_DATE) {
            Date setDate = (Date) data.getSerializableExtra(DateDialogFragment.SET_DATE);
            datePickerTextView.setText(formatDate(setDate));
        }
    }

    public void loadCues() {
        Log.i(TAG, "LiftFragment#loadCues called");
        cueLinearLayout.removeAllViews();
        Bundle args = new Bundle();
        args.putLong(CUE_LIFT_ID, lift.getId());
        getLoaderManager().initLoader(ID_LOAD_CUES, args, this);
    }

    public void reloadCues() {
        Log.i(TAG, "LiftFragment#reloadCues called");
        cueLinearLayout.removeAllViews();
        Bundle args = new Bundle();
        args.putLong(CUE_LIFT_ID, lift.getId());
        getLoaderManager().restartLoader(ID_LOAD_CUES, args, this);
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

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id == ID_LOAD_CUES) {
            long liftId = args.getLong(CUE_LIFT_ID, -1);
            if (liftId == -1) Log.e(TAG, "LIFT's LIFT ID CAME BACK AS -1");
            return new CueCursorLoader(getActivity(), liftId);
        } else {
            Log.e(TAG, "UNRECOGNIZED ID FOR LOAD REQUEST");
            return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cueCursor != null) cueCursor.close();
        cueCursor = (LiftDatabaseHelper.CueCursor) cursor;
        Log.i(TAG, "Loaded " + cueCursor.getCount() + " cues");

        // counting on cursor starting before the first row
        while(cueCursor.moveToNext()) {
            LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final LinearLayout cueItem = (LinearLayout) inflater.inflate(R.layout.list_item_cue, cueLinearLayout, false);

            TextView cueTextView = (TextView) cueItem.findViewById(R.id.cue_text_text_view);
            cueTextView.setText(cueCursor.getCue().getCue());

            final ImageButton deleteCueImageButton = (ImageButton) cueItem.findViewById(R.id.delete_cue_button);
            deleteCueImageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int index = cueLinearLayout.indexOfChild((LinearLayout) deleteCueImageButton.getParent());
                    cueCursor.moveToPosition(index);
                    if (cueCursor.isBeforeFirst() || cueCursor.isAfterLast()) {
                        Toast.makeText(getActivity().getApplicationContext(), R.string.cannot_delete_cue, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    liftManager.deleteCue(cueCursor.getCue());
                    reloadCues();
                }
            });

            cueLinearLayout.addView(cueItem);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        cueLinearLayout.removeAllViews();
    }

    private class CueCursorAdapter extends CursorAdapter {
        private LiftDatabaseHelper.CueCursor cueCursor;

        public CueCursorAdapter(Context context, LiftDatabaseHelper.CueCursor cueCursor) {
            super(context, cueCursor, 0);
            this.cueCursor = cueCursor;
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            return inflater.inflate(R.layout.list_item_cue, parent, false);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            Cue cue = cueCursor.getCue();

            TextView cueTextTextView = (TextView) view.findViewById(R.id.cue_text_text_view);
            cueTextTextView.setText(cue.getCue());
        }
    }
}

