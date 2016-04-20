package webinar.pubnub.insitu.managers;

import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.data.ChartData;

/**
 * Created by Konstantinos Michail on 4/20/2016.
 */
public interface IChartManager {
    void setup(Chart<?> chart);
    void styleData(ChartData data);
    void preparePieData();

}
