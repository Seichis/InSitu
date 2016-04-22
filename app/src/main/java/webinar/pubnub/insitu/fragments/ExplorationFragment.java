package webinar.pubnub.insitu.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gc.materialdesign.views.ButtonFloat;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BubbleChart;
import com.github.mikephil.charting.data.realm.implementation.RealmBubbleData;
import com.github.mikephil.charting.data.realm.implementation.RealmBubbleDataSet;
import com.github.mikephil.charting.interfaces.datasets.IBubbleDataSet;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.RealmResults;
import webinar.pubnub.insitu.Constants;
import webinar.pubnub.insitu.R;
import webinar.pubnub.insitu.Utils;
import webinar.pubnub.insitu.managers.ChartManager;
import webinar.pubnub.insitu.model.MyBubbleChartData;

public class ExplorationFragment extends Fragment implements
        TimePickerDialog.OnTimeSetListener,
        DatePickerDialog.OnDateSetListener {

    public static final int SHOW_DISTRESS = 0;
    public static final int SHOW_INTENSITY = 1;
    public static final int SHOW_SINGLE_DAY = 0;
    public static final int SHOW_RANGE = 1;
    public static final int SHOW_ACTIVITIES = 0;
    public static final int SHOW_WEATHER = 1;
    public static final int SHOW_BODY_PARTS = 2;
    private static final String TAG = "Exploration";
    // int[] to represent the options.
    // OPTIONS[0]= dates ==> single or range,
    // OPTIONS[1]= intensity or distress
    // OPTIONS[2]= by activities,weather or body parts
    private static int[] OPTIONS = new int[]{SHOW_SINGLE_DAY, SHOW_INTENSITY, SHOW_ACTIVITIES};
    //By default show intensity
    private static ExplorationFragment explorationFragment;
    private static DateTime dt;
    ArrayList<DateTime> dateRange = new ArrayList<>();
    @Bind(R.id.bubble_chart)
    BubbleChart bubbleChart;
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
    private OnExplorationInteractionListener mListener;

    public ExplorationFragment() {
        // Required empty public constructor
    }

    public static ExplorationFragment getInstance() {
        return explorationFragment;
    }

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
        OPTIONS[0] = SHOW_RANGE;
        dateRange.clear();
        openCalendar();
    }

    @OnClick(R.id.pick_day_button)
    void pickSingleDay() {
        OPTIONS[0] = SHOW_SINGLE_DAY;
        dateRange.clear();
        openCalendar();
    }

    private void openCalendar() {
        Calendar now = Calendar.getInstance();
        DatePickerDialog dpd = DatePickerDialog.newInstance(
                ExplorationFragment.this,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );

//        dpd.vibrate(vibrateDate.isChecked());
        dpd.dismissOnPause(true);
        dpd.showYearPickerFirst(false);
        dpd.setAccentColor(ContextCompat.getColor(getContext(), Constants.colors[12]));
        String title="";
        switch (OPTIONS[0]){
            case SHOW_SINGLE_DAY:
                title="Choose a date to explore";
                break;
            case SHOW_RANGE:
                if (dateRange.isEmpty()){
                    title="From";
                }else{
                    Calendar tmp=Calendar.getInstance();
                    tmp.setTimeInMillis(dateRange.get(0).getMillis());
                    dpd.setMinDate(tmp);
                    title="Until";
                }

        }

        dpd.setTitle(title);

        dpd.show(getActivity().getFragmentManager(), "Datepickerdialog");

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        explorationFragment = this;
        ButterKnife.bind(this, view);

        ChartManager.getInstance().setup(bubbleChart);

        bubbleChart.getXAxis().setDrawGridLines(true);
        bubbleChart.getAxisLeft().setDrawGridLines(false);
        bubbleChart.setPinchZoom(true);
        bubbleChart.setAutoScaleMinMaxEnabled(false);
        bubbleChart.getLegend().setEnabled(false);
        updateChartByOptions();
        setData();

    }

    void updateChartByOptions() {
        if (dt == null) {
            dt = DateTime.now();
        }
        switch (OPTIONS[0]) {
            case SHOW_SINGLE_DAY:
                pickDayButtonFloat.setBackgroundColor(ContextCompat.getColor(getContext(), android.R.color.holo_blue_bright));
                pickRangeButtonFloat.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.high));

                break;
            case SHOW_RANGE:
                pickRangeButtonFloat.setBackgroundColor(ContextCompat.getColor(getContext(), android.R.color.holo_blue_bright));
                pickDayButtonFloat.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.high));

                break;
        }

        switch (OPTIONS[1]) {
            case SHOW_INTENSITY:
                intensityButtonFloat.setBackgroundColor(ContextCompat.getColor(getContext(), android.R.color.holo_blue_bright));
                distressButtonFloat.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.graph_color8));

                break;
            case SHOW_DISTRESS:
                distressButtonFloat.setBackgroundColor(ContextCompat.getColor(getContext(), android.R.color.holo_blue_bright));
                intensityButtonFloat.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.graph_color8));


                break;
        }

        switch (OPTIONS[2]) {
            case SHOW_ACTIVITIES:
                activitiesButtonFloat.setBackgroundColor(ContextCompat.getColor(getContext(),android.R.color.holo_blue_bright));
                bodyPartButtonFloat.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorPrimaryDark));
                weatherButtonFloat.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorPrimaryDark));

                break;
            case SHOW_BODY_PARTS:
                bodyPartButtonFloat.setBackgroundColor(ContextCompat.getColor(getContext(),android.R.color.holo_blue_bright));
                weatherButtonFloat.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorPrimaryDark));
                activitiesButtonFloat.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorPrimaryDark));

                break;
            case SHOW_WEATHER:
                weatherButtonFloat.setBackgroundColor(ContextCompat.getColor(getContext(),android.R.color.holo_blue_bright));
                bodyPartButtonFloat.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorPrimaryDark));
                activitiesButtonFloat.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorPrimaryDark));

                break;
        }
    }

    private void setData() {

        RealmResults<MyBubbleChartData> result = ChartManager.getInstance().getBubbleChartData();

        RealmBubbleDataSet<MyBubbleChartData> set = new RealmBubbleDataSet<MyBubbleChartData>(result, "value", "classId", "bubbleSize");
        set.setLabel("Realm BubbleDataSet");
        set.setColors(Constants.colors, getContext());

//        set.setColors(ColorTemplate.JOYFUL_COLORS);
        ArrayList<IBubbleDataSet> dataSets = new ArrayList<IBubbleDataSet>();
        dataSets.add(set); // add the dataset

        // create a data object with the dataset list
        RealmBubbleData data = new RealmBubbleData(result, "className", dataSets);
        ChartManager.getInstance().styleData(data);

        // set data
        bubbleChart.setData(data);
        bubbleChart.getXAxis().setLabelRotationAngle(30);
        bubbleChart.getXAxis().setLabelsToSkip(0);
        bubbleChart.getAxisLeft().setAxisMinValue(0);
        bubbleChart.getAxisLeft().setAxisMaxValue(10);

        bubbleChart.animateY(1400, Easing.EasingOption.EaseInOutQuart);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_exploration, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.OnExplorationInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnExplorationInteractionListener) {
            mListener = (OnExplorationInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnExplorationInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        explorationFragment = null;
    }

    public void updateBubbleChart() {
        ChartManager.getInstance().setup(bubbleChart);
        setData();
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        switch (OPTIONS[0]) {
            case SHOW_SINGLE_DAY:
                DateTime dt = Utils.getDate(year, monthOfYear + 1, dayOfMonth);
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

    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int second) {

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
    public interface OnExplorationInteractionListener {
        // TODO: Update argument type and name
        void OnExplorationInteraction(Uri uri);
    }
}