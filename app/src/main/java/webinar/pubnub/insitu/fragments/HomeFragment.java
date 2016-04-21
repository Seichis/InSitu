package webinar.pubnub.insitu.fragments;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.realm.implementation.RealmPieData;
import com.github.mikephil.charting.data.realm.implementation.RealmPieDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.joda.time.DateTime;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.RealmResults;
import webinar.pubnub.insitu.MainActivity;
import webinar.pubnub.insitu.R;
import webinar.pubnub.insitu.adapters.HomeSymptomsAdapter;
import webinar.pubnub.insitu.managers.ChartManager;
import webinar.pubnub.insitu.managers.SymptomManager;
import webinar.pubnub.insitu.model.MyChartData;

public class HomeFragment extends Fragment {
    static HomeFragment homeFragment;
    ChartManager chartManager;
    @Bind(R.id.pie_chart_main)
    PieChart pieChart;
    @Bind(R.id.home_listview)
    ListView listView;
    HomeSymptomsAdapter symptomsAdapter;
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
        homeFragment = this;

        mainActivity = MainActivity.getInstance();
        chartManager = ChartManager.getInstance();
        chartManager.setup(pieChart);
        setData();
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.OnHomeInteraction(uri);
        }
    }


    private void setData() {

        RealmResults<MyChartData> result = ChartManager.getInstance().getChartData();


        RealmPieDataSet<MyChartData> set = new RealmPieDataSet<>(result, "count", "id"); // stacked entries

        set.setColors(new int[]{R.color.high, R.color.colorAccent, R.color.very_low, R.color.common_google_signin_btn_text_light, R.color.colorPrimary}, getContext());
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
        symptomsAdapter = null;
        symptomsAdapter = new HomeSymptomsAdapter(getContext(), SymptomManager.getInstance().getAllSymptomsByDay(DateTime.now().getMillis()));
        listView.setAdapter(symptomsAdapter);


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
        chartManager.setup(pieChart);
        setData();
        Log.i("update", "called");
//        pieChart.invalidate();
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

}
