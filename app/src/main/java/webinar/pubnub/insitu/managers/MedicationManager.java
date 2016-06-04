package webinar.pubnub.insitu.managers;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import org.joda.time.DateTime;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import webinar.pubnub.insitu.BackgroundService;
import webinar.pubnub.insitu.Utils;
import webinar.pubnub.insitu.model.Medication;
import webinar.pubnub.insitu.model.RealmString;
import webinar.pubnub.insitu.model.RoutineMedication;

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
        medication.setRoutine(isRoutineMedication(medication));
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

    public boolean isRoutineMedication(Medication medication){
        RealmResults<RoutineMedication> routineMedications=RoutineMedicationManager.getInstance().getRoutineMedications();
        long medDate=medication.getTimestamp();
        String day=Utils.getDay(medDate).toLowerCase();
        for (RoutineMedication rtm:routineMedications){
            String[] hourMin=rtm.getHourOfRoutineConsumption().split(":");
            RealmList<RealmString> days=rtm.getDaysRepeat();

//            if(medication.getMedicationName().equals(rtm.getMedicationName())){
//                return true;
//            }

            for (RealmString rls:days){
                if (day.startsWith(rls.getValue().toLowerCase())){
                    long routineMedDate=Utils.getDateFromHourAndMin(Integer.parseInt(hourMin[0]),Integer.parseInt(hourMin[1]));
                    float difMinutes = Math.abs((float) (medDate - routineMedDate) / (1000 * 60));
                    if (difMinutes<=60){
                        return true;
                    }
                }
            }

        }
        return false;
    }

}
