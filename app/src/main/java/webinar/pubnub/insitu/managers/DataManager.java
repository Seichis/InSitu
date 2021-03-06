package webinar.pubnub.insitu.managers;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.zetterstrom.com.forecast.ForecastClient;
import android.zetterstrom.com.forecast.ForecastConfiguration;
import android.zetterstrom.com.forecast.models.DataPoint;
import android.zetterstrom.com.forecast.models.Forecast;
import android.zetterstrom.com.forecast.models.Unit;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multisets;
import com.midhunarmid.movesapi.MovesAPI;
import com.midhunarmid.movesapi.MovesHandler;
import com.midhunarmid.movesapi.activity.ActivityData;
import com.midhunarmid.movesapi.activity.TrackPointsData;
import com.midhunarmid.movesapi.auth.AuthData;
import com.midhunarmid.movesapi.profile.ProfileData;
import com.midhunarmid.movesapi.segment.SegmentData;
import com.midhunarmid.movesapi.storyline.StorylineData;
import com.midhunarmid.movesapi.summary.SummaryData;
import com.midhunarmid.movesapi.summary.SummaryListData;
import com.midhunarmid.movesapi.util.MovesStatus;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Callback;
import retrofit2.Response;
import webinar.pubnub.insitu.BackgroundService;
import webinar.pubnub.insitu.model.SymptomContext;

/**
 * Created by Konstantinos Michail on 2/14/2016.
 */
public class DataManager {

    private static final String TAG2 = "Moves";

