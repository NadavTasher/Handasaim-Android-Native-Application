package nadav.tasher.handasaim.activities;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import nadav.tasher.handasaim.R;
import nadav.tasher.handasaim.tools.architecture.Starter;
import nadav.tasher.handasaim.values.Values;
import nadav.tasher.lightool.info.Device;
import nadav.tasher.lightool.tools.Animations;

import static nadav.tasher.handasaim.tools.architecture.Starter.beginDND;
import static nadav.tasher.handasaim.values.Values.fontColor;
import static nadav.tasher.handasaim.values.Values.fontColorDefault;
import static nadav.tasher.handasaim.values.Values.prefName;

public class Welcome extends Activity {

    private SharedPreferences sp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initStageA();
    }

    private void taskDesc() {
        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        ActivityManager.TaskDescription taskDesc;
        taskDesc = new ActivityManager.TaskDescription(getString(R.string.app_name), bm, (Main.getColorB(getApplicationContext())));
        setTaskDescription(taskDesc);
    }

    private void initStageA() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER_PORTRAIT);
        sp = getSharedPreferences(Values.prefName, Context.MODE_PRIVATE);
        initStageB();
    }

    private void initStageB() {
        taskDesc();
        getWindow().setStatusBarColor(Main.getColorA(getApplicationContext()));
        getWindow().setNavigationBarColor(Main.getColorB(getApplicationContext()));
        LinearLayout part1 = new LinearLayout(this);
        part1.setGravity(Gravity.CENTER);
        part1.setOrientation(LinearLayout.VERTICAL);
        part1.setBackground(Main.getGradient(getApplicationContext()));
        //part1
        ImageView icon = new ImageView(this);
        final Button setup = new Button(this);
        final TextView welcome = new TextView(this);
        setup.setTypeface(Main.getTypeface(getApplicationContext()));
        setup.setAllCaps(false);
        welcome.setTypeface(Main.getTypeface(getApplicationContext()));
        setup.setText(R.string.begin_text);
        setup.setAllCaps(false);
        setup.setAlpha(0);
        setup.setBackgroundColor(Color.TRANSPARENT);
        setup.setTextSize((float) 30);
        welcome.setAlpha(0);
        welcome.setGravity(Gravity.CENTER);
        welcome.setTextSize((float) 29);
        welcome.setTextColor(Color.WHITE);
        welcome.setText(R.string.welcome);
        icon.setImageDrawable(getDrawable(R.drawable.ic_icon));
        int is = (int) (Device.screenX(getApplicationContext()) * 0.7);
        icon.setLayoutParams(new LinearLayout.LayoutParams(is, is));
        ObjectAnimator iconSlide = ObjectAnimator.ofFloat(icon, View.TRANSLATION_X, -Device.screenX(getApplicationContext()), 0);
        iconSlide.setDuration(1000);
        iconSlide.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                ObjectAnimator buttonAn = ObjectAnimator.ofFloat(setup, View.ALPHA, Animations.INVISIBLE_TO_VISIBLE);
                buttonAn.setDuration(500);
                buttonAn.start();
                ObjectAnimator welAn = ObjectAnimator.ofFloat(welcome, View.ALPHA, Animations.INVISIBLE_TO_VISIBLE);
                welAn.setDuration(500);
                welAn.start();
            }

            @Override
            public void onAnimationCancel(Animator animator) {
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
            }
        });
        iconSlide.start();
        part1.addView(welcome);
        part1.addView(icon);
        part1.addView(setup);
        setup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                writeDefaults();
                beginDND(getApplicationContext());
                Starter.scheduleJobs(getApplicationContext());
                Main.startMe(Welcome.this);
            }
        });
        setContentView(part1);
    }
    public static void startMe(Activity c){
        Intent intent = new Intent(c, Welcome.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        c.startActivity(intent);
        c.overridePendingTransition(R.anim.out, R.anim.in);
        c.finish();
    }

    private void writeDefaults() {
        final SharedPreferences sp = getSharedPreferences(prefName, Context.MODE_PRIVATE);
        SharedPreferences.Editor se = sp.edit();
        se.putInt(Values.fontSizeNumber, Values.fontSizeDefault);
        se.putBoolean(Values.pushService, Values.pushDefault);
        se.putBoolean(Values.breakTime, Values.breakTimeDefault);
        se.putBoolean(Values.autoMute, Values.autoMuteDefault);
        se.putInt(fontColor, fontColorDefault);
        se.putInt(Values.colorA, Values.defaultColorA);
        se.putInt(Values.colorB, Values.defaultColorB);
        se.putBoolean(Values.firstLaunch, false);
        se.apply();
    }



}
