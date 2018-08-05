package nadav.tasher.handasaim.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.io.File;

import nadav.tasher.handasaim.R;
import nadav.tasher.handasaim.architecture.app.Center;
import nadav.tasher.handasaim.architecture.app.PreferenceManager;
import nadav.tasher.handasaim.tools.online.FileDownloader;
import nadav.tasher.handasaim.tools.specific.GetLink;
import nadav.tasher.lightool.communication.network.Ping;
import nadav.tasher.lightool.graphics.ColorFadeAnimation;
import nadav.tasher.lightool.info.Device;

public class SplashActivity extends Activity {

    PreferenceManager pm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initVars();
        go();
    }

    @Override
    public Context getApplicationContext() {
        return this;
    }

    private void initVars() {
        pm = new PreferenceManager(getApplicationContext());
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
    }

    private void go() {
        getWindow().setStatusBarColor(Center.getColorTop(getApplicationContext()));
        getWindow().setNavigationBarColor(Center.getColorBottom(getApplicationContext()));
        final LinearLayout ll = new LinearLayout(getApplicationContext());
        ll.setGravity(Gravity.CENTER);
        ll.setOrientation(LinearLayout.VERTICAL);
        ll.setBackgroundColor(Center.getColorTop(getApplicationContext()));
        final ImageView icon = new ImageView(getApplicationContext());
        icon.setScaleType(ImageView.ScaleType.FIT_XY);
        icon.setImageDrawable(getApplicationContext().getDrawable(R.drawable.ic_icon));
        int is = (int) (Device.screenX(getApplicationContext()) * 0.8);
        icon.setLayoutParams(new LinearLayout.LayoutParams(is, is));
        ll.addView(icon);
        setContentView(ll);
        ColorFadeAnimation cfa = new ColorFadeAnimation(Center.getColorBottom(getApplicationContext()), Center.getColorTop(getApplicationContext()), new ColorFadeAnimation.ColorState() {
            @Override
            public void onColor(final int color) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ll.setBackground(generateGradient(color, Center.getColorBottom(getApplicationContext())));
                        getWindow().setNavigationBarColor(Center.getColorBottom(getApplicationContext()));
                        getWindow().setStatusBarColor(color);
                    }
                });
            }
        });
        cfa.start(3000);
        initStageC();
    }

    private Drawable generateGradient(int colorA, int colorB) {
        return new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, new int[]{
                colorA,
                colorB
        });
    }

    private void initStageC() {
        //        Log.i("Stage", "C");
        if (Device.isOnline(getApplicationContext())) {
            new Ping(getString(R.string.provider_internal), 5000, new Ping.OnEnd() {
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
        //        Log.i("Stage", "D");
        new GetLink(getString(R.string.provider_internal_schedule), new GetLink.GotLink() {

            @Override
            public void onLinkGet(String link) {
                if (link != null) {
                    String fileName = "hs.xls";
                    if (link.endsWith(".xlsx")) {
                        fileName = "hs.xlsx";
                    }
                    String date = link.split("/")[link.split("/").length - 1].split("\\.")[0];
                    if (pm.getCoreManager().getDate() == null || !pm.getCoreManager().getDate().equals(date)) {
                        pm.getCoreManager().setDate(date);
                        if (!pm.getServicesManager().getScheduleNotifiedAlready(date))
                            pm.getServicesManager().setScheduleNotifiedAlready(date);
                        new FileDownloader(link, new File(getApplicationContext().getFilesDir(), fileName), new FileDownloader.OnDownload() {
                            @Override
                            public void onFinish(final File file, final boolean be) {
                                pm.getCoreManager().setFile(file.toString());
                                initStageE(true);
                            }

                            @Override
                            public void onProgressChanged(File file, int i) {
                            }
                        }).execute();
                    } else {
                        initStageE(false);
                    }
                } else {
                    initStageD();
                }
            }

            @Override
            public void onFail(String e) {
                initStageD();
            }
        }).execute();
    }

    private void initStageE(boolean newSchedule) {
        //        Log.i("Stage", "E");
        if (!pm.getUserManager().get(R.string.preferences_user_launch_first, true)) {
            if (newSchedule) {
                if (pm.getKeyManager().isKeyLoaded(R.string.preferences_keys_type_news)) {
                    Center.enter(this, HomeActivity.class);
                } else {
                    Center.enter(this, NewsActivity.class);
                }
            } else {
                Center.enter(this, HomeActivity.class);
            }
        } else {
            Center.enter(this, TutorialActivity.class);
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