    private static final String CLIENT_ID = "U2hOIga3gar6ftAXiHzJ0e1YWAi0tNUF";
    private static final String CLIENT_SECRET = "JPYX6Xf57bG6j4y1JyfDdp25jQpDDo8Ds01Oh2oX0xDA0LkS86a4ul6BdPf21b8P";
    private static final String CLIENT_REDIRECTURL = "http://dfuinspector.com/";
    private static final String CLIENT_SCOPES = "activity location";
    private static final String TAG = "DataManager";
    private static DataManager dataManager = new DataManager();
    BackgroundService backgroundService;
    SymptomContext symptomContext;
    String cityForWeather;
    //    WeatherClient client;
//    WeatherClient.ClientBuilder builder;
    private Context context;
    private MovesHandler<AuthData> authDialogHandler = new MovesHandler<AuthData>() {
        @Override
        public void onSuccess(AuthData arg0) {
            Log.i(TAG2, "Access Token : " + arg0.getAccessToken() + "\n"
                    + "Expires In : " + arg0.getExpiresIn() + "\n"
                    + "User ID : " + arg0.getUserID());
        }

        @Override
        public void onFailure(MovesStatus status, String message) {
            Log.i(TAG2, "Request Failed! \n"
                    + "Status Code : " + status + "\n"
                    + "Status Message : " + message + "\n\n"
                    + "Specific Message : " + status.getStatusMessage());
        }
    };
    private MovesHandler<ProfileData> profileHandler = new MovesHandler<ProfileData>() {

        @Override
        public void onSuccess(ProfileData arg0) {
            Log.i(TAG2, "User ID : " + arg0.getUserID()
                    + "\nUser Platform : " + arg0.getPlatform());
        }

        @Override
        public void onFailure(MovesStatus status, String message) {
            Log.i(TAG2, "Request Failed! \n"
                    + "Status Code : " + status + "\n"
                    + "Status Message : " + message + "\n\n"
                    + "Specific Message : " + status.getStatusMessage());
        }
    };
    private MovesHandler<ArrayList<SummaryListData>> summaryHandler = new MovesHandler<ArrayList<SummaryListData>>() {
        @Override
        public void onSuccess(ArrayList<SummaryListData> result) {
            for (SummaryListData sld : result) {
                Log.i(TAG2, "-Calories Idle : " + sld.getCaloriesIdle());
                Log.i(TAG2, "-Last update : " + sld.getLastUpdate());
                Log.i(TAG2, "-Date : " + sld.getDate());
                Log.i(TAG2, "===== Summaries =====");

                ArrayList<SummaryData> summaries = sld.getSummaries();
                for (SummaryData sd : summaries) {
                    Log.i(TAG2, "--Activity : " + sd.getActivity());
                    Log.i(TAG2, "--Calories : " + sd.getCalories());
                    Log.i(TAG2, "--Distance : " + sd.getDistance());
                    Log.i(TAG2, "--Duration : " + sd.getDuration());
                    Log.i(TAG2, "--Group : " + sd.getGroup());
                    Log.i(TAG2, "--Steps : " + sd.getSteps());
                }
            }
        }

        @Override
        public void onFailure(MovesStatus status, String message) {
            Log.i(TAG2, "Request Failed! \n"
                    + "Status Code : " + status + "\n"
                    + "Status Message : " + message + "\n\n"
                    + "Specific Message : " + status.getStatusMessage());
        }
    };
    private MovesHandler<ArrayList<StorylineData>> storylineHandler = new MovesHandler<ArrayList<StorylineData>>() {
        @Override
        public void onSuccess(ArrayList<StorylineData> result) {

            for (StorylineData sld : result) {
                Log.i(TAG2, "===== Storyline =====");

                Log.i(TAG2, "-Calories Idle : " + sld.getCaloriesIdle());
                Log.i(TAG2, "-Last update : " + sld.getLastUpdate());
                Log.i(TAG2, "-Date : " + sld.getDate());
                Log.i(TAG2, "===== Segments =====");

                ArrayList<SegmentData> segments = sld.getSegments();
                for (SegmentData sd : segments) {
                    Log.i(TAG2, "-Segments: Start time : " + sd.getStartTime());
                    Log.i(TAG2, "-Segments: End time : " + sd.getEndTime());
                    Log.i(TAG2, "-Segments: Type : " + sd.getType());

                    if (sd.getPlace() != null) {
                        Log.i(TAG2, "-Segments: place foursquare: " + sd.getPlace().getFoursquareId());
                        Log.i(TAG2, "-Segments: place type : " + sd.getPlace().getType());
                        Log.i(TAG2, "-Segments: place name: " + sd.getPlace().getName());
                        Log.i(TAG2, "-Segments: place foursquare id : " + sd.getPlace().getFoursquareCategoryIds());
                        Log.i(TAG2, "-Segments: place location lat : " + sd.getPlace().getLocation().getLat());
                        Log.i(TAG2, "-Segments: place location lon : " + sd.getPlace().getLocation().getLon());
                    }
                    Log.i(TAG2, "===== Activities =====");

                    ArrayList<ActivityData> activities = sd.getActivities();
                    for (ActivityData ad : activities) {
                        Log.i(TAG2, "--Activities: name : " + ad.getActivity());
                        Log.i(TAG2, "--Activities: start time : " + ad.getStartTime());
                        Log.i(TAG2, "--Activities: end time : " + ad.getEndTime());
                        Log.i(TAG2, "--Activities: calories : " + ad.getCalories());
                        Log.i(TAG2, "--Activities: manual : " + ad.getManual());
                        Log.i(TAG2, "--Activities: distance : " + ad.getDistance());
                        Log.i(TAG2, "--Activities: duration : " + ad.getDuration());
                        Log.i(TAG2, "--Activities: group : " + ad.getGroup());
                        Log.i(TAG2, "--Activities: steps : " + ad.getSteps());
                        ArrayList<TrackPointsData> trackPoints = ad.getTrackPoints();

                        Log.i(TAG2, "===== Trackpoints =====");

                        for (TrackPointsData tpd : trackPoints) {
                            Log.i(TAG2, "---Track points" + tpd.getLat() + " ; " + tpd.getLon());
                            Log.i(TAG2, "---Track points" + tpd.getTime());
                        }

                    }

                }

                Log.i(TAG2, "===== Summaries =====");

                ArrayList<SummaryData> summaries = sld.getSummary();
                for (SummaryData sd : summaries) {
                    Log.i(TAG2, "-Summary Activity : " + sd.getActivity());
                    Log.i(TAG2, "-Summary Calories : " + sd.getCalories());
                    Log.i(TAG2, "-Summary Distance : " + sd.getDistance());
                    Log.i(TAG2, "-Summary Duration : " + sd.getDuration());
                    Log.i(TAG2, "-Summary Group : " + sd.getGroup());
                    Log.i(TAG2, "-Summary Steps : " + sd.getSteps());
                }

            }

        }

        @Override
        public void onFailure(MovesStatus status, String message) {
            Log.i(TAG2, "Request Failed! \n"
                    + "Status Code : " + status + "\n"
                    + "Status Message : " + message + "\n\n"
                    + "Specific Message : " + status.getStatusMessage());
        }
    };

    private DataManager() {
    }

    public static DataManager getInstance() {
        return dataManager;
    }

    public void init(Context context) {
        this.context = context;
        initializeMoves();
        backgroundService = BackgroundService.getInstance();
        ForecastConfiguration configuration =
                new ForecastConfiguration.Builder("fea0689d34004538e4db36555b89bf40").setDefaultUnit(Unit.AUTO)
                        .build();
        ForecastClient.create(configuration);
    }

