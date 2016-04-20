package webinar.pubnub.insitu.managers;

import android.content.Context;
import android.os.CountDownTimer;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import webinar.pubnub.insitu.Constants;
import webinar.pubnub.insitu.Utils;
import webinar.pubnub.insitu.model.Diary;
import webinar.pubnub.insitu.model.MyChartData;
import webinar.pubnub.insitu.model.Symptom;

/**
 * Created by Konstantinos Michail on 2/14/2016.
 */
public class SymptomManager {
    static List<Double> symptomInputList = new ArrayList<>();
    static SymptomManager symptomManager = new SymptomManager();
    static String TAG = "SymptomManager";
    boolean isWeatherException = false; //Check if an exception was caught while getting the weather

    //    Context context;
//    Date timeStamp = null;
    Diary diary;
    Symptom symptom;
    DataManager dataManager = DataManager.getInstance();
    CountDownTimer countDownTimer;
    Realm realm;
    Context context;
    private boolean isCountDown;

    private SymptomManager() {

    }

    public static SymptomManager getInstance() {
        return symptomManager;
    }

    public void init(Context context, Realm realm) {
        this.context = context;
//        dataManager.init(context);
        this.realm = realm;
    }

    public void manageSymptomInput(double input) {
//        final Symptom symptom = new Symptom();
        dataManager.getLast30MinutesActivity();
        if (symptomInputList.size() == 0) {
//            timeStamp = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.UK).format(new Date());
            symptom = new Symptom();
            // A symptom occured. get the weather data and start a timer for ending the registration of a symptom
            dataManager.fetchWeatherData();
            isCountDown = true;

            countDownTimer = new CountDownTimer(15000, 1000) {

                public void onTick(long millisUntilFinished) {
                    // If an exception is caught and it is not a network exception try to get weather again
                    if (isWeatherException) {
                        dataManager.fetchWeatherData();
                    }
                    Log.i(TAG, "seconds remaining: " + millisUntilFinished / 1000);
                }

                public void onFinish() {

                    symptom.setContext(dataManager.getSymptomContext());
                    Log.i(TAG, "Symptom context: " + symptom.getContext());

                    symptom.setIntensity(Float.valueOf(String.valueOf(Collections.max(symptomInputList))));
                    diary = DiaryManager.getInstance().getActiveDiary();
                    symptom.setDiary(diary);

                    saveSymptomInput();
                    isCountDown = false;
                }
            }.start();
//            Log.i(TAG, timeStamp);
        }
        if (isCountDown && symptomInputList.size() < 4) {
            symptomInputList.add(input);
            Log.i(TAG, "  " + symptomInputList.size());
        } else {
            Log.i(TAG, "Measurement completed");
        }
    }


    private void resetSymptomInput() {
        symptomInputList.clear();
        Log.i(TAG, "List cleared");

    }

    private void saveSymptomInput() {
        realm.beginTransaction();
        realm.copyToRealm(symptom);
        realm.commitTransaction();
        resetSymptomInput();
    }

    public List<Symptom> getAllSymptoms() {
        return realm.where(Symptom.class).findAll();
    }

    public List<Symptom> getAllSymptomsRS() {
        return realm.allObjects(Symptom.class);
    }

    public RealmResults<Symptom> getAllSymptomsByActivity(int activityId) {
        return realm.allObjects(Symptom.class).where().equalTo("activityId", Constants.SAVED_ACTIVITIES[activityId]).findAll();
    }

//    public RealmResults<Symptom> getAllSymptomsBicycle() {
//        return realm.allObjects(Symptom.class).where().equalTo("activityIdFloat", (float) Constants.ON_BICYCLE).findAll();
//    }
//
//    public RealmResults<Symptom> getAllSymptomsVehicle() {
//        return realm.allObjects(Symptom.class).where().equalTo("activityIdFloat", (float) Constants.IN_VEHICLE).findAll();
//    }
//
//    public RealmResults<Symptom> getAllSymptomsRunning() {
//        return realm.allObjects(Symptom.class).where().equalTo("activityIdFloat", (float) Constants.RUNNING).findAll();
//    }
//
//    public RealmResults<Symptom> getAllSymptomsStill() {
//        return realm.allObjects(Symptom.class).where().equalTo("activityIdFloat", (float) Constants.STILL).findAll();
//    }

    public ArrayList<LatLng> getSymptomslatLonByType(String type) {
        ArrayList<LatLng> mlist = new ArrayList<>();
        List<Symptom> symptoms;
        symptoms = realm.where(Symptom.class).contains("type", type).findAll();
        for (Symptom s : symptoms) {
            mlist.add(new LatLng(s.getContext().getLatitude(), s.getContext().getLongitude()));
        }
        return mlist;
    }


    public void fillData() {
        realm.beginTransaction();
        realm.clear(Symptom.class);
        realm.clear(MyChartData.class);
        realm.commitTransaction();
        for (int i = 0; i < 1000; i++) {
            realm.beginTransaction();
            int activity = Utils.randInt(0, 4);
            Symptom s = realm.createObject(Symptom.class);
            s.setActivityId(activity);
            s.setActivityIdFloat((float) activity);
            s.setId(i);
            s.setIntensity((float) (Math.random() * 10));
            s.setDuringActivity(Constants.getSavedActivityString(context, Constants.SAVED_ACTIVITIES[activity]));
            realm.commitTransaction();
        }
        realm.beginTransaction();
        float totalSymptoms=(float)getAllSymptoms().size();
        for (int i = 0; i < Constants.SAVED_ACTIVITIES.length; i++) {
            float count = 100*((float) getAllSymptomsByActivity(Constants.SAVED_ACTIVITIES[i]).size())/totalSymptoms;
            MyChartData myChartData = new MyChartData(Constants.getSavedActivityString(context,Constants.SAVED_ACTIVITIES[i]),count,i);
            realm.copyToRealm(myChartData);
        }
        realm.commitTransaction();

    }

}
