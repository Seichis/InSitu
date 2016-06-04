package webinar.pubnub.insitu.model;

import io.realm.RealmObject;
import io.realm.annotations.Index;

public class SymptomContext extends RealmObject {
    // Location variables

    double latitude;
    double longitude;
    private String address;
    private String postCode;
    private String country;
    private String placeType;
    private String city;
    // Weather variables
    private float temperature;
    @Index
    private String weatherCondition;
    private float humidity;
    private float pressure;
    private String altitude;
    float apparentTemperature;
    float cloudCover;
    float dewPoint;
    float ozone;
    float precipitationProbability;
    float windSpeed;
    float windBearing;
    String precipitationType;

    public float getApparentTemperature() {
        return apparentTemperature;
    }

    public void setApparentTemperature(float apparentTemperature) {
        this.apparentTemperature = apparentTemperature;
    }

    public float getCloudCover() {
        return cloudCover;
    }

    public void setCloudCover(float cloudCover) {
        this.cloudCover = cloudCover;
    }

    public float getDewPoint() {
        return dewPoint;
    }

    public void setDewPoint(float dewPoint) {
        this.dewPoint = dewPoint;
    }

    public float getOzone() {
        return ozone;
    }

    public void setOzone(float ozone) {
        this.ozone = ozone;
    }

    public float getPrecipitationProbability() {
        return precipitationProbability;
    }

    public void setPrecipitationProbability(float precipitationProbability) {
        this.precipitationProbability = precipitationProbability;
    }

    public float getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(float windSpeed) {
        this.windSpeed = windSpeed;
    }

    public float getWindBearing() {
        return windBearing;
    }

    public void setWindBearing(float windBearing) {
        this.windBearing = windBearing;
    }

    public String getPrecipitationType() {
        return precipitationType;
    }

    public void setPrecipitationType(String precipitationType) {
        this.precipitationType = precipitationType;
    }

    public SymptomContext() {

    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
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


    public float getHumidity() {
        return humidity;
    }

    public void setHumidity(float humidity) {
        this.humidity = humidity;
    }


    /**
     * @return
     */
    public float getPressure() {
        return pressure;
    }

    /**
     * @param pressure
     */
    public void setPressure(float pressure) {
        this.pressure = pressure;
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
