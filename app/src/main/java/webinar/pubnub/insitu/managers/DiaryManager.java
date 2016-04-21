package webinar.pubnub.insitu.managers;

import android.content.Context;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
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

    public void init(Context context) {
        this.context = context;
        realm=Realm.getDefaultInstance();
    }

    public void createDiary(String name, String description) {
        // All writes must be wrapped in a transaction to facilitate safe multi threading
        RealmResults<Diary> existingDiaries = realm.allObjects(Diary.class);

        realm.beginTransaction();
        if (existingDiaries.isEmpty()) {
            // Add a person
            Diary diary = realm.createObject(Diary.class);
            diary.setId(1);
            diary.setName(name);
            diary.setDescription(description);
            diary.setActive(true);
        } else {
            long maxId = existingDiaries.max("id").longValue();
            for (Diary d : existingDiaries){
                d.setActive(false);
            }
            Diary diary = realm.createObject(Diary.class);
            diary.setId(maxId+1);
            diary.setName(name);
            diary.setDescription(description);
            diary.setActive(true);
        }
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

