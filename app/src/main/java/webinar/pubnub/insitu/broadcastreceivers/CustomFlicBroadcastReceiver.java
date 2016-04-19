package webinar.pubnub.insitu.broadcastreceivers;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import io.flic.lib.FlicBroadcastReceiver;
import io.flic.lib.FlicButton;
import io.flic.lib.FlicManager;
import webinar.pubnub.insitu.BackgroundService;
import webinar.pubnub.insitu.MainActivity;
import webinar.pubnub.insitu.Utils;
import webinar.pubnub.insitu.managers.SymptomManager;

/**
 * Created by User1 on 8/1/2016.
 */
public class CustomFlicBroadcastReceiver extends FlicBroadcastReceiver {
    static long tStart;
    static long tEnd;
    //    static long measureEndTime=10000;
    static String TAG = "CustomFlicBroadcastReceiver";

    @Override
    protected void onRequestAppCredentials(Context context) {
        // Set app credentials by calling FlicManager.setAppCredentials here
        FlicManager.setAppCredentials("59eab426-39a4-4457-8e7d-2f67f9733d54", "d0ef92f6-a494-4f3d-96c0-841c6b434909", "ScaleMeasurement");

    }


//    @Override
//    public void onButtonClickOrHold(Context context, FlicButton button, boolean wasQueued, int timeDiff, boolean isClick, boolean isHold) {
//        super.onButtonClickOrHold(context, button, wasQueued, timeDiff, isClick, isHold);
//
//    }
//
//    @Override
//    public void onButtonSingleOrDoubleClick(Context context, FlicButton button, boolean wasQueued, int timeDiff, boolean isSingleClick, boolean isDoubleClick) {
//        super.onButtonSingleOrDoubleClick(context, button, wasQueued, timeDiff, isSingleClick, isDoubleClick);
//    }
//
//    @Override
//    public void onButtonSingleOrDoubleClickOrHold(Context context, FlicButton button, boolean wasQueued, int timeDiff, boolean isSingleClick, boolean isDoubleClick, boolean isHold) {
//        super.onButtonSingleOrDoubleClickOrHold(context, button, wasQueued, timeDiff, isSingleClick, isDoubleClick, isHold);
//    }

    @Override
    public void onButtonUpOrDown(Context context, FlicButton button, boolean wasQueued, int timeDiff, boolean isUp, boolean isDown) {
        long tDelta;
        double elapsedSeconds;

        if (isDown) {
            if (!Utils.isMyServiceRunning(BackgroundService.class, context)) {
                Log.i(TAG, "service null, button down");
                //        myIntent.
//                context.startActivity(new Intent(context, WelcomeMain.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
            } else {
                tStart = System.currentTimeMillis();
                Log.i("time", "  " + tStart);
            }
        } else if (isUp) {
            if (!Utils.isMyServiceRunning(BackgroundService.class, context)) {
                //        myIntent.
                Log.i(TAG, "service null, button up");

                context.startActivity(new Intent(context, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
            } else {
                tEnd = System.currentTimeMillis();
                tDelta = tEnd - tStart;
                elapsedSeconds = tDelta;
                SymptomManager symptomManager = SymptomManager.getInstance();
                symptomManager.manageSymptomInput(elapsedSeconds);
                Log.i(TAG, "  " + tStart);
                Log.i(TAG, "  " + tEnd);
            }
        }


    }

    @Override
    public void onButtonRemoved(Context context, FlicButton button) {
        // Button was removed
    }
}
