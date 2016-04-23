package webinar.pubnub.insitu;

import android.app.ActivityManager;
import android.content.Context;
import android.util.Log;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Random;
import java.util.TimeZone;

/**
 * Created by Konstantinos Michail on 1/16/2016.
 */
public class Utils {

    private static final String TAG = "Utils";

    public static int randInt(int min, int max) {

        Random rand = new Random();

        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive
        int randomNum = rand.nextInt((max - min) + 1) + min;

        return randomNum;
    }

    public static boolean isMyServiceRunning(Class<?> serviceClass, Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            Log.i(TAG, service.service.getClassName());
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public static boolean isMyServiceRunning(String serviceClassName, Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            Log.i(TAG, service.service.getClassName());
            if (serviceClassName.equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public static long getDayStart(long date,int pastDays){
        DateTime inDate = new DateTime(date, DateTimeZone.forTimeZone(TimeZone.getDefault()));
        return (pastDays<=1)?inDate.withTimeAtStartOfDay().getMillis():inDate.minus(pastDays-1).withTimeAtStartOfDay().getMillis();

    }

    public static long getDaysEnd(long date){
        DateTime inDate = new DateTime(date, DateTimeZone.forTimeZone(TimeZone.getDefault()));
        return inDate.plusDays(1).withTimeAtStartOfDay().getMillis();
    }

    public static DateTime getDate(int year,int month,int day){
        DateTime dt = new DateTime((DateTimeZone.forTimeZone(TimeZone.getDefault())));
        return dt.withDate(year,month,day);
    }

    public static String getFormatedDate(DateTime date){
        DateTimeFormatter dtfOut = DateTimeFormat.forPattern("MM/dd/yyyy");
        return dtfOut.print(date);
    }

}
