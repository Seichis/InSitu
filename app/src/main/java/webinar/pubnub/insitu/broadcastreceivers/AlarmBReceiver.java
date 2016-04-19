package webinar.pubnub.insitu.broadcastreceivers;

/**
 * Created by Konstantinos Michail on 2/17/2016.
 */

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

import java.util.Calendar;

import webinar.pubnub.insitu.BackgroundService;
import webinar.pubnub.insitu.Utils;

public class AlarmBReceiver extends BroadcastReceiver {
    private final static int TWO_AM = 13;
    private final static int ZERO_MINUTES = 50;
    private final static long ONCE_PER_DAY = 1000 * 60 * 60 * 24;
    private static final String TAG = "Alarm";

    //    private BackgroundService backgroundService=null;
    private static long getMillisUntilMorning2AM() {

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, TWO_AM);
        cal.set(Calendar.MINUTE, ZERO_MINUTES);

        Log.i(TAG, "time until 2 " + (cal.getTimeInMillis() - System.currentTimeMillis()));
        return cal.getTimeInMillis();
    }

    @Override
    public void onReceive(Context context, Intent intent) {

//        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
//        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "");
//        wl.acquire();
//
//        // Put here YOUR code.
//        Toast.makeText(context, "Alarm !!!!!!!!!!", Toast.LENGTH_LONG).show(); // For example
        if (Utils.isMyServiceRunning(BackgroundService.class, context)) {
            Log.i(TAG, "alarm");
            Handler mHandler;
            mHandler = new Handler();
//            mHandler.post(new MovesRunnable(context, BackgroundService.getInstance().getHelper()));
        }
//        wl.release();
    }

    public void setAlarm(Context context) {
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(context, AlarmBReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);

        am.setInexactRepeating(AlarmManager.RTC, getMillisUntilMorning2AM(), 20000, pi); // Millisec * Second * Minute
    }

    public void cancelAlarm(Context context) {
        Intent intent = new Intent(context, AlarmBReceiver.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);
    }
}


