package nadav.tasher.handasaim.activities;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.File;

import nadav.tasher.handasaim.R;
import nadav.tasher.handasaim.tools.architecture.Starter;
import nadav.tasher.handasaim.tools.online.FileDownloader;
import nadav.tasher.handasaim.tools.specific.GetLink;
import nadav.tasher.handasaim.values.Values;
import nadav.tasher.lightool.communication.network.Ping;
import nadav.tasher.lightool.graphics.ColorFadeAnimation;
import nadav.tasher.lightool.info.Device;
import nadav.tasher.lightool.tools.Animations;

import static nadav.tasher.handasaim.tools.architecture.Starter.beginDND;

public class Splash extends Activity {

    private Main.MyGraphics.CurvedTextView ctv;
    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initStageA();
    }

    private void taskDesc() {
        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        ActivityManager.TaskDescription taskDesc;
        taskDesc = new ActivityManager.TaskDescription(getString(R.string.app_name), bm, (Main.getColorA(getApplicationContext())));
        setTaskDescription(taskDesc);
    }

    private void initStageA() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER_PORTRAIT);
        sp = getSharedPreferences(Values.prefName, Context.MODE_PRIVATE);
        initStageB();
    }

    private void initStageB() {
        final Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Main.getColorA(getApplicationContext()));
        window.setNavigationBarColor(Main.getColorB(getApplicationContext()));
        taskDesc();
        final LinearLayout ll = new LinearLayout(this);
        ll.setGravity(Gravity.CENTER);
        ll.setOrientation(LinearLayout.VERTICAL);
        ll.setBackgroundColor(Main.getColorA(getApplicationContext()));
        final ImageView icon = new ImageView(this);
        icon.setScaleType(ImageView.ScaleType.FIT_XY);
        icon.setImageDrawable(getDrawable(R.drawable.ic_icon));
        int is = (int) (Device.screenX(getApplicationContext()) * 0.8);
        icon.setLayoutParams(new LinearLayout.LayoutParams(is, is));
        ll.addView(icon);
        String curved = getString(R.string.app_name);
        ctv = new Main.MyGraphics.CurvedTextView(this, curved, 50, Values.bakedIconColor, Device.screenX(this), (int) (Device.screenY(getApplicationContext()) * 0.3), (int) (Device.screenY(getApplicationContext()) * 0.15) / 2);
        ctv.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) (Device.screenY(getApplicationContext()) * 0.3)));
        ll.addView(ctv);
        RotateAnimation rotateAnimation = new RotateAnimation(0, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setInterpolator(new LinearInterpolator());
        rotateAnimation.setDuration(2500);
        rotateAnimation.setRepeatCount(Animation.INFINITE);
        ctv.startAnimation(rotateAnimation);
        ctv.setVisibility(View.VISIBLE);
        ctv.setAlpha(0);
        ObjectAnimator oa = ObjectAnimator.ofFloat(ctv, View.ALPHA, Animations.INVISIBLE_TO_VISIBLE);
        oa.setDuration(1000);
        oa.start();
        setContentView(ll);
        ColorFadeAnimation cfa = new ColorFadeAnimation(Main.getColorB(getApplicationContext()),Main.getColorA(getApplicationContext()), new ColorFadeAnimation.ColorState() {
            @Override
            public void onColor(final int color) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ll.setBackground(generateGradient(color,Main.getColorB(getApplicationContext())));
                        getWindow().setNavigationBarColor(Main.getColorB(getApplicationContext()));
                        getWindow().setStatusBarColor(color);
                    }
                });
            }
        });
        cfa.start(2000);
        if (getIntent().getExtras() != null) {
            if (getIntent().getExtras().getString("toast") != null) {
                Toast.makeText(this, getIntent().getExtras().getString("toast"), Toast.LENGTH_LONG).show();
            } else if (getIntent().getExtras().getString("popup") != null) {
                AlertDialog.Builder pop = new AlertDialog.Builder(this);
                pop.setCancelable(true);
                pop.setMessage(getIntent().getExtras().getString("popup"));
                pop.setPositiveButton("OK", null);
                pop.show();
            }
        }
        initStageC();
    }

    private Drawable generateGradient(int colorA,int colorB){
            return new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, new int[]{colorA, colorB});
    }

    private void initStageC() {
        if (Device.isOnline(getApplicationContext())) {
            new Ping(Values.serviceProvider, 5000, new Ping.OnEnd() {
                @Override
                public void onPing(boolean b) {
                    if (b) {
                        initStageD();
                    } else {
                        initStageC();
                    }
                }
            }).execute();
        } else {
            popup("No Internet Connection.");
        }
    }

    private void initStageD() {
        Starter.scheduleJobs(getApplicationContext());
        new GetLink(Values.scheduleProvider, new GetLink.GotLink() {

            @Override
            public void onLinkGet(String link) {
                if (link != null) {
                    String fileName = "hs.xls";
                    if (link.endsWith(".xlsx")) {
                        fileName = "hs.xlsx";
                    }
                    String date = link.split("/")[link.split("/").length - 1].split("\\.")[0];
                    if (!sp.getString(Values.latestFileDate, "").equals(date)) {
                        sp.edit().putString(Values.latestFileDate, date).commit();
                        sp.edit().putString(Values.latestFileDateRefresher, date).commit();
                        new FileDownloader(link, new File(getApplicationContext().getFilesDir(), fileName), new FileDownloader.OnDownload() {
                            @Override
                            public void onFinish(final File file, final boolean be) {
                                sp.edit().putString(Values.scheduleFile, file.toString()).apply();
                                initStageE();
                            }

                            @Override
                            public void onProgressChanged(File file, int i) {
                            }
                        }).execute();
                    } else {
                        initStageE();
                    }
                } else {
                    initStageD();
                }
            }

            @Override
            public void onFail(String e) {
                //                popup("Failed");
                initStageD();
            }
        }).execute();
    }

    private void initStageE() {
        if (!sp.getBoolean(Values.firstLaunch, true)) {
            beginDND(getApplicationContext());
            News.startMe(this);
        } else {
            Welcome.startMe(this);
        }
    }

    private void popup(String text) {
        AlertDialog.Builder pop = new AlertDialog.Builder(this);
        pop.setCancelable(true);
        pop.setMessage(text);
        pop.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                initStageC();
            }
        });
        pop.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                initStageC();
            }
        });
        pop.show();
    }
}
