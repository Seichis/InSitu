package webinar.pubnub.insitu.model;

import io.realm.RealmObject;

/**
 * Created by Konstantinos Michail on 4/20/2016.
 */
public class Description extends RealmObject {
    float distress = 0f;
    String comments;
    String painType;
    float duration = 0f;
    String bodyPart = "";
    String bodyPartDetails;
    String specificActivity;
    String dayPainkiller;
    String hourPainkiller;
    String monthPainkiller;
    String yearPainkiller;
    String weekPainkiller;

    public String getWeekPainkiller() {
        return weekPainkiller;
    }

    public void setWeekPainkiller(String weekPainkiller) {
        this.weekPainkiller = weekPainkiller;
    }

    String painkiller;
    float painkillerAmount = 0f;
    long datePainkillerConsumption = 0;
    String nonDrugTechniques;

    public Description() {
    }

    public String getDayPainkiller() {
        return dayPainkiller;
    }

    public void setDayPainkiller(String dayPainkiller) {
        this.dayPainkiller = dayPainkiller;
    }

    public String getHourPainkiller() {
        return hourPainkiller;
    }

    public void setHourPainkiller(String hourPainkiller) {
        this.hourPainkiller = hourPainkiller;
    }

    public String getMonthPainkiller() {
        return monthPainkiller;
    }

    public void setMonthPainkiller(String monthPainkiller) {
        this.monthPainkiller = monthPainkiller;
    }

    public String getYearPainkiller() {
        return yearPainkiller;
    }

    public void setYearPainkiller(String yearPainkiller) {
        this.yearPainkiller = yearPainkiller;
    }

    public String getPainkiller() {
        return painkiller;
    }

    public void setPainkiller(String painkiller) {
        this.painkiller = painkiller;
    }

    public float getPainkillerAmount() {
        return painkillerAmount;
    }

    public void setPainkillerAmount(float painkillerAmount) {
        this.painkillerAmount = painkillerAmount;
    }

    public long getDatePainkillerConsumption() {
        return datePainkillerConsumption;
    }

    public void setDatePainkillerConsumption(long datePainkillerConsumption) {
        this.datePainkillerConsumption = datePainkillerConsumption;
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


    public String getNonDrugTechniques() {
        return nonDrugTechniques;
    }

    public void setNonDrugTechniques(String nonDrugTechniques) {
        this.nonDrugTechniques = nonDrugTechniques;
    }

}
