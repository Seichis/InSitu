package webinar.pubnub.insitu.managers;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;

import com.github.mikephil.charting.charts.BarLineChartBase;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.ChartData;
import com.github.mikephil.charting.formatter.PercentFormatter;

import java.util.HashMap;
import java.util.Map;

import io.realm.Realm;
import io.realm.RealmResults;
import webinar.pubnub.insitu.Constants;
import webinar.pubnub.insitu.R;
import webinar.pubnub.insitu.fragments.ExplorationFragment;
import webinar.pubnub.insitu.fragments.HomeFragment;
import webinar.pubnub.insitu.model.Description;
import webinar.pubnub.insitu.model.MyBubbleChartData;
import webinar.pubnub.insitu.model.MyChartData;
import webinar.pubnub.insitu.model.Symptom;

/**
 * Created by Konstantinos Michail on 4/20/2016.
 */
public class ChartManager implements IChartManager {
    public static final int INTENSITY = 0;
    public static final int DISTRESS = 1;
    public static final int BY_ACTIVITIES = 0;
    public static final int BY_WEATHER_CONDITION = 1;
    public static final int BY_BODY_PART = 2;
    private final static String TAG = "ChartManager";
    static ChartManager chartManager = new ChartManager();
    SymptomManager symptomManager;
    Context context;
    Realm realm;
    RealmResults<Symptom> symptoms;

    public static ChartManager getInstance() {
        return chartManager;
    }

    public void init(Context context) {
        this.context = context;
        realm = Realm.getDefaultInstance();
        symptomManager = SymptomManager.getInstance();
    }

    @Override
    public void setup(Chart<?> chart) {


        // no description text
        chart.setDescription("");
        chart.setNoDataTextDescription("You need to provide data for the chart.");

        // enable touch gestures
        chart.setTouchEnabled(true);

        if (chart instanceof BarLineChartBase) {

            BarLineChartBase mChart = (BarLineChartBase) chart;

            mChart.setDrawGridBackground(false);

            // enable scaling and dragging
            mChart.setDragEnabled(true);
            mChart.setScaleEnabled(true);

            // if disabled, scaling can be done on x- and y-axis separately
            mChart.setPinchZoom(false);

            YAxis leftAxis = mChart.getAxisLeft();
            leftAxis.removeAllLimitLines(); // reset all limit lines to avoid overlapping lines
            leftAxis.setTextSize(8f);
            leftAxis.setTextColor(Color.DKGRAY);
            leftAxis.setValueFormatter(new PercentFormatter());

            XAxis xAxis = mChart.getXAxis();
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setTextSize(8f);
            xAxis.setTextColor(Color.DKGRAY);

            mChart.getAxisRight().setEnabled(false);
        }
    }

    @Override
    public void styleData(ChartData data) {
        data.setValueTextSize(8f);
        data.setValueTextColor(Color.DKGRAY);
        data.setValueFormatter(new PercentFormatter());
    }

    @Override
    public void updatePieChartData() {
        realm.beginTransaction();
        realm.clear(MyChartData.class);
        symptoms = symptomManager.getAllSymptoms();
        float totalSymptoms = getSymptomsCount();
        for (int i = 0; i < Constants.SAVED_ACTIVITIES.length; i++) {
            float count = 100 * ((float) symptomManager.getAllSymptomsByActivity(Constants.SAVED_ACTIVITIES[i]).size()) / totalSymptoms;
            if (count == 0f) {
                continue;
            }
            MyChartData myChartData = new MyChartData(Constants.getSavedActivityString(context, Constants.SAVED_ACTIVITIES[i]), count, i);
            realm.copyToRealm(myChartData);
        }
        realm.commitTransaction();
        if (HomeFragment.getInstance() != null) {
            HomeFragment.getInstance().updatePiechart();
        }
    }


    public RealmResults<MyChartData> getPieChartData() {
        return realm.allObjects(MyChartData.class);
    }

