package webinar.pubnub.insitu;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import webinar.pubnub.insitu.fragments.ManageDiariesFragment;
import webinar.pubnub.insitu.managers.PatientManager;

public class CreatePatientActivity extends AppCompatActivity implements ManageDiariesFragment.OnManageDiariesInteractionListener {
    // 0 male,1 female
    static int GENDER = -1;
    @Bind(R.id.viewpagerCreatePatient)
    ViewPager viewPager;
    @Bind(R.id.viewpagertabCreatePatient)
    SmartTabLayout viewPagerTab;
    FragmentPagerItemAdapter adapter;
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
    @Bind(R.id.add_diary_and_symptoms)
    RelativeLayout diarySymptomsLayout;

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

            noPatientLayout.setVisibility(View.GONE);
            setupDiaryLayout();

        }
    }

    boolean isPatientValid() {
        return (PatientManager.getInstance().getPatient() != null);
    }

    private void setupDiaryLayout() {
        diarySymptomsLayout.setVisibility(View.VISIBLE);
        FragmentPagerItems.Creator creator = FragmentPagerItems.with(this);
        creator.add("Diary", ManageDiariesFragment.class);
//        creator.add("Symptoms", SymptomFragment.class);
        adapter = new FragmentPagerItemAdapter(getSupportFragmentManager(), creator.create());
        viewPager.setAdapter(adapter);
        viewPagerTab.setViewPager(viewPager);
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
        if (isPatientValid()) {
            noPatientLayout.setVisibility(View.GONE);
            diarySymptomsLayout.setVisibility(View.VISIBLE);
        } else {
            noPatientLayout.setVisibility(View.VISIBLE);
            diarySymptomsLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void OnManageDiariesInteraction(Uri uri) {

    }
}