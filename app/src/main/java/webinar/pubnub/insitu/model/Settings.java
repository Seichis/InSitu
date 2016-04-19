package webinar.pubnub.insitu.model;

import io.realm.RealmObject;

public class Settings extends RealmObject {

    public static final int NO_BUTTON_MODE = 0;
    public static final int BINARY_BUTTON_MODE = 1;
    public static final int FULL_BUTTON_MODE = 2;
    boolean isMovesActivated;
    long id;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    boolean isLifelogActivated;

    boolean isFlicActivated;
    //Default mode no button
    int mode;
    Patient patient;

    public Settings() {
    }

    public boolean isMovesActivated() {
        return isMovesActivated;
    }

    public void setMovesActivated(boolean movesActivated) {
        isMovesActivated = movesActivated;
    }

    public boolean isLifelogActivated() {
        return isLifelogActivated;
    }

    public void setLifelogActivated(boolean lifelogActivated) {
        isLifelogActivated = lifelogActivated;
    }

    public boolean isFlicActivated() {
        return isFlicActivated;
    }

    public void setFlicActivated(boolean flicActivated) {
        isFlicActivated = flicActivated;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }
}
