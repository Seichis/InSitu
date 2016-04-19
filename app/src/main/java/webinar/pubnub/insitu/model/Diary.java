package webinar.pubnub.insitu.model;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.TreeMap;

import io.realm.RealmObject;

/**
 * Created by Konstantinos Michail on 2/17/2016.
 */


public class Diary extends RealmObject {

    private String name;
    private String color;
    private String description;
    private String symptomTypes;
    long id;
    boolean isActive;

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Diary() {
        // all persisted classes must define a no-arg constructor with at least package visibility
//        super(updatedAt, createdAt);
    }

    public TreeMap<Integer, String> getSymptomTypes() {
        return new Gson().fromJson(symptomTypes, new TypeToken<TreeMap<Integer, String>>() {
        }.getType());
    }

    public void setSymptomTypes(String symptomTypes) {
        this.symptomTypes = symptomTypes;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
