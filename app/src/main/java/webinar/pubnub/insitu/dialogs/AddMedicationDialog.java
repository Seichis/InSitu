package webinar.pubnub.insitu.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.UUID;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmList;
import webinar.pubnub.insitu.R;
import webinar.pubnub.insitu.managers.RoutineMedicationManager;
import webinar.pubnub.insitu.model.RealmString;
import webinar.pubnub.insitu.model.RoutineMedication;

/**
 * Created by Konstantinos Michail on 5/29/2016.
 */
public class AddMedicationDialog extends DialogFragment implements
        TimePickerDialog.OnTimeSetListener {

    public static final String TAG = "AddMedDialog";
    @Bind(R.id.input_medication)
    EditText inputMedEditText;
    @Bind(R.id.checkbox_monday)
    CheckBox mondayCheckBox;
    @Bind(R.id.checkbox_tuesday)
    CheckBox tuesdayCheckBox;
    @Bind(R.id.checkbox_wednesday)
    CheckBox wednesdayCheckBox;
    @Bind(R.id.checkbox_thursday)
    CheckBox thursdayCheckBox;
    @Bind(R.id.checkbox_friday)
    CheckBox fridayCheckBox;
    @Bind(R.id.checkbox_saturday)
    CheckBox saturdayCheckBox;
    @Bind(R.id.checkbox_sunday)
    CheckBox sundayCheckBox;
    @Bind(R.id.select_deselect_all)
    CheckBox selectDeselectCheckBox;
    @Bind(R.id.automatic_collection_switch)
    Switch automaticSwitch;
    @Bind(R.id.alarm_switch)
    Switch alarmSwitch;
    @Bind(R.id.activate_flic_switch)
    Switch flicSwitch;
    @Bind(R.id.show_chosen_time_tv)
    TextView medTimeTv;
    @Bind(R.id.input_amount_med_routine)
    EditText inputAmountMed;
    RoutineMedication routineMedication;
    boolean isAccessed;
    ArrayList<CheckBox> checkBoxes;

    public static AddMedicationDialog newInstance(String mode, String routineMedicationId) {
        AddMedicationDialog fragment = new AddMedicationDialog();
        Bundle args = new Bundle();
        args.putInt("layout", R.layout.add_medication_dialog);
        args.putString("mode", mode);
        args.putString("id", routineMedicationId);
        fragment.setArguments(args);

        return fragment;
    }

    @OnCheckedChanged(R.id.select_deselect_all)
    void selectDeselect() {
        if (selectDeselectCheckBox.getText().toString().equals(getString(R.string.deselect_all))) {
            for (CheckBox cb : checkBoxes) {
                cb.setChecked(false);
                selectDeselectCheckBox.setText(getString(R.string.select_all));
            }
        } else {
            for (CheckBox cb : checkBoxes) {
                cb.setChecked(true);
                selectDeselectCheckBox.setText(getString(R.string.deselect_all));
            }
        }
    }

    @OnClick(R.id.pick_time_routine_med)
    void pickTime() {
        openPickTimeDialog();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int layout = getArguments().getInt("layout");
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(layout, null);
        ButterKnife.bind(this, view);
        isAccessed = false;
        checkBoxes = new ArrayList<>();
        checkBoxes.add(fridayCheckBox);
        checkBoxes.add(mondayCheckBox);
        checkBoxes.add(tuesdayCheckBox);
        checkBoxes.add(saturdayCheckBox);
        checkBoxes.add(sundayCheckBox);
        checkBoxes.add(thursdayCheckBox);
        checkBoxes.add(wednesdayCheckBox);
        if (getArguments().getString("mode", null).equals("edit")) {
            routineMedication = RoutineMedicationManager.getInstance().getById(getArguments().getString("id"));
            ArrayList<String> days = new ArrayList<>();
            for (RealmString rs : routineMedication.getDaysRepeat()) {
                days.add(rs.getValue());
            }
            for (CheckBox cb : checkBoxes) {
                if (!days.contains(cb.getText().toString())) {
                    cb.setChecked(false);
                }
            }
            inputMedEditText.setText(routineMedication.getMedicationName());
            medTimeTv.setText(getContext().getString(R.string.medication_time_textview, routineMedication.getHourOfRoutineConsumption()));
            medTimeTv.setVisibility(View.VISIBLE);
            alarmSwitch.setChecked(routineMedication.isAlarmEnabled());
            automaticSwitch.setChecked(routineMedication.isAutomaticLogging());
            flicSwitch.setChecked(routineMedication.isFlicButtonLogging());
        } else {
            routineMedication = new RoutineMedication();
        }

        return new AlertDialog.Builder(getActivity())
                .setView(view)
                .setPositiveButton(R.string.done, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (getArguments().getString("mode", null).equals("edit")) {
                            Realm realm = Realm.getDefaultInstance();
                            realm.beginTransaction();
                            routineMedication.setMedicationName(inputMedEditText.getText().toString());
                            routineMedication.setAmount(inputAmountMed.getText().toString());
                            routineMedication.setAlarmEnabled(alarmSwitch.isChecked());
                            routineMedication.setAutomaticLogging(automaticSwitch.isChecked());
                            routineMedication.setFlicButtonLogging(flicSwitch.isChecked());
                            RealmList<RealmString> days = routineMedication.getDaysRepeat();
                            for (CheckBox cb : checkBoxes) {
                                if (cb.isChecked()) {
                                    RealmString day = realm.where(RealmString.class).equalTo("val", cb.getText().toString()).findFirst();

                                    if (day == null) {
                                        day = new RealmString();
                                        day.setValue(cb.getText().toString());
                                        days.add(day);
                                    } else {
                                        if (!days.contains(day)) {
                                            days.add(day);
                                        }
                                    }
                                }
                            }
                            routineMedication.setDaysRepeat(days);
                            realm.commitTransaction();
                            dialogInterface.dismiss();
                        } else {
                            if ((isAccessed && (inputMedEditText.getText().length() == 0 || inputAmountMed.getText().length() == 0)) || (!isAccessed && (inputAmountMed.getText().length() > 0 || inputMedEditText.getText().length() > 0))) {
                                Toast.makeText(getContext(), "Please fill all details regarding the medication", Toast.LENGTH_LONG).show();
                                dialogInterface.dismiss();
                            } else {
                                routineMedication.setMedicationName(inputMedEditText.getText().toString());
                                routineMedication.setAmount(inputAmountMed.getText().toString());
                                routineMedication.setAlarmEnabled(alarmSwitch.isChecked());
                                routineMedication.setAutomaticLogging(automaticSwitch.isChecked());
                                routineMedication.setFlicButtonLogging(flicSwitch.isChecked());
                                routineMedication.setId(UUID.randomUUID().toString());
                                RealmList<RealmString> days = new RealmList<>();
                                for (CheckBox cb : checkBoxes) {
                                    if (cb.isChecked()) {
                                        Realm realm=Realm.getDefaultInstance();
                                        RealmString day = realm.where(RealmString.class).equalTo("val", cb.getText().toString()).findFirst();
                                        if (day == null) {
                                            day = new RealmString();
                                            day.setValue(cb.getText().toString());
                                            days.add(day);
                                        } else {

                                                days.add(day);
                                        }
                                    }
                                }
                                routineMedication.setDaysRepeat(days);
                                RoutineMedicationManager.getInstance().createRoutineMed(routineMedication);
//                            CreatePatientActivity.getInstance().refreshRoutineMedicationList();
                                dialogInterface.dismiss();
                            }
                        }
                    }
                })
                .create();
    }

    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int second) {
        routineMedication.setHourOfRoutineConsumption(hourOfDay + ":" + minute);
        medTimeTv.setVisibility(View.VISIBLE);
        isAccessed = true;
        medTimeTv.setText(getContext().getString(R.string.medication_time_textview, hourOfDay + ":" + minute));
    }

    void openPickTimeDialog() {
        Calendar now = Calendar.getInstance();
        TimePickerDialog tpd = TimePickerDialog.newInstance(
                this,
                now.get(Calendar.HOUR_OF_DAY),
                now.get(Calendar.MINUTE),
                true
        );

        tpd.dismissOnPause(true);
        tpd.enableSeconds(false);
        tpd.setAccentColor(ContextCompat.getColor(getContext(), R.color.graph_color5));
        tpd.setTitle("What time do you take your medication?");


        tpd.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                Log.d("TimePicker", "Dialog was cancelled");
            }
        });
        tpd.show(((AppCompatActivity) getContext()).getFragmentManager(), "Timepickerdialog");
    }
}
