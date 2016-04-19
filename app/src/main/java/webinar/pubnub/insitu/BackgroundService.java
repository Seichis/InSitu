package webinar.pubnub.insitu;

import android.Manifest;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.common.base.Functions;
import com.google.common.collect.EvictingQueue;
import com.google.common.collect.Ordering;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Queue;

import io.flic.lib.FlicAppNotInstalledException;
import io.flic.lib.FlicManager;
import io.flic.lib.FlicManagerInitializedCallback;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import webinar.pubnub.insitu.activityrecognition.DetectedActivitiesIntentService;
import webinar.pubnub.insitu.managers.DataManager;
import webinar.pubnub.insitu.managers.DiaryManager;
import webinar.pubnub.insitu.managers.PatientManager;
import webinar.pubnub.insitu.managers.SettingsManager;
import webinar.pubnub.insitu.managers.SymptomManager;
import webinar.pubnub.insitu.model.Settings;

public class BackgroundService extends Service implements IBackgroundSettingsService, LocationListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, ResultCallback<Status> {
    private final static String TAG = BackgroundService.class.getName();
    private static final int notif_id = 1337;
    static Notification notification;
    private static PendingIntent pi;
    private static BackgroundService backgroundService;
    protected GoogleApiClient mGoogleApiClient;
    protected ActivityDetectionBroadcastReceiver mBroadcastReceiver;
    EvictingQueue<String> activityQueue;
    MainActivity mainActivity = null;
//    AlarmBReceiver alarmBReceiver = null;
    AlertDialog alert;
    DataManager dataManager;
    PatientManager patientManager;
    DiaryManager diaryManager;
    SymptomManager symptomManager;
    Settings settings;
    SettingsManager settingsManager;
    private Location currentLocation;
    private GoogleApiClient locationClient;
    private Realm realm;
    private RealmConfiguration realmConfig;
    public BackgroundService() {
    }

    public static BackgroundService getInstance() {
        return backgroundService;
    }

    public Location getCurrentLocation() {
        return currentLocation;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mainActivity = MainActivity.getInstance();
        activityQueue = EvictingQueue.create(3);
        backgroundService = this;
        initializeManagers();
        runAsForeground();
        buildAlertMessageNoGps();
        buildLocationClient();
        setupActivityRecognition();
        checkSettings();
        return START_NOT_STICKY;
    }

    private void initializeManagers() {
        // These operations are small enough that
        // we can generally safely run them on the UI thread.
        // Create the Realm configuration
        realmConfig = new RealmConfiguration.Builder(backgroundService).build();
        // Open the Realm for the UI thread.
        Realm.setDefaultConfiguration(realmConfig);

        realm = Realm.getDefaultInstance();
        dataManager = DataManager.getInstance();
        dataManager.init(backgroundService);

        diaryManager = DiaryManager.getInstance();
        diaryManager.init(backgroundService,realm);

        symptomManager = SymptomManager.getInstance();
        symptomManager.init(backgroundService,realm);

        patientManager = PatientManager.getInstance();
        patientManager.init(backgroundService,realm);

        settingsManager = SettingsManager.getInstance();
        settingsManager.init(backgroundService,realm);


    }

    private void setupActivityRecognition() {
        buildGoogleApiClient();

        mGoogleApiClient.connect();
        mBroadcastReceiver = new ActivityDetectionBroadcastReceiver();

        LocalBroadcastManager.getInstance(this).registerReceiver(mBroadcastReceiver,
                new IntentFilter(Constants.BROADCAST_ACTION));

    }

