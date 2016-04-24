package webinar.pubnub.insitu.fragments;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gc.materialdesign.views.ButtonFloat;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.realm.implementation.RealmPieData;
import com.github.mikephil.charting.data.realm.implementation.RealmPieDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Calendar;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.RealmResults;
import webinar.pubnub.insitu.Constants;
import webinar.pubnub.insitu.MainActivity;
import webinar.pubnub.insitu.R;
import webinar.pubnub.insitu.Utils;
import webinar.pubnub.insitu.managers.ChartManager;
import webinar.pubnub.insitu.managers.SymptomManager;
import webinar.pubnub.insitu.model.MyChartData;

public class HomeFragment extends Fragment implements
        DatePickerDialog.OnDateSetListener {

private static final int SHOW_DISTRESS = 1;
private static final int SHOW_INTENSITY = 0;
private static final int SHOW_SINGLE_DAY = 0;
private static final int SHOW_RANGE = 1;
private static final int SHOW_ACTIVITIES = 0;
private static final int SHOW_WEATHER = 1;
private static final int SHOW_BODY_PARTS = 2;
// int[] to represent the options.
// OPTIONS[0]= dates ==> single or range,
// OPTIONS[1]= intensity or distress
// OPTIONS[2]= by activities,weather or body parts
private int[] OPTIONS = new int[]{SHOW_SINGLE_DAY, SHOW_INTENSITY, SHOW_ACTIVITIES};
    private static final String TAG = "HomeFragment";
    static HomeFragment homeFragment;
    ChartManager chartManager;
    @Bind(R.id.pie_chart_main)
    PieChart pieChart;
    MainActivity mainActivity;
    private OnHomeInteractionListener mListener;

    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment getInstance() {
        return homeFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        symptomManager=SymptomManager.getInstance();
        homeFragment = this;

        mainActivity = MainActivity.getInstance();
        chartManager = ChartManager.getInstance();
        chartManager.setup(pieChart);

        updateChartByOptions();

    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.OnHomeInteraction(uri);
        }
    }


    private void setData() {
        RealmResults<MyChartData> result = ChartManager.getInstance().getPieChartData();


        RealmPieDataSet<MyChartData> set = new RealmPieDataSet<>(result, "count", "id"); // stacked entries
//        set.setColors(ColorTemplate.JOYFUL_COLORS);

        set.setColors(Constants.colors, getContext());
        Log.i("pie chart", "entries" + set.getEntryCount());

        set.setLabel("");

        set.setSliceSpace(2);
        // create a data object with the dataset list
        RealmPieData data = new RealmPieData(result, "activity", set);
        chartManager.styleData(data);
        data.setValueTextColor(Color.WHITE);
        data.setValueTextSize(12f);

        // set data
        pieChart.setData(data);
        pieChart.animateY(1400);
        pieChart.setCenterText(generateCenterSpannableText());
        pieChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
                Log.i(TAG, "select" + e.getVal());
            }

            @Override
            public void onNothingSelected() {

            }
        });


    }


    private SpannableString generateCenterSpannableText() {
        int symptomCountToday = (int) ChartManager.getInstance().getSymptomsCount();
        SpannableString s = new SpannableString(Html.fromHtml(getString(R.string.pie_center, symptomCountToday, 4.5f)));
//        s.setSpan(new ForegroundColorSpan(Color.rgb(240, 115, 126)), 0, 10, 0);
//        s.setSpan(new RelativeSizeSpan(2.2f), 0, 10, 0);
        s.setSpan(new StyleSpan(Typeface.ITALIC), 11, s.length(), 0);
        s.setSpan(new ForegroundColorSpan(ColorTemplate.getHoloBlue()), 11, s.length(), 0);
        s.setSpan(new RelativeSizeSpan(2f), 9, s.length(), 0);
        return s;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnHomeInteractionListener) {
            mListener = (OnHomeInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnHomeInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        homeFragment = null;
    }

    public void updatePiechart() {
//        chartManager.setup(pieChart);
        setData();
        Log.i("update", "called");
//        pieChart.invalidate();
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
    public interface OnHomeInteractionListener {
        // TODO: Update argument type and name
        void OnHomeInteraction(Uri uri);

    }
    @Bind(R.id.by_activities_button)
    ButtonFloat activitiesButtonFloat;
    @Bind(R.id.by_body_part_button)
    ButtonFloat bodyPartButtonFloat;
    @Bind(R.id.by_weather_button)
    ButtonFloat weatherButtonFloat;
    @Bind(R.id.intensity_button)
    ButtonFloat intensityButtonFloat;
    @Bind(R.id.distress_button)
    ButtonFloat distressButtonFloat;
    @Bind(R.id.pick_range_button)
    ButtonFloat pickRangeButtonFloat;
    @Bind(R.id.pick_day_button)
    ButtonFloat pickDayButtonFloat;
    private static DateTime dt;
    ArrayList<DateTime> dateRange = new ArrayList<>();
    @OnClick(R.id.by_activities_button)
    void chartByActivities() {
        OPTIONS[2] = SHOW_ACTIVITIES;
        updateChartByOptions();
    }

    @OnClick(R.id.by_body_part_button)
    void chartByBodyPart() {
        OPTIONS[2] = SHOW_BODY_PARTS;

        updateChartByOptions();
    }

    @OnClick(R.id.by_weather_button)
    void chartByWeather() {
        OPTIONS[2] = SHOW_WEATHER;
        updateChartByOptions();
    }

    @OnClick(R.id.intensity_button)
    void chartByIntensity() {
                OPTIONS[1] = SHOW_INTENSITY;

        updateChartByOptions();
    }

    @OnClick(R.id.distress_button)
    void chartByDistress() {
                OPTIONS[1] = SHOW_DISTRESS;
        updateChartByOptions();
    }

    @OnClick(R.id.pick_range_button)
    void pickRange() {
        if(!symptomManager.noSymptoms()){

            OPTIONS[0] = SHOW_RANGE;
            dateRange.clear();
            openCalendar();
        }
    }
    SymptomManager symptomManager;
    @OnClick(R.id.pick_day_button)
    void pickSingleDay() {
        if(!symptomManager.noSymptoms()){
            OPTIONS[0] = SHOW_SINGLE_DAY;
            dateRange.clear();
            openCalendar();}
    }

    private void openCalendar() {
        Calendar now = Calendar.getInstance();
        DatePickerDialog dpd = DatePickerDialog.newInstance(
                HomeFragment.this,
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
                    title = "From "+ Utils.getFormatedDate(dateRange.get(0)) +"\nUntil";
                }

        }

        dpd.setTitle(title);

        dpd.show(getActivity().getFragmentManager(), "Datepickerdialog");

    }

    void updateChartByOptions() {
        if (dt == null) {
            Log.i(TAG,"null dt");
            dt = DateTime.now();
        }
        switch (OPTIONS[0]) {
            case SHOW_SINGLE_DAY:
                pickDayButtonFloat.setBackgroundColor(ContextCompat.getColor(getContext(), android.R.color.holo_blue_bright));
                pickRangeButtonFloat.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.high));
                chartManager.updatePieChartByDay(OPTIONS[2],dt.getMillis());
                break;
            case SHOW_RANGE:
                pickRangeButtonFloat.setBackgroundColor(ContextCompat.getColor(getContext(), android.R.color.holo_blue_bright));
                pickDayButtonFloat.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.high));
                chartManager.updatePieChartByRange(OPTIONS[2],dateRange.get(0).getMillis(), dateRange.get(1).getMillis());
                break;
        }

        switch (OPTIONS[1]) {
            case SHOW_INTENSITY:
                intensityButtonFloat.setBackgroundColor(ContextCompat.getColor(getContext(), android.R.color.holo_blue_bright));
                distressButtonFloat.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.graph_color2));

                break;
            case SHOW_DISTRESS:
                distressButtonFloat.setBackgroundColor(ContextCompat.getColor(getContext(), android.R.color.holo_blue_bright));
                intensityButtonFloat.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.graph_color7));
                break;
        }

        switch (OPTIONS[2]) {
            case SHOW_ACTIVITIES:
                activitiesButtonFloat.setBackgroundColor(ContextCompat.getColor(getContext(), android.R.color.holo_blue_bright));
                bodyPartButtonFloat.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorPrimaryDark));
                weatherButtonFloat.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorPrimaryDark));
                break;
            case SHOW_BODY_PARTS:
                bodyPartButtonFloat.setBackgroundColor(ContextCompat.getColor(getContext(), android.R.color.holo_blue_bright));
                weatherButtonFloat.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorPrimaryDark));
                activitiesButtonFloat.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorPrimaryDark));

                break;
            case SHOW_WEATHER:
                weatherButtonFloat.setBackgroundColor(ContextCompat.getColor(getContext(), android.R.color.holo_blue_bright));
                bodyPartButtonFloat.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorPrimaryDark));
                activitiesButtonFloat.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorPrimaryDark));

                break;
        }
    }


}
