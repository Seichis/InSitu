package webinar.pubnub.insitu;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import net.danlew.android.joda.JodaTimeAndroid;

import butterknife.ButterKnife;

public class MyApplication extends Application {

    // Debugging switch
    public static final boolean APPDEBUG = false;

    // Debugging tag for the application
    public static final String APPTAG = "Symptoms";


    @Override
    public void onCreate() {
        super.onCreate();

//        ButterKnife.setDebug(BuildConfig.DEBUG);
        JodaTimeAndroid.init(this);
    }

}