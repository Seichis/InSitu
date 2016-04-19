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

    public boolean addSymptomType(Diary diary, String symType) {
        TreeMap<Integer, String> symTypes = diary.getSymptomTypes();

        if (symTypes == null) {
            symTypes = new TreeMap<>();
            symTypes.put(1, symType);
            realm.beginTransaction();
            diary.setSymptomTypes(new Gson().toJson(symTypes));
            realm.commitTransaction();
            return true;
        } else {
            if (symTypes.containsValue(symType) || symTypes.size() >= 3) {
                // TODO Show the user feedback that the symptom list is full for this diary:

                Log.i(TAG, "This Diary has no more room for more symptoms");
                return false;
            } else {
                int size = symTypes.size() + 1;
                symTypes.put(size, symType);
                realm.beginTransaction();
                diary.setSymptomTypes(new Gson().toJson(symTypes));
                realm.commitTransaction();
                Log.i(TAG, "More symptom types existed.Symptom type added." + symTypes.size());
                return true;
            }
        }
    }

//    public void updateDiary(Diary diary) {
//        // All writes must be wrapped in a transaction to facilitate safe multi threading
//        realm.beginTransaction();
//        Diary toUpdate=realm.where(Diary.class).contains("id",diary.getName()).findFirst();
//        toUpdate=diary
//        // When the transaction is committed, all changes a synced to disk.
//        realm.commitTransaction();
//    }

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

    public void refreshSymptoms(String s) {
        realm.beginTransaction();
        getActiveDiary().setSymptomTypes(s);
        realm.commitTransaction();
    }
}

