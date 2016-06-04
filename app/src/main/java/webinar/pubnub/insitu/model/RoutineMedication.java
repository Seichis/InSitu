package webinar.pubnub.insitu.model;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by Konstantinos Michail on 5/31/2016.
 */
public class RoutineMedication extends RealmObject {
    boolean automaticLogging=false;
    boolean flicButtonLogging=false;
    boolean alarmEnabled=false;
    RealmList<RealmString> daysRepeat;
    String hourOfRoutineConsumption;
    String medicationType;
    String medicationName="";
    String amount;
    String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMedicationType() {
        return medicationType;
    }

    public void setMedicationType(String medicationType) {
        this.medicationType = medicationType;
    }

    public String getMedicationName() {
        return medicationName;
    }

    public void setMedicationName(String medicationName) {
        this.medicationName = medicationName;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public boolean isAutomaticLogging() {
        return automaticLogging;
    }

    public void setAutomaticLogging(boolean automaticLogging) {
        this.automaticLogging = automaticLogging;
    }

    public boolean isFlicButtonLogging() {
        return flicButtonLogging;
    }

    public void setFlicButtonLogging(boolean flicButtonLogging) {
        this.flicButtonLogging = flicButtonLogging;
    }

    public boolean isAlarmEnabled() {
        return alarmEnabled;
    }

    public void setAlarmEnabled(boolean alarmEnabled) {
        this.alarmEnabled = alarmEnabled;
    }

    public RealmList<RealmString> getDaysRepeat() {
        return daysRepeat;
    }

    public void setDaysRepeat(RealmList<RealmString> daysRepeat) {
        this.daysRepeat = daysRepeat;
    }

    public String getHourOfRoutineConsumption() {
        return hourOfRoutineConsumption;
    }

    public void setHourOfRoutineConsumption(String hourOfRoutineConsumption) {
        this.hourOfRoutineConsumption = hourOfRoutineConsumption;
    }
}
