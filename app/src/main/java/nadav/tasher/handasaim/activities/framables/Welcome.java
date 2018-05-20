package nadav.tasher.handasaim.activities.framables;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import nadav.tasher.handasaim.R;
import nadav.tasher.handasaim.architecture.app.Framable;
import nadav.tasher.handasaim.tools.architecture.KeyManager;
import nadav.tasher.handasaim.values.Values;
import nadav.tasher.lightool.info.Device;
import nadav.tasher.lightool.tools.Animations;

import static nadav.tasher.handasaim.values.Values.fontColor;
import static nadav.tasher.handasaim.values.Values.fontColorDefault;

public class Welcome extends Framable{

    public Welcome(Activity a, SharedPreferences sp, KeyManager keyManager) {
        super(a, sp, keyManager);
    }

    private void taskDesc() {
        Bitmap bm = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.ic_icon);
        ActivityManager.TaskDescription taskDesc;
        taskDesc = new ActivityManager.TaskDescription(getApplicationContext().getString(R.string.app_name), bm, (Main.getColorB(getApplicationContext())));
        a.setTaskDescription(taskDesc);
    }
    @Override
    public void go() {
        taskDesc();
        a.getWindow().setStatusBarColor(Main.getColorA(getApplicationContext()));
        a.getWindow().setNavigationBarColor(Main.getColorB(getApplicationContext()));
        LinearLayout part1 = new LinearLayout(getApplicationContext());
        part1.setGravity(Gravity.CENTER);
        part1.setOrientation(LinearLayout.VERTICAL);
        part1.setBackground(Main.getGradient(getApplicationContext()));
        //part1
        ImageView icon = new ImageView(getApplicationContext());
        final Button setup = new Button(getApplicationContext());
        final TextView welcome = new TextView(getApplicationContext());
        setup.setTypeface(Main.getTypeface(getApplicationContext()));
        setup.setAllCaps(false);
        welcome.setTypeface(Main.getTypeface(getApplicationContext()));
        setup.setText(R.string.begin_text);
        setup.setAllCaps(false);
        setup.setAlpha(0);
        setup.setBackgroundColor(Color.TRANSPARENT);
        setup.setTextSize((float) 30);
        setup.setTextColor(Main.getTextColor(getApplicationContext()));
        welcome.setAlpha(0);
        welcome.setGravity(Gravity.CENTER);
        welcome.setTextSize((float) 29);
        welcome.setTextColor(Color.WHITE);
        welcome.setText(R.string.welcome);
        icon.setImageDrawable(getApplicationContext().getDrawable(R.drawable.ic_icon));
        icon.setAlpha(0f);
        int is = (int) (Device.screenX(getApplicationContext()) * 0.7);
        icon.setLayoutParams(new LinearLayout.LayoutParams(is, is));
        ObjectAnimator buttonAn = ObjectAnimator.ofFloat(setup, View.ALPHA, Animations.INVISIBLE_TO_VISIBLE);
        buttonAn.setDuration(2000);
        buttonAn.start();
        ObjectAnimator welAn = ObjectAnimator.ofFloat(welcome, View.ALPHA, Animations.INVISIBLE_TO_VISIBLE);
        welAn.setDuration(2000);
        welAn.start();
        ObjectAnimator icoAn = ObjectAnimator.ofFloat(icon, View.ALPHA, Animations.INVISIBLE_TO_VISIBLE);
        icoAn.setDuration(2000);
        icoAn.start();
        part1.addView(welcome);
        part1.addView(icon);
        part1.addView(setup);
        setup.setBackground(Main.generateCoaster(getApplicationContext(),Main.getColorA(getApplicationContext())));
        setup.setLayoutParams(new LinearLayout.LayoutParams((int) (Device.screenX(getApplicationContext())*0.6), ViewGroup.LayoutParams.WRAP_CONTENT));
        setup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                writeDefaults();
                Main main=new Main(a,sp,keyManager);
                main.start();
            }
        });
        a.setContentView(part1);
    }

    private void writeDefaults() {
        SharedPreferences.Editor se = sp.edit();
        se.putInt(Values.fontSizeNumber, Values.fontSizeDefault);
        se.putBoolean(Values.pushService, Values.pushDefault);
        se.putBoolean(Values.breakTime, Values.breakTimeDefault);
        se.putInt(fontColor, fontColorDefault);
        se.putInt(Values.colorA, Values.defaultColorA);
        se.putInt(Values.colorB, Values.defaultColorB);
        se.putBoolean(Values.firstLaunch, false);
        se.apply();
    }



}
