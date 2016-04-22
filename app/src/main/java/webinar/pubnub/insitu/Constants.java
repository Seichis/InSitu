package webinar.pubnub.insitu;

import android.content.Context;
import android.content.res.Resources;

import com.google.android.gms.location.DetectedActivity;

public class Constants {

    public static final String PACKAGE_NAME = "com.masterthesis.personaldata.symptoms";
    /**
     * The desired time between activity detections. Larger values result in fewer activity
     * detections while improving battery life. A value of 0 results in activity detections at the
     * fastest possible rate. Getting frequent updates negatively impact battery life and a real
     * app may prefer to request less frequent updates.
     */
    public static final long DETECTION_INTERVAL_IN_MILLISECONDS = 60000;

    //    public static final String SHARED_PREFERENCES_DIARIES_NAME = PACKAGE_NAME + ".diaries";
//    public static final String SHARED_PREFERENCES_SYMPTOMS_ORDER_NAME = PACKAGE_NAME + ".symptoms";
    public static final String BROADCAST_ACTION = PACKAGE_NAME + ".BROADCAST_ACTION";
    public static final String ACTIVITY_EXTRA = PACKAGE_NAME + ".ACTIVITY_EXTRA";
    public static final String SHARED_PREFERENCES_NAME = PACKAGE_NAME + ".SHARED_PREFERENCES";
    public static final String ACTIVITY_UPDATES_REQUESTED_KEY = PACKAGE_NAME +
            ".ACTIVITY_UPDATES_REQUESTED";
    public static final String DETECTED_ACTIVITIES = PACKAGE_NAME + ".DETECTED_ACTIVITIES";
    /**
     * List of DetectedActivity types that we monitor in this sample.
     */
    public static final int[] MONITORED_ACTIVITIES = {
            DetectedActivity.STILL,
            DetectedActivity.ON_FOOT,
            DetectedActivity.WALKING,
            DetectedActivity.RUNNING,
            DetectedActivity.ON_BICYCLE,
            DetectedActivity.IN_VEHICLE,
            DetectedActivity.TILTING,
            DetectedActivity.UNKNOWN
    };
    public static final int IN_VEHICLE = 0;
    public static final int ON_BICYCLE = 1;
    public static final int STILL = 2;
    public static final int WALKING = 3;
    public static final int RUNNING = 4;
    public static final int[] SAVED_ACTIVITIES = {
            IN_VEHICLE, ON_BICYCLE, STILL, WALKING, RUNNING
    };
    /**
     * Constants for location tracking
     */
    /*
     * Define a request code to send to Google Play services This code is returned in
     * Activity.onActivityResult
     */
    public final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    /*
     * Constants for location update parameters
     */
// Milliseconds per second
    public static final int MILLISECONDS_PER_SECOND = 1000;
    // The update interval
    public static final int UPDATE_INTERVAL_IN_SECONDS = 60;
    // A fast interval ceiling
    public static final int FAST_CEILING_IN_SECONDS = 1;
    // Update interval in milliseconds
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = MILLISECONDS_PER_SECOND
            * UPDATE_INTERVAL_IN_SECONDS;
    // A fast ceiling of update intervals, used when the app is visible
    public static final long FAST_INTERVAL_CEILING_IN_MILLISECONDS = MILLISECONDS_PER_SECOND
            * FAST_CEILING_IN_SECONDS;
    public static final int BUTTON_NOT_TRACKING = 0;
    public static final int SERVICE_CRASHED = 1;


    //############ Intent rules main activity
    public static final int NEED_RESET = 2;
    public static final int UNKNOWN_ERROR = 3;
    public static final int[] colors = new int[]{
            R.color.graph_color1,
            R.color.graph_color2,
            R.color.graph_color3,
            R.color.graph_color4,
            R.color.graph_color5,
            R.color.graph_color6,
            R.color.graph_color7,
            R.color.graph_color8,
            R.color.graph_color9,
            R.color.graph_color10,
            R.color.graph_color11,
            R.color.graph_color12,
            R.color.graph_color13,
            R.color.graph_color14,
    };


    private Constants() {
    }

    /**
     * Returns a human readable String corresponding to a detected activity type.
     */
    public static String getActivityString(Context context, int detectedActivityType) {
        Resources resources = context.getResources();
        switch (detectedActivityType) {
            case DetectedActivity.IN_VEHICLE:
                return resources.getString(R.string.in_vehicle);
            case DetectedActivity.ON_BICYCLE:
                return resources.getString(R.string.on_bicycle);
            case DetectedActivity.ON_FOOT:
                return resources.getString(R.string.on_foot);
            case DetectedActivity.RUNNING:
                return resources.getString(R.string.running);
            case DetectedActivity.STILL:
                return resources.getString(R.string.still);
            case DetectedActivity.TILTING:
                return resources.getString(R.string.tilting);
            case DetectedActivity.UNKNOWN:
                return resources.getString(R.string.unknown);
            case DetectedActivity.WALKING:
                return resources.getString(R.string.walking);
            default:
                return resources.getString(R.string.unidentifiable_activity, detectedActivityType);
        }
    }

    /**
     * Returns a human readable String corresponding to a detected activity type.
     */
    public static String getSavedActivityString(Context context, int detectedActivityType) {
        Resources resources = context.getResources();
        switch (detectedActivityType) {
            case IN_VEHICLE:
                return resources.getString(R.string.in_vehicle);
            case ON_BICYCLE:
                return resources.getString(R.string.on_bicycle);
            case RUNNING:
                return resources.getString(R.string.running);
            case STILL:
                return resources.getString(R.string.still);
            case WALKING:
                return resources.getString(R.string.walking);
            default:
                return resources.getString(R.string.unidentifiable_activity, detectedActivityType);
        }
    }



}
