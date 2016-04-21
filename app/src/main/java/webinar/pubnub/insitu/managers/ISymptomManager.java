package webinar.pubnub.insitu.managers;

import io.realm.RealmResults;
import webinar.pubnub.insitu.model.Symptom;

/**
 * Created by Konstantinos Michail on 4/21/2016.
 */
public interface ISymptomManager {

    RealmResults<Symptom> getAllSymptomsByDay(long date);

    RealmResults<Symptom> getAllSymptoms();

    RealmResults<Symptom> getAllSymptomsByRange(long from, long until);

    RealmResults<Symptom> getAllSymptomsByActivityByDay(int activityId, long date);

    RealmResults<Symptom> getAllSymptomsByActivityByRange(int activityId, long from,long until);

    Symptom getSymptomById(int id);

    RealmResults<Symptom> getAllSymptomsByActivity(int activityId);

    float getMeanIntensityByDay(long date);

    float getMeanIntensityByRange(long from, long until);

    float getMeanDistressByDay(long date);

    float getMeanDistressByRange(long from, long until);


}
