package webinar.pubnub.insitu.model;

import com.google.android.gms.maps.model.LatLng;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;

public class SymptomContext extends RealmObject {
    // Location variables

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    double latitude;
    double longitude;
    private String address;
    private String postCode;

    private String country;
    private String placeType;
    private String city;
    // Weather variables
    private float temperature;
    private String weatherCondition;
    private String windChill;
    private String windDirection;
    private String windSpeed;
    private float humidity;
    private String pressure;
    private String baroPressureRising;
    private String visibility;
    private String duringActivity;
    private String afterActivity;

    public String getDuringActivity() {
        return duringActivity;
    }

    public void setDuringActivity(String duringActivity) {
        this.duringActivity = duringActivity;
    }

    public String getAfterActivity() {
        return afterActivity;
    }

    public void setAfterActivity(String afterActivity) {
        this.afterActivity = afterActivity;
    }

    private String altitude;

    public SymptomContext() {

    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }


    public String getAltitude() {
        return altitude;
    }

    public void setAltitude(String altitude) {
        this.altitude = altitude;
    }

    /**
     * @return Temperature in Celsius
     */
    public float getTemperature() {
        return temperature;
    }

    /**
     * @param temperature Temperature in Celsius ("20")
     */
    public void setTemperature(float temperature) {
        this.temperature = temperature;
    }

    /**
     * @return General weather condition ("Mostly cloudy")
     */
    public String getWeatherCondition() {
        return weatherCondition;
    }

    /**
     * @param weatherCondition General weather condition ("Mostly cloudy")
     */
    public void setWeatherCondition(String weatherCondition) {
        this.weatherCondition = weatherCondition;
    }

    /**
     * @return
     */
    public String getWindChill() {
        return windChill;
    }

    /**
     * @param windChill
     */
    public void setWindChill(String windChill) {
        this.windChill = windChill;
    }

    /**
     * @return
     */
    public String getWindDirection() {
        return windDirection;
    }

    /**
     * @param windDirection
     */
    public void setWindDirection(String windDirection) {
        this.windDirection = windDirection;
    }

    /**
     * @return
     */
    public String getWindSpeed() {
        return windSpeed;
    }

    /**
     * @param windSpeed
     */
    public void setWindSpeed(String windSpeed) {
        this.windSpeed = windSpeed;
    }

    public float getHumidity() {
        return humidity;
    }

    public void setHumidity(float humidity) {
        this.humidity = humidity;
    }


    /**
     * @return
     */
    public String getPressure() {
        return pressure;
    }

    /**
     * @param pressure
     */
    public void setPressure(String pressure) {
        this.pressure = pressure;
    }

    /**
     * @return
     */
    public String getBaroPressureRising() {
        return baroPressureRising;
    }

    /**
     * @param baroPressureRising
     */
    public void setBaroPressureRising(String baroPressureRising) {
        this.baroPressureRising = baroPressureRising;
    }

    /**
     * @return
     */
    public String getVisibility() {
        return visibility;
    }

    /**
     * @param visibility
     */
    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    /**
     * @return Municipality name ("Ballerup)
     */
    public String getCity() {
        return city;
    }

    /**
     * @param city Municipality name ("Ballerup)
     */
    public void setCity(String city) {
        this.city = city;
    }

    /**
     * @return Address name and number ("Sømoseparken 78")
     */
    public String getAddress() {
        return address;
    }

    /**
     * @param address Address name and number ("Sømoseparken 78")
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * @return Postal code ("2750")
     */
    public String getPostCode() {
        return postCode;
    }

    /**
     * @param postCode Postal code ("2750")
     */
    public void setPostCode(String postCode) {
        this.postCode = postCode;
    }



    /**
     * @return Country name ("Denmark")
     */
    public String getCountry() {
        return country;
    }

    /**
     * @param country Country name ("Denmark")
     */
    public void setCountry(String country) {
        this.country = country;
    }

    /**
     * @return Type of the place as inputted by the user at the moves app
     * or inferred from Foursquare ("Home")
     */
    public String getPlaceType() {
        return placeType;
    }

    /**
     * @param type Type of the place as inputted by the user at the moves app
     *             or inferred from Foursquare ("Home")
     */
    public void setPlaceType(String type) {
        this.placeType = type;
    }


}
