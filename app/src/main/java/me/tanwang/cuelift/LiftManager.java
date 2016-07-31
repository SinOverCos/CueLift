package me.tanwang.cuelift;

import android.content.Context;
import android.util.Log;

public class LiftManager {

    private static final String TAG = "LiftManager";

    private Context appContext;
    private LiftDatabaseHelper databaseHelper;
    private static LiftManager liftManager;

    private LiftManager(Context appContext) {
        this.appContext = appContext;
        databaseHelper = new LiftDatabaseHelper(appContext);
    }

    public static LiftManager get(Context appContext) {
        if(liftManager == null) {
            liftManager = new LiftManager(appContext);
        }
        return liftManager;
    }

    /*** Lift ***/

    public long insertLift(Lift lift) {
        return databaseHelper.insertLift(lift);
    }

    public Lift getLift(long liftId) {
        return databaseHelper.getLift(liftId).getLift();
    }

    public int updateLift(Lift lift) {
        Log.e(TAG, "LiftManager is updating lift " + lift);
        return databaseHelper.updateLift(lift);
    }

    public LiftDatabaseHelper.LiftCursor queryLifts() {
        return databaseHelper.queryLifts();
    }

    /*** Cue ***/

    public long insertCue(Cue cue) {
        return databaseHelper.insertCue(cue);
    }
}
