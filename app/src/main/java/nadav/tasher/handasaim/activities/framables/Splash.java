package nadav.tasher.handasaim.activities.framables;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.File;

import nadav.tasher.handasaim.R;
import nadav.tasher.handasaim.architecture.app.Framable;
import nadav.tasher.handasaim.tools.architecture.KeyManager;
import nadav.tasher.handasaim.tools.graphics.CurvedTextView;
import nadav.tasher.handasaim.tools.online.FileDownloader;
import nadav.tasher.handasaim.tools.specific.GetLink;
import nadav.tasher.handasaim.values.Values;
import nadav.tasher.lightool.communication.network.Ping;
import nadav.tasher.lightool.graphics.ColorFadeAnimation;
import nadav.tasher.lightool.info.Device;
import nadav.tasher.lightool.tools.Animations;

public class Splash extends Framable {

    private CurvedTextView ctv;

    public Splash(Activity a, SharedPreferences sp, KeyManager keyManager) {
        super(a, sp, keyManager);
    }

    private void taskDesc() {
        ActivityManager.TaskDescription taskDesc = new ActivityManager.TaskDescription(null, null, (Main.getColorA(getApplicationContext())));
        a.setTaskDescription(taskDesc);
    }

    @Override
    public void go() {
        getWindow().setStatusBarColor(Main.getColorA(getApplicationContext()));
        getWindow().setNavigationBarColor(Main.getColorB(getApplicationContext()));
        taskDesc();
        final LinearLayout ll = new LinearLayout(getApplicationContext());
        ll.setGravity(Gravity.CENTER);
        ll.setOrientation(LinearLayout.VERTICAL);
        ll.setBackgroundColor(Main.getColorA(getApplicationContext()));
        final ImageView icon = new ImageView(getApplicationContext());
        icon.setScaleType(ImageView.ScaleType.FIT_XY);
        icon.setImageDrawable(getApplicationContext().getDrawable(R.drawable.ic_icon));
        int is = (int) (Device.screenX(getApplicationContext()) * 0.8);
        icon.setLayoutParams(new LinearLayout.LayoutParams(is, is));
        String curved = getApplicationContext().getString(R.string.app_name);
        if (keyManager.isKeyLoaded(KeyManager.TYPE_TEACHER_MODE)) {
            curved = "Find My Shlomi";
        }
        if (sp.getBoolean(Values.devMode, Values.devModeDefault)) {
            curved = "Developer Mode";
        }
        ctv = new CurvedTextView(getApplicationContext(), curved, Main.getFontSize(getApplicationContext()) * 2, Values.bakedIconColor, Device.screenX(getApplicationContext()), (int) (Device.screenY(getApplicationContext()) * 0.3), (int) (Device.screenY(getApplicationContext()) * 0.15) / 2);
        ctv.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) (Device.screenY(getApplicationContext()) * 0.3)));
        ll.addView(icon);
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
        ColorFadeAnimation cfa = new ColorFadeAnimation(Main.getColorB(getApplicationContext()), Main.getColorA(getApplicationContext()), new ColorFadeAnimation.ColorState() {
            @Override
            public void onColor(final int color) {
                a.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ll.setBackground(generateGradient(color, Main.getColorB(getApplicationContext())));
                        getWindow().setNavigationBarColor(Main.getColorB(getApplicationContext()));
                        getWindow().setStatusBarColor(color);
                    }
                });
            }
        });
        cfa.start(3000);
        if (a.getIntent().getExtras() != null) {
            if (a.getIntent().getExtras().getString("toast") != null) {
                Toast.makeText(getApplicationContext(), a.getIntent().getExtras().getString("toast"), Toast.LENGTH_LONG).show();
            } else if (a.getIntent().getExtras().getString("popup") != null) {
                AlertDialog.Builder pop = new AlertDialog.Builder(a);
                pop.setCancelable(true);
                pop.setMessage(a.getIntent().getExtras().getString("popup"));
                pop.setPositiveButton("OK", null);
                pop.show();
            }
        }
        initStageC();
    }

    private Drawable generateGradient(int colorA, int colorB) {
        return new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, new int[]{
                colorA,
                colorB
        });
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
        new GetLink(Values.scheduleProvider, new GetLink.GotLink() {

            @Override
            public void onLinkGet(String link) {
                //                link="http://handasaim.co.il/wp-content/uploads/2017/06/22-5-1.xlsx";
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
            News news = new News(a, sp, keyManager);
            news.start();
        } else {
            Welcome welcome = new Welcome(a, sp, keyManager);
            welcome.start();
        }
    }

    private void popup(String text) {
        AlertDialog.Builder pop = new AlertDialog.Builder(a);
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
