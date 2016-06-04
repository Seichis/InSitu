package webinar.pubnub.insitu.managers;

import android.content.Context;
import android.os.CountDownTimer;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.TimeZone;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import webinar.pubnub.insitu.BackgroundService;
import webinar.pubnub.insitu.Constants;
import webinar.pubnub.insitu.R;
import webinar.pubnub.insitu.Utils;
import webinar.pubnub.insitu.maps.HeatmapsDemoActivity;
import webinar.pubnub.insitu.model.Description;
import webinar.pubnub.insitu.model.Diary;
import webinar.pubnub.insitu.model.Medication;
import webinar.pubnub.insitu.model.MyChartData;
import webinar.pubnub.insitu.model.Symptom;
import webinar.pubnub.insitu.model.SymptomContext;

public class SymptomManager implements ISymptomManager {
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

    public Symptom getLastSymptom() {
        return realm.allObjects(Symptom.class).where().findAllSorted("timestamp", Sort.DESCENDING).first();
    }

    public boolean noSymptoms() {
        return realm.where(Symptom.class).findFirst() == null;
    }

    public void init(Context context) {
        this.context = context;

        realm = Realm.getDefaultInstance();
    }

    public void manageSymptomInput(double input) {

        if (symptomInputList.size() == 0) {
            symptom = new Symptom();
            symptom.setError(false);
            symptom.setTimestamp(DateTime.now().getMillis());
            // Add activity
            String s = dataManager.getLast30MinutesActivity();
            if (s.equals(context.getString(R.string.still))) {
                symptom.setActivityId(Constants.STILL);
            } else if (s.equals(context.getString(R.string.walking))) {
                symptom.setActivityId(Constants.WALKING);

            } else if (s.equals(context.getString(R.string.on_foot))) {
                symptom.setActivityId(Constants.WALKING);

            } else if (s.equals(context.getString(R.string.on_bicycle))) {
                symptom.setActivityId(Constants.ON_BICYCLE);

            } else if (s.equals(context.getString(R.string.in_vehicle))) {
                symptom.setActivityId(Constants.IN_VEHICLE);

            } else if (s.equals(context.getString(R.string.unknown))) {
                symptom.setActivityId(Constants.STILL);

            } else if (s.equals(context.getString(R.string.tilting))) {
                symptom.setActivityId(Constants.STILL);

            } else if (s.equals(context.getString(R.string.running))) {
                symptom.setActivityId(Constants.RUNNING);

            } else {
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
                    float maxInput = Float.valueOf(String.valueOf(Collections.max(symptomInputList)));
                    symptom.setIntensity(getTimeToVAS(maxInput));
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
        ChartManager.getInstance().updatePieChartDataByActivityByDay(DateTime.now().getMillis());
        BackgroundService.getInstance().createOrUpdateBubble();
        ChartManager.getInstance().updateBubbleChartByDay(ChartManager.BY_ACTIVITIES, ChartManager.INTENSITY, DateTime.now().getMillis());
        if (HeatmapsDemoActivity.getInstance() != null) {
            HeatmapsDemoActivity.getInstance().updateMapByOptions();
        }

    }


    @Override
    public RealmResults<Symptom> getAllSymptomsByDay(long date) {
        long yesterdayStart = Utils.getDayStart(date, 1);
        long tomorrowStart = Utils.getDaysEnd(date);
        Log.i(TAG, "yest  " + yesterdayStart + "today  " + tomorrowStart);
        return getAllSymptomsByRange(yesterdayStart, tomorrowStart);
    }


    @Override
    public RealmResults<Symptom> getAllSymptoms() {
        return realm.where(Symptom.class).findAll();
    }

    @Override
    public RealmResults<Symptom> getAllSymptomsByRange(long from, long until) {
        long yesterdayStart = Utils.getDayStart(from, 1);
        long tomorrowStart = Utils.getDaysEnd(until);
        return realm.where(Symptom.class).greaterThanOrEqualTo("timestamp", yesterdayStart).lessThanOrEqualTo("timestamp", tomorrowStart).findAll();

    }


    @Override
    public RealmResults<Symptom> getAllSymptomsByActivity(int activityId) {
        return realm.allObjects(Symptom.class).where().equalTo("activityId", Constants.SAVED_ACTIVITIES[activityId]).findAll();
    }

    @Override
    public float getMeanIntensityByDay(long date) {
        return 0;
    }

    @Override
    public float getMeanIntensityByRange(long from, long until) {
        return 0;
    }

    @Override
    public float getMeanDistressByDay(long date) {
        return 0;
    }

    @Override
    public float getMeanDistressByRange(long from, long until) {
        return 0;
    }

    @Override
    public RealmResults<Symptom> getAllSymptomsByActivityByDay(int activityId, long date) {
        return getAllSymptomsByDay(date).where().equalTo("activityId", Constants.SAVED_ACTIVITIES[activityId]).findAll();
    }

    @Override
    public RealmResults<Symptom> getAllSymptomsByActivityByRange(int activityId, long from, long until) {
        return getAllSymptomsByRange(from, until).where().equalTo("activityId", Constants.SAVED_ACTIVITIES[activityId]).findAll();
    }

    public RealmResults<Symptom> getAllSymptomsByWeatherConditionByRange(String weatherCondition, long from, long until) {
        return getAllSymptomsByRange(from, until).where().equalTo("context.weatherCondition", weatherCondition).findAll();
    }

    public RealmResults<Symptom> getAllSymptomsByWeatherConditionByDay(String weatherCondition, long date) {
        return getAllSymptomsByDay(date).where().equalTo("context.weatherCondition", weatherCondition).findAll();
    }

    public RealmResults<Symptom> getAllSymptomsByBodyPartByRange(String bodyPart, long from, long until) {
        return getAllSymptomsByRange(from, until).where().equalTo("description.bodyPart", bodyPart).findAll();
    }

    public RealmResults<Symptom> getAllSymptomsByBodyPartByDay(String bodyPart, long date) {
        return getAllSymptomsByDay(date).where().equalTo("description.bodyPart", bodyPart).findAll();
    }

    public RealmResults<Symptom> getAllSymptomsPerDayByRange(String day, long from, long until) {
        return getAllSymptomsByRange(from, until).where().equalTo("day", day).findAll();
    }

    public RealmResults<Symptom> getAllSymptomsPerHourByRange(String hour, long from, long until) {
        return getAllSymptomsByRange(from, until).where().equalTo("hour", hour).findAll();
    }

    public RealmResults<Symptom> getAllSymptomsPerMonthByRange(String month, long from, long until) {
        return getAllSymptomsByRange(from, until).where().equalTo("month", month).findAll();
    }

    public RealmResults<Symptom> getAllSymptomsPerYearByRange(String year, long from, long until) {
        return getAllSymptomsByRange(from, until).where().equalTo("year", year).findAll();
    }

    public RealmResults<Symptom> getAllSymptomsPerWeekByRange(String week, long from, long until) {
        return getAllSymptomsByRange(from, until).where().equalTo("week", week).findAll();
    }


    public ArrayList<LatLng> getSymptomsLatLon(long from, long until) {
        ArrayList<LatLng> latLngHashMap = new ArrayList<>();
        for (Symptom s : getAllSymptomsByRange(from, until)) {
            if (s.getContext() != null) {
                latLngHashMap.add(new LatLng(s.getContext().getLatitude(), s.getContext().getLongitude()));
            }
        }
        return latLngHashMap;
    }

    public ArrayList<LatLng> getSymptomsLatLonByDistress(long from, long until) {
        ArrayList<LatLng> latLngHashMap = new ArrayList<>();
        for (Symptom s : getAllSymptomsByRange(from, until)) {
            if (s.getDescription() != null) {
                if (s.getDescription().getDistress() > 0) {
                    latLngHashMap.add(new LatLng(s.getContext().getLatitude(), s.getContext().getLongitude()));
                }
            }
        }
        return latLngHashMap;
    }


    public void fillData() {
        String[] cancerDrugs = context.getResources().getStringArray(R.array.CancerDrugs);

        realm.beginTransaction();
        realm.clear(Symptom.class);
        realm.clear(MyChartData.class);
        realm.commitTransaction();
        realm.beginTransaction();
        DateTime now = DateTime.now(DateTimeZone.forTimeZone(TimeZone.getDefault()));

        for (int i = 0; i < 1500; i++) {
            int activity = Utils.randInt(0, 4);
            Symptom s = new Symptom();
            s.setActivityId(activity);
            long timestamp = now.minusHours(Utils.randInt(0, 15000)).getMillis();
            s.setTimestamp(timestamp);
            s.setId(i);
            s.setHour(Utils.getHour(timestamp));
            s.setDay(Utils.getDay(timestamp));
            s.setWeek(Utils.getWeek(timestamp));
            s.setMonth(Constants.months[Integer.parseInt(Utils.getMonth(timestamp)) - 1]);
            s.setYear(Utils.getYear(timestamp));
            float intensity = (float) Utils.randInt(0, 10);
            s.setIntensity(intensity);
            long medicationTimestamp = now.minusHours(Utils.randInt(0, 15000)).getMillis();

            Medication medication = new Medication();
            medication.setMedicationName(cancerDrugs[Utils.randInt(0, cancerDrugs.length - 1)]);
            medication.setTimestamp(medicationTimestamp);
            medication.setHour(Utils.getHour(medicationTimestamp));
            medication.setDay(Utils.getDay(medicationTimestamp));
            medication.setWeek(Utils.getWeek(medicationTimestamp));
            medication.setMonth(Constants.months[Integer.parseInt(Utils.getMonth(medicationTimestamp)) - 1]);
            medication.setYear(Utils.getYear(medicationTimestamp));
            medication.setAmount(Utils.randInt(1, 3));

            // Adding description
            Description description = new Description();
            String[] bp = context.getResources().getStringArray(R.array.BodyParts);
            description.setBodyPart(bp[Utils.randInt(0, bp.length - 1)]);
            if ((Utils.randInt(2, 1000) % 2) == 0) {
                long painkillerTimestamp = timestamp + DateTime.now().plus(Utils.randInt(500000, 7200000)).getMillis();
                description.setHourPainkiller(Utils.getHour(painkillerTimestamp));
                description.setDayPainkiller(Utils.getDay(painkillerTimestamp));
                description.setWeekPainkiller(Utils.getWeek(painkillerTimestamp));

                description.setMonthPainkiller(Constants.months[Integer.parseInt(Utils.getMonth(painkillerTimestamp)) - 1]);
                description.setYearPainkiller(Utils.getYear(painkillerTimestamp));
                description.setDatePainkillerConsumption(Utils.randInt(1, 3));
            }
            description.setDistress(intensity + (float) Utils.randInt(0, (int) (10f - intensity)));
            s.setDescription(description);

            // Adding Context
            float baseLat = 55.6761f;
            float baseLon = 12.5683f;
            SymptomContext sc = new SymptomContext();
            float minX = 0.001f;
            float maxX = 0.1f;

            Random rand = new Random();

            float finalX = rand.nextFloat() * (maxX - minX) + minX;
            float finalY = rand.nextFloat() * (maxX - minX) + minX;

            if ((Utils.randInt(2, 1000) % 2) == 0) {
                sc.setLongitude(baseLon - finalX / 2);
            } else {
                sc.setLongitude(baseLon + finalX / 2);
            }
            if ((Utils.randInt(2, 1000) % 2) == 0) {
                sc.setLatitude(baseLat - finalY / 2);
            } else {
                sc.setLatitude(baseLat + finalY / 2);

            }

            sc.setTemperature((float) Utils.randInt(-15, 30) + rand.nextFloat());
            String[] weatherCondition = context.getResources().getStringArray(R.array.WeatherConditions);
            sc.setWeatherCondition(weatherCondition[Utils.randInt(0, weatherCondition.length - 1)]);
            s.setContext(sc);
            realm.copyToRealm(s);
        }
        realm.commitTransaction();
//        ChartManager.getInstance().updatePieChartDataByActivityByRange(DateTime.now().minusDays(1).getMillis(), DateTime.now().getMillis());
        ChartManager.getInstance().updatePieChartDataByActivityByDay(DateTime.now().getMillis());
//        ChartManager.getInstance().updateBubbleChartDataByDay(DateTime.now().getMillis());
//        ChartManager.getInstance().updateBubbleChartByRange(ChartManager.BY_ACTIVITIES,ChartManager.INTENSITY,DateTime.now().minusDays(5).getMillis(), DateTime.now().getMillis());


    }


    @Override
    public Symptom getSymptomById(int id) {
        return realm.where(Symptom.class).equalTo("id", id).findFirst();
    }


    public float getTimeToVAS(float time) {

        return Math.max(0, Math.min(time * 2.5f / 1000, 10));

    }

    public Calendar getMinDate() {
        Calendar tmp = Calendar.getInstance();
        tmp.setTimeInMillis(realm.allObjects(Symptom.class).where().min("timestamp").longValue());
        return tmp;
    }

    public RealmResults<Symptom> getTodaySymptomsWithoutDescription() {
        return getAllSymptomsByDay(DateTime.now().getMillis()).where().isNull("description").findAll();
    }

    public void addMedicationDate(Symptom s, long date) {
        realm.beginTransaction();
        Description description;

        if (s.getDescription() == null) {
            description = realm.createObject(Description.class);
            description.setDatePainkillerConsumption(date);
            s.setDescription(description);
        } else {
            s.getDescription().setDatePainkillerConsumption(date);

        }
        realm.commitTransaction();
    }

    public void addMedicationDescription(Symptom s, String s1) {
        realm.beginTransaction();
        Description description;

        if (s.getDescription() == null) {
            description = realm.createObject(Description.class);
            description.setPainkiller(s1);
            s.setDescription(description);
        } else {
            s.getDescription().setPainkiller(s1);
        }
        realm.commitTransaction();
    }

    public void addBodyPartDescription(Symptom s, String s1) {
        realm.beginTransaction();
        Description description;

        if (s.getDescription() == null) {
            description = realm.createObject(Description.class);
            description.setBodyPartDetails(s1);
            s.setDescription(description);
        } else {
            s.getDescription().setBodyPartDetails(s1);
        }
        realm.commitTransaction();
    }

    public void addBodyPart(Symptom s, String s1) {
        realm.beginTransaction();
        Description description;

        if (s.getDescription() == null) {
            description = realm.createObject(Description.class);
            description.setBodyPart(s1);
            s.setDescription(description);
        } else {
            s.getDescription().setBodyPart(s1);
        }
        realm.commitTransaction();
    }

    public void addNonDrugTechnique(Symptom s, String s1) {
        realm.beginTransaction();
        Description description;

        if (s.getDescription() == null) {
            description = realm.createObject(Description.class);
            description.setNonDrugTechniques(s1);
            s.setDescription(description);
        } else {
            s.getDescription().setNonDrugTechniques(s1);
        }
        realm.commitTransaction();
    }

    public void addDistress(Symptom s, float i) {
        realm.beginTransaction();
        Description description;

        if (s.getDescription() == null) {
            description = realm.createObject(Description.class);
            description.setDistress(i);
            s.setDescription(description);
        } else {
            s.getDescription().setDistress(i);
        }
        realm.commitTransaction();
    }

    public void addOtherSymptoms(Symptom s, String s1) {
        realm.beginTransaction();
        Description description;

        if (s.getDescription() == null) {
            description = realm.createObject(Description.class);
            description.setOtherSymptoms(s1);
            s.setDescription(description);
        } else {
            s.getDescription().setOtherSymptoms(s1);
        }
        realm.commitTransaction();
    }


    public void addNewIntensity(Symptom s, float i) {
        realm.beginTransaction();
        s.setDeltaIntensity(s.getIntensity() - i);
        s.setIntensity(i);
        realm.commitTransaction();
    }
}
