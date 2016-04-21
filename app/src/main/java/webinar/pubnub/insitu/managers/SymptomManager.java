package webinar.pubnub.insitu.managers;

import android.content.Context;
import android.content.res.Resources;
import android.os.CountDownTimer;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import webinar.pubnub.insitu.Constants;
import webinar.pubnub.insitu.R;
import webinar.pubnub.insitu.Utils;
import webinar.pubnub.insitu.model.Diary;
import webinar.pubnub.insitu.model.MyChartData;
import webinar.pubnub.insitu.model.Symptom;

public class SymptomManager {
    static SymptomManager symptomManager = new SymptomManager();
    static String TAG = "SymptomManager";
    List<Double> symptomInputList = new ArrayList<>();
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

    public void init(Context context) {
        this.context = context;
//        dataManager.init(context);
        realm = Realm.getDefaultInstance();
    }

    public void manageSymptomInput(double input){

        if (symptomInputList.size() == 0) {
//            timeStamp = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.UK).format(new Date());
            symptom = new Symptom();
            // Add activity
            String s = dataManager.getLast30MinutesActivity();
            if (s.equals(context.getString(R.string.still))){
                symptom.setActivityId(Constants.STILL);
            }else if(s.equals(context.getString(R.string.walking))){
                symptom.setActivityId(Constants.WALKING);

            }else if(s.equals(context.getString(R.string.on_foot))){
                symptom.setActivityId(Constants.WALKING);

            }else if(s.equals(context.getString(R.string.on_bicycle))){
                symptom.setActivityId(Constants.ON_BICYCLE);

            }else if(s.equals(context.getString(R.string.in_vehicle))){
                symptom.setActivityId(Constants.IN_VEHICLE);

            }else if(s.equals(context.getString(R.string.unknown))){
                symptom.setActivityId(Constants.STILL);

            }else if(s.equals(context.getString(R.string.tilting))){
                symptom.setActivityId(Constants.STILL);

            }else if(s.equals(context.getString(R.string.running))){
                symptom.setActivityId(Constants.RUNNING);

            }else{
                symptom.setActivityId(Constants.STILL);

            }
            if (realm.where(Symptom.class).equalTo("id", 1).findFirst() == null) {
                symptom.setId(1);
            } else {
                long maxId = realm.allObjects(Symptom.class).max("id").longValue();
                symptom.setId(maxId + 1);
            }
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
        // Update the data for the charts
        ChartManager.getInstance().updateChartData();
    }

    public List<Symptom> getAllSymptoms() {
        return realm.where(Symptom.class).findAll();
    }

    public RealmResults<Symptom> getAllSymptomsByActivity(int activityId) {
        return realm.allObjects(Symptom.class).where().equalTo("activityId", Constants.SAVED_ACTIVITIES[activityId]).findAll();
    }

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
//        realm.beginTransaction();
//
//        for (int i = 0; i < 50; i++) {
//            int activity = Utils.randInt(0, 4);
//            Symptom s = new Symptom();
//            s.setActivityId(activity);
//            s.setId(i);
//            s.setIntensity((float) (Math.random() * 10));
//            realm.copyToRealm(s);
//        }
//        realm.commitTransaction();
//
//        realm.beginTransaction();
//        float totalSymptoms = (float) getAllSymptoms().size();
//        for (int i = 0; i < Constants.SAVED_ACTIVITIES.length; i++) {
//            float count = 100 * ((float) getAllSymptomsByActivity(Constants.SAVED_ACTIVITIES[i]).size()) / totalSymptoms;
//            MyChartData myChartData = new MyChartData(Constants.getSavedActivityString(context, Constants.SAVED_ACTIVITIES[i]), count, i);
//            realm.copyToRealm(myChartData);
//        }
//        realm.commitTransaction();

    }

    public Symptom getSymptomById(int id) {
        return realm.where(Symptom.class).equalTo("id", id).findFirst();
    }


}
