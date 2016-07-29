package me.tanwang.cuelift;

import android.support.v4.app.FragmentManager;
import android.app.LoaderManager;
import android.content.Loader;
import android.database.Cursor;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class LiftPagerActivity extends AppCompatActivity implements LiftFragment.LiftFragmentCallbacks, LoaderManager.LoaderCallbacks<Cursor> {

    private ViewPager viewPager;
    private LiftDatabaseHelper.LiftCursor liftCursor;

    private static final int ID_LOAD_LIFTS = 0;
    private static final String TAG = "LiftPagerActivity";

    public void onLiftUpdated(Lift lift) {
        getLoaderManager().restartLoader(ID_LOAD_LIFTS, null, this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id == ID_LOAD_LIFTS) {
            return new LiftListCursorLoader(this);
        } else {
            Log.e(TAG, "UNRECOGNIZED ID FOR LOAD REQUEST");
            return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        viewPager = new ViewPager(this);
        viewPager.setId(R.id.viewPager);
        setContentView(viewPager);


        liftCursor = (LiftDatabaseHelper.LiftCursor) cursor;

        FragmentManager fragmentManager = getSupportFragmentManager();
        viewPager.setAdapter(new FragmentStatePagerAdapter(fragmentManager) {
            @Override
            public Fragment getItem(int position) {
                if(liftCursor.moveToPosition(position)) {
                    Lift lift = liftCursor.getLift();
                    return LiftFragment.newInstance(lift);
                } else {
                    Log.e(TAG, "(FragmentStatePagerAdapter) Cursor moved out of bounds to" + position);
                    return null;
                }
            }

            @Override
            public int getCount() {
                return liftCursor.getCount();
            }
        });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            @Override
            public void onPageScrollStateChanged(int state) {}

            @Override
            public void onPageSelected(int position) {
                if(liftCursor.moveToPosition(position)) {
                    Lift lift = liftCursor.getLift();
                    setTitle(lift.getDisplayName());
                } else {
                    Log.e(TAG, "(OnPageChangeListener) Cursor moved out of bounds to" + position);
                }
            }
        });
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {}
}
