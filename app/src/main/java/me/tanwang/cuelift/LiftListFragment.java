package me.tanwang.cuelift;

import android.app.Activity;
import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

public class LiftListFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String TAG = "LiftListFragment";

    public static final int ID_LOAD_LIFTS = 0;

    private LiftLiftFragmentCallbacks callbacks;

    // Required for hosting activities
    public interface LiftLiftFragmentCallbacks {
        void onLiftSelected(Lift lift);
    }

    // see http://stackoverflow.com/questions/32083053/android-fragment-onattach-deprecated
    @Override
    public void onAttach(Context hostActivity) {
        super.onAttach(hostActivity);
        Log.i(TAG, "onAttach(Context) called");
        callbacks = (LiftLiftFragmentCallbacks) hostActivity;
        Log.i(TAG, "Callbacks added through onAttach(Context)");
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onAttach(Activity hostActivity) {
        super.onAttach(hostActivity);
        Log.i(TAG, "onAttach(Activity) called");
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            Log.i(TAG, "Callbacks added through onAttach(Activity)");
            callbacks = (LiftLiftFragmentCallbacks) hostActivity;
        }
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        return view;
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
        LiftCursorAdapter adapter = new LiftCursorAdapter(getActivity(), (LiftDatabaseHelper.LiftCursor) cursor);
        setListAdapter(adapter);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        setListAdapter(null);
    }

    // http://stackoverflow.com/questions/31999869/why-cant-loaders-be-non-static-inner-classes
    // this class must be static
    // loaders are designed to survive configuration changes
    // non-static inner class will contains implicit reference to containing instance, so will create memory leak
    private static class LiftListCursorLoader extends SQLiteCursorLoader {
        public LiftListCursorLoader(Context context) {
            super(context);
        }

        @Override
        protected Cursor loadCursor() {
            return LiftManager.get(getContext()).queryLifts();
        }
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
            TextView textView = (TextView) view.findViewById(R.id.textview);
            textView.setText(lift.toString());
        }
    }
}
