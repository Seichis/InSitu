package webinar.pubnub.insitu.managers;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import org.joda.time.DateTime;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;
import webinar.pubnub.insitu.BackgroundService;
import webinar.pubnub.insitu.Utils;
import webinar.pubnub.insitu.model.Medication;

/**
 * Created by Konstantinos Michail on 4/25/2016.
 */
public class MedicationManager {
    private static final String TAG = "MedicationManager";
    private static MedicationManager medicationManager = new MedicationManager();
    Context context;
    Realm realm;

    public MedicationManager(){}

    public static MedicationManager getInstance() {
        return medicationManager;
    }

    public void init(Context context) {
        this.context = context;
        realm = Realm.getDefaultInstance();
    }

    public void manageMedicationInput() {
        realm.beginTransaction();
        Medication medication = realm.createObject(Medication.class);
        medication.setTimestamp(DateTime.now().getMillis());
        Location location=BackgroundService.getInstance().getCurrentLocation();
        if (location!=null){
            medication.setLatitude(location.getLatitude());
            medication.setLongitude(location.getLongitude());
        }
        Log.i(TAG, String.valueOf(medication.getTimestamp()));
        realm.commitTransaction();
    }

    public RealmResults<Medication> getMedicationByType(String type) {
        return realm.allObjects(Medication.class).where().equalTo("medicationType", type).findAll();
    }

    public RealmResults<Medication> getMedicationByName(String name) {
        return realm.allObjects(Medication.class).where().equalTo("medicationName", name).findAll();
    }

    public RealmResults<Medication> getMedicationByDate(long date) {
        return getMedicationByRange(Utils.getDayStart(date, 1), Utils.getDaysEnd(date));
    }

    public RealmResults<Medication> getMedicationByRange(long from, long until) {
        long yesterdayStart = Utils.getDayStart(from, 1);
        long tomorrowStart = Utils.getDaysEnd(until);
        return realm.where(Medication.class).between("timestamp", yesterdayStart, tomorrowStart).findAll();
    }


}
