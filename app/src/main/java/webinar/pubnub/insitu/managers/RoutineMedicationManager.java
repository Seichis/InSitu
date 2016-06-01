package webinar.pubnub.insitu.managers;

import android.content.Context;

import io.realm.Realm;
import io.realm.RealmResults;
import webinar.pubnub.insitu.model.RoutineMedication;

/**
 * Created by Konstantinos Michail on 5/31/2016.
 */
public class RoutineMedicationManager {
    static RoutineMedicationManager routineMedicationManager = new RoutineMedicationManager();
    Realm realm;
    Context context;
    public RoutineMedicationManager(){}

    public static RoutineMedicationManager getInstance() {
        return routineMedicationManager;
    }

    public void init(Context context) {
        this.context = context;
        realm = Realm.getDefaultInstance();

    }

    public RealmResults<RoutineMedication> getAutologgingMedication() {
        return realm.where(RoutineMedication.class).equalTo("automaticLogging", true).findAll();
    }

    public RealmResults<RoutineMedication> getAlarmEnabledMedication() {
        return realm.where(RoutineMedication.class).equalTo("alarmEnabled", true).findAll();
    }

    public RealmResults<RoutineMedication> getFlicButtonMedication() {
        return realm.where(RoutineMedication.class).equalTo("flicButtonLogging", true).findAll();
    }

    public void createRoutineMed(RoutineMedication medication) {
        realm.beginTransaction();
        realm.copyToRealm(medication);
        realm.commitTransaction();
    }

    public RealmResults<RoutineMedication> getRoutineMedications() {
        return realm.where(RoutineMedication.class).findAll();
    }
}
