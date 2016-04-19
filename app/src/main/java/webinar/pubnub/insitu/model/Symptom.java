package webinar.pubnub.insitu.model;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import io.realm.RealmObject;

/**
 * Created by Konstantinos Michail on 2/11/2016.
 */
public class Symptom extends RealmObject {

    String symptomType;

    public SymptomContext getContext() {
        return context;
    }

    public void setContext(SymptomContext context) {
        this.context = context;
    }

    SymptomContext context;
    double intensity;
    private Diary diary;
    boolean isError;

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

    long id;

    public Symptom() {
    }

    public Diary getDiary() {

        return diary;
    }

    public void setDiary(Diary diary) {
        this.diary = diary;
    }

    public double getIntensity() {
        return intensity;
    }

    public void setIntensity(double intensity) {
        this.intensity = intensity;
    }

    public String getSymptomType() {
        return symptomType;
    }

    public void setSymptomType(String symptomType) {
        this.symptomType = symptomType;
    }



}
