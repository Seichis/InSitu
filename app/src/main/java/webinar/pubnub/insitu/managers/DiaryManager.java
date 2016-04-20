package webinar.pubnub.insitu.managers;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;

import java.util.List;
import java.util.TreeMap;

import io.realm.Realm;
import webinar.pubnub.insitu.model.Diary;


public class DiaryManager {
    private static final String TAG = "DiaryManager";
    private static DiaryManager diaryManager = new DiaryManager();
    Realm realm;
    private Context context;

    private DiaryManager() {

    }

    public static DiaryManager getInstance() {
        return diaryManager;
    }

    public void init(Context context, Realm realm) {
        this.context = context;
        this.realm = realm;
    }

    public void createDiary(String name, String description) {
        // All writes must be wrapped in a transaction to facilitate safe multi threading
        realm.beginTransaction();

        // Add a person
        Diary diary = realm.createObject(Diary.class);
        diary.setId(1);
        diary.setName(name);
        diary.setDescription(description);
        diary.setActive(true);
        // When the transaction is committed, all changes a synced to disk.
        realm.commitTransaction();
    }


    public void deleteDiary() {

    }

    public List<Diary> getDiaries() {
        return realm.allObjects(Diary.class);
    }

    public Diary searchByName(String name) {
        return realm.where(Diary.class).contains("name", name).findFirst();
    }

    public Diary getActiveDiary() {
        return realm.where(Diary.class).equalTo("isActive", true).findFirst();
    }

}