    private void initializeMoves() {
        try {
            MovesAPI.init(context, CLIENT_ID, CLIENT_SECRET, CLIENT_SCOPES, CLIENT_REDIRECTURL);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void fetchWeatherData() {
        Location location = backgroundService.getCurrentLocation();
//        if (backgroundService.getCurrentLocation() == null) {
//            location = MainActivity.getInstance().getCurrentLocation();
//        } else {
//            location = MainActivity.getInstance().getCurrentLocation();
//        Log.i(TAG, location.toString());
//        }
        if (location != null) {
            Log.i(TAG,location.toString());

            Geocoder geocoder;
            symptomContext = new SymptomContext();
            symptomContext.setAltitude(String.valueOf(location.getAltitude()));
            List<Address> addresses;
            geocoder = new Geocoder(context, Locale.ENGLISH);

            try {
                addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                Address address = addresses.get(0);
                symptomContext.setAddress(address.getAddressLine(0)); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                symptomContext.setCity(address.getLocality());
                symptomContext.setPostCode(address.getPostalCode());
                symptomContext.setCountry(address.getCountryName());
                symptomContext.setLatitude(location.getLatitude());
                symptomContext.setLongitude(location.getLongitude());
//                cityForWeather = address.getAddressLine(0);
            } catch (IOException e) {
                e.printStackTrace();
            }
            ForecastClient.getInstance()
                    .getForecast(location.getLatitude(), location.getLongitude(), new Callback<Forecast>() {
                        @SuppressWarnings("ConstantConditions")
                        @Override
                        public void onResponse(Response<Forecast> response) {
                            if (response.isSuccess()) {
                                Forecast forecast = response.body();
                                DataPoint currentWeather = forecast.getCurrently();
                                if (currentWeather != null) {
                                    symptomContext.setWeatherCondition(currentWeather.getSummary());
                                    symptomContext.setTemperature(currentWeather.getTemperature().floatValue());
                                    symptomContext.setApparentTemperature(currentWeather.getApparentTemperature().floatValue());
                                    symptomContext.setHumidity(currentWeather.getHumidity().floatValue());
                                    symptomContext.setCloudCover(currentWeather.getCloudCover().floatValue());
                                    symptomContext.setWindBearing(currentWeather.getWindBearing().floatValue());
                                    symptomContext.setWindSpeed(currentWeather.getWindSpeed().floatValue());
                                    symptomContext.setDewPoint(currentWeather.getDewPoint().floatValue());
                                    symptomContext.setHumidity(currentWeather.getHumidity().floatValue());
                                    symptomContext.setPrecipitationProbability(currentWeather.getPrecipProbability().floatValue());
                                    if (currentWeather.getPrecipitationType() != null) {
                                        symptomContext.setPrecipitationType(currentWeather.getPrecipitationType().getText());
                                    }
                                    symptomContext.setOzone(currentWeather.getOzone().floatValue());
                                }
//                                Log.i(TAG, "weather condition" + forecast.getCurrently().getSummary());
//                                Log.i(TAG, "apparentTemperature" + forecast.getCurrently().getApparentTemperature());
//                                Log.i(TAG, "cloudCover" + forecast.getCurrently().getCloudCover());
//                                Log.i(TAG, "pressure" + forecast.getCurrently().getPressure());
//                                Log.i(TAG, "dewPoint" + forecast.getCurrently().getDewPoint());
//                                Log.i(TAG, "humidity" + forecast.getCurrently().getHumidity());
//                                Log.i(TAG, "ozone" + forecast.getCurrently().getOzone());
//                                Log.i(TAG, "precipitationType" + forecast.getCurrently().getPrecipitationType().name());
//                                Log.i(TAG, "temperature" + forecast.getCurrently().getTemperature());
//                                Log.i(TAG, "precipitationProbability" + forecast.getCurrently().getPrecipProbability());
//                                Log.i(TAG, "windSpeed" + forecast.getCurrently().getWindSpeed());
//                                Log.i(TAG, "windBearing" + forecast.getCurrently().getWindBearing());
//                                Log.i(TAG,""+forecast.getCurrently().());
                            }
                        }

                        @Override
                        public void onFailure(Throwable t) {

                        }
                    });
        } else {
            Log.i(TAG, "null location");
        }
    }


    public SymptomContext getSymptomContext() {
        return symptomContext;
    }


    public void movesAuthenticate(AppCompatActivity activity) {
        MovesAPI.authenticate(authDialogHandler, activity);
    }

    public AuthData getMovesAuthData() {

        return MovesAPI.getAuthData();
    }

    public void movesProfile() {
        MovesAPI.getProfile(profileHandler);
    }

    /**
     * @param day Format example "20160212"
     */
    public void movesSummarySingleDay(String day) {
        MovesAPI.getSummary_SingleDay(summaryHandler, day, null);
    }

    /**
     */
    public void movesSummaryToday() {
        String format = "yyyyMMdd";
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.ENGLISH);
        String day = sdf.format(new Date());
        MovesAPI.getSummary_SingleDay(summaryHandler, day, null);
    }

    /**
     * @param week Format example "2016-W06"
     */
    public void movesSummaryWeek(String week) {
        MovesAPI.getSummary_SpecificWeek(summaryHandler, week, null);
    }

    /**
     * @param month Format example "201602"
     */
    public void movesSummaryMonth(String month) {
        MovesAPI.getSummary_SpecificMonth(summaryHandler, month, null);
    }

    /**
     * @param start Format example "20160211"
     * @param end   Format example "20160212"
     */
    public void movesSummaryRange(String start, String end) {
        MovesAPI.getSummary_WithinRange(summaryHandler, start, end, null);
    }

    /**
     * @param pastDays Format example "2"
     */
    public void movesSummaryPastDays(String pastDays) {
        MovesAPI.getSummary_PastDays(summaryHandler, pastDays, null);

    }

    /**
     * @param day Format example "20160212"
     * @param trp To return track points or not
     */
    public void movesStorylineDay(String day, boolean trp) {
        MovesAPI.getStoryline_SingleDay(storylineHandler, day, null, trp);
    }

    /**
     * @param trp To return track points or not
     */
    public void movesStorylineToday(boolean trp) {
        String format = "yyyyMMdd";
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.ENGLISH);
        String day = sdf.format(new Date());
        MovesAPI.getStoryline_SingleDay(storylineHandler, day, null, trp);
    }

