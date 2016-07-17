package me.tanwang.cuelift;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SetDatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "SetDatabaseHelper";

    private static final String DB_NAME = "cuelift.sqlite";
    private static final int VERSION = 1;

    private static final String TABLE_LIFT = "lift";
    private static final String LIFT_ID = "_id";
    private static final String LIFT_NAME = "displayName";

    private static final String TABLE_SET = "set";
    private static final String SET_DATE = "timestamp";
    private static final String SET_REPS = "reps";
    private static final String SET_WEIGHT = "weight";
    private static final String SET_LIFT_ID = "lift_id";

    private static final String TABLE_CUE = "cue";
    private static final String CUE_HINT = "hint";
    private static final String CUE_LIFT_ID = "lift_id";

    public SetDatabaseHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        String createLiftTableSql = "create table " + TABLE_LIFT + " (" + LIFT_ID + " integer primary key autoincrement, " + LIFT_NAME + " varchar(255))";
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
