package webinar.pubnub.insitu.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BubbleChart;
import com.github.mikephil.charting.data.realm.implementation.RealmBubbleData;
import com.github.mikephil.charting.data.realm.implementation.RealmBubbleDataSet;
import com.github.mikephil.charting.interfaces.datasets.IBubbleDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.RealmResults;
import webinar.pubnub.insitu.R;
import webinar.pubnub.insitu.managers.ChartManager;
import webinar.pubnub.insitu.model.MyBubbleChartData;

public class ExplorationFragment extends Fragment {

    private static ExplorationFragment explorationFragment;
    @Bind(R.id.bubble_chart)
    BubbleChart bubbleChart;
    private OnExplorationInteractionListener mListener;

    public ExplorationFragment() {
        // Required empty public constructor
    }

    public static ExplorationFragment getInstance() {
        return explorationFragment;
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
        setData();
    }

    private void setData() {

        RealmResults<MyBubbleChartData> result = ChartManager.getInstance().getBubbleChartData();

        RealmBubbleDataSet<MyBubbleChartData> set = new RealmBubbleDataSet<MyBubbleChartData>(result, "value", "classId", "bubbleSize");
        set.setLabel("Realm BubbleDataSet");
//        set.setColors(new int[]{R.color.high, R.color.colorAccent, R.color.very_low, R.color.common_google_signin_btn_text_light, R.color.colorPrimary});
        set.setColors(ColorTemplate.JOYFUL_COLORS);
        ArrayList<IBubbleDataSet> dataSets = new ArrayList<IBubbleDataSet>();
        dataSets.add(set); // add the dataset

        // create a data object with the dataset list
        RealmBubbleData data = new RealmBubbleData(result, "className", dataSets);
//        ChartManager.getInstance().styleData(data);

        // set data
        bubbleChart.setData(data);
        bubbleChart.getXAxis().setLabelRotationAngle(30);
        bubbleChart.getXAxis().setLabelsToSkip(0);
//        bubbleChart.getAxisLeft().setAxisMinValue(0);
//        bubbleChart.getAxisLeft().setAxisMaxValue(10);

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
