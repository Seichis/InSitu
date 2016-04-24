package webinar.pubnub.insitu.model;

import io.realm.RealmObject;

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
    Description description=null;
    private Diary diary;

    public Symptom() {
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