    public void updatePieChartDataByActivityByDay(long date) {
        realm.beginTransaction();
        realm.clear(MyChartData.class);
        symptoms = symptomManager.getAllSymptomsByDay(date);

        float totalSymptoms = getSymptomsCount();
        for (int i = 0; i < Constants.SAVED_ACTIVITIES.length; i++) {
            float count = 100 * ((float) symptomManager.getAllSymptomsByActivityByDay(Constants.SAVED_ACTIVITIES[i], date).size()) / totalSymptoms;
            if (count == 0f) {
                continue;
            }
            MyChartData myChartData = new MyChartData(Constants.getSavedActivityString(context, Constants.SAVED_ACTIVITIES[i]), count, i);
            realm.copyToRealm(myChartData);
        }
        realm.commitTransaction();
        if (HomeFragment.getInstance() != null) {
            HomeFragment.getInstance().updatePiechart();
        }
    }

    public float getSymptomsCount() {
        return (symptoms == null) ? 0f : (float) this.symptoms.size();
    }

    public void updatePieChartDataByActivityByRange(long from, long until) {
        realm.beginTransaction();
        realm.clear(MyChartData.class);
        symptoms = symptomManager.getAllSymptomsByRange(from, until);
        float totalSymptoms = getSymptomsCount();
        for (int i = 0; i < Constants.SAVED_ACTIVITIES.length; i++) {
            float count = 100 * ((float) symptomManager.getAllSymptomsByActivityByRange(Constants.SAVED_ACTIVITIES[i], from, until).size()) / totalSymptoms;

            if (count == 0f) {
                continue;
            }
            MyChartData myChartData = new MyChartData(Constants.getSavedActivityString(context, Constants.SAVED_ACTIVITIES[i]), count, i);
            realm.copyToRealm(myChartData);
        }
        realm.commitTransaction();
        if (HomeFragment.getInstance() != null) {
            HomeFragment.getInstance().updatePiechart();
        }
    }

    public void updateBubbleChartDataByActivityByDay(long date) {

        realm.beginTransaction();
        realm.clear(MyBubbleChartData.class);
        symptoms = symptomManager.getAllSymptomsByDay(date);
        float totalSymptoms = (float) symptoms.size();

        for (int i = 0; i < Constants.SAVED_ACTIVITIES.length; i++) {
            RealmResults<Symptom> symptomsPerActivity = symptomManager.getAllSymptomsByActivityByDay(Constants.SAVED_ACTIVITIES[i], date);
            float count = (float) symptomsPerActivity.size();
            if (count > 0) {
                MyBubbleChartData data = new MyBubbleChartData();
                float meanIntensity = (float) symptomsPerActivity.average("intensity");
                data.setBubbleSize(count / totalSymptoms);
                data.setClassId(i);
                data.setClassName(Constants.getSavedActivityString(context, Constants.SAVED_ACTIVITIES[i]));
                data.setValue(meanIntensity);
                realm.copyToRealm(data);
            }
        }
        realm.commitTransaction();
        if (ExplorationFragment.getInstance() != null) {
            ExplorationFragment.getInstance().updateBubbleChart();
        }
    }

    public void updateBubbleChartDataByActivityByRange(long from, long until) {
        realm.beginTransaction();
        realm.clear(MyBubbleChartData.class);
        symptoms = symptomManager.getAllSymptomsByRange(from, until);
        float totalSymptoms = getSymptomsCount();
        for (int i = 0; i < Constants.SAVED_ACTIVITIES.length; i++) {
            RealmResults<Symptom> symptomsPerActivity = symptomManager.getAllSymptomsByActivityByRange(Constants.SAVED_ACTIVITIES[i], from, until);
            float count = (float) symptomsPerActivity.size();
            if (count > 0) {
                MyBubbleChartData data = new MyBubbleChartData();
                float meanIntensity = (float) symptomsPerActivity.average("intensity");
                data.setBubbleSize(count / totalSymptoms);
                data.setClassId(i);
                data.setClassName(Constants.getSavedActivityString(context, Constants.SAVED_ACTIVITIES[i]));
                data.setValue(meanIntensity);
                realm.copyToRealm(data);
            }
        }
        realm.commitTransaction();
        if (ExplorationFragment.getInstance() != null) {
            ExplorationFragment.getInstance().updateBubbleChart();
        }
    }

