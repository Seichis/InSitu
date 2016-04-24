package webinar.pubnub.insitu.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.andreabaccega.widget.FormEditText;
import com.gc.materialdesign.views.ButtonFloat;
import com.nhaarman.listviewanimations.itemmanipulation.expandablelistitem.ExpandableListItemAdapter;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnFocusChange;
import webinar.pubnub.insitu.AddMoreInfoActivity;
import webinar.pubnub.insitu.R;
import webinar.pubnub.insitu.Utils;
import webinar.pubnub.insitu.dialogs.MoreInfoDialog;
import webinar.pubnub.insitu.managers.SymptomManager;
import webinar.pubnub.insitu.model.Symptom;

public class MyExpandableListItemAdapter extends ExpandableListItemAdapter<Symptom> {

    private static final String TAG = "Expandable";
    private final Context mContext;

    @Bind(R.id.body_part_spinner)Spinner bodyPartsSpinner;
    @Bind(R.id.non_drug_tech_spinner)Spinner nonDrugSpinner;
    @Bind(R.id.button_medication_details)
    ButtonFloat medicationButton;
    /**
     * Creates a new ExpandableListItemAdapter with the specified list, or an empty list if
     * items == null.
     */
    public MyExpandableListItemAdapter(final Context context) {
        super(context, SymptomManager.getInstance().getTodaySymptomsWithoutDescription());
        mContext = context;

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
        medicationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MoreInfoDialog.newInstance((int)s.getId(),R.layout.medication_details).show(AddMoreInfoActivity.getInstance().getSupportFragmentManager(),"Medication");
            }
        });

        return convertView;
    }
}