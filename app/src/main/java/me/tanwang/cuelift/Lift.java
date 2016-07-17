package me.tanwang.cuelift;

public class Lift {

    private static final String TAG = "Lift";

    private String displayName;
    private long id;

    public Lift() {
        id = -1;
        displayName = null;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
