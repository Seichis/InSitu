package webinar.pubnub.insitu;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class BaseActivity extends AppCompatActivity {


    public static final int REQUEST_FINE_LOCATION = 0;
    private static final int REQUEST_COARSE_LOCATION = 1;
    private static final int SYSTEM_WINDOW_ALERT = 2;
    private static final String TAG = "BaseActivity";
    /**
     * Requests the fine location permission.
     * If the permission has been denied previously, a SnackBar will prompt the user to grant the
     * permission, otherwise it is requested directly.
     */
    @Nullable
    View view;
    /*
     * Other class member variables
     */


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }


    /*
       * Called when the Activity is no longer visible at all. Stop updates and disconnect.
       */
    @Override
    public void onStop() {
        super.onStop();
    }
//
//    /**
//     * Requests the Coarse location permission.
//     * If the permission has been denied previously, a SnackBar will prompt the user to grant the
//     * permission, otherwise it is requested directly.
//     */
//    public void requestCoarseLocationPermission() {
//
//        // BEGIN_INCLUDE(camera_permission_request)
//        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
//                Manifest.permission.ACCESS_COARSE_LOCATION)) {
//            // Provide an additional rationale to the user if the permission was not granted
//            // and the user would benefit from additional context for the use of the permission.
//            // For example if the user has previously denied the permission.
//
//            Snackbar.make(view, R.string.permission_location_rationale,
//                    Snackbar.LENGTH_INDEFINITE)
//                    .setAction(R.string.ok, new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                            ActivityCompat.requestPermissions(getParent(),
//                                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
//                                    REQUEST_COARSE_LOCATION);
//                            checkPermissions();
//
//                        }
//                    })
//                    .show();
//        } else {
//
//            // Camera permission has not been granted yet. Request it directly.
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
//                    REQUEST_COARSE_LOCATION);
//        }
//    }
/**
     * Requests the Coarse location permission.
     * If the permission has been denied previously, a SnackBar will prompt the user to grant the
     * permission, otherwise it is requested directly.
     */
//    public void requestSystemWindowPermission() {
//
//        // BEGIN_INCLUDE(camera_permission_request)
//        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
//                Manifest.permission.SYSTEM_ALERT_WINDOW)) {
//            // Provide an additional rationale to the user if the permission was not granted
//            // and the user would benefit from additional context for the use of the permission.
//            // For example if the user has previously denied the permission.
//
//            Snackbar.make(view, R.string.permission_system_alert_rationale,
//                    Snackbar.LENGTH_INDEFINITE)
//                    .setAction(R.string.ok, new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                            ActivityCompat.requestPermissions(getParent(),
//                                    new String[]{Manifest.permission.SYSTEM_ALERT_WINDOW},
//                                    SYSTEM_WINDOW_ALERT);
//                            checkPermissions();
//
//                        }
//                    })
//                    .show();
//        } else {
//
//            // Camera permission has not been granted yet. Request it directly.
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SYSTEM_ALERT_WINDOW},
//                    SYSTEM_WINDOW_ALERT);
//        }
//    }

    /*
     * Called when the Activity is restarted, even before it becomes visible.
     */
    @Override
    public void onStart() {
        super.onStart();
        checkPermissions();

        // Connect to the location services client
    }

    /*
     * Called when the Activity is resumed. Updates the view.
     */
    @Override
    protected void onResume() {
        super.onResume();

        try {
            view = findViewById(android.R.id.content).getRootView();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }


    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        switch (requestCode){
            case REQUEST_FINE_LOCATION:
                // BEGIN_INCLUDE(permission_result)
                // Received permission result for camera permission.

                // Check if the only required permission has been granted
                if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Camera permission has been granted, preview can be displayed
                    Snackbar.make(view, R.string.permision_available_location,
                            Snackbar.LENGTH_SHORT).show();
                } else {
                    Snackbar.make(view, R.string.permissions_not_granted,
                            Snackbar.LENGTH_SHORT).show();

                }
                // END_INCLUDE(permission_result)

                break;
            case REQUEST_COARSE_LOCATION:
                // BEGIN_INCLUDE(permission_result)

                // Check if the only required permission has been granted
                if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    Snackbar.make(view, R.string.permissions_not_granted,
                            Snackbar.LENGTH_SHORT).show();

                }
                // END_INCLUDE(permission_result)

                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                break;
        }
    }




    public void requestFineLocationPermission() {
        // BEGIN_INCLUDE(camera_permission_request)

        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // For example if the user has previously denied the permission.
            Snackbar.make(view, R.string.permission_location_rationale,
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ActivityCompat.requestPermissions(getParent(),
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                                            },
                                    REQUEST_FINE_LOCATION);
                        }
                    })
                    .show();
        }else if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)) {
            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // For example if the user has previously denied the permission.

            Snackbar.make(view, R.string.permission_location_rationale,
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ActivityCompat.requestPermissions(getParent(),
                                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                                    REQUEST_COARSE_LOCATION);
                        }
                    })
                    .show();
        }
        // END_INCLUDE(camera_permission_request)
    }


    protected void checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) { // Permission was added in API Level 16
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED ||ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},REQUEST_FINE_LOCATION);
//                requestFineLocationPermission();
            }
        }
    }
}
