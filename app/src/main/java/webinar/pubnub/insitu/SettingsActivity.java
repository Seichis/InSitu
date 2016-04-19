package webinar.pubnub.insitu;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.RadioGroup;
import android.widget.Switch;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import io.realm.Realm;
import webinar.pubnub.insitu.managers.SettingsManager;
import webinar.pubnub.insitu.model.Settings;

public class SettingsActivity extends AppCompatActivity {

    BackgroundService backgroundService;
    @Bind(R.id.activate_moves_switch)
    Switch movesSwitch;
    @Bind(R.id.activate_lifelog_switch)
    Switch lifelogSwitch;
    @Bind(R.id.activate_flic_switch)
    Switch flicSwitch;
    @Bind(R.id.radio_group_mode)
    RadioGroup radioGroup;
    Settings settings;
    SettingsManager settingsManager;
    @OnClick(R.id.apply_changes)
    void applyChanges() {
        this.finish();
    }

    @OnClick(R.id.discard_changes)
    void discardChanges() {
        this.finish();
    }

    @OnCheckedChanged(R.id.activate_moves_switch)
    void activateMoves() {
        backgroundService.activateMoves(this);
        Realm.getDefaultInstance().beginTransaction();
        settings.setMovesActivated(true);
        Realm.getDefaultInstance().commitTransaction();
    }

    @OnCheckedChanged(R.id.activate_flic_switch)
    void activateFlicButton() {
        if (flicSwitch.isChecked()) {
            backgroundService.activateFlic();
            Realm.getDefaultInstance().beginTransaction();
            settings.setFlicActivated(true);
            Realm.getDefaultInstance().commitTransaction();
        } else {
            backgroundService.deactivateFlic();
            Realm.getDefaultInstance().beginTransaction();
            settings.setFlicActivated(false);
            Realm.getDefaultInstance().commitTransaction();
        }
    }

    @OnCheckedChanged(R.id.activate_lifelog_switch)
    void activateLifelog() {

    }

    @OnClick(R.id.radio_group_mode)
    void changeMode() {
        switch (radioGroup.getCheckedRadioButtonId()) {
            case (R.id.no_mode):
                settingsManager.changeMode(Settings.NO_BUTTON_MODE);
//                backgroundService.setMode(Settings.NO_BUTTON_MODE);
                break;
            case (R.id.binary_mode):
                settingsManager.changeMode(Settings.BINARY_BUTTON_MODE);

//                backgroundService.setMode(Settings.BINARY_BUTTON_MODE);
                break;
            case (R.id.full_button_mode):
                settingsManager.changeMode(Settings.FULL_BUTTON_MODE);

//                backgroundService.setMode(Settings.FULL_BUTTON_MODE);
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);
        settingsManager=SettingsManager.getInstance();
        settings = settingsManager.getSettings();
        backgroundService = BackgroundService.getInstance();
        setupViewWithSettings();
    }

    void setupViewWithSettings() {
        if (settings.isFlicActivated()) {
            flicSwitch.setChecked(true);
        }
        if (settings.isLifelogActivated()){
            lifelogSwitch.setChecked(true);
        }
        if(settings.isMovesActivated()){
            movesSwitch.setChecked(true);
        }
        switch (settings.getMode()){
            case 0:
                radioGroup.check(R.id.no_mode);
                break;
            case 1:
                radioGroup.check(R.id.binary_mode);
                break;
            case 2:
                radioGroup.check(R.id.full_button_mode);
        }
    }

}
