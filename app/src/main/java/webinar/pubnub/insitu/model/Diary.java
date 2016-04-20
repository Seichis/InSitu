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
    private String description;
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


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
