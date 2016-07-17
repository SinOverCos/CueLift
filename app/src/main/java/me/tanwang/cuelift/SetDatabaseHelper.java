package me.tanwang.cuelift;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SetDatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "cuelift.sqlite";
    private static final int VERSION = 1;

    private static final String SET_DATE = "date";
    private static final String SET_REPS = "reps";
    private static final String SET_WEIGHT = "weight";

    public SetDatabaseHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {}

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {}
}
