package me.tanwang.cuelift;

import android.content.Context;
import android.database.Cursor;

// http://stackoverflow.com/questions/31999869/why-cant-loaders-be-non-static-inner-classes
// this class must be static
// loaders are designed to survive configuration changes
// non-static inner class will contains implicit reference to containing instance, so will create memory leak
public class LiftListCursorLoader extends SQLiteCursorLoader {

    public LiftListCursorLoader(Context context) {
        super(context);
    }

    @Override
    protected Cursor loadCursor() {
        return LiftManager.get(getContext().getApplicationContext()).queryLifts();
    }
}