    public void updateBubbleChartDataByWeatherByRange(long from, long until) {
        realm.beginTransaction();
        realm.clear(MyBubbleChartData.class);
        symptoms = symptomManager.getAllSymptomsByRange(from, until);
        float totalSymptoms = getSymptomsCount();
        String[] cond = context.getResources().getStringArray(R.array.WeatherConditions);
        for (int i = 0; i < cond.length; i++) {
            RealmResults<Symptom> symptomsPerCondition = symptomManager.getAllSymptomsByWeatherConditionByRange(cond[i], from, until);
            float count = (float) symptomsPerCondition.size();
            if (count > 0) {
                MyBubbleChartData data = new MyBubbleChartData();

//                float t=0f;
//                Log.i(TAG, "symptoms count" + symptomsPerCondition.size());
//
//                for (Symptom s :symptomsPerCondition){
//                    t+=s.getContext().getTemperature();
//                }
//                float meantemperature = t/(float)symptoms.size() ;

                float meantemperature = (float) symptomsPerCondition.average("intensity");

                data.setBubbleSize(count / totalSymptoms);
                data.setClassId(i);
                data.setClassName(cond[i]);
                data.setValue(meantemperature);
                realm.copyToRealm(data);
            }
        }
        realm.commitTransaction();
        if (ExplorationFragment.getInstance() != null) {
            ExplorationFragment.getInstance().updateBubbleChart();
        }
    }


    public void updateBubbleChartDataByWeatherByDay(long date) {

        realm.beginTransaction();
        realm.clear(MyBubbleChartData.class);
        symptoms = symptomManager.getAllSymptomsByDay(date);
        float totalSymptoms = (float) symptoms.size();

        String[] cond = context.getResources().getStringArray(R.array.WeatherConditions);
        for (int i = 0; i < cond.length; i++) {
            RealmResults<Symptom> symptomsPerCondition = symptomManager.getAllSymptomsByWeatherConditionByDay(cond[i], date);
            float count = (float) symptomsPerCondition.size();
            if (count > 0) {
                MyBubbleChartData data = new MyBubbleChartData();
//                float t=0f;
//                for (Symptom s :symptomsPerCondition){
//                    t+=s.getContext().getTemperature();
//                }
//
//                float meantemperature = t/(float)symptoms.size() ;
                float meantemperature = (float) symptomsPerCondition.average("intensity");
                data.setBubbleSize(count / totalSymptoms);
                data.setClassId(i);
                data.setClassName(cond[i]);
                data.setValue(meantemperature);
                realm.copyToRealm(data);
            }
        }
        realm.commitTransaction();
        if (ExplorationFragment.getInstance() != null) {
            ExplorationFragment.getInstance().updateBubbleChart();
        }
    }

    public RealmResults<MyBubbleChartData> getBubbleChartData() {
        return realm.allObjects(MyBubbleChartData.class);
    }