    /**
     * Builds a GoogleApiClient. Uses the {@code #addApi} method to request the
     * ActivityRecognition API.
     */
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(ActivityRecognition.API)
                .build();
    }

    private void buildLocationClient() {
        // Create a new location client, using the enclosing class to handle callbacks.
        locationClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .build();
        locationClient.connect();

    }

    void checkSettings(){
        settings = SettingsManager.getInstance().getSettings();
        if (settings==null) {
            startActivity(new Intent(backgroundService, CreatePatientActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        } else {
            Log.i("settings", settings.getPatient().getPatientName());
        }
    }
    private void flicSetup() {
        FlicManager.setAppCredentials("59eab426-39a4-4457-8e7d-2f67f9733d54", "d0ef92f6-a494-4f3d-96c0-841c6b434909", "ScaleMeasurement");
        if (mainActivity != null) {
            if (mainActivity.getButton() == null) {
                try {
                    FlicManager.getInstance(backgroundService, new FlicManagerInitializedCallback() {
                        @Override
                        public void onInitialized(FlicManager manager) {
                            manager.initiateGrabButton(mainActivity);

                        }
                    });
                } catch (FlicAppNotInstalledException err) {
                    Toast.makeText(backgroundService, "Flic App is not installed", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        // If the client is connected
        if (locationClient.isConnected()) {
            // After disconnect() is called, the client is considered "dead".
            locationClient.disconnect();

        }
        removeActivityUpdates();
        mGoogleApiClient.disconnect();
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void runAsForeground() {
        PendingIntent contentIntent = PendingIntent.getActivity(backgroundService,
                0, new Intent(backgroundService, MainActivity.class), PendingIntent.FLAG_ONE_SHOT);
        notification = getMyActivityNotification("No run yet", contentIntent);
//        if (alarmBReceiver==null) {
//            alarmBReceiver = new AlarmBReceiver();
//            alarmBReceiver.setAlarm(backgroundService);
//        }
        backgroundService.startForeground(notif_id, notification);
    }

    private Notification getMyActivityNotification(String text, PendingIntent pendingIntent) {
        // The PendingIntent to launch our activity if the user selects
        // this notification
        CharSequence title = getText(R.string.app_name);

        return new Notification.Builder(backgroundService)
                .setContentTitle(title)
                .setContentText(text)
                .setSmallIcon(R.drawable.common_google_signin_btn_icon_light)
                .setContentIntent(pendingIntent).build();
    }

    /**
     * This is the method that can be called to update the Notification
     */
    public void updateNotification(String text, PendingIntent pendingIntent) {


        notification = getMyActivityNotification(text, pendingIntent);

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(notif_id, notification);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (locationClient.isConnected()) {
            currentLocation = getLocation();
        }
        if (mGoogleApiClient.isConnected()) {
            requestActivityUpdates();
        }

    }


    /**
     * Registers for activity recognition updates using
     * {@link com.google.android.gms.location.ActivityRecognitionApi#requestActivityUpdates} which
     * returns a {@link com.google.android.gms.common.api.PendingResult}. Since this activity
     * implements the PendingResult interface, the activity itself receives the callback, and the
     * code within {@code onResult} executes. Note: once {@code requestActivityUpdates()} completes
     * successfully, the {@code DetectedActivitiesIntentService} starts receiving callbacks when
     * activities are detected.
     */
    public void requestActivityUpdates() {
        ActivityRecognition.ActivityRecognitionApi.requestActivityUpdates(
                mGoogleApiClient,
                Constants.DETECTION_INTERVAL_IN_MILLISECONDS,
                getActivityDetectionPendingIntent()
        ).setResultCallback(this);
    }

    /**
     * Removes activity recognition updates using
     * {@link com.google.android.gms.location.ActivityRecognitionApi#removeActivityUpdates} which
     * returns a {@link com.google.android.gms.common.api.PendingResult}. Since this activity
     * implements the PendingResult interface, the activity itself receives the callback, and the
     * code within {@code onResult} executes. Note: once {@code removeActivityUpdates()} completes
     * successfully, the {@code DetectedActivitiesIntentService} stops receiving callbacks about
     * detected activities.
     */
    public void removeActivityUpdates() {
        if (!mGoogleApiClient.isConnected()) {
            Toast.makeText(this, getString(R.string.not_connected), Toast.LENGTH_SHORT).show();
            return;
        }
        // Remove all activity updates for the PendingIntent that was used to request activity
        // updates.
        ActivityRecognition.ActivityRecognitionApi.removeActivityUpdates(
                mGoogleApiClient,
                getActivityDetectionPendingIntent()
        ).setResultCallback(this);
    }

    /**
     * Runs when the result of calling requestActivityUpdates() and removeActivityUpdates() becomes
     * available. Either method can complete successfully or with an error.
     *
     * @param status The Status returned through a PendingIntent when requestActivityUpdates()
     *               or removeActivityUpdates() are called.
     */
    public void onResult(Status status) {
        if (status.isSuccess()) {
            // Toggle the status of activity updates requested, and save in shared preferences.
            boolean requestingUpdates = !getUpdatesRequestedState();
            setUpdatesRequestedState(requestingUpdates);

            // Update the UI. Requesting activity updates enables the Remove Activity Updates
            // button, and removing activity updates enables the Add Activity Updates button.

            Toast.makeText(
                    this,
                    getString(requestingUpdates ? R.string.activity_updates_added :
                            R.string.activity_updates_removed),
                    Toast.LENGTH_SHORT
            ).show();
        } else {
            Log.e(TAG, "Error adding or removing activity detection: " + status.getStatusMessage());
        }
    }

    /**
     * Gets a PendingIntent to be sent for each activity detection.
     */
    private PendingIntent getActivityDetectionPendingIntent() {
        Intent intent = new Intent(this, DetectedActivitiesIntentService.class);

        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when calling
        // requestActivityUpdates() and removeActivityUpdates().
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
        locationClient.connect();
    }

    @Override
    public void onLocationChanged(Location location) {
        currentLocation = location;
    }

    private void buildAlertMessageNoGps() {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                            startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                            dialog.cancel();
                        }
                    });
            alert = builder.create();

            alert.show();
        }

    }


    private Location getLocation() {
        // If Google Play Services is available
        if (servicesConnected()) {
            // Get the current location
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                checkPermissions();
                Log.i(TAG, "no permissions");
                return null;
            }
            Log.i(TAG, "getting  location");

            return LocationServices.FusedLocationApi.getLastLocation(locationClient);
        } else {
            return null;
        }
    }

    protected void checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) { // Permission was added in API Level 16
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED) {
                if (mainActivity != null) {
                    mainActivity.requestFineLocationPermission();
                }
            }
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED) {
                if (mainActivity != null) {
                    mainActivity.requestCoarseLocationPermission();
                }
            }
        }
    }


    /*
    * Verify that Google Play services is available before making a request.
    *
    * @return true if Google Play services is available, otherwise false
    */
    private boolean servicesConnected() {
        // Check that Google Play services is available
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int resultCode = googleAPI.isGooglePlayServicesAvailable(this);

        // If Google Play services is available
        if (ConnectionResult.SUCCESS == resultCode) {
            if (MyApplication.APPDEBUG) {
                // In debug mode, log the status
                Log.d(MyApplication.APPTAG, "Google play services available");
            }
            // Continue
            return true;
            // Google Play services was not available for some reason
        } else {
            // Display an error dialog
            Dialog dialog = googleAPI.getErrorDialog(mainActivity, resultCode, 0);
            if (dialog != null) {
                ErrorDialogFragment errorFragment = new ErrorDialogFragment();
                errorFragment.setDialog(dialog);
                errorFragment.show(mainActivity.getSupportFragmentManager(), MyApplication.APPTAG);
            }
            return false;
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void activateFlic() {
        flicSetup();
    }

    @Override
    public void deactivateFlic() {
    }

    @Override
    public void activateMoves(AppCompatActivity activity) {
        dataManager.movesAuthenticate(activity);
    }

    @Override
    public void deactivateMoves() {

    }

    @Override
    public void setMode(int mode) {

    }

    /**
     * Retrieves a SharedPreference object used to store or read values in this app. If a
     * preferences file passed as the first argument to {@link #getSharedPreferences}
     * does not exist, it is created when {@link SharedPreferences.Editor} is used to commit
     * data.
     */
    private SharedPreferences getSharedPreferencesInstance() {
        return getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, MODE_PRIVATE);
    }

    /**
     * Retrieves the boolean from SharedPreferences that tracks whether we are requesting activity
     * updates.
     */
    private boolean getUpdatesRequestedState() {
        return getSharedPreferencesInstance()
                .getBoolean(Constants.ACTIVITY_UPDATES_REQUESTED_KEY, false);
    }

    /**
     * Sets the boolean in SharedPreferences that tracks whether we are requesting activity
     * updates.
     */
    private void setUpdatesRequestedState(boolean requestingUpdates) {
        getSharedPreferencesInstance()
                .edit()
                .putBoolean(Constants.ACTIVITY_UPDATES_REQUESTED_KEY, requestingUpdates)
                .commit();
    }

    /**
     * Processes the list of freshly detected activities. Asks the adapter to update its list of
     * DetectedActivities with new {@code DetectedActivity} objects reflecting the latest detected
     * activities.
     */
    protected void updateDetectedActivitiesList(ArrayList<DetectedActivity> detectedActivities) {
        HashMap<String, Integer> detectedActivitiesMap = new HashMap<>();
        for (DetectedActivity activity : detectedActivities) {
            detectedActivitiesMap.put(Constants.getActivityString(this, activity.getType()), activity.getConfidence());
        }

        final List<String> sortedKeys = Ordering.natural().onResultOf(Functions.forMap(detectedActivitiesMap)).immutableSortedCopy(detectedActivitiesMap.keySet()).reverse();


        activityQueue.add((sortedKeys.get(0).equals(DetectedActivity.UNKNOWN) ? sortedKeys.get(1) : sortedKeys.get(0)));

        Log.i(TAG, String.valueOf(activityQueue));

    }

    public Queue<String> getActivityQueue() {
        return activityQueue;
    }

    /*
     * Define a DialogFragment to display the error dialog generated in showErrorDialog.
     */
    public static class ErrorDialogFragment extends DialogFragment {
        // Global field to contain the error dialog
        private Dialog mDialog;

        /**
         * Default constructor. Sets the dialog field to null
         */
        public ErrorDialogFragment() {
            super();
            mDialog = null;
        }

        /*
         * Set the dialog to display
         *
         * @param dialog An error dialog
         */
        public void setDialog(Dialog dialog) {
            mDialog = dialog;
        }

        /*
         * This method must return a Dialog to the DialogFragment.
         */
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return mDialog;
        }
    }

    /**
     * Receiver for intents sent by DetectedActivitiesIntentService via a sendBroadcast().
     * Receives a list of one or more DetectedActivity objects associated with the current state of
     * the device.
     */
    public class ActivityDetectionBroadcastReceiver extends BroadcastReceiver {
        protected static final String TAG = "activity-detection-response-receiver";

        @Override
        public void onReceive(Context context, Intent intent) {
            ArrayList<DetectedActivity> updatedActivities =
                    intent.getParcelableArrayListExtra(Constants.ACTIVITY_EXTRA);
            updateDetectedActivitiesList(updatedActivities);
        }
    }


}


