package webinar.pubnub.insitu.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;

import io.realm.RealmObject;

public class Patient extends RealmObject {

    byte[] profilePic;
    String patientName;
    String gender;
    int age;
    long id;

    public Patient() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }



    public void setProfilePic(byte[] profilePic) {

        this.profilePic = profilePic;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public Bitmap getProfilePic() {
        return BitmapFactory.decodeByteArray(profilePic, 0, profilePic.length);
    }

    public void setProfilePic(Bitmap bitmap) {
        ByteArrayOutputStream blob = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, blob);
        this.profilePic = blob.toByteArray();
    }


}

