/*
 * Copyright 2014 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package webinar.pubnub.insitu.maps;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;

import com.gc.materialdesign.views.ButtonFloat;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.maps.android.heatmaps.Gradient;
import com.google.maps.android.heatmaps.HeatmapTileProvider;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import webinar.pubnub.insitu.Constants;
import webinar.pubnub.insitu.R;
import webinar.pubnub.insitu.Utils;
import webinar.pubnub.insitu.managers.SymptomManager;

/**
 * A demo of the Heatmaps library. Demonstrates how the HeatmapTileProvider can be used to create
 * a colored map overlay that visualises many points of weighted importance/intensity, with
 * different colors representing areas of high and low concentration/combined intensity of points.
 */
public class HeatmapsDemoActivity extends BaseDemoActivity implements
        DatePickerDialog.OnDateSetListener {
    public static final float[] ALT_HEATMAP_GRADIENT_START_POINTS = {
            0.0f, 0.10f, 0.20f, 0.60f, 1.0f
    };

    static final int SHOW_DISTRESS = 1;
    static final int SHOW_INTENSITY = 0;
    static final int SHOW_SINGLE_DAY = 0;
    static final int SHOW_RANGE = 1;
    /**
     * Alternative radius for convolution
     */
    private static final int ALT_HEATMAP_RADIUS = 10;
    private static final String TAG = "Heatmap";
    /**
     * Alternative opacity of heatmap overlay
     */
    private static final double ALT_HEATMAP_OPACITY = 0.4;
    /**
     * Alternative heatmap gradient (blue -> red)
     * Copied from Javascript version
     */
    private static final int[] ALT_HEATMAP_GRADIENT_COLORS = {
            Color.argb(0, 0, 255, 255),// transparent
            Color.argb(255 / 3 * 2, 0, 255, 255),
            Color.rgb(0, 191, 255),
            Color.rgb(0, 0, 127),
            Color.rgb(255, 0, 0)
    };
    public static final Gradient ALT_HEATMAP_GRADIENT = new Gradient(ALT_HEATMAP_GRADIENT_COLORS,
            ALT_HEATMAP_GRADIENT_START_POINTS);
    private static DateTime dt;
    @Bind(R.id.intensity_button)
    ButtonFloat intensityButtonFloat;
    @Bind(R.id.distress_button)
    ButtonFloat distressButtonFloat;
    @Bind(R.id.pick_range_button)
    ButtonFloat pickRangeButtonFloat;
    @Bind(R.id.pick_day_button)
    ButtonFloat pickDayButtonFloat;
    //    ArrayList<String> options = new ArrayList<>();
    SymptomManager symptomManager;
    ArrayList<DateTime> dateRange = new ArrayList<>();
    private int[] OPTIONS = new int[]{SHOW_SINGLE_DAY, SHOW_INTENSITY};
    private HeatmapTileProvider mProvider;
    private TileOverlay mOverlay;
    private boolean mDefaultGradient = true;
    private boolean mDefaultRadius = true;
    private boolean mDefaultOpacity = true;
    /**
     * Maps name of data set to data (list of LatLngs)
     * Also maps to the URL of the data set for attribution
     */
    private HashMap<String, DataSet> mLists = new HashMap<>();
    private static HeatmapsDemoActivity heatmapsDemoActivity;

    public static HeatmapsDemoActivity getInstance() {
        return heatmapsDemoActivity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        symptomManager=SymptomManager.getInstance();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.heatmaps_demo;
    }

    @Override
    protected void startDemo() {

        getMap().moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(55.6761, 12.5683), 10));
        updateMapByOptions();
    }

    public void changeRadius(View view) {
        if (mDefaultRadius) {
            mProvider.setRadius(ALT_HEATMAP_RADIUS);
        } else {
            mProvider.setRadius(HeatmapTileProvider.DEFAULT_RADIUS);
        }
        mOverlay.clearTileCache();
        mDefaultRadius = !mDefaultRadius;
    }

    public void changeGradient(View view) {
        if (mDefaultGradient) {
            mProvider.setGradient(ALT_HEATMAP_GRADIENT);
        } else {
            mProvider.setGradient(HeatmapTileProvider.DEFAULT_GRADIENT);
        }
        mOverlay.clearTileCache();
        mDefaultGradient = !mDefaultGradient;
    }

    public void changeOpacity(View view) {
        if (mDefaultOpacity) {
            mProvider.setOpacity(ALT_HEATMAP_OPACITY);
        } else {
            mProvider.setOpacity(HeatmapTileProvider.DEFAULT_OPACITY);
        }
        mOverlay.clearTileCache();
        mDefaultOpacity = !mDefaultOpacity;
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        switch (OPTIONS[0]) {
            case SHOW_SINGLE_DAY:
                dt = Utils.getDate(year, monthOfYear + 1, dayOfMonth);
                updateMapByOptions();
                break;
            case SHOW_RANGE:
                // if empty set the from date
                if (dateRange.isEmpty()) {
                    dateRange.add(Utils.getDate(year, monthOfYear + 1, dayOfMonth));
                    openCalendar();
                } else if (dateRange.size() == 1) {
                    dateRange.add(Utils.getDate(year, monthOfYear + 1, dayOfMonth));
                    updateMapByOptions();
                } else {
                    dateRange.clear();
                }

        }
    }

    @OnClick(R.id.intensity_button)
    void chartByIntensity() {

                OPTIONS[1] = SHOW_INTENSITY;

        updateMapByOptions();
    }

    @OnClick(R.id.distress_button)
    void chartByDistress() {

                OPTIONS[1] = SHOW_DISTRESS;


        updateMapByOptions();
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
                this,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );

        dpd.dismissOnPause(true);
        dpd.showYearPickerFirst(false);
        dpd.setAccentColor(ContextCompat.getColor(this, Constants.colors[12]));

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

        dpd.show(this.getFragmentManager(), "Datepickerdialog");
    }

    public void updateMapByOptions() {
        if (dt == null) {
            Log.i(TAG, "null dt");
            dt = DateTime.now();
        }
        switch (OPTIONS[0]) {
            case SHOW_SINGLE_DAY:
                pickDayButtonFloat.setBackgroundColor(ContextCompat.getColor(this, android.R.color.holo_blue_bright));
                pickRangeButtonFloat.setBackgroundColor(ContextCompat.getColor(this, R.color.high));
                updateMapByDay(dt.getMillis());
                break;
            case SHOW_RANGE:
                pickRangeButtonFloat.setBackgroundColor(ContextCompat.getColor(this, android.R.color.holo_blue_bright));
                pickDayButtonFloat.setBackgroundColor(ContextCompat.getColor(this, R.color.high));
                updateMapByRange(dateRange.get(0).getMillis(), dateRange.get(1).getMillis());
                break;
        }

        switch (OPTIONS[1]) {
            case SHOW_INTENSITY:
                intensityButtonFloat.setBackgroundColor(ContextCompat.getColor(this, android.R.color.holo_blue_bright));
                distressButtonFloat.setBackgroundColor(ContextCompat.getColor(this, R.color.graph_color2));

                break;
            case SHOW_DISTRESS:
                distressButtonFloat.setBackgroundColor(ContextCompat.getColor(this, android.R.color.holo_blue_bright));
                intensityButtonFloat.setBackgroundColor(ContextCompat.getColor(this, R.color.graph_color7));
                break;
        }
    }

    private void updateMapByDay(long date) {
        updateMapByRange(Utils.getDayStart(date, 1), Utils.getDaysEnd(date));
    }

    private void updateMapByRange(long from, long until) {
        // Check if need to instantiate (avoid setData etc twice)
        ArrayList<LatLng> pos=new ArrayList<>();
        String label="";
//        ArrayList<LatLng> posDistress = SymptomManager.getInstance().getSymptomsLatLonByDistress(DateTime.now().minusDays(10).getMillis(), DateTime.now().getMillis());
        switch (OPTIONS[1]){
            case SHOW_INTENSITY:
                label="Intensity";
                pos = symptomManager.getSymptomsLatLon(from,until);
                break;
            case SHOW_DISTRESS:
                label="Distress";
                pos=symptomManager.getSymptomsLatLonByDistress(from,until);
                break;
            default:
                break;
        }
        mLists.put(label, new DataSet(pos, label));
        try {
            if (mProvider == null) {
                mProvider = new HeatmapTileProvider.Builder().data(
                        mLists.get(label).getData()).build();
                mOverlay = getMap().addTileOverlay(new TileOverlayOptions().tileProvider(mProvider));
                // Render links
            } else {
                mProvider.setData(mLists.get(label).getData());
                mOverlay.clearTileCache();
            }
            // Update attribution
        } catch (IllegalArgumentException e) {
            Log.i(TAG, e.getMessage());
        }
    }

    /**
     * Helper class - stores data sets and sources.
     */
    private class DataSet {
        private ArrayList<LatLng> mDataset;
        private String mUrl;

        public DataSet(ArrayList<LatLng> dataSet, String url) {
            this.mDataset = dataSet;
            this.mUrl = url;
        }

        public ArrayList<LatLng> getData() {
            return mDataset;
        }

        public String getUrl() {
            return mUrl;
        }
    }

}
