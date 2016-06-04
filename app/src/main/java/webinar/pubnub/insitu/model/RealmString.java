package webinar.pubnub.insitu.model;

import io.realm.RealmObject;

/**
 * Created by Konstantinos Michail on 5/29/2016.
 */
public class RealmString extends RealmObject {
        private String val;
        private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getValue() {
            return val;
        }

        public void setValue(String value) {
            this.val = value;
        }
}
