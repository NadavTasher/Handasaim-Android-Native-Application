package nadav.tasher.handasaim.activities;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.WindowManager;

import nadav.tasher.handasaim.activities.framables.Main;
import nadav.tasher.handasaim.activities.framables.Splash;
import nadav.tasher.handasaim.tools.TowerHub;
import nadav.tasher.handasaim.tools.architecture.KeyManager;
import nadav.tasher.handasaim.tools.architecture.Starter;
import nadav.tasher.handasaim.values.Values;

import static nadav.tasher.handasaim.values.Values.colorForce;
import static nadav.tasher.handasaim.values.Values.colorForceDefault;

public class Launcher extends Activity {

    private SharedPreferences sp;
    private KeyManager keyManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        lockState();
    }

    private void lockState() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER_PORTRAIT);
        sp = getSharedPreferences(Values.prefName, Context.MODE_PRIVATE);
        keyManager = new KeyManager(getApplicationContext());
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        Starter.scheduleJobs(getApplicationContext());
        if (!sp.getBoolean(Values.colorForce, colorForceDefault)) {
            Main.installColors(getApplicationContext());
            sp.edit().putBoolean(colorForce, true).apply();
        }
        startSplash();
    }

    private void startSplash() {
        Splash splash = new Splash(this, sp, keyManager);
        splash.start();
    }

    @Override
    public void onBackPressed() {
        if (TowerHub.current!=null) {
            TowerHub.current.onBackPressed();
        }
    }
}