    public void updateBubbleChartByRange(int by, int what, long from, long until) {
        String[] conditions = context.getResources().getStringArray(R.array.WeatherConditions);
        String[] bodyParts=context.getResources().getStringArray(R.array.BodyParts);
        realm.beginTransaction();
        realm.clear(MyBubbleChartData.class);
        HashMap<String, RealmResults<Symptom>> symptomsBy = new HashMap<>();
        symptoms = symptomManager.getAllSymptomsByRange(from, until);
        float totalSymptoms = getSymptomsCount();
        int j=0;
        switch (by) {
            case BY_ACTIVITIES:
                for (int i = 0; i < Constants.SAVED_ACTIVITIES.length; i++) {
                    RealmResults<Symptom> symptomsPerActivity = symptomManager.getAllSymptomsByActivityByRange(Constants.SAVED_ACTIVITIES[i], from, until);

                    if (symptomsPerActivity.size()== 0) {
                        continue;
                    }
                    String id=String.valueOf(j);

                    j++;
                    symptomsBy.put(id+";"+Constants.getSavedActivityString(context, Constants.SAVED_ACTIVITIES[i]), symptomsPerActivity);

                    Log.i(TAG,"activities"+id+" hash map "+symptomsBy.size() );

                }
                break;
            case BY_BODY_PART:
                for (int i = 0; i < bodyParts.length; i++) {
                    RealmResults<Symptom> symptomsPerBodyPart= symptomManager.getAllSymptomsByBodyPartByRange(bodyParts[i], from, until);

                    if (symptomsPerBodyPart.size() == 0) {
                        continue;
                    }
                    String id=String.valueOf(j);

                    j++;
                    symptomsBy.put(id+";"+bodyParts[i], symptomsPerBodyPart);

                    Log.i(TAG,"body parts "+id+" hash map "+symptomsBy.size() );

                }
                break;
            case BY_WEATHER_CONDITION:
                for (int i = 0; i < conditions.length; i++) {
                    RealmResults<Symptom> symptomsPerCondition = symptomManager.getAllSymptomsByWeatherConditionByRange(conditions[i], from, until);

                    if (symptomsPerCondition.size() == 0) {
                        continue;
                    }
                    String id=String.valueOf(j);
                    j++;
                    symptomsBy.put(id+";"+conditions[i], symptomsPerCondition);

                    Log.i(TAG,"weather"+id+" hashmap "+symptomsBy.size() );

                }
                break;
            default:
                break;
        }
        MyBubbleChartData data;
        switch (what) {
            case INTENSITY:
                data = new MyBubbleChartData();
                for (Map.Entry<String, RealmResults<Symptom>> entry : symptomsBy.entrySet()) {
                    float count = (float) entry.getValue().size();
                    float meanValue=(float)entry.getValue().average("intensity");
                    String [] classInfo=entry.getKey().split(";");

                    data.setBubbleSize(count / totalSymptoms);

                    data.setClassId(Integer.parseInt(classInfo[0]));
                    data.setClassName(classInfo[1]);
                    data.setValue(meanValue);
                    realm.copyToRealm(data);
                }
                realm.commitTransaction();
                if (ExplorationFragment.getInstance() != null) {
                    ExplorationFragment.getInstance().updateBubbleChart();
                }
                break;
            case DISTRESS:
                data = new MyBubbleChartData();
                for (Map.Entry<String, RealmResults<Symptom>> entry : symptomsBy.entrySet()) {
                    float count = (float) entry.getValue().size();
                    float t = 0f;
                    for (Symptom s : entry.getValue()) {
                        t += s.getDescription().getDistress();
                    }
                    float meanValue = t / count;
                    String [] classInfo=entry.getKey().split(";");

                    data.setBubbleSize(count / totalSymptoms);

                    data.setClassId(Integer.parseInt(classInfo[0]));
                    data.setClassName(classInfo[1]);
                    data.setValue(meanValue);
                    realm.copyToRealm(data);
                }
                realm.commitTransaction();
                if (ExplorationFragment.getInstance() != null) {
                    ExplorationFragment.getInstance().updateBubbleChart();
                }
                break;
            default:
                break;
        }
    }

