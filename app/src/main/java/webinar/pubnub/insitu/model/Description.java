package webinar.pubnub.insitu.model;

import java.util.Date;

import io.realm.RealmObject;

/**
 * Created by Konstantinos Michail on 4/20/2016.
 */
public class Description extends RealmObject {

    float distress;
    String comments;
    String painType;
    float duration;
    String bodyPart;
    String bodyPartDetails;
    String specificActivity;
    String medicineName;
    float medicineAmount;
    Date dateMedicationConsumption;
    String nonDrugTechniques;

    public Description() {
    }

    public float getDistress() {
        return distress;
    }

    public void setDistress(float distress) {
        this.distress = distress;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getPainType() {
        return painType;
    }

    public void setPainType(String painType) {
        this.painType = painType;
    }

    public float getDuration() {
        return duration;
    }

    public void setDuration(float duration) {
        this.duration = duration;
    }

    public String getBodyPart() {
        return bodyPart;
    }

    public void setBodyPart(String bodyPart) {
        this.bodyPart = bodyPart;
    }

    public String getBodyPartDetails() {
        return bodyPartDetails;
    }

    public void setBodyPartDetails(String bodyPartDetails) {
        this.bodyPartDetails = bodyPartDetails;
    }

    public String getSpecificActivity() {
        return specificActivity;
    }

    public void setSpecificActivity(String specificActivity) {
        this.specificActivity = specificActivity;
    }

    public String getMedicineName() {
        return medicineName;
    }

    public void setMedicineName(String medicineName) {
        this.medicineName = medicineName;
    }

    public float getMedicineAmount() {
        return medicineAmount;
    }

    public void setMedicineAmount(float medicineAmount) {
        this.medicineAmount = medicineAmount;
    }

    public Date getDateMedicationConsumption() {
        return dateMedicationConsumption;
    }

    public void setDateMedicationConsumption(Date dateMedicationConsumption) {
        this.dateMedicationConsumption = dateMedicationConsumption;
    }

    public String getNonDrugTechniques() {
        return nonDrugTechniques;
    }

    public void setNonDrugTechniques(String nonDrugTechniques) {
        this.nonDrugTechniques = nonDrugTechniques;
    }

}
