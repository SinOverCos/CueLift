package me.tanwang.cuelift;

import android.content.Context;
import android.database.Cursor;

// http://stackoverflow.com/questions/31999869/why-cant-loaders-be-non-static-inner-classes
// this class must be static
// loaders are designed to survive configuration changes
// non-static inner class will contains implicit reference to containing instance, so will create memory leak
public class CueCursorLoader extends SQLiteCursorLoader {

    private long liftId;

    public CueCursorLoader(Context context, long liftId) {
        super(context);
        this.liftId = liftId;
    }

    @Override
    protected Cursor loadCursor() {
        return LiftManager.get(getContext().getApplicationContext()).queryCues(liftId);
    }
}
