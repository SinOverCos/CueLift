package me.tanwang.cuelift;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.database.Cursor;

public abstract class SQLiteCursorLoader extends AsyncTaskLoader<Cursor> {

    private Cursor cursor;

    public SQLiteCursorLoader(Context context) {
        super(context);
    }

    protected abstract Cursor loadCursor();

    // use generic cursor instead of specific LiftCursor or OtherCursor because this is used for different queries
    @Override
    public Cursor loadInBackground() {
        Cursor cursor = loadCursor();
        if (cursor != null) {
            // ensure that the content window is filled
            // see: http://stackoverflow.com/a/38422344/3629654
            cursor.getCount();
        }
        return cursor;
    }

    @Override
    public void deliverResult(Cursor data) {
        Cursor oldCursor = cursor;
        cursor = data;
        if (isStarted()) {
            super.deliverResult(data);
        }

        if (oldCursor != null && oldCursor != data && !oldCursor.isClosed()) {
            oldCursor.close();
        }
    }

    @Override
    public void onStartLoading() {
        if (cursor != null) {
            deliverResult(cursor);
        }
        if (takeContentChanged() || cursor == null) {
            forceLoad();
        }
    }

    @Override
    public void onStopLoading() {
        cancelLoad();
    }

    @Override
    public void onCanceled(Cursor cursor) {
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
    }

    @Override
    public void onReset() {
        super.onReset();
        onStopLoading(); // make sure load stopped
        if (cursor != null && ! cursor.isClosed()) {
            cursor.close();
        }
        cursor = null;
    }
}
