package webinar.pubnub.insitu.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.andreabaccega.widget.FormEditText;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.util.Calendar;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnFocusChange;
import webinar.pubnub.insitu.R;
import webinar.pubnub.insitu.Utils;
import webinar.pubnub.insitu.managers.SymptomManager;
import webinar.pubnub.insitu.model.Symptom;


public class MoreInfoDialog extends DialogFragment implements
        TimePickerDialog.OnTimeSetListener {
    @Nullable
    @Bind(R.id.medication_details)
    FormEditText medicationEditText;
    @Nullable
    @Bind(R.id.input_time_dummy)
    EditText inputTime;
    Symptom symptom;
    SymptomManager symptomManager;
    @Nullable
    @Bind(R.id.body_part_details_where)
    FormEditText bodyPartWhereFormEditText;
    @Nullable
    @Bind(R.id.body_part_details)
    FormEditText bodyPartDetailsFormEditText;
    @Nullable
    @Bind(R.id.body_part_details_where_tv)
    TextView bodyPartWhereTextView;
    @Nullable
    @Bind(R.id.body_dialog_details_title)
    TextView bodyDetailsTitle;

    public static MoreInfoDialog newInstance(int symptomId, int layout) {
        MoreInfoDialog fragment = new MoreInfoDialog();
        Bundle args = new Bundle();
        args.putInt("id", symptomId);
        args.putInt("layout", layout);
        fragment.setArguments(args);

        return fragment;
    }

    @Nullable
    @OnClick(R.id.pick_time_medication)
    void pickTime() {
        openPickTimeDialog();
    }

    @Nullable
    @OnFocusChange(R.id.input_time_dummy)
    void inputTimeFocus() {
        if (inputTime.hasFocus()) {
            inputTime.setText("");
            pickTime();
        }
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
        tpd.setTitle("What time did you take your medication?");

        int[] hourMin = Utils.getHourAndMin(symptom.getTimestamp());
        tpd.setMinTime(hourMin[0], hourMin[1], 0);

        tpd.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                Log.d("TimePicker", "Dialog was cancelled");
            }
        });
        tpd.show(getActivity().getFragmentManager(), "Timepickerdialog");
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int id = getArguments().getInt("id");
        int layout = getArguments().getInt("layout");
        symptomManager = SymptomManager.getInstance();
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(layout, null);
        ButterKnife.bind(this, view);


        symptom = SymptomManager.getInstance().getSymptomById(id);

        setupViews();
        return new AlertDialog.Builder(getActivity())
                .setView(view)
//                .setTitle(getString(R.string.more_info_title))
//                .setMessage(getString(R.string.more_info_message))
                .setPositiveButton(R.string.done, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (medicationEditText != null && medicationEditText.getText().length() > 0) {
                            symptomManager.addMedicationDescription(symptom, medicationEditText.getText().toString());

                        }
                        String bodyPartDetails = "";
                        if (bodyPartDetailsFormEditText != null && bodyPartDetailsFormEditText.getText().length() > 0) {
                            bodyPartDetails += bodyPartDetailsFormEditText.getText().toString();
                        }
                        if (bodyPartWhereFormEditText != null && bodyPartWhereFormEditText.getText().length() > 0) {
                            bodyPartDetails += bodyPartWhereFormEditText.getText().toString();
                        }
                        if (!bodyPartDetails.isEmpty()) {
                            symptomManager.addBodyPartDescription(symptom, bodyPartDetails);
                        }
                        dialogInterface.dismiss();
                    }
                })
                .create();
    }

    private void setupViews() {
        if (medicationEditText != null) {
            if (symptom.getDescription().getMedicineName() != null) {
                medicationEditText.setText(symptom.getDescription().getMedicineName());
                inputTime.setText(Utils.convertFromMillisToHourMinute(symptom.getDescription().getDateMedicationConsumption()));
            }
        }
        if (bodyPartDetailsFormEditText != null) {
            if (symptom.getDescription().getBodyPartDetails() != null) {
                bodyPartDetailsFormEditText.setText(symptom.getDescription().getBodyPartDetails());
            }
        }

        if (bodyPartWhereTextView != null) {
            bodyPartWhereTextView.setText(getString(R.string.body_part_details_where_tv, symptom.getDescription().getBodyPart()));
        }
        if (bodyDetailsTitle != null) {
            bodyDetailsTitle.setText(getString(R.string.body_part_details_dialog_title, symptom.getDescription().getBodyPart()));
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
    }

    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int second) {
        long date = Utils.getDateFromHourAndMin(hourOfDay, minute);

        SymptomManager.getInstance().addMedicationDate(symptom, date);

        inputTime.setText(Utils.getHourAndMinuteFormat(hourOfDay, minute));
    }
}
