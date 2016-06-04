package webinar.pubnub.insitu.adapters;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmBaseAdapter;
import io.realm.RealmResults;
import webinar.pubnub.insitu.R;
import webinar.pubnub.insitu.dialogs.AddMedicationDialog;
import webinar.pubnub.insitu.model.RealmString;
import webinar.pubnub.insitu.model.RoutineMedication;

/**
 * Created by Konstantinos Michail on 5/29/2016.
 */
public class CreateMedicationAdapter extends RealmBaseAdapter<RoutineMedication> {

    @Bind(R.id.routine_medication_tv)
    TextView routineTextView;
    @Bind(R.id.edit_medication)
    ImageButton editImageButton;
    @Bind(R.id.alarm_switch)
    Switch alarmSwitch;
    @Bind(R.id.automatic_collection_switch)
    Switch automaticSwitch;
    @Bind(R.id.activate_flic_switch)
    Switch flicSwitch;
    public CreateMedicationAdapter(Context context, RealmResults<RoutineMedication> medications) {
        super(context, medications, true);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        final RoutineMedication medication = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.medication_setup_list_item, parent, false);
        }

        ButterKnife.bind(this, convertView);

        String daysRepeat = "";
        for (RealmString rs : medication.getDaysRepeat()) {
            if (medication.getDaysRepeat().indexOf(rs) == medication.getDaysRepeat().size() - 1) {
                daysRepeat+= " and " + rs.getValue();
            } else {
                daysRepeat += rs.getValue() + ", ";
            }
        }
        alarmSwitch.setChecked(medication.isAlarmEnabled());
        automaticSwitch.setChecked(medication.isAutomaticLogging());
        flicSwitch.setChecked(medication.isFlicButtonLogging());

        alarmSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Realm realm=Realm.getDefaultInstance();
                realm.beginTransaction();
                medication.setAlarmEnabled(isChecked);
                realm.commitTransaction();

            }
        });

        editImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = ((AppCompatActivity) context).getSupportFragmentManager();
                AddMedicationDialog.newInstance("edit", medication.getId()).show(fm, "editmed");
            }
        });
        automaticSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Realm realm=Realm.getDefaultInstance();
                realm.beginTransaction();
                medication.setAutomaticLogging(isChecked);
                realm.commitTransaction();
            }
        });

        flicSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Realm realm=Realm.getDefaultInstance();
                realm.beginTransaction();
                medication.setFlicButtonLogging(isChecked);
                realm.commitTransaction();
            }
        });

        routineTextView.setText(context.getString(R.string.routine_medication_tv, medication.getMedicationName(), medication.getHourOfRoutineConsumption(), daysRepeat));

        // Return the completed view to render on screen
        return convertView;
    }


}