    /**
     * @param week Format example "2016-W06"
     * @param trp  To return track points or not
     */
    public void movesStorylineWeek(String week, boolean trp) {
        MovesAPI.getStoryline_SpecificWeek(storylineHandler, week, null, trp);
    }

    /**
     * @param month Format example "201602"
     */
    public void movesStorylineMonth(String month) {
        MovesAPI.getStoryline_SpecificMonth(storylineHandler, month, null, false);
    }

    /**
     * @param start Format example "20160211"
     * @param end   Format example "20160212"
     * @param trp   To return track points or not
     */
    public void movesStorylineRange(String start, String end, boolean trp) {
        MovesAPI.getStoryline_WithinRange(storylineHandler, start, end, null, trp);
    }

    /**
     * @param pastDays Format example "2"
     * @param trp      To return track points or not
     */
    public void movesStorylinePastDays(String pastDays, boolean trp) {
        MovesAPI.getStoryline_PastDays(storylineHandler, pastDays, null, trp);
    }

    /**
     * @param day Format example "20160212"
     */
    public void movesActivitiesDay(String day) {
        MovesAPI.getActivities_SingleDay(storylineHandler, day, null);
    }

    /**
     * @param week Format example "2016-W06"
     */
    public void movesActivitiesWeek(String week) {
        MovesAPI.getActivities_SpecificWeek(storylineHandler, week, null);
    }

    /**
     * @param month Format example "201602"
     */
    public void movesActivitiesMonth(String month) {
        MovesAPI.getActivities_SpecificMonth(storylineHandler, "201602", null);
    }

    /**
     * @param start Format example "20160211"
     * @param end   Format example "20160212"
     */
    public void movesActivitiesRange(String start, String end) {
        MovesAPI.getActivities_WithinRange(storylineHandler, "20160211", "20160212", null);
    }

    /**
     * @param pastDays Format example "2"
     */
    public void movesActivitiesPastDays(String pastDays) {
        MovesAPI.getActivities_PastDays(storylineHandler, "31", null);
    }


    public void movesActivitiesToday() {
        String format = "yyyyMMdd";
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.ENGLISH);
        String day = sdf.format(new Date());
        MovesAPI.getActivities_SingleDay(storylineHandler, day, null);
    }

    public String getLast30MinutesActivity() {

        Multiset<String> activityCount = HashMultiset.create(BackgroundService.getInstance().getActivityQueue());
        Log.i(TAG, String.valueOf(activityCount));
//        for (String activity : Multisets.copyHighestCountFirst(activityCount).elementSet()) {
//            Log.i(TAG, activity + "  " + activityCount.count(activity));
//        }
        if (activityCount.isEmpty()) {
            return "";
        } else {
            String activity = Multisets.copyHighestCountFirst(activityCount).elementSet().toArray()[0].toString();
            Log.i(TAG, activity + "  " + activityCount.count(activity));
            return activity;
        }

    }
}

