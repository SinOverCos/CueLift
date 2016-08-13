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
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class LiftFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = "LiftFragment";
    private static final String CUE_LIFT_ID = "me.tanwang.cuelift.cue_lift_id";
    private static final String SET_LIFT_ID = "me.tanwang.cuelift.set_lift_id";

    private static final int ID_LOAD_CUES = 1;
    private static final int ID_LOAD_SETS = 2;
    private static final int ID_RELOAD_SETS = 3;
    private static final int REQUEST_DATE = 10;
    private static final int REQUEST_REPS = 11;

    private LiftFragmentCallbacks callbacks;
    private LiftManager liftManager;
    private LiftDatabaseHelper.CueCursor cueCursor;
    private LiftDatabaseHelper.SetCursor setCursor;

    private LinearLayout cueLinearLayout;

    private ImageButton liftIconImageButton;
    private EditText liftNameEditText;
    private EditText addCueEditText;
    private FloatingActionButton addCueFab;

    private TextView datePickerTextView;
    private TextView repPickerTextView;
    private EditText weightEditText;
    private FloatingActionButton addSetFab;

    private TableLayout todaysLifts;
    private TableLayout lastDaysLifts;

    private TextView weightPrTextView;
    private TextView volumePrTextView;

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

    private String formatDate(Date date) {
        String formatted = new SimpleDateFormat("dd MMMM yyyy", Locale.CANADA).format(date);
        return formatted;
    }

    private Date parseDateFromText(String date) {
        try {
            return new SimpleDateFormat("dd MMMM yyyy", Locale.CANADA).parse(date);
        } catch (ParseException e) {
            return new Date();
        }
    }

    private int findRepsFromText(String text) {
        String[] split = text.split(" ");
        if (split.length != 2) {
            Log.e(TAG, "Either text is bad, or failed to split properly: " + text);
            return 0;
        }
        try {
            return Integer.parseInt(split[0]);
        } catch (NumberFormatException e) {
            Log.e(TAG, "Failed to parse: " + split[0]);
            return 0;
        }
    }

    private void hideSoftKeyboard() {
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
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
        weightEditText = (EditText) view.findViewById(R.id.weight_edit_text);

        addSetFab = (FloatingActionButton) view.findViewById(R.id.add_set_fab);
        todaysLifts = (TableLayout) view.findViewById(R.id.todays_lifts);
        lastDaysLifts = (TableLayout) view.findViewById(R.id.last_days_lifts);

        weightPrTextView = (TextView) view.findViewById(R.id.lift_pr_weight_text_view);
        volumePrTextView = (TextView) view.findViewById(R.id.lift_pr_volume_text_view);

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
                addCueEditText.clearFocus();
                hideSoftKeyboard();
                Cue cue = new Cue(addCueEditText.getText().toString(), lift.getId());
                if (cue.getCue().equals("")) return;
                addCueEditText.setText("");
                addCueEditText.clearFocus();
                liftManager.insertCue(cue);
                reloadCues();
            }
        });

        loadCues();
        loadSets();

        datePickerTextView.setText(formatDate(new Date()));
        datePickerTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getActivity().getFragmentManager();
                Date date = parseDateFromText(datePickerTextView.getText().toString());
                DateDialogFragment dateDialogFragment = DateDialogFragment.newInstance(date);
                dateDialogFragment.setTargetFragment(LiftFragment.this, REQUEST_DATE);
                dateDialogFragment.show(fragmentManager, getResources().getString(R.string.set_date));
            }
        });

        repPickerTextView.setText(String.format(getResources().getString(R.string.x_reps), 0));
        repPickerTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getActivity().getFragmentManager();
                int currentReps = findRepsFromText(repPickerTextView.getText().toString());
                RepDialogFragment repDialogFragment = RepDialogFragment.newInstance(currentReps);
                repDialogFragment.setTargetFragment(LiftFragment.this, REQUEST_REPS);
                repDialogFragment.show(fragmentManager, getResources().getString(R.string.reps));
            }
        });

        addSetFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                weightEditText.clearFocus();
                hideSoftKeyboard();
                if (weightEditText.getText().toString().equals("")) return;
                int reps = findRepsFromText(repPickerTextView.getText().toString());
                int weight = Integer.parseInt(weightEditText.getText().toString());
                if (reps == 0) return;
                if (weight == 0) return;
                liftManager.insertSet(new Set(parseDateFromText(datePickerTextView.getText().toString()).getTime(),
                                                findRepsFromText(repPickerTextView.getText().toString()),
                                                Integer.parseInt(weightEditText.getText().toString()),
                                                lift.getId()));

                reloadSets(ID_LOAD_SETS);
            }
        });

        weightPrTextView.setText(String.format(Locale.CANADA, getResources().getString(R.string.weight_pr), lift.getMaxWeight()));
        volumePrTextView.setText(String.format(Locale.CANADA, getResources().getString(R.string.volume_pr), lift.getMaxVolume()));

        // TODO reload sets on delete

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
            reloadSets(ID_LOAD_SETS);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_DATE) {
            Date setDate = (Date) data.getSerializableExtra(DateDialogFragment.SET_DATE);
            datePickerTextView.setText(formatDate(setDate));
        } else if(requestCode == REQUEST_REPS) {
            int reps = data.getIntExtra(RepDialogFragment.SET_REPS, -1);
            if (reps == -1) {
                Log.e(TAG, "No reps received from NumberPicker!");
                reps = 0;
            }
            String repsText = String.format(getResources().getString(R.string.x_reps), reps);
            repPickerTextView.setText(repsText);
        }
    }

    private Bundle cueLoadPrep() {
        cueLinearLayout.removeAllViews();
        Bundle args = new Bundle();
        args.putLong(CUE_LIFT_ID, lift.getId());
        return args;
    }

    private void loadCues() {
        Log.i(TAG, "LiftFragment#loadCues called");
        getLoaderManager().initLoader(ID_LOAD_CUES, cueLoadPrep(), this);
    }

    private void reloadCues() {
        Log.i(TAG, "LiftFragment#reloadCues called");
        getLoaderManager().restartLoader(ID_LOAD_CUES, cueLoadPrep(), this);
    }

    private Bundle setLoadPrep(int id) {
        if (id != ID_RELOAD_SETS) {
            todaysLifts.removeAllViews();
            lastDaysLifts.removeAllViews();
        }
        Bundle args = new Bundle();
        args.putLong(SET_LIFT_ID, lift.getId());
        return args;
    }

    private void updatePrs() {
        setCursor.moveToPosition(-1);
        Set set;
        int volume = 0;
        int weight = 0;
        while (setCursor.moveToNext()) {
            set = setCursor.getSet();
            int setVolume = set.getReps() * set.getWeight();
            int setWeight = set.getWeight();
            if (setVolume > volume) volume = setVolume;
            if (setWeight > weight) weight = setWeight;
        }
        if (weight > lift.getMaxWeight())
            Toast.makeText(getActivity(), R.string.prw_toast, Toast.LENGTH_SHORT).show();
        if (volume > lift.getMaxVolume())
            Toast.makeText(getActivity(), R.string.prv_toast, Toast.LENGTH_SHORT).show();
        lift.setMaxWeight(weight);
        lift.setMaxVolume(volume);
        weightPrTextView.setText(String.format(Locale.CANADA, getResources().getString(R.string.weight_pr), weight));
        volumePrTextView.setText(String.format(Locale.CANADA, getResources().getString(R.string.volume_pr), volume));
    }

    private  void loadSets() {
        Log.i(TAG, "LiftFragment#loadSets called");
        getLoaderManager().initLoader(ID_LOAD_SETS, setLoadPrep(ID_LOAD_SETS), this);
    }

    private void reloadSets(int id) {
        Log.i(TAG, "LiftFragment#reloadSets called");
        getLoaderManager().restartLoader(id, setLoadPrep(id), this);
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
        updateLift();
        super.onPause();
    }

    public void updateLift() {
        liftManager.updateLift(lift);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        long liftId;
        if (id == ID_LOAD_CUES) {
            liftId = args.getLong(CUE_LIFT_ID, -1);
            if (liftId == -1) Log.e(TAG, "LIFT'S LIFT ID CAME BACK AS -1 (CUE_LIFT_ID)");
        } else if (id == ID_LOAD_SETS || id == ID_RELOAD_SETS) {
            liftId = args.getLong(SET_LIFT_ID, -1);
            if (liftId == -1) Log.e(TAG, "LIFT'S LIFT ID CAME BACK AS -1 (SET_LIFT_ID)");
        } else {
            Log.e(TAG, "UNRECOGNIZED ID FOR LOAD REQUEST");
            return null;
        }

        if (id == ID_LOAD_CUES) {
            return new CueCursorLoader(getActivity(), liftId);
        } else {
            return new SetCursorLoader(getActivity(), liftId);
        }
    }

    private void applyLoadedCues(Cursor cursor) {
        if (cueCursor != null) cueCursor.close();
        cueCursor = (LiftDatabaseHelper.CueCursor) cursor;
        Log.i(TAG, "Loaded " + cueCursor.getCount() + " cues");

        // counting on cursor starting before the first row
        while (cueCursor.moveToNext()) {
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

    private View makeSetRow(Set set) {
        View rowLift = getActivity().getLayoutInflater().inflate(R.layout.row_lift, null);
        ((TextView) rowLift.findViewById(R.id.date_cell_text_view)).setText(formatDate(new Date(set.getDate())));
        ((TextView) rowLift.findViewById(R.id.rep_cell_text_view)).setText(Integer.toString(set.getReps()));
        ((TextView) rowLift.findViewById(R.id.weight_cell_text_view)).setText(Integer.toString(set.getWeight()));
        final ImageButton deleteSetImageButton = ((ImageButton) rowLift.findViewById(R.id.delete_set_button));
        deleteSetImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TableRow row = (TableRow) deleteSetImageButton.getParent();
                TableLayout table = (TableLayout) row.getParent();
                int index = table.indexOfChild(row) - 1; // -1 for the header child
                if (table.getId() == R.id.last_days_lifts) {
                    index += todaysLifts.getChildCount() - 1;
                }
                setCursor.moveToPosition(index);
                if (setCursor.isBeforeFirst() || setCursor.isAfterLast()) {
                    Toast.makeText(getActivity().getApplicationContext(), R.string.cannot_delete_set, Toast.LENGTH_SHORT).show();
                    return;
                }
                liftManager.deleteSet(setCursor.getSet());
                if (table.getChildCount() == 2) {
                    reloadSets(ID_LOAD_SETS);
                } else {
                    table.removeView(row);
                    reloadSets(ID_RELOAD_SETS);
                }
            }
        });
        return rowLift;
    }

    private void applyLoadedSets(Cursor cursor) {
        View header1 = getActivity().getLayoutInflater().inflate(R.layout.table_header, null);
        View header2 = getActivity().getLayoutInflater().inflate(R.layout.table_header, null);
        todaysLifts.addView(header1);
        lastDaysLifts.addView(header2);

        if (setCursor != null) setCursor.close();
        setCursor = (LiftDatabaseHelper.SetCursor) cursor;
        Log.i(TAG, "Loaded " + setCursor.getCount() + " sets");

        if (!setCursor.moveToFirst()) {
            Log.w(TAG, "cursor containing sets is empty");
            return;
        }

        Set mostRecentSet = setCursor.getSet();
        ArrayList<Set> todaySets = new ArrayList<Set>();
        todaySets.add(mostRecentSet);
        long mostRecentWorkout = mostRecentSet.getDate();
        while (setCursor.moveToNext()) {
            Set set = setCursor.getSet();
            if (set.getDate() == mostRecentWorkout) {
                todaySets.add(set);
            } else {
                break;
            }
        }

        for (Set set : todaySets) {
            todaysLifts.addView(makeSetRow(set));
        }

        if (setCursor.isAfterLast()) {
            Log.i(TAG, "No sets from previous day found");
            return;
        }

        Set mostRecentLastDaySet = setCursor.getSet();
        ArrayList<Set> lastDaySets = new ArrayList<Set>();
        lastDaySets.add(mostRecentLastDaySet);
        long lastWorkout = mostRecentLastDaySet.getDate();
        while (setCursor.moveToNext()) {
            Set set = setCursor.getSet();
            if (set.getDate() == lastWorkout) {
                lastDaySets.add(set);
            } else {
                break;
            }
        }

        for (Set set : lastDaySets) {
            lastDaysLifts.addView(makeSetRow(set));
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        int loaderId = loader.getId();

        if (loaderId == ID_LOAD_CUES) {
            applyLoadedCues(cursor);
        } else if (loaderId == ID_LOAD_SETS) {
            applyLoadedSets(cursor);
            updatePrs();
        } else if (loaderId == ID_RELOAD_SETS) { // only reset cursor, don't reload rows in the table
            setCursor = (LiftDatabaseHelper.SetCursor) cursor;
            updatePrs();
        } else {
            Log.e(TAG, "Unrecognized loader ID: " + loaderId);
        }

        addCueEditText.clearFocus();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {}
}

