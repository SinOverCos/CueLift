package me.tanwang.cuelift;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class LiftDatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "LiftDatabaseHelper";

    private static final String DB_NAME = "cuelift.sqlite";
    private static final int VERSION = 1;

    private static final String TABLE_LIFT = "lift";
    private static final String LIFT_ID = "_id";
    private static final String LIFT_NAME = "displayName";
    private static final String LIFT_MAX_WEIGHT = "maxWeight";
    private static final String LIFT_MAX_VOL = "maxVolume";

    private static final String TABLE_SET = "liftSet";
    private static final String SET_DATE = "timestamp";
    private static final String SET_REPS = "reps";
    private static final String SET_WEIGHT = "weight";
    private static final String SET_LIFT_ID = "lift_id";

    private static final String TABLE_CUE = "cue";
    private static final String CUE_HINT = "hint";
    private static final String CUE_LIFT_ID = "lift_id";

    public LiftDatabaseHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
    }



    public long insertLift(Lift lift) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(LIFT_NAME, lift.getDisplayName());
        contentValues.put(LIFT_MAX_WEIGHT, lift.getMaxWeight());
        contentValues.put(LIFT_MAX_VOL, lift.getMaxVolume());
        return getWritableDatabase().insert(TABLE_LIFT, null, contentValues);
    }

    public LiftCursor getLift(long liftId) {
        Cursor singleLiftCursor = getReadableDatabase().query(TABLE_LIFT, null, LIFT_ID + "=" + liftId, null, null, null, null, "1");
        return new LiftCursor(singleLiftCursor);
    }

    public LiftCursor queryLifts() {
        // select * from TABLE_LIFT order by name asc
        Cursor wrapped = getReadableDatabase().query(TABLE_LIFT, null, null, null, null, null, LIFT_NAME + " asc");
        return new LiftCursor(wrapped);
    }

    public static class LiftCursor extends CursorWrapper {

        public LiftCursor(Cursor cursor) {
            super(cursor);
        }

        public Lift getLift() {
            if (isBeforeFirst() || isAfterLast()) { return null; }
            Lift lift = new Lift();
            lift.setId(getLong(getColumnIndex(LIFT_ID)));
            lift.setDisplayName(getString(getColumnIndex(LIFT_NAME)));
            return lift;
        }
    }



    /*** DB STUFF ***/

    @Override
    public void onCreate(SQLiteDatabase database) {
        String createLiftTableSql = "create table " + TABLE_LIFT + " (" + LIFT_ID + " integer primary key autoincrement, " + LIFT_NAME + " varchar(255), " + LIFT_MAX_WEIGHT + " integer, " + LIFT_MAX_VOL + " integer)";
        String createSetTableSql = "create table " + TABLE_SET + " (" + SET_DATE + " integer, " + SET_REPS + " integer, " + SET_WEIGHT + " integer, " + SET_LIFT_ID + " integer references " + TABLE_LIFT + "(" + LIFT_ID + "))";
        String createCueTableSql = "create table " + TABLE_CUE + " (" + CUE_HINT + " varchar(255), " + CUE_LIFT_ID + " integer references " + TABLE_LIFT + "(" + LIFT_ID + "))";
        Log.i(TAG, "Creating DB: " + createLiftTableSql);
        Log.i(TAG, "Creating DB: " + createSetTableSql);
        Log.i(TAG, "Creating DB: " + createCueTableSql);
        database.execSQL(createLiftTableSql);
        database.execSQL(createSetTableSql);
        database.execSQL(createCueTableSql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {}
}