    public void updateBubbleChartByDay(int by, int what, long date) {
        String[] conditions = context.getResources().getStringArray(R.array.WeatherConditions);
        String[] bodyParts=context.getResources().getStringArray(R.array.BodyParts);
        realm.beginTransaction();
        realm.clear(MyBubbleChartData.class);
        HashMap<String, RealmResults<Symptom>> symptomsBy = new HashMap<>();
        symptoms = symptomManager.getAllSymptomsByDay(date);
        float totalSymptoms = getSymptomsCount();
        int j = 0;
        switch (by) {
            case BY_ACTIVITIES:
                for (int i = 0; i < Constants.SAVED_ACTIVITIES.length; i++) {
                    RealmResults<Symptom> symptomsPerActivity = symptomManager.getAllSymptomsByActivityByDay(Constants.SAVED_ACTIVITIES[i], date);

                    if (symptomsPerActivity.isEmpty()) {
                        continue;
                    }
                    String id=String.valueOf(j);

                    j++;

                    symptomsBy.put(id+";"+Constants.getSavedActivityString(context, Constants.SAVED_ACTIVITIES[i]), symptomsPerActivity);

                }
                break;
            case BY_BODY_PART:


                for (int i = 0; i < bodyParts.length; i++) {
                    RealmResults<Symptom> symptomsPerBodyPart= symptomManager.getAllSymptomsByBodyPartByDay(bodyParts[i],date);

                    if (symptomsPerBodyPart.isEmpty()) {
                        continue;
                    }

                    String id=String.valueOf(j);

                    j++;
                    symptomsBy.put(id+";"+bodyParts[i], symptomsPerBodyPart);
                    Log.i(TAG,"body parts "+id+" hash map "+symptomsBy.size() );

                }
                break;
            case BY_WEATHER_CONDITION:

                for (int i = 0; i < conditions.length; i++) {

                    RealmResults<Symptom> symptomsPerCondition = symptomManager.getAllSymptomsByWeatherConditionByDay(conditions[i], date);

                    if (symptomsPerCondition.isEmpty()) {
                        continue;
                    }

                    String id=String.valueOf(j);

                    j++;
                    symptomsBy.put(id+";"+conditions[i], symptomsPerCondition);
                    Log.i(TAG,"weather"+id+" hashmap "+symptomsBy.size() );

                }
                break;
            default:
                break;
        }
        MyBubbleChartData data;
        switch (what) {
            case INTENSITY:
                data = new MyBubbleChartData();
                for (Map.Entry<String, RealmResults<Symptom>> entry : symptomsBy.entrySet()) {
                    float count = (float) entry.getValue().size();
                    float meanValue=(float)entry.getValue().average("intensity");
                    String [] classInfo=entry.getKey().split(";");

                    data.setBubbleSize(count / totalSymptoms);

                    data.setClassId(Integer.parseInt(classInfo[0]));
                    data.setClassName(classInfo[1]);
                    data.setValue(meanValue);
                    realm.copyToRealm(data);
                }
                realm.commitTransaction();
                if (ExplorationFragment.getInstance() != null) {
                    ExplorationFragment.getInstance().updateBubbleChart();
                }
                break;
            case DISTRESS:
                data = new MyBubbleChartData();
                for (Map.Entry<String, RealmResults<Symptom>> entry : symptomsBy.entrySet()) {
                    RealmResults<Symptom> filteredSymptoms=entry.getValue().where().greaterThan("description.distress",0f).findAll();
                    if (filteredSymptoms.isEmpty()){
                        continue;
                    }
                    float count = (float)filteredSymptoms.size();
                    float t = 0f;
                    for (Symptom s : filteredSymptoms) {
                        t += s.getDescription().getDistress();
                    }
                    float meanValue = t / count;
                    String [] classInfo=entry.getKey().split(";");

                    data.setBubbleSize(count / totalSymptoms);

                    data.setClassId(Integer.parseInt(classInfo[0]));
                    data.setClassName(classInfo[1]);
                    data.setValue(meanValue);
                    realm.copyToRealm(data);
                }
                realm.commitTransaction();
                if (ExplorationFragment.getInstance() != null) {
                    ExplorationFragment.getInstance().updateBubbleChart();
                }
                break;
            default:
                break;
        }
    }
}
