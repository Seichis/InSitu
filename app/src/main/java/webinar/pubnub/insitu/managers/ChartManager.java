package webinar.pubnub.insitu.managers;

import android.content.Context;
import android.graphics.Color;

import com.github.mikephil.charting.charts.BarLineChartBase;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.ChartData;
import com.github.mikephil.charting.formatter.PercentFormatter;

import io.realm.Realm;
import io.realm.RealmResults;
import webinar.pubnub.insitu.Constants;
import webinar.pubnub.insitu.MainActivity;
import webinar.pubnub.insitu.fragments.HomeFragment;
import webinar.pubnub.insitu.model.MyChartData;

/**
 * Created by Konstantinos Michail on 4/20/2016.
 */
public class ChartManager implements IChartManager {
    static ChartManager chartManager = new ChartManager();
SymptomManager symptomManager;
    Context context;
    public static ChartManager getInstance() {
        return chartManager;
    }
    Realm realm;
    public void init(Context context){
        this.context=context;
        realm=Realm.getDefaultInstance();
        symptomManager=SymptomManager.getInstance();
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
    public void updateChartData() {
        realm.beginTransaction();
        realm.clear(MyChartData.class);
        float totalSymptoms = (float) symptomManager.getAllSymptoms().size();
        for (int i = 0; i < Constants.SAVED_ACTIVITIES.length; i++) {
            float count = 100 * ((float) symptomManager.getAllSymptomsByActivity(Constants.SAVED_ACTIVITIES[i]).size()) / totalSymptoms;
            MyChartData myChartData = new MyChartData(Constants.getSavedActivityString(context, Constants.SAVED_ACTIVITIES[i]), count, i);
            realm.copyToRealm(myChartData);
        }
        realm.commitTransaction();
        if (HomeFragment.getInstance()!=null) {
            HomeFragment.getInstance().updatePiechart();
        }
    }


    public RealmResults<MyChartData> getChartData(){
        return realm.allObjects(MyChartData.class);
    }


}
