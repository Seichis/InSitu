package webinar.pubnub.insitu.model;

import io.realm.RealmObject;
import io.realm.annotations.Index;

/**
 * Created by Konstantinos Michail on 2/11/2016.
 */
public class Symptom extends RealmObject {

    SymptomContext context;
    float intensity;
    int activityId;
    boolean isError = false;
    long id;
    long timestamp;
    Description description = null;
    float deltaIntensity;
    @Index
    String day;
    @Index
    String hour;
    @Index
    String month;
    @Index
    String week;
    @Index
    String year;
    private Diary diary;

    public Symptom() {
    }

    public String getWeek() {
        return week;
    }

    public void setWeek(String week) {
        this.week = week;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public float getDeltaIntensity() {
        return deltaIntensity;
    }

    public void setDeltaIntensity(float deltaIntensity) {
        this.deltaIntensity = deltaIntensity;
    }

    public Description getDescription() {
        return description;
    }

    public void setDescription(Description description) {
        this.description = description;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public SymptomContext getContext() {
        return context;
    }

    public void setContext(SymptomContext context) {
        this.context = context;
    }

    public int getActivityId() {
        return activityId;
    }

    public void setActivityId(int activityId) {
        this.activityId = activityId;
    }

    public boolean isError() {
        return isError;
    }

    public void setError(boolean error) {
        isError = error;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Diary getDiary() {

        return diary;
    }

    public void setDiary(Diary diary) {
        this.diary = diary;
    }

    public float getIntensity() {
        return intensity;
    }

    public void setIntensity(float intensity) {
        this.intensity = intensity;
    }


}
