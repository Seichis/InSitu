package webinar.pubnub.insitu;

import android.support.v7.app.AppCompatActivity;

/**
 * Created by Konstantinos Michail on 4/17/2016.
 */
public interface IBackgroundSettingsService {

    void activateFlic();
    void deactivateFlic();

    void activateMoves(AppCompatActivity activity);

    void deactivateMoves();

    void setMode(int mode);


}
