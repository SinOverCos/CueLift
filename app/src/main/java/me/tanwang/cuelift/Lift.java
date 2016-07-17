package me.tanwang.cuelift;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.json.JSONException;
import org.json.JSONObject;

public class Lift {

    private String displayName;
    private String dbName;

    private static final String TAG = "Lift";

    private static final String JSON_DISPLAY_NAME = "displayName";
    private static final String JSON_DB_NAME = "dbName";

    public Lift(Context context, String displayName) {
        this.displayName = displayName;
        dbName = displayName.replaceAll("[^a-zA-Z0-9]", "");
        String tempDbName = dbName;
        int suffix = 0;
        boolean nameTaken = false;
        SetDatabaseHelper databaseHelper = new SetDatabaseHelper(context);
        SQLiteDatabase database = databaseHelper.getWritableDatabase();
        // there probably won't be so many lifts that this query actually lags the UI
        Cursor cursor = database.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);

        do {
            if(cursor.moveToFirst()) {
                while(!cursor.isAfterLast()) {
                    if (cursor.getString(0).equals(tempDbName + suffix)) {
                        nameTaken = true;
                        suffix++;
                    }
                    cursor.moveToNext();
                }
            }
        } while (nameTaken);

        dbName = tempDbName;
    }

    public Lift(JSONObject jsonObject) throws JSONException {
        displayName = jsonObject.getString(JSON_DISPLAY_NAME);
        dbName = jsonObject.getString(JSON_DB_NAME);
    }

}
