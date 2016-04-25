package webinar.pubnub.insitu.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;

import com.gc.materialdesign.views.ButtonFloat;
import com.gc.materialdesign.views.Slider;
import com.nhaarman.listviewanimations.itemmanipulation.expandablelistitem.ExpandableListItemAdapter;

import org.joda.time.DateTime;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.Sort;
import webinar.pubnub.insitu.AddMoreInfoActivity;
import webinar.pubnub.insitu.R;
import webinar.pubnub.insitu.Utils;
import webinar.pubnub.insitu.dialogs.MoreInfoDialog;
import webinar.pubnub.insitu.managers.SymptomManager;
import webinar.pubnub.insitu.model.Symptom;

public class MyExpandableListItemAdapter extends ExpandableListItemAdapter<Symptom> {

    private static final String TAG = "Expandable";
    private final Context mContext;
    SymptomManager symptomManager;
    @Bind(R.id.body_part_spinner)
    Spinner bodyPartsSpinner;
    @Bind(R.id.non_drug_tech_spinner)
    Spinner nonDrugSpinner;
    @Bind(R.id.button_medication_details)
    ButtonFloat medicationButton;
    @Bind(R.id.intensity_slider)
    Slider intensitySlider;
    @Bind(R.id.distress_slider)
    Slider distressSlider;
    @Bind(R.id.expand_title_more_info)
    TextView titleTextView;

    /**
     * Creates a new ExpandableListItemAdapter with the specified list, or an empty list if
     * items == null.
     */
    public MyExpandableListItemAdapter(final Context context) {
//        super(context, SymptomManager.getInstance().getTodaySymptomsWithoutDescription());
        super(context, SymptomManager.getInstance().getAllSymptomsByRange(DateTime.now().minusDays(2).getMillis(), DateTime.now().getMillis()).where().findAllSorted("timestamp", Sort.DESCENDING));
        mContext = context;
        symptomManager = SymptomManager.getInstance();

    }


    @NonNull
    @Override
    public View getTitleView(final int position, final View convertView, @NonNull final ViewGroup parent) {
        TextView tv = (TextView) convertView;
        if (tv == null) {
            tv = new TextView(mContext);
        }
        Symptom s = getItem(position);
        tv.setText(mContext.getString(R.string.symptom_details_title, s.getIntensity(), Utils.getDateFormatForListview(s.getTimestamp())));

        return tv;

    }

    @NonNull
    @Override
    public View getContentView(final int position, View convertView, @NonNull final ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.content_expand_details, parent, false);
        }
        ButterKnife.bind(this, convertView);
        final Symptom s = getItem(position);
        if (s.getContext() != null) {
            titleTextView.setText(mContext.getString(R.string.more_info_head, "at " + s.getContext().getAddress(), Utils.getDateFormatForListview(s.getTimestamp())));
        } else {
            titleTextView.setText(mContext.getString(R.string.more_info_head, "", Utils.getDateFormatForListview(s.getTimestamp())));
        }
        medicationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MoreInfoDialog.newInstance((int) s.getId(), R.layout.medication_details).show(AddMoreInfoActivity.getInstance().getSupportFragmentManager(), "Medication");
            }
        });


        bodyPartsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    String stmp = parent.getItemAtPosition(position).toString();
                    symptomManager.addBodyPart(s, stmp);
                    MoreInfoDialog.newInstance((int) s.getId(), R.layout.body_parts_details).show(AddMoreInfoActivity.getInstance().getSupportFragmentManager(), "Bodyparts");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        nonDrugSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (position > 0) {
                    String stmp = parent.getItemAtPosition(position).toString();
                    symptomManager.addNonDrugTechnique(s, stmp);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        intensitySlider.setValue((int) (s.getIntensity() * 100));
        intensitySlider.setOnValueChangedListener(new Slider.OnValueChangedListener() {
            @Override
            public void onValueChanged(int i) {
                symptomManager.addNewIntensity(s, (float) i / 100f);
            }
        });

        if (s.getDescription() != null) {
            if (s.getDescription().getDistress() > 0) {
                distressSlider.setValue(((int) (s.getDescription().getDistress() * 100)));
            }
        }
        distressSlider.setOnValueChangedListener(new Slider.OnValueChangedListener() {
            @Override
            public void onValueChanged(int i) {
                symptomManager.addDistress(s, (float) i / 100f);
            }
        });


        return convertView;
    }
}