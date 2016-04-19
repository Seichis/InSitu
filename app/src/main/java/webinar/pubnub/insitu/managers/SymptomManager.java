package webinar.pubnub.insitu.managers;

import android.content.Context;
import android.os.CountDownTimer;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

import io.realm.Realm;
import webinar.pubnub.insitu.model.Diary;
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

//            SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd", Locale.UK);


//            dataManager.movesStorylineDay(format.format(new Date()),true);


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

                    symptom.setIntensity(Collections.max(symptomInputList));
                    diary = DiaryManager.getInstance().getActiveDiary();
                    symptom.setDiary(diary);

                    int symptomT = symptomInputList.size() - 1;
                    TreeMap<Integer, String> symptomTypes = diary.getSymptomTypes();
                    if (!symptomTypes.isEmpty()) {
                        for (TreeMap.Entry<Integer, String> st : symptomTypes.entrySet()) {
                            int position = st.getKey();
                            String type = st.getValue();
                            Log.i(TAG, "symptom type" + type);

                            if (position == symptomT) {
                                Log.i(TAG, "symptom type" + position);
                                symptom.setSymptomType(type);
                            }
                        }
                    }
                    saveSymptomInput();


//                    //TODO save the completed symptom in the db
//                    if (symptom.isValid()) {
//                        Log.i(TAG, "Symptom saved");
//                    }
                    isCountDown = false;
                }
            }.start();
//            Log.i(TAG, timeStamp);
        }
        if (symptomInputList.size() < 4) {
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

    public HashMap<String, List<Symptom>> getSymptomsByDiary(Diary diary) {
        TreeMap<Integer, String> symptomTypes = diary.getSymptomTypes();
        HashMap<String, List<Symptom>> symptomsMap = new HashMap<>();
        if (symptomTypes != null) {
            for (String type : symptomTypes.values()) {
                symptomsMap.put(type, realm.where(Symptom.class).contains("type", type).findAll());
            }
        }
        return symptomsMap;
    }

    public ArrayList<LatLng> getSymptomslatLonByType(String type) {
        ArrayList<LatLng> mlist = new ArrayList<>();
        List<Symptom> symptoms;
        symptoms = realm.where(Symptom.class).contains("type", type).findAll();
        for (Symptom s : symptoms) {
            mlist.add(s.getContext().getLatLng());
        }
        return mlist;
    }


}
