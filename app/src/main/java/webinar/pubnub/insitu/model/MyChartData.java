package webinar.pubnub.insitu.model;

import io.realm.RealmObject;

/**
 * Created by Konstantinos Michail on 4/20/2016.
 */
public class MyChartData extends RealmObject {
    float count;
    int id;
    String activity;

    public MyChartData() {
    }
    public MyChartData(String activity, float count, int id) {
        this.activity = activity;
        this.id = id;
        this.count = count;
    }

    public float getCount() {
        return count;
    }

    public int getId() {
        return id;
    }

    public String getActivity() {
        return activity;
    }

}
