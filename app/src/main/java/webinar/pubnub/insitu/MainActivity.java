package webinar.pubnub.insitu;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.flic.lib.FlicBroadcastReceiverFlags;
import io.flic.lib.FlicButton;
import io.flic.lib.FlicManager;
import io.flic.lib.FlicManagerInitializedCallback;
import io.realm.RealmResults;
import webinar.pubnub.insitu.managers.PatientManager;
import webinar.pubnub.insitu.managers.RoutineMedicationManager;
import webinar.pubnub.insitu.managers.SymptomManager;
import webinar.pubnub.insitu.maps.HeatmapsDemoActivity;
import webinar.pubnub.insitu.model.Patient;
import webinar.pubnub.insitu.model.RoutineMedication;
import webinar.pubnub.insitu.model.Symptom;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "MainActivity";
    static MainActivity mainActivity;

    Intent serviceIntent;
    FlicButton button;
    HashMap<String, Class<? extends Fragment>> fragmentTitleMap;
    Patient patient;
    RealmResults<RoutineMedication> routineMedications;
    Symptom symptom;
    @Bind(R.id.patient_image)
    ImageView patientImageView;
    @Bind(R.id.main_head)
    TextView mainTextView;
    @Bind(R.id.main_head_sec)
    TextView mainSecTextView;
    @Bind(R.id.last_symptom_layout)
    LinearLayout lastSymptomLinearLayout;
    @Bind(R.id.last_pain)
    TextView lastIntensityTextView;
    @Bind(R.id.last_distress)
    TextView lastDistressTextView;

    public static MainActivity getInstance() {
        return mainActivity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainActivity = this;
        handler=new Handler();
        ButterKnife.bind(this);
        setupLayout();
        startBackgroundService();

        if (getIntent().getExtras() != null) {
            Bundle extras = getIntent().getExtras();
            checkRule(extras.getInt("rule", -1));
        }
    }

    private void checkRule(int rule) {
        switch (rule) {
            case Constants.START_FILL_MORE_DATA_ACTIVITY:

                break;
            default:
                Log.i(TAG, "Rules : Invalid rule");
                break;
        }
    }

    private void setupLayout() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void startBackgroundService() {
        serviceIntent = new Intent(this, BackgroundService.class);
        if (!Utils.isMyServiceRunning(BackgroundService.class, this)) {
            startService(serviceIntent);
        } else {
            Log.i(TAG, "Service running");
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.insight) {
            startActivity(new Intent(this, InsightActivity.class));
        } else if (id == R.id.symptoms_on_map) {
            startActivity(new Intent(this, HeatmapsDemoActivity.class));
        } else if (id == R.id.raw_history) {
            startActivity(new Intent(this, AddMoreInfoActivity.class));
        } else if (id == R.id.app_settings) {
            startActivity(new Intent(mainActivity, SettingsActivity.class));
        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    @Bind(R.id.medication_status_btn)ImageButton medicationStatus;
    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {

        if (requestCode == 1337) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (android.provider.Settings.canDrawOverlays(this)) {
                    // continue here - permission was granted
                }
            }
        } else {
            FlicManager.getInstance(this, new FlicManagerInitializedCallback() {
                @Override
                public void onInitialized(FlicManager manager) {
                    button = manager.completeGrabButton(requestCode, resultCode, data);
                    if (button != null) {
                        button.registerListenForBroadcast(FlicBroadcastReceiverFlags.UP_OR_DOWN | FlicBroadcastReceiverFlags.CLICK_OR_DOUBLE_CLICK | FlicBroadcastReceiverFlags.REMOVED);
//                    button.setActiveMode(false);
                        Toast.makeText(MainActivity.this, "Grabbed a button", Toast.LENGTH_SHORT).show();
                        Log.i(TAG, "button id" + button.getButtonId());
                        Log.i(TAG, "button connection status" + button.getConnectionStatus());

                    } else {
                        Toast.makeText(MainActivity.this, "Did not grab any button", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        stopService(serviceIntent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (BackgroundService.getInstance() != null) {
            setupContent();
        }
    }

    private void setupContent() {
        symptom = SymptomManager.getInstance().getLastSymptom();
        patient = PatientManager.getInstance().getPatient();
        if (patient != null) {
            if (patient.getGender().equals("m")) {
                patientImageView.setImageResource(R.drawable.avatar_jack2);
            } else if (patient.getGender().equals("f")) {
                patientImageView.setImageResource(R.drawable.avatar_maggie2);
            } else {
                patientImageView.setImageResource(android.R.drawable.picture_frame);
            }
            if (!patient.getPatientName().equals("")) {
                mainTextView.setText(getString(R.string.main_head, patient.getPatientName()));
            } else {
                mainTextView.setText("Welcome");
            }
            if (symptom != null) {
                lastSymptomLinearLayout.setVisibility(View.VISIBLE);
                mainSecTextView.setText(getString(R.string.main_head_sec, Utils.getDateFormatForListview(symptom.getTimestamp()), symptom.getContext().getAddress()));
                lastIntensityTextView.setText(getString(R.string.last_pain, String.valueOf(symptom.getIntensity())));
                if (symptom.getDescription() != null) {
                    lastDistressTextView.setText(getString(R.string.last_distress, String.valueOf(symptom.getDescription().getDistress())));
                }
            } else {
                mainSecTextView.setText("No pain occurences registered");
                lastDistressTextView.setVisibility(View.GONE);
                lastIntensityTextView.setVisibility(View.GONE);
            }
            if(timer==null){
                timer=new Timer();
                timer.scheduleAtFixedRate(timerTask,0,30000);

            }
        }
    }

    Handler handler;
    Timer timer;
    TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {
            handler.post(new Runnable() {
                @Override
                public void run() {
//                    if (RoutineMedicationManager.getInstance().isRoutineMedicationFollowed()){
                        medicationStatus.setImageResource(android.R.drawable.btn_star_big_on);}
//                }
            });
        }
    };

    public FlicButton getButton() {
        return button;
    }

}
