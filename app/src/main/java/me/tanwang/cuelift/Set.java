package me.tanwang.cuelift;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Set {

    private long id;
    private long date;
    private int reps;
    private int weight;
    private long liftId;

    public Set() {
        this.id = -1;
        this.date = -1;
        this.reps = -1;
        this.weight = -1;
        this.liftId = -1;
    }

    public Set(long mills, int reps, int weight, long liftId) {
        this.id = -1;
        this.date = mills;
        this.reps = reps;
        this.weight = weight;
        this.liftId = liftId;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getLiftId() {
        return liftId;
    }

    public void setLiftId(long liftId) {
        this.liftId = liftId;
    }

    public int getReps() {
        return reps;
    }

    public void setReps(int reps) {
        this.reps = reps;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }
}
