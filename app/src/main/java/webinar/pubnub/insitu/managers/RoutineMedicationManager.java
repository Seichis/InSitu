package webinar.pubnub.insitu.managers;

import android.content.Context;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import io.realm.Sort;
import webinar.pubnub.insitu.Utils;
import webinar.pubnub.insitu.model.Medication;
import webinar.pubnub.insitu.model.RealmString;
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



    public RoutineMedication getById(String id){
        return realm.where(RoutineMedication.class).equalTo("id",id).findFirst();
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

    public boolean isRoutineMedicationFollowed(){
//        if(realm.allObjects(Medication.class).size()==0){
//            return true;
//        }
        Medication medication= realm.allObjects(Medication.class).where().equalTo("isRoutine",true).findAllSorted("timestamp", Sort.DESCENDING).first();

        RealmResults<RoutineMedication> routineMedications=getRoutineMedications();
        long medDate=medication.getTimestamp();
        String day= Utils.getDay(medDate).toLowerCase();
        for (RoutineMedication rtm:routineMedications){
            String[] hourMin=rtm.getHourOfRoutineConsumption().split(":");
            RealmList<RealmString> days=rtm.getDaysRepeat();
            for (RealmString rls:days){
                if (day.startsWith(rls.getValue().toLowerCase())){
                    long routineMedDate=Utils.getDateFromHourAndMin(Integer.parseInt(hourMin[0]),Integer.parseInt(hourMin[1]));
                    float difMinutes = Math.abs((float) (medDate - routineMedDate) / (1000 * 60));
                    if(rtm.getMedicationName().equals(medication.getMedicationName()) && difMinutes<=60){
                        return true;
                    }
                }
            }
        }
        return false;
    }

}
