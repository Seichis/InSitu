package webinar.pubnub.insitu;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.RealmResults;
import webinar.pubnub.insitu.adapters.CreateMedicationAdapter;
import webinar.pubnub.insitu.dialogs.AddMedicationDialog;
import webinar.pubnub.insitu.fragments.ManageDiariesFragment;
import webinar.pubnub.insitu.managers.DiaryManager;
import webinar.pubnub.insitu.managers.PatientManager;
import webinar.pubnub.insitu.managers.RoutineMedicationManager;
import webinar.pubnub.insitu.model.RoutineMedication;

public class CreatePatientActivity extends AppCompatActivity implements ManageDiariesFragment.OnManageDiariesInteractionListener {
    private static final String TAG = "CreatePatient";
    // 0 male,1 female
    static int GENDER = -1;
    static CreatePatientActivity createPatientActivity;
    @Bind(R.id.no_patient_layout)
    LinearLayout noPatientLayout;
    @Bind(R.id.save_patient_info)
    Button savePatientButton;
    @Bind(R.id.male)
    ImageButton maleButton;
    @Bind(R.id.female)
    ImageButton femaleButton;
    @Bind(R.id.name_input)
    EditText nameInputEditText;
    @Bind(R.id.medication_info_layout)
    LinearLayout medicationLayout;
    @Bind(R.id.add_medication_lv)
    ListView listView;
    CreateMedicationAdapter adapter;
    RealmResults<RoutineMedication> medications;

    public static CreatePatientActivity getInstance() {
        return createPatientActivity;
    }

    @OnClick(R.id.fab_add_medication)
    void addMedication() {
        AddMedicationDialog.newInstance("create", "").show(getSupportFragmentManager(), "addmed");
    }

    @OnClick(R.id.male)
    void male() {
        GENDER = 0;
        maleButton.setBackgroundColor(ContextCompat.getColor(this, R.color.high));
        femaleButton.setBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent));
        Log.i("gender", "male" + GENDER);
    }

    @OnClick(R.id.female)
    void female() {
        GENDER = 1;
        maleButton.setBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent));
        femaleButton.setBackgroundColor(ContextCompat.getColor(this, R.color.high));
    }

    @OnClick(R.id.save_patient_info)
    void savePatient() {
        String name, gender = "";

        if (nameInputEditText.getText().length() > 0) {
            name = nameInputEditText.getText().toString();
            switch (GENDER) {
                case 0:
                    gender = "f";
                    break;
                case 1:
                    gender = "m";
                    break;
                default:
                    gender = "NA";
                    break;
            }
            PatientManager.getInstance().createNewProfile(name, gender);
            DiaryManager.getInstance().createDiary(getString(R.string.fixed_diary_name, name), "Pain management");
            noPatientLayout.setVisibility(View.GONE);
            medicationLayout.setVisibility(View.VISIBLE);
            setupDiaryLayout();

        }
    }

    boolean isPatientValid() {
        return (PatientManager.getInstance().getPatient() != null);
    }

    private void setupDiaryLayout() {
        FragmentPagerItems.Creator creator = FragmentPagerItems.with(this);
        creator.add("Diary", ManageDiariesFragment.class);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_patient);
        ButterKnife.bind(this);
//        refreshRoutineMedicationList();
        if (isPatientValid()) {
            noPatientLayout.setVisibility(View.GONE);
            medicationLayout.setVisibility(View.VISIBLE);
        } else {
            noPatientLayout.setVisibility(View.VISIBLE);
            medicationLayout.setVisibility(View.GONE);
        }
        medications = RoutineMedicationManager.getInstance().getRoutineMedications();
        Log.i(TAG, "med created, meds size : " + medications.size());
        adapter = new CreateMedicationAdapter(this, medications);

        listView.setAdapter(adapter);
        createPatientActivity = this;
    }

    @OnClick(R.id.save_medication_info)
    void done() {
        this.finish();
    }

    //    public void refreshRoutineMedicationList(){
//
//        medications = RoutineMedicationManager.getInstance().getRoutineMedications();
//        Log.i(TAG, "med created, meds size : " + medications.size());
//        adapter=new CreateMedicationAdapter(this,medications);
//
//        listView.setAdapter(adapter);
//    }
    @Override
    public void OnManageDiariesInteraction(Uri uri) {

    }
}