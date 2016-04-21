package webinar.pubnub.insitu.managers;

import android.content.Context;

import io.realm.Realm;
import webinar.pubnub.insitu.model.Patient;
import webinar.pubnub.insitu.model.Settings;

public class SettingsManager {
    private static SettingsManager settingsManager = new SettingsManager();
    Realm realm;
    Patient patient;
    PatientManager patientManager;
    Context context;
    public static SettingsManager getInstance() {
        return settingsManager;
    }

    public void init(Context context) {
        this.context=context;
        realm=Realm.getDefaultInstance();
        patientManager = PatientManager.getInstance();
        patient = patientManager.getPatient();
    }

    public Settings getSettings() {
        return realm.where(Settings.class).findFirst();
    }

    public void changeMode(int mode) {
        realm.beginTransaction();
        getSettings().setMode(mode);
        realm.commitTransaction();
    }

    public void createSettings(Patient p) {
        realm.beginTransaction();
        Settings settings = realm.createObject(Settings.class);
        settings.setId(1);
        settings.setPatient(p);
        settings.setFlicActivated(true);
        settings.setMovesActivated(false);
        settings.setLifelogActivated(true);
        settings.setMode(1);
        realm.commitTransaction();
    }
}
