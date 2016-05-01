package webinar.pubnub.insitu.model;

import io.realm.RealmObject;
import io.realm.annotations.Index;

/**
 * Created by Konstantinos Michail on 4/25/2016.
 */
public class MyLineChartData extends RealmObject{

    int classId;
    String className;
    float value;
    @Index
    int setId;

    public int getClassId() {
        return classId;
    }

    public void setClassId(int classId) {
        this.classId = classId;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }

    public int getSetId() {
        return setId;
    }

    public void setSetId(int setId) {
        this.setId = setId;
    }
}
