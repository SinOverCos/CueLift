package me.tanwang.cuelift;

import java.io.Serializable;

public class Lift implements Serializable {

    public static final String EXTRA_LIFT = "com.tanwang.cuelift.lift";

    private static final String TAG = "Lift";

    private long id;
    private String displayName;
    private int maxWeight;
    private int maxVolume;

    public Lift() {
        id = -1;
        displayName = null;
        maxWeight = 0;
        maxVolume = 0;
    }


    public int getMaxWeight() {
        return maxWeight;
    }

    public void setMaxWeight(int maxWeight) {
        this.maxWeight = maxWeight;
    }

    public int getMaxVolume() {
        return maxVolume;
    }

    public void setMaxVolume(int maxVolume) {
        this.maxVolume = maxVolume;
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
