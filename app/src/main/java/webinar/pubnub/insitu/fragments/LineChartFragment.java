package webinar.pubnub.insitu.fragments;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gc.materialdesign.views.ButtonFloat;
import com.gc.materialdesign.views.ButtonRectangle;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.data.realm.implementation.RealmLineData;
import com.github.mikephil.charting.data.realm.implementation.RealmLineDataSet;
import com.github.mikephil.charting.formatter.LargeValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.TreeMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.RealmResults;
import webinar.pubnub.insitu.Constants;
import webinar.pubnub.insitu.R;
import webinar.pubnub.insitu.Utils;
import webinar.pubnub.insitu.managers.ChartManager;
import webinar.pubnub.insitu.managers.SymptomManager;
import webinar.pubnub.insitu.model.MyLineChartData;

/**
 * Created by Konstantinos Michail on 4/25/2016.
 */
public class LineChartFragment extends Fragment implements
        DatePickerDialog.OnDateSetListener {

    private static final int SHOW_DISTRESS = 1;
    private static final int SHOW_INTENSITY = 0;
    private static final int SHOW_INTENSITY_AND_DISTRESS = 2;
    private static final int SHOW_MEDICATION = 3;
    private static final int SHOW_MEDICATION_AND_INTENSITY = 4;
    private static final int SHOW_MEDICATION_AND_DISTRESS = 5;
    private static final int SHOW_ALL = 6;
    private static final int SHOW_SINGLE_DAY = 0;
    private static final int SHOW_RANGE = 1;
    private static final int SHOW_BY_HOUR = 0;
    private static final int SHOW_BY_DAY = 1;
    private static final int SHOW_BY_WEEK = 2;
    private static final int SHOW_BY_MONTH = 3;
    private static final int SHOW_BY_YEAR = 4;

    private static final String TAG = "Exploration";
    //By default show intensity
    private static LineChartFragment lineChartFragment;
    private static DateTime dt;
    ArrayList<DateTime> dateRange = new ArrayList<>();
    @Bind(R.id.line_chart)
    LineChart lineChart;
    @Bind(R.id.intensity_button)
    ButtonFloat intensityButtonFloat;
    @Bind(R.id.distress_button)
    ButtonFloat distressButtonFloat;
    @Bind(R.id.pick_range_button)
    ButtonFloat pickRangeButtonFloat;
    @Bind(R.id.pick_day_button)
    ButtonFloat pickDayButtonFloat;
    @Bind(R.id.by_hour_button)
    ButtonRectangle hourButton;
    @Bind(R.id.by_day_button)
    ButtonRectangle dayButton;
    @Bind(R.id.by_week_button)
    ButtonRectangle weekButton;
    @Bind(R.id.by_month_button)
    ButtonRectangle monthButton;
    @Bind(R.id.by_year_button)
    ButtonRectangle yearButton;

    SymptomManager symptomManager;
    boolean showByAverage = false;
    // int[] to represent the options.
    // OPTIONS[0]= dates ==> single or range,
    // OPTIONS[1]= intensity or distress
    // OPTIONS[2]= by hour, day,month or year
    private int[] OPTIONS = new int[]{SHOW_SINGLE_DAY, SHOW_INTENSITY, SHOW_BY_HOUR};
    private OnLineChartInteractionListener mListener;

    public LineChartFragment() {
        // Required empty public constructor
    }

    public static LineChartFragment getInstance() {
        return lineChartFragment;
    }

    void resetOptions() {
        OPTIONS[2] = -1;
    }

    @OnClick(R.id.by_hour_button)
    void byHour() {
        changeState(hourButton, SHOW_BY_HOUR);
    }

    @OnClick(R.id.by_day_button)
    void byDay() {
        changeState(dayButton, SHOW_BY_DAY);
    }

    void changeState(ButtonRectangle b, int option) {
        ArrayList<ButtonRectangle> buttonRectangles = new ArrayList<>(Arrays.asList(hourButton, dayButton, weekButton, monthButton, yearButton));
        buttonRectangles.remove(b);
        Log.i(TAG, "dcc " + b.getDrawingCacheBackgroundColor() + "res c" + ContextCompat.getColor(getContext(), R.color.graph_color6));
        if (showByAverage) {
            if (b.getDrawingCacheBackgroundColor() == ContextCompat.getColor(getContext(), R.color.graph_color6)) {
                showByAverage = false;

                b.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.backgroundColor));
                b.setDrawingCacheBackgroundColor(ContextCompat.getColor(getContext(), R.color.backgroundColor));

                updateChartByOptions();
            } else {
                b.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.graph_color6));
                b.setDrawingCacheBackgroundColor(ContextCompat.getColor(getContext(), R.color.graph_color6));

                OPTIONS[2] = option;
                switchToAverage(option);
                for (ButtonRectangle br : buttonRectangles) {
                    br.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.backgroundColor));
                    br.setDrawingCacheBackgroundColor(ContextCompat.getColor(getContext(), R.color.backgroundColor));

                }
            }
        } else {
            showByAverage = true;
            b.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.graph_color6));
            OPTIONS[2] = option;
            for (ButtonRectangle br : buttonRectangles) {
                br.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.backgroundColor));
                br.setDrawingCacheBackgroundColor(ContextCompat.getColor(getContext(), R.color.backgroundColor));
            }
            switchToAverage(option);
        }
    }

    @OnClick(R.id.by_week_button)
    void byWeek() {
        changeState(weekButton, SHOW_BY_WEEK);
    }

    @OnClick(R.id.by_month_button)
    void byMonth() {
        changeState(monthButton, SHOW_BY_MONTH);


    }

    @OnClick(R.id.by_year_button)
    void byYear() {
        changeState(yearButton, SHOW_BY_YEAR);

    }

    @OnClick(R.id.intensity_button)
    void chartByIntensity() {
        switch (OPTIONS[1]) {
            case SHOW_INTENSITY:
                OPTIONS[1] = SHOW_INTENSITY;
                break;
            case SHOW_DISTRESS:
                OPTIONS[1] = SHOW_INTENSITY_AND_DISTRESS;
                break;
            case SHOW_INTENSITY_AND_DISTRESS:
                OPTIONS[1] = SHOW_DISTRESS;
                break;
        }
        updateChartByOptions();
    }

    @OnClick(R.id.distress_button)
    void chartByDistress() {
        switch (OPTIONS[1]) {
            case SHOW_DISTRESS:
                OPTIONS[1] = SHOW_DISTRESS;
                break;
            case SHOW_INTENSITY:
                OPTIONS[1] = SHOW_INTENSITY_AND_DISTRESS;
                break;
            case SHOW_INTENSITY_AND_DISTRESS:
                OPTIONS[1] = SHOW_INTENSITY;
                break;
        }
        updateChartByOptions();
    }

    @OnClick(R.id.pick_range_button)
    void pickRange() {
        if (!symptomManager.noSymptoms()) {

            OPTIONS[0] = SHOW_RANGE;
            dateRange.clear();
            openCalendar();
        }
    }

    @OnClick(R.id.pick_day_button)
    void pickSingleDay() {
        if (!symptomManager.noSymptoms()) {
            OPTIONS[0] = SHOW_SINGLE_DAY;
            dateRange.clear();
            openCalendar();
        }
    }

    private void openCalendar() {
        Calendar now = Calendar.getInstance();
        DatePickerDialog dpd = DatePickerDialog.newInstance(
                LineChartFragment.this,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );

        dpd.dismissOnPause(true);
        dpd.showYearPickerFirst(false);
        dpd.setAccentColor(ContextCompat.getColor(getContext(), Constants.colors[12]));

        dpd.setMaxDate(now);
        dpd.setMinDate(SymptomManager.getInstance().getMinDate());
        String title = "";
        switch (OPTIONS[0]) {
            case SHOW_SINGLE_DAY:
                title = "Choose a date to explore";
                break;
            case SHOW_RANGE:
                if (dateRange.isEmpty()) {
                    title = "From";
                    dpd.autoDismiss(true);
                } else {
                    Calendar tmp = Calendar.getInstance();
                    tmp.setTimeInMillis(dateRange.get(0).getMillis());
                    dpd.setMinDate(tmp);
                    title = "From " + Utils.getFormatedDate(dateRange.get(0)) + "\nUntil";
                }

        }

        dpd.setTitle(title);

        dpd.show(getActivity().getFragmentManager(), "Datepickerdialog");

    }

    void checkDateRange(long from, long until) {
        if (!symptomManager.noSymptoms()) {
            showByAverage = true;
            switch (Utils.getDateResolution(from, until)) {
                case Constants.DAY_VIEW:
                    hourButton.setVisibility(View.VISIBLE);
                    dayButton.setVisibility(View.GONE);
                    weekButton.setVisibility(View.GONE);
                    monthButton.setVisibility(View.GONE);
                    yearButton.setVisibility(View.GONE);
                    break;
                case Constants.WEEK_VIEW:
                    hourButton.setVisibility(View.VISIBLE);
                    dayButton.setVisibility(View.VISIBLE);
                    weekButton.setVisibility(View.GONE);
                    monthButton.setVisibility(View.GONE);
                    yearButton.setVisibility(View.GONE);
                    break;
                case Constants.MONTH_VIEW:
                    hourButton.setVisibility(View.VISIBLE);
                    dayButton.setVisibility(View.VISIBLE);
                    weekButton.setVisibility(View.VISIBLE);
                    monthButton.setVisibility(View.GONE);
                    yearButton.setVisibility(View.GONE);
                    break;
                case Constants.YEAR_VIEW:
                    hourButton.setVisibility(View.VISIBLE);
                    dayButton.setVisibility(View.VISIBLE);
                    weekButton.setVisibility(View.VISIBLE);
                    monthButton.setVisibility(View.VISIBLE);
                    yearButton.setVisibility(View.VISIBLE);
                    break;
            }
        } else {
            hourButton.setVisibility(View.GONE);
            dayButton.setVisibility(View.GONE);
            weekButton.setVisibility(View.GONE);
            monthButton.setVisibility(View.GONE);
            yearButton.setVisibility(View.GONE);
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        lineChartFragment = this;
        symptomManager = SymptomManager.getInstance();
        ButterKnife.bind(this, view);

        ChartManager.getInstance().setup(lineChart);
        updateChartByOptions();
//        setData();
    }

    void updateChartByOptions() {
        if (dt == null) {
            Log.i(TAG, "null dt");
            dt = DateTime.now();
        }
        long from;
        long until;
        showByAverage = false;
        ArrayList<ButtonRectangle> buttonRectangles = new ArrayList<>(Arrays.asList(hourButton, dayButton, weekButton, monthButton, yearButton));
        for (ButtonRectangle br : buttonRectangles) {
            br.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.backgroundColor));
            br.setDrawingCacheBackgroundColor(ContextCompat.getColor(getContext(), R.color.backgroundColor));
        }
        switch (OPTIONS[0]) {
            case SHOW_SINGLE_DAY:
                pickDayButtonFloat.setBackgroundColor(ContextCompat.getColor(getContext(), android.R.color.holo_blue_bright));
                pickRangeButtonFloat.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.high));
