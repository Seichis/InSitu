package webinar.pubnub.insitu.broadcastreceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;

import webinar.pubnub.insitu.BackgroundService;
import webinar.pubnub.insitu.Utils;


/**
 * Created by Konstantinos Michail on 2/17/2016.
 */
public class BootBReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        final Context c = context;
        final Intent myIntent = new Intent(c, BackgroundService.class);
//        myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

        // If flic service is not running wait for 10 seconds to start
        if (Utils.isMyServiceRunning("io.flic.app.FlicService", context)) {
            c.startService(myIntent);
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    c.startService(myIntent);
                }
            }, 5000);
        }
    }
}
