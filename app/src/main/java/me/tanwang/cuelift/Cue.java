package me.tanwang.cuelift;

public class Cue {

    private static final String TAG = "Cue";

    private long id;
    private long liftId;
    private String cue;

    public Cue() {
        id = -1;
        cue = null;
        liftId = -1;
    }

    public Cue(long id, String cue, long liftId) {
        this.id = id;
        this.cue = cue;
        this.liftId = liftId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }


    public String toString() {
        return "CUE id = " + id + ", cue = " + cue;
    }

    public String getCue() {
        return cue;
    }

    public void setCue(String cue) {
        if (cue.length() > 255) cue = cue.substring(0, 256);
        this.cue = cue;
    }

    public long getLiftId() {
        return liftId;
    }

    public void setLiftId(long liftId) {
        this.liftId = liftId;
    }

}