//                ChartManager.getInstance().updateLineChartByDay(OPTIONS[2], OPTIONS[1], dt.getMillis());
                from = Utils.getDayStart(dt.getMillis(), 1);
                until = Utils.getDaysEnd(dt.getMillis());
                if (showByAverage) {
                    switchToAverage(OPTIONS[2]);
                } else {

                    ChartManager.getInstance().updateLineChartTimeline(OPTIONS[1], from, until);
                }
                checkDateRange(from, until);


                break;
            case SHOW_RANGE:
                pickRangeButtonFloat.setBackgroundColor(ContextCompat.getColor(getContext(), android.R.color.holo_blue_bright));
                pickDayButtonFloat.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.high));
                from = dateRange.get(0).getMillis();
                until = dateRange.get(1).getMillis();
                if (showByAverage) {
                    switchToAverage(OPTIONS[2]);
                } else {
                    ChartManager.getInstance().updateLineChartTimeline(OPTIONS[1], from, until);
                }
                checkDateRange(from, until);


                break;
        }

        switch (OPTIONS[1]) {
            case SHOW_INTENSITY:
                distressButtonFloat.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.dark_gray_press));
                intensityButtonFloat.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.graph_color7));

                break;
            case SHOW_DISTRESS:
                intensityButtonFloat.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.dark_gray_press));
                distressButtonFloat.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.graph_color2));
                break;
            case SHOW_INTENSITY_AND_DISTRESS:
                distressButtonFloat.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.graph_color2));
                intensityButtonFloat.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.graph_color7));
                break;
            case SHOW_MEDICATION:
                break;
            case SHOW_MEDICATION_AND_INTENSITY:
                break;
            case SHOW_MEDICATION_AND_DISTRESS:
                break;
            case SHOW_ALL:
                break;
        }


    }

    private void setData() {

        TreeMap<Integer, RealmResults<MyLineChartData>>
                realmResults = new TreeMap<>();
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();


        RealmResults<MyLineChartData> result = ChartManager.getInstance().getLineChartData();
        RealmResults<MyLineChartData> distinctObjects = result.where().distinct("setId");
        for (MyLineChartData cd : distinctObjects) {
            realmResults.put(cd.getSetId(), result.where().equalTo("setId", cd.getSetId()).findAll());
        }

        RealmResults<MyLineChartData> finalResult = null;
        for (TreeMap.Entry<Integer, RealmResults<MyLineChartData>> chartDatas : realmResults.entrySet()) {
            finalResult = chartDatas.getValue();

            RealmLineDataSet<MyLineChartData> set = new RealmLineDataSet<MyLineChartData>(finalResult, "value", "classId");

            set.setColor(ContextCompat.getColor(getContext(), getBubbleColor(chartDatas.getKey())));
            set.setDrawCubic(true);
            set.setDrawFilled(true);
            set.setCubicIntensity(0.05f);
            set.setFillColor(ContextCompat.getColor(getContext(), getBubbleColor(chartDatas.getKey())));
            set.setValueFormatter(new LargeValueFormatter());
            set.setDrawValues(false);
            dataSets.add(set);
            lineChart.getAxisLeft().addLimitLine(getAvarageLimitLine(finalResult, chartDatas.getKey()));


        }

        // create a data object with the dataset list
        if (finalResult != null) {
            RealmLineData data = new RealmLineData(finalResult, "className", dataSets);
            data.setValueTextSize(12f);
            data.setValueTextColor(Color.DKGRAY);
            data.setValueFormatter(new LargeValueFormatter());


            lineChart.setData(data);

//            lineChart.getXAxis().setDrawGridLines(true);
            lineChart.getAxisLeft().setDrawGridLines(false);
            lineChart.setPinchZoom(true);

            lineChart.setAutoScaleMinMaxEnabled(false);
            lineChart.getLegend().setEnabled(false);
            lineChart.getXAxis().setLabelRotationAngle(30);
            lineChart.getXAxis().setLabelsToSkip(0);
            lineChart.getAxisLeft().setAxisMinValue(0);
            lineChart.getAxisLeft().setAxisMaxValue(10);
            lineChart.getAxisLeft().setValueFormatter(new LargeValueFormatter());
            lineChart.animateY(1400, Easing.EasingOption.EaseInOutQuart);
        }

        // set data

    }

    private LimitLine getAvarageLimitLine(RealmResults<MyLineChartData> chartDatas, int setId) {
        float mean;
        LimitLine ll;
        switch (setId) {
            case 1:
                mean = (float) chartDatas.where().average("value");
                ll = new LimitLine(mean, "Distress : " + String.format("%.2f", mean));
                ll.setLineColor(ContextCompat.getColor(getContext(), getBubbleColor(setId)));
                ll.setLineWidth(5f);
                ll.setTextColor(ContextCompat.getColor(getContext(), getBubbleColor(setId)));

                ll.setTextSize(12f);
                return ll;
            case 2:
                mean = (float) chartDatas.where().average("value");
                ll = new LimitLine(mean, "Intensity : " + String.format("%.2f", mean));
                ll.setLineColor(ContextCompat.getColor(getContext(), getBubbleColor(setId)));
                ll.setLineWidth(5f);
                ll.setTextColor(ContextCompat.getColor(getContext(), getBubbleColor(setId)));
                ll.setTextSize(12f);

                return ll;
            default:
                return new LimitLine(0f, "");
        }
    }


