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
        return databaseHelper.updateLift(lift);
    }

    public LiftDatabaseHelper.LiftCursor queryLifts() {
        return databaseHelper.queryLifts();
    }

    /*** Cue ***/

    public long insertCue(Cue cue) {
        return databaseHelper.insertCue(cue);
    }

    public Cue getCue(long cueId) {
        return databaseHelper.getCue(cueId).getCue();
    }

    public int updateCue(Cue cue) {
        return databaseHelper.updateCue(cue);
    }

    public int deleteCue(Cue cue) {
        return databaseHelper.deleteCue(cue);
    }

    public LiftDatabaseHelper.CueCursor queryCues(long liftId) {
        return databaseHelper.queryCues(liftId);
    }


    /*** Set ***/

    public long insertSet(Set set) {
        return databaseHelper.insertSet(set);
    }

    public Set getSet(long setId) {
        return databaseHelper.getSet(setId).getSet();
    }

    public int deleteSet(Set set) {
        return databaseHelper.deleteSet(set);
    }

    public LiftDatabaseHelper.SetCursor querySets(long liftId) {
        return databaseHelper.querySets(liftId);
    }
}
