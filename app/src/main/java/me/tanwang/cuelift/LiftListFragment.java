package me.tanwang.cuelift;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.io.FilenameFilter;
import java.net.URI;

public class LiftListFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String TAG = "LiftListFragment";

    public static final int ID_LOAD_LIFTS = 0;

    private LiftLiftFragmentCallbacks callbacks;
    private LiftManager liftManager;
    private LiftDatabaseHelper.LiftCursor liftCursor;


    // Required for hosting activities
    public interface LiftLiftFragmentCallbacks {
        void onLiftSelected(Lift lift);
    }

    /*
    // see http://stackoverflow.com/questions/32083053/android-fragment-onattach-deprecated
    @Override
    public void onAttach(Context hostActivity) {
        super.onAttach(hostActivity);
        Log.i(TAG, "onAttach(Context) called");
        callbacks = (LiftLiftFragmentCallbacks) hostActivity;
        Log.i(TAG, "Callbacks added through onAttach(Context)");
    }
    */

    @Override
    @SuppressWarnings("deprecation")
    public void onAttach(Activity hostActivity) {
        super.onAttach(hostActivity);
        Log.i(TAG, "onAttach(Activity) called");
        Log.i(TAG, "Callbacks added through onAttach(Activity)");
        callbacks = (LiftLiftFragmentCallbacks) hostActivity;
    }

    @Override
    public void onStart() {
        super.onStart();
        TextView emptyListTextView = (TextView) getActivity().getLayoutInflater().inflate(R.layout.view_empty_lift_list, getListView(), false);
        ((ViewGroup) getListView().getParent()).addView(emptyListTextView);
        getListView().setEmptyView(emptyListTextView);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLoaderManager().initLoader(ID_LOAD_LIFTS, null, this);
    }

    @Override
    public void onResume() {
        super.onResume();
        getLoaderManager().restartLoader(ID_LOAD_LIFTS, null, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        liftManager = LiftManager.get(getActivity().getApplicationContext());

        /* Dummy Lifts to populate list
        Lift benchPress = new Lift();
        benchPress.setDisplayName("Bench Press");
        benchPress.setMaxVolume(1085);
        benchPress.setMaxWeight(185);

        Lift deadLift = new Lift();
        deadLift.setDisplayName("Dead Lift");
        deadLift.setMaxVolume(2150);
        deadLift.setMaxWeight(275);

        liftManager.insertLift(benchPress);
        liftManager.insertLift(deadLift);

        getLoaderManager().restartLoader(ID_LOAD_LIFTS, null, this);
        //*/
        return view;
    }

    @Override
    public void onListItemClick(ListView listView, View view, int position, long id) {
        // id is given by cursor adapter since table in db has "_id" column
        Log.i(TAG, "Lift at position " + position + " selected with id = " + id);
        liftCursor.moveToPosition(position);
        callbacks.onLiftSelected(liftCursor.getLift());
    }



    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id == ID_LOAD_LIFTS) {
            return new LiftListCursorLoader(getActivity());
        } else {
            Log.e(TAG, "UNRECOGNIZED ID FOR LOAD REQUEST");
            return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (liftCursor != null) liftCursor.close();
        liftCursor = (LiftDatabaseHelper.LiftCursor) cursor;
        LiftCursorAdapter adapter = new LiftCursorAdapter(getActivity(), liftCursor);
        setListAdapter(adapter);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        setListAdapter(null);
    }

    private class LiftCursorAdapter extends CursorAdapter {
        private LiftDatabaseHelper.LiftCursor liftCursor;

        public LiftCursorAdapter(Context context, LiftDatabaseHelper.LiftCursor liftCursor) {
            super(context, liftCursor, 0);
            this.liftCursor = liftCursor;
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            return inflater.inflate(R.layout.list_item_lift, parent, false);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            Lift lift = liftCursor.getLift();

            TextView liftNameTextView = (TextView) view.findViewById(R.id.lift_name_text_view);
            TextView liftDetailTextView = (TextView) view.findViewById(R.id.lift_detail_text_view);
            ImageButton liftIconImageButton = (ImageButton) view.findViewById(R.id.lift_icon_image_button);

            liftNameTextView.setText(lift.getDisplayName());
            String detail = String.format(getResources().getString(R.string.lift_detail), lift.getMaxWeight(), lift.getMaxVolume());
            liftDetailTextView.setText(detail);

            if (lift.getIconPath() != null) {
                Log.i(TAG, lift.getIconPath());
                liftIconImageButton.setImageURI(Uri.parse(lift.getIconPath()));
            } else {
                Log.i(TAG, lift.getIconPath());
            }
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        callbacks = null;
    }
}
