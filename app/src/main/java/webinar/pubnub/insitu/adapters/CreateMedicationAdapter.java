package webinar.pubnub.insitu.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;

import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.util.Calendar;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import io.realm.RealmBaseAdapter;
import io.realm.RealmResults;
import webinar.pubnub.insitu.R;
import webinar.pubnub.insitu.model.RoutineMedication;

/**
 * Created by Konstantinos Michail on 5/29/2016.
 */
public class CreateMedicationAdapter extends RealmBaseAdapter<RoutineMedication> {

    static int pos;
    @Bind(R.id.routine_medication_tv) TextView routineTextView;
    @Bind(R.id.edit_medication)
    ImageButton editImageButton;
    @Bind(R.id.alarm_switch)Switch alarmSwitch;
    @Bind(R.id.automatic_collection_switch) Switch automaticSwitch;
    @Bind(R.id.activate_flic_switch)Switch flicSwitch;

    public CreateMedicationAdapter(Context context, RealmResults<RoutineMedication> medications) {
        super(context,medications,true);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        RoutineMedication medication = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.medication_setup_list_item, parent, false);
        }
        setupView();

        ButterKnife.bind(this, convertView);
        pos = position;
        // Return the completed view to render on screen
        return convertView;
    }

    private void setupView() {
        getItem(pos);
    }


}