//    private ArrayList<LimitLine> getLimitLines(){
//        XAxis xAxis = lineChart.getXAxis();
//
//        LimitLine ll = new LimitLine(140f, "Critical Blood Pressure");
//        ll.setLineColor(Color.RED);
//        ll.setLineWidth(4f);
//        ll.setTextColor(Color.BLACK);
//        ll.setTextSize(12f);
//// .. and more styling options
//
//        xAxis.addLimitLine(ll);
//
//    }

    private void switchToAverage(int per) {
        if (dateRange.isEmpty()) {
            ChartManager.getInstance().updateLineChartByRange(per, OPTIONS[1], Utils.getDayStart(dt.getMillis(), 1), Utils.getDaysEnd(dt.getMillis()));
        } else {
            ChartManager.getInstance().updateLineChartByRange(per, OPTIONS[1], dateRange.get(0).getMillis(), dateRange.get(1).getMillis());
        }

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.line_chart_fragment, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.OnLineChartInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnLineChartInteractionListener) {
            mListener = (OnLineChartInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnLineChartInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        lineChartFragment = null;
    }

    public void updateLineChart() {
        ChartManager.getInstance().setup(lineChart);
        setData();
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        switch (OPTIONS[0]) {
            case SHOW_SINGLE_DAY:
                dt = Utils.getDate(year, monthOfYear + 1, dayOfMonth);
                updateChartByOptions();
                break;
            case SHOW_RANGE:
                // if empty set the from date
                if (dateRange.isEmpty()) {
                    dateRange.add(Utils.getDate(year, monthOfYear + 1, dayOfMonth));
                    openCalendar();
                } else if (dateRange.size() == 1) {
                    dateRange.add(Utils.getDate(year, monthOfYear + 1, dayOfMonth));
                    updateChartByOptions();
                } else {
                    dateRange.clear();
                }

        }
    }

    public int getBubbleColor(int setId) {
//        if (OPTIONS[1] == SHOW_INTENSITY) {
//            return Constants.colors[1];
//        } else if (OPTIONS[1] == SHOW_DISTRESS) {
//            return Constants.colors[6];
//        } else
        if (setId == 1) {
            return Constants.colors[1];
        } else if (setId == 2) {
            return Constants.colors[6];
        } else {
            return Constants.colors[setId + 1];
        }

    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnLineChartInteractionListener {
        // TODO: Update argument type and name
        void OnLineChartInteraction(Uri uri);
    }
}
