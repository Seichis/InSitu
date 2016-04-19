package webinar.pubnub.insitu.managers;

import android.content.Context;


import java.sql.SQLException;
import java.util.List;

import io.realm.Realm;
import webinar.pubnub.insitu.model.Patient;

/**
 * Created by Konstantinos Michail on 4/17/2016.
 */
public class PatientManager {
    private static PatientManager patientManager = new PatientManager();
    Realm realm;
    public static PatientManager getInstance() {
        return patientManager;
    }

    public void init(Context context, Realm realm) {
        this.realm=realm;
    }

    public Patient getPatient() {
        return realm.where(Patient.class).findFirst();
    }

    public void createNewProfile(String name,String gender) {
        realm.beginTransaction();
        Patient patient=realm.createObject(Patient.class);
        patient.setId(1);
        patient.setPatientName(name);
        patient.setGender(gender);
        realm.commitTransaction();
        SettingsManager.getInstance().createSettings(patient);

    }

}
