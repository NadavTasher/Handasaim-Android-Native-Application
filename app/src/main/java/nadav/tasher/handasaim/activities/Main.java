package nadav.tasher.handasaim.activities;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.poi.ss.usermodel.Sheet;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;

import nadav.tasher.handasaim.R;
import nadav.tasher.handasaim.architecture.StudentClass;
import nadav.tasher.handasaim.architecture.Teacher;
import nadav.tasher.handasaim.tools.TunnelHub;
import nadav.tasher.handasaim.tools.graphics.LessonView;
import nadav.tasher.handasaim.tools.graphics.MessageBar;
import nadav.tasher.handasaim.tools.graphics.bar.Bar;
import nadav.tasher.handasaim.tools.graphics.bar.Squircle;
import nadav.tasher.handasaim.tools.specific.GetNews;
import nadav.tasher.handasaim.values.Filters;
import nadav.tasher.handasaim.values.Values;
import nadav.tasher.lightool.communication.OnFinish;
import nadav.tasher.lightool.communication.SessionStatus;
import nadav.tasher.lightool.communication.Tunnel;
import nadav.tasher.lightool.communication.network.Ping;
import nadav.tasher.lightool.communication.network.request.Post;
import nadav.tasher.lightool.communication.network.request.RequestParameter;
import nadav.tasher.lightool.graphics.views.AppView;
import nadav.tasher.lightool.graphics.views.ColorPicker;
import nadav.tasher.lightool.graphics.views.DragNavigation;
import nadav.tasher.lightool.info.Device;

import static nadav.tasher.handasaim.tools.architecture.AppCore.getBreak;
import static nadav.tasher.handasaim.tools.architecture.AppCore.getClasses;
import static nadav.tasher.handasaim.tools.architecture.AppCore.getDay;
import static nadav.tasher.handasaim.tools.architecture.AppCore.getGrade;
import static nadav.tasher.handasaim.tools.architecture.AppCore.getMessages;
import static nadav.tasher.handasaim.tools.architecture.AppCore.getRealEndTimeForHourNumber;
import static nadav.tasher.handasaim.tools.architecture.AppCore.getRealTimeForHourNumber;
import static nadav.tasher.handasaim.tools.architecture.AppCore.getSheet;
import static nadav.tasher.handasaim.tools.architecture.AppCore.getTeacherSchudleForClasses;
import static nadav.tasher.handasaim.tools.architecture.AppCore.getTimeForLesson;
import static nadav.tasher.handasaim.tools.architecture.AppCore.minuteOfDay;

public class Main extends Activity {
    static int textColor = Values.fontColorDefault;
    private int colorA = Values.defaultColorA;
    private int colorB = Values.defaultColorB;
    private int coasterColor = Values.defaultColorB;
    private int keyentering = 0;
    private String day;
    private StudentClass currentClass;
    private Teacher currentTeacher;
    private Bar bar;
    private Squircle main;
    private MessageBar messageBar;
    private LinearLayout scheduleLayout,lessonViewHolder;
    private Drawable gradient;
    private AppView mAppView;
    private ArrayList<StudentClass> classes;
    private ArrayList<Teacher> teachers;
    private ArrayList<String> messages;
    private SharedPreferences sp;
    private boolean breakTime = true;

    public static int getFontSize(Context c) {
        SharedPreferences sp = c.getSharedPreferences(Values.prefName, Context.MODE_PRIVATE);
        return sp.getInt(Values.fontSizeNumber, Values.fontSizeDefault);
    }

    public static int getColorA(Context c) {
        SharedPreferences sp = c.getSharedPreferences(Values.prefName, Context.MODE_PRIVATE);
        return sp.getInt(Values.colorA, Values.defaultColorA);
    }

    public static int getColorB(Context c) {
        SharedPreferences sp = c.getSharedPreferences(Values.prefName, Context.MODE_PRIVATE);
        return sp.getInt(Values.colorB, Values.defaultColorB);
    }

    public static int getTextColor(Context c) {
        SharedPreferences sp = c.getSharedPreferences(Values.prefName, Context.MODE_PRIVATE);
        return sp.getInt(Values.fontColor, Values.fontColorDefault);
    }

    public static Drawable getGradient(Context c) {
        return new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, new int[]{
                getColorA(c),
                getColorB(c)
        });
    }

    public static Typeface getTypeface(Context c) {
        return Typeface.createFromAsset(c.getAssets(), Values.fontName);
    }

    public static void startMe(Activity c) {
        Intent intent = new Intent(c, Main.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        c.startActivity(intent);
        c.overridePendingTransition(R.anim.out, R.anim.in);
        c.finish();
    }

    public static void returnToMe(Activity c) {
        Intent intent = new Intent(c, Main.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        c.startActivity(intent);
        c.overridePendingTransition(R.anim.back_out, R.anim.back_in);
        c.finish();
    }

    public static Drawable generateCoaster(Context c, int color) {
        GradientDrawable gd = (GradientDrawable) c.getDrawable(R.drawable.rounded_rect);
        if (gd != null) {
            gd.setColor(color);
        }
        return gd;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initStageA();
    }

    private void loadTheme() {
        textColor = sp.getInt(Values.fontColor, Values.fontColorDefault);
        colorA = sp.getInt(Values.colorA, colorA);
        colorB = sp.getInt(Values.colorB, colorB);
        gradient = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, new int[]{
                colorA,
                colorB
        });
        coasterColor = Color.argb(Values.squircleAlpha, Color.red(colorA), Color.green(colorA), Color.blue(colorA));
    }

    private void refreshTheme() {
        loadTheme();
        mAppView.setBackground(gradient);
        mAppView.setTopColor(colorA);
        mAppView.setBottomColor(colorB);
        mAppView.overlaySelf(getWindow());
        TunnelHub.colorAChangeTunnle.send(colorA);
        TunnelHub.colorBChangeTunnle.send(colorB);
        taskDesc();
    }

    private void taskDesc() {
        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        ActivityManager.TaskDescription taskDesc;
        if (mAppView == null) {
            taskDesc = new ActivityManager.TaskDescription(getString(R.string.app_name), bm, (colorA));
        } else {
            taskDesc = new ActivityManager.TaskDescription(getString(R.string.app_name), bm, (mAppView.getDragNavigation().calculateOverlayedColor(colorA)));
        }
        setTaskDescription(taskDesc);
    }

    private void initStageA() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER_PORTRAIT);
        sp = getSharedPreferences(Values.prefName, Context.MODE_PRIVATE);
        String file = sp.getString(Values.scheduleFile, null);
        if (file != null) {
            parseAndLoad(new File(file));
        }
    }

    private void loadKey(int type) {
        switch (type) {
            case 1:
                sp.edit().putBoolean(Values.messageBoardSkipEnabler, true).commit();
                break;
            case 2:
                Toast.makeText(getApplicationContext(), "Teacher Mode Enabled.", Toast.LENGTH_SHORT).show();
                sp.edit().putBoolean(Values.teacherModeEnabler, true).commit();
                break;
            default:
                break;
        }
        Toast.makeText(getApplicationContext(), "Key loaded successfully.", Toast.LENGTH_SHORT).show();
    }

    private void checkAndLoadKey(final String key) {
        new Ping(Values.puzProvider, 10000, new Ping.OnEnd() {
            @Override
            public void onPing(boolean b) {
                if (b) {
                    RequestParameter[] requestParameters = new RequestParameter[]{new RequestParameter("deactivate", key)};
                    new Post(Values.keyProvider, requestParameters, new OnFinish() {
                        @Override
                        public void onFinish(SessionStatus sessionStatus) {
                            try {
                                JSONObject o = new JSONObject(sessionStatus.getExtra());
                                if (o.getBoolean("success")) {
                                    if (o.getString("key").equals(key)) {
                                        loadKey(o.getInt("type"));
                                    } else {
                                        Toast.makeText(getApplicationContext(), "Key comparison failed.", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(getApplicationContext(), "Key does not exist, or already used", Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(getApplicationContext(), "Key verification failed.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                    ).execute();
                } else {
                    Toast.makeText(getApplicationContext(), "Key provider unreachable.", Toast.LENGTH_SHORT).show();
                }
            }
        }).execute();
    }

    private void popupKeyEntering() {
        AlertDialog.Builder pop = new AlertDialog.Builder(this);
        pop.setCancelable(true);
        pop.setTitle("Enter Unlock Key");
        final EditText key = new EditText(this);
        key.setFilters(new InputFilter[]{
                Filters.codeFilter,
                new InputFilter.AllCaps()
        });
        pop.setView(key);
        key.setLayoutParams(new WindowManager.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        pop.setPositiveButton("Unlock", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                checkAndLoadKey(key.getText().toString().toUpperCase());
            }
        });
        pop.setNegativeButton("Close", null);
        pop.show();
    }

    private void aboutPopup() {
        AlertDialog.Builder ab = new AlertDialog.Builder(Main.this);
        ab.setTitle(R.string.app_name);
        ab.setMessage("Made By Nadav Tasher.\nVersion: " + Device.getVersionName(getApplicationContext(), getPackageName()) + "\nBuild: " + Device.getVersionCode(getApplicationContext(), getPackageName()));
        ab.setCancelable(true);
        ab.setPositiveButton("Close", null);
        keyentering++;
        if (keyentering == Values.maxKeyEntering) {
            keyentering = 0;
            ab.setNegativeButton("Enter Code", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    popupKeyEntering();
                }
            });
        } else {
            ab.setNegativeButton("Developing For H+", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(Values.developingUrl));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            });
        }
        ab.show();
    }

    private void initStageB() {
        final int x = Device.screenX(getApplicationContext());
        final int squirclePadding = x / 30;
        final int squircleSize = (int) (x / 4.2);
        breakTime = sp.getBoolean(Values.breakTime, Values.breakTimeDefault);
        LinearLayout topper = new LinearLayout(this);
        topper.setOrientation(LinearLayout.VERTICAL);
        topper.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM);
        topper.setPadding(squirclePadding, 0, squirclePadding, 0);
        mAppView = new AppView(getApplicationContext(), getDrawable(R.drawable.ic_icon), Values.navColor);
        mAppView.getDragNavigation().setOnIconClick(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                aboutPopup();
            }
        });
        mAppView.setBackground(gradient);
        mAppView.setTopColor(colorA);
        mAppView.setBottomColor(colorB);
        main = new Squircle(getApplicationContext(), squircleSize, colorA);
        bar = new Bar(getApplicationContext(), main);
        bar.setOnMainSquircle(new Squircle.OnState() {
            @Override
            public void onOpen() {
            }

            @Override
            public void onClose() {
                mAppView.getDragNavigation().close(true);
            }

            @Override
            public void onBoth(boolean isOpened) {
            }
        });
        bar.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, squircleSize + 2 * squirclePadding));
        topper.addView(bar);
        mAppView.addView(topper);
        //        masterLayout.addView(optionHolder);
        //        circleView.setX(x - circleView.xy - circlePadding);
        //        circleView.setY(y - circleView.xy - getNavSize() / 2 - circlePadding);
        //        optionHolder.setY(y - (y - circleView.getY()) - (((options.length + 1) * circlePadding) / 2) - (options.length * circleSize + circlePadding));
        mAppView.getDragNavigation().setOnStateChangedListener(new DragNavigation.OnStateChangedListener() {
            @Override
            public void onOpen() {
                mAppView.getDragNavigation().emptyContent();
                mAppView.getDragNavigation().setContent(getNews());
            }

            @Override
            public void onClose() {
                mAppView.getDragNavigation().emptyContent();
            }
        });
        refreshTheme();
        bar.addSquircles(getSquircles(squircleSize));

        scheduleLayout=new LinearLayout(this);
        scheduleLayout.setGravity(Gravity.START | Gravity.CENTER_HORIZONTAL);
        scheduleLayout.setOrientation(LinearLayout.VERTICAL);
        scheduleLayout.setPadding(10, 10, 10, 10);
        messageBar=new MessageBar(this,messages);
        messageBar.start();
        scheduleLayout.addView(messageBar);
        lessonViewHolder = new LinearLayout(this);
        lessonViewHolder.setGravity(Gravity.START | Gravity.CENTER_HORIZONTAL);
        lessonViewHolder.setOrientation(LinearLayout.VERTICAL);
        scheduleLayout.addView(lessonViewHolder);
        mAppView.setContent(scheduleLayout);

        StudentClass c = getFavoriteClass();
        if (classes != null) if (c != null) setStudentMode(c);
        setContentView(mAppView);
    }

    private StudentClass getFavoriteClass() {
        int selectedClass = 0;
        if (sp.getString(Values.favoriteClass, null) != null) {
            if (classes != null) {
                for (int fc = 0; fc < classes.size(); fc++) {
                    if (sp.getString(Values.favoriteClass, "").equals(classes.get(fc).name)) {
                        return classes.get(fc);
                    }
                }
                return classes.get(selectedClass);
            } else {
                return null;
            }
        } else {
            return classes.get(selectedClass);
        }
    }
    //    private MyGraphics.CircleView.CircleOption[] getCircleOptions(int circleSize, int circlePadding) {
    //        MyGraphics.CircleView.CircleOption share = new MyGraphics.CircleView.CircleOption(getApplicationContext(), circleSize, circlePadding);
    //        share.setIcon(getDrawable(R.drawable.ic_share));
    //        share.setDesiredViewOnDemand(new MyGraphics.CircleView.CircleOption.OnDemand() {
    //            @Override
    //            public View demandView() {
    //                return generateShareView();
    //            }
    //        });
    //        MyGraphics.CircleView.CircleOption changeClass = new MyGraphics.CircleView.CircleOption(getApplicationContext(), circleSize, circlePadding);
    //        changeClass.setIcon(getDrawable(R.drawable.ic_class));
    //        changeClass.setDesiredViewOnDemand(new MyGraphics.CircleView.CircleOption.OnDemand() {
    //            @Override
    //            public View demandView() {
    //                return generateClassSwitchView();
    //            }
    //        });
    //        MyGraphics.CircleView.CircleOption settings = new MyGraphics.CircleView.CircleOption(getApplicationContext(), circleSize, circlePadding);
    //        settings.setIcon(getDrawable(R.drawable.ic_gear));
    //        settings.setDesiredView(getSettingsView());
    //        MyGraphics.CircleView.CircleOption[] options = new MyGraphics.CircleView.CircleOption[]{
    //                share, changeClass, settings
    //        };
    //        return options;
    //    }

    private Teacher getFavoriteTeacher() {
        int selectedTeacher = 0;
        if (sp.getString(Values.favoriteTeacher, null) != null) {
            if (teachers != null) {
                for (int fc = 0; fc < teachers.size(); fc++) {
                    if (sp.getString(Values.favoriteTeacher, "").equals(teachers.get(fc).mainName)) {
                        return teachers.get(fc);
                    }
                }
                return teachers.get(selectedTeacher);
            } else {
                return null;
            }
        } else {
            return teachers.get(selectedTeacher);
        }
    }

    private ArrayList<Squircle> getSquircles(int size) {
        ArrayList<Squircle> squircles = new ArrayList<>();
        final Squircle news = new Squircle(getApplicationContext(), size, colorA);
        news.setDrawable(getDrawable(R.drawable.ic_news));
        final FrameLayout newsContent = getNews();
        news.setOnState(new Squircle.OnState() {
            @Override
            public void onOpen() {
                mAppView.getDragNavigation().emptyContent();
                mAppView.getDragNavigation().open(false);
                mAppView.getDragNavigation().setContent(newsContent);
            }

            @Override
            public void onClose() {
                if (mAppView.getDragNavigation().isOpen()) {
                    if (mAppView.getDragNavigation().getContent() == newsContent) {
                        mAppView.getDragNavigation().close(true);
                    } else {
                        news.setState(true);
                        this.onOpen();
                    }
                } else {
                    news.setState(true);
                    this.onOpen();
                }
            }

            @Override
            public void onBoth(boolean isOpened) {
            }
        });
        squircles.add(news);
        final Squircle choose = new Squircle(getApplicationContext(), size, colorA);
        choose.setDrawable(getDrawable(R.drawable.ic_class));
        final ScrollView chooseContent = getSwitcher();
        choose.setOnState(new Squircle.OnState() {
            @Override
            public void onOpen() {
                mAppView.getDragNavigation().emptyContent();
                mAppView.getDragNavigation().open(false);
                mAppView.getDragNavigation().setContent(chooseContent);
            }

            @Override
            public void onClose() {
                if (mAppView.getDragNavigation().isOpen()) {
                    if (mAppView.getDragNavigation().getContent() == chooseContent) {
                        mAppView.getDragNavigation().close(true);
                    } else {
                        choose.setState(true);
                        this.onOpen();
                    }
                } else {
                    choose.setState(true);
                    this.onOpen();
                }
            }

            @Override
            public void onBoth(boolean isOpened) {
            }
        });
        squircles.add(choose);
        final Squircle share = new Squircle(getApplicationContext(), size, colorA);
        share.setDrawable(getDrawable(R.drawable.ic_share));
        share.setOnState(new Squircle.OnState() {

            private LinearLayout shareContent;

            @Override
            public void onOpen() {
                shareContent = getShare();
                mAppView.getDragNavigation().emptyContent();
                mAppView.getDragNavigation().open(false);
                mAppView.getDragNavigation().setContent(shareContent);
            }

            @Override
            public void onClose() {
                if (mAppView.getDragNavigation().isOpen()) {
                    if (mAppView.getDragNavigation().getContent() == shareContent) {
                        mAppView.getDragNavigation().close(true);
                    } else {
                        share.setState(true);
                        this.onOpen();
                    }
                } else {
                    share.setState(true);
                    this.onOpen();
                }
            }

            @Override
            public void onBoth(boolean isOpened) {
            }
        });
        squircles.add(share);
        final Squircle settings = new Squircle(getApplicationContext(), size, colorA);
        settings.setDrawable(getDrawable(R.drawable.ic_gear));
        final ScrollView settingsContent = getSettings();
        settings.setOnState(new Squircle.OnState() {

            @Override
            public void onOpen() {
                mAppView.getDragNavigation().emptyContent();
                mAppView.getDragNavigation().open(false);
                mAppView.getDragNavigation().setContent(settingsContent);
            }

            @Override
            public void onClose() {
                if (mAppView.getDragNavigation().isOpen()) {
                    if (mAppView.getDragNavigation().getContent() == settingsContent) {
                        mAppView.getDragNavigation().close(true);
                    } else {
                        settings.setState(true);
                        this.onOpen();
                    }
                } else {
                    settings.setState(true);
                    this.onOpen();
                }
            }

            @Override
            public void onBoth(boolean isOpened) {
            }
        });
        squircles.add(settings);

        final Squircle devPanel = new Squircle(getApplicationContext(), size, colorA);
        devPanel.setDrawable(getDrawable(R.drawable.ic_developer));
        devPanel.setOnState(new Squircle.OnState() {

            @Override
            public void onOpen() {
            }

            @Override
            public void onClose() {
            }

            @Override
            public void onBoth(boolean isOpened) {
                Developer.startMe(Main.this);
            }
        });
        if(sp.getBoolean(Values.devMode,Values.devModeDefault)) {
            squircles.add(devPanel);
        }
        return squircles;
    }

    private LinearLayout getShare() {
        LinearLayout shareView = new LinearLayout(this);
        shareView.setGravity(Gravity.CENTER);
        shareView.setOrientation(LinearLayout.VERTICAL);
        TextView shareTitle = new TextView(this);
        shareTitle.setTextSize(getFontSize());
        shareTitle.setTypeface(getTypeface());
        shareTitle.setTextColor(textColor);
        shareTitle.setText(R.string.share_menu);
        shareTitle.setGravity(Gravity.CENTER);
        final Switch shareTimeSwitch = new Switch(this);
        shareTimeSwitch.setPadding(10, 0, 10, 0);
        shareTimeSwitch.setText(R.string.lesson_time);
        shareTimeSwitch.setTypeface(getTypeface());
        shareTimeSwitch.setTextSize(getFontSize() - 4);
        shareTimeSwitch.setTextColor(textColor);
        shareTimeSwitch.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        Button shareB = new Button(this);
        String shareText = getString(R.string.share) + " " + currentClass.name;
        shareB.setText(shareText);
        shareB.setBackground(generateCoaster(coasterColor));
        shareB.setTextColor(textColor);
        shareB.setTextSize(getFontSize() - 7);
        shareB.setTypeface(getTypeface());
        shareB.setAllCaps(false);
        shareB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                share(currentClass.name + "\n" + scheduleForClassString(currentClass, shareTimeSwitch.isChecked()));
            }
        });
        shareView.addView(shareTitle);
        shareView.addView(shareTimeSwitch);
        shareView.addView(shareB);
        return shareView;
    }

    private ScrollView getSettings() {
        ScrollView sv = new ScrollView(getApplicationContext());
        sv.setPadding(10, 10, 10, 10);
        LinearLayout settings = new LinearLayout(this);
        settings.setOrientation(LinearLayout.VERTICAL);
        settings.setGravity(Gravity.START);
        settings.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        final Switch autoMuteSwitch = new Switch(this),devSwitch = new Switch(this), newScheduleSwitch = new Switch(this), breakTimeSwitch = new Switch(this), pushSwitch = new Switch(this);
        pushSwitch.setText(R.string.live_messages);
        newScheduleSwitch.setText(R.string.schedule_notification);
        breakTimeSwitch.setText(R.string.show_breaks);
        autoMuteSwitch.setText(R.string.auto_mute);
        devSwitch.setText(R.string.developer_mode);
        pushSwitch.setChecked(sp.getBoolean(Values.pushService, Values.pushDefault));
        newScheduleSwitch.setChecked(sp.getBoolean(Values.scheduleService, Values.scheduleDefault));
        breakTimeSwitch.setChecked(sp.getBoolean(Values.breakTime, Values.breakTimeDefault));
        autoMuteSwitch.setChecked(sp.getBoolean(Values.autoMute, Values.autoMuteDefault));
        devSwitch.setChecked(sp.getBoolean(Values.devMode, Values.devModeDefault));
        pushSwitch.setTextSize((float) (getFontSize() / 1.5));
        breakTimeSwitch.setTextSize((float) (getFontSize() / 1.5));
        autoMuteSwitch.setTextSize((float) (getFontSize() / 1.5));
        newScheduleSwitch.setTextSize((float) (getFontSize() / 1.5));
        devSwitch.setTextSize((float) (getFontSize() / 1.5));
        pushSwitch.setTypeface(getTypeface());
        newScheduleSwitch.setTypeface(getTypeface());
        breakTimeSwitch.setTypeface(getTypeface());
        autoMuteSwitch.setTypeface(getTypeface());
        devSwitch.setTypeface(getTypeface());
        breakTimeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                sp.edit().putBoolean(Values.breakTime, isChecked).apply();
                //                breakTime=sp.getBoolean(Values.breakTime,Values.breakTimeDefault);
                breakTime = isChecked;
                TunnelHub.breakTimeTunnle.send(breakTime);
            }
        });
        autoMuteSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    boolean granted = false;
                    if (nm != null) {
                        granted = nm.isNotificationPolicyAccessGranted();
                    }
                    if (!granted) {
                        AlertDialog.Builder pop = new AlertDialog.Builder(Main.this);
                        pop.setCancelable(true);
                        pop.setMessage("You need to enable 'Do Not Disturb' permissions for the app.");
                        pop.setPositiveButton("Open Settings", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (Build.VERSION.SDK_INT >= 23) {
                                    sp.edit().putBoolean(Values.autoMute, true).apply();
                                    startActivityForResult(new Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS), 0);
                                }
                            }
                        });
                        pop.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                autoMuteSwitch.setChecked(false);
                                sp.edit().putBoolean(Values.autoMute, false).apply();
                            }
                        });
                        pop.setOnCancelListener(new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialogInterface) {
                                autoMuteSwitch.setChecked(false);
                                sp.edit().putBoolean(Values.autoMute, false).apply();
                            }
                        });
                        pop.show();
                    } else {
                        sp.edit().putBoolean(Values.autoMute, true).apply();
                    }
                } else {
                    sp.edit().putBoolean(Values.autoMute, false).apply();
                }
            }
        });
        pushSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                sp.edit().putBoolean(Values.pushService, isChecked).apply();
            }
        });
        newScheduleSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                sp.edit().putBoolean(Values.scheduleService, isChecked).apply();
            }
        });
        devSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    devModeConfirm(devSwitch);
                }else{
                    if(sp.getBoolean(Values.devMode,Values.devModeDefault)!=isChecked) {
                        sp.edit().putBoolean(Values.devMode, false).apply();
                        Splash.startMe(Main.this);
                    }
                }
            }
        });
        final TextView explainTextSize = new TextView(getApplicationContext());
        explainTextSize.setText(R.string.choose_text_size);
        explainTextSize.setTypeface(getTypeface());
        explainTextSize.setTextSize((float) (getFontSize() / 1.5));
        explainTextSize.setTextColor(textColor);
        explainTextSize.setGravity(Gravity.CENTER);
        final TextView explainTextColor = new TextView(getApplicationContext());
        explainTextColor.setText(R.string.choose_text_color);
        explainTextColor.setTypeface(getTypeface());
        explainTextColor.setTextSize((float) (getFontSize() / 1.5));
        explainTextColor.setTextColor(textColor);
        explainTextColor.setGravity(Gravity.CENTER);
        final TextView explainColorA = new TextView(getApplicationContext());
        explainColorA.setText(R.string.choose_first_color);
        explainColorA.setTypeface(getTypeface());
        explainColorA.setTextSize((float) (getFontSize() / 1.5));
        explainColorA.setTextColor(textColor);
        explainColorA.setGravity(Gravity.CENTER);
        final TextView explainColorB = new TextView(getApplicationContext());
        explainColorB.setText(R.string.choose_second_color);
        explainColorB.setTypeface(getTypeface());
        explainColorB.setTextSize((float) (getFontSize() / 1.5));
        explainColorB.setTextColor(textColor);
        explainColorB.setGravity(Gravity.CENTER);
        SeekBar fontSizeSeekBar = new SeekBar(this);
        fontSizeSeekBar.setMax(70);
        fontSizeSeekBar.setProgress(sp.getInt(Values.fontSizeNumber, Values.fontSizeDefault));
        fontSizeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    sp.edit().putInt(Values.fontSizeNumber, progress).apply();
                    refreshTheme();
                    TunnelHub.fontSizeChangeTunnle.send(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        ColorPicker textColorPicker = new ColorPicker(this, textColor);
        textColorPicker.setOnColorChanged(new ColorPicker.OnColorChanged() {
            @Override
            public void onColorChange(int color) {
                sp.edit().putInt(Values.fontColor, color).apply();
                refreshTheme();
                TunnelHub.textColorChangeTunnle.send(textColor);
            }
        });
        ColorPicker colorApicker = new ColorPicker(this, colorA);
        colorApicker.setOnColorChanged(new ColorPicker.OnColorChanged() {
            @Override
            public void onColorChange(int color) {
                sp.edit().putInt(Values.colorA, color).apply();
                refreshTheme();
            }
        });
        ColorPicker colorBpicker = new ColorPicker(this, colorB);
        colorBpicker.setOnColorChanged(new ColorPicker.OnColorChanged() {
            @Override
            public void onColorChange(int color) {
                sp.edit().putInt(Values.colorB, color).apply();
                refreshTheme();
            }
        });
        textColorPicker.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Device.screenY(getApplicationContext()) / 5));
        colorApicker.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Device.screenY(getApplicationContext()) / 5));
        colorBpicker.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Device.screenY(getApplicationContext()) / 5));
        TunnelHub.fontSizeChangeTunnle.addReceiver(new Tunnel.OnTunnel<Integer>() {
            @Override
            public void onReceive(Integer response) {
                pushSwitch.setTextSize((float) (response / 1.5));
                breakTimeSwitch.setTextSize((float) (response / 1.5));
                autoMuteSwitch.setTextSize((float) (response / 1.5));
                newScheduleSwitch.setTextSize((float) (response / 1.5));
                devSwitch.setTextSize((float) (response / 1.5));
                explainColorA.setTextSize((float) (response / 1.5));
                explainColorB.setTextSize((float) (response / 1.5));
                explainTextColor.setTextSize((float) (response / 1.5));
                explainTextSize.setTextSize((float) (response / 1.5));
            }
        });
        TunnelHub.textColorChangeTunnle.addReceiver(new Tunnel.OnTunnel<Integer>() {
            @Override
            public void onReceive(Integer response) {
                pushSwitch.setTextColor(response);
                breakTimeSwitch.setTextColor(response);
                autoMuteSwitch.setTextColor(response);
                newScheduleSwitch.setTextColor(response);
                devSwitch.setTextColor(response);
                explainColorA.setTextColor(response);
                explainColorB.setTextColor(response);
                explainTextColor.setTextColor(response);
                explainTextSize.setTextColor(response);
            }
        });
        settings.addView(autoMuteSwitch);
        settings.addView(newScheduleSwitch);
        settings.addView(pushSwitch);
        settings.addView(breakTimeSwitch);
        settings.addView(explainTextSize);
        settings.addView(fontSizeSeekBar);
        settings.addView(explainTextColor);
        settings.addView(textColorPicker);
        settings.addView(explainColorA);
        settings.addView(colorApicker);
        settings.addView(explainColorB);
        settings.addView(colorBpicker);
        settings.addView(devSwitch);
        sv.addView(settings);
        sv.setVerticalScrollBarEnabled(false);
        return sv;
    }

    private void devModeConfirm(final Switch s) {
        AlertDialog.Builder pop = new AlertDialog.Builder(this);
        pop.setCancelable(false);
        pop.setTitle("Be Cautious!");
        pop.setMessage("I'm not responsible for anything that happens because of a script you ran.");
        pop.setPositiveButton("I Agree", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                sp.edit().putBoolean(Values.devMode, true).apply();
                Splash.startMe(Main.this);
            }
        });
        pop.setNegativeButton("I Do Not Agree", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                s.setChecked(false);
            }
        });
        pop.show();
    }

    private ScrollView getSwitcher() {
        LinearLayout all = new LinearLayout(this);
        all.setOrientation(LinearLayout.VERTICAL);
        all.setGravity(Gravity.CENTER);
        LinearLayout students = new LinearLayout(this);
        students.setPadding(10, 10, 10, 10);
        students.setOrientation(LinearLayout.VERTICAL);
        students.setGravity(Gravity.CENTER);
        LinearLayout teachersv = new LinearLayout(this);
        teachersv.setPadding(10, 10, 10, 10);
        teachersv.setOrientation(LinearLayout.VERTICAL);
        teachersv.setGravity(Gravity.CENTER);
        TextView studentsTitle = new TextView(this);
        studentsTitle.setText(R.string.students_text);
        studentsTitle.setTypeface(getTypeface());
        studentsTitle.setTextSize(getFontSize() - 5);
        studentsTitle.setTextColor(textColor);
        studentsTitle.setGravity(Gravity.CENTER);
        TextView teachersTitle = new TextView(this);
        teachersTitle.setText(R.string.teachers_text);
        teachersTitle.setTypeface(getTypeface());
        teachersTitle.setTextSize(getFontSize() - 5);
        teachersTitle.setTextColor(textColor);
        teachersTitle.setGravity(Gravity.CENTER);
        students.addView(studentsTitle);
        for (int cs = 0; cs < classes.size(); cs++) {
            Button cls = new Button(getApplicationContext());
            cls.setTextSize((float) getFontSize());
            cls.setGravity(Gravity.CENTER);
            cls.setText(classes.get(cs).name);
            cls.setTextColor(textColor);
            cls.setBackground(generateCoaster(coasterColor));
            cls.setPadding(10, 0, 10, 0);
            cls.setTypeface(getTypeface());
            cls.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (Device.screenY(getApplicationContext()) / 12)));
            students.addView(cls);
            final int finalCs = cs;
            cls.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    sp.edit().putString(Values.favoriteClass, classes.get(finalCs).name).commit();
                    setStudentMode(classes.get(finalCs));
                }
            });
        }
        all.addView(students);
        if (sp.getBoolean(Values.teacherModeEnabler, false)) {
            teachersv.addView(teachersTitle);
            for (int cs = 0; cs < teachers.size(); cs++) {
                Button cls = new Button(getApplicationContext());
                cls.setTextSize((float) getFontSize());
                cls.setGravity(Gravity.CENTER);
                cls.setText(teachers.get(cs).mainName);
                cls.setTextColor(textColor);
                cls.setBackground(generateCoaster(coasterColor));
                cls.setPadding(10, 0, 10, 0);
                cls.setTypeface(getTypeface());
                cls.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (Device.screenY(getApplicationContext()) / 12)));
                teachersv.addView(cls);
                final int finalCs = cs;
                cls.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        sp.edit().putString(Values.favoriteTeacher, teachers.get(finalCs).mainName).commit();
                        setTeacherMode(teachers.get(finalCs));
                    }
                });
            }
            all.addView(teachersv);
        }
        ScrollView sv = new ScrollView(this);
        sv.addView(all);
        return sv;
    }

    private Drawable generateCoaster(int color) {
        GradientDrawable gd = (GradientDrawable) getDrawable(R.drawable.rounded_rect);
        if (gd != null) {
            gd.setColor(color);
        }
        return gd;
    }

    @Override
    public void onBackPressed() {
        if (mAppView != null) {
            if (mAppView.getDragNavigation() != null) {
                if (mAppView.getDragNavigation().isOpen()) {
                    mAppView.getDragNavigation().close(true);
                }
            }
        }
        if (bar != null) {
            if (bar.isOpen()) {
                bar.close();
            }
        }
    }

    private FrameLayout getNews() {
        final FrameLayout masterLayout = new FrameLayout(getApplicationContext());
        masterLayout.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        TextView load = new TextView(getApplicationContext());
        load.setTextSize(getFontSize());
        load.setTypeface(getTypeface());
        load.setTextColor(textColor);
        load.setText(R.string.loading_text);
        load.setGravity(Gravity.CENTER);
        load.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        masterLayout.removeAllViews();
        masterLayout.addView(load);
        final int fontSize = getFontSize();
        final ScrollView masterScroll = new ScrollView(getApplicationContext());
        masterScroll.setOverScrollMode(View.OVER_SCROLL_NEVER);
        masterScroll.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        final LinearLayout news = new LinearLayout(getApplicationContext());
        masterScroll.addView(news);
        news.setOrientation(LinearLayout.VERTICAL);
        news.setGravity(Gravity.CENTER);
        TextView newsTitle;
        newsTitle = new TextView(getApplicationContext());
        newsTitle.setText(R.string.news);
        news.addView(newsTitle);
        newsTitle.setTextColor(textColor);
        newsTitle.setTextSize(fontSize);
        newsTitle.setGravity(Gravity.CENTER);
        newsTitle.setTypeface(getTypeface());
        if (Device.isOnline(getApplicationContext())) {
            new GetNews(Values.serviceProvider, new GetNews.GotNews() {
                @Override
                public void onNewsGet(final ArrayList<GetNews.Link> link) {
                    for (int i = 0; i < link.size(); i++) {
                        Button cls = new Button(getApplicationContext());
                        cls.setPadding(10, 10, 10, 10);
                        cls.setTextSize((float) fontSize - 10);
                        cls.setGravity(Gravity.CENTER);
                        cls.setText(link.get(i).name);
                        cls.setTextColor(textColor);
                        cls.setEllipsize(TextUtils.TruncateAt.END);
                        cls.setLines(2);
                        //                            cls.setBackgroundColor(Color.TRANSPARENT);
                        cls.setBackground(generateCoaster(coasterColor));
                        cls.setTypeface(getTypeface());
                        cls.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (Device.screenY(getApplicationContext()) / 8)));
                        news.addView(cls);
                        if (!link.get(i).url.equals("")) {
                            final int finalI = i;
                            cls.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    String url = link.get(finalI).url;
                                    Intent i = new Intent(Intent.ACTION_VIEW);
                                    i.setData(Uri.parse(url));
                                    startActivity(i);
                                }
                            });
                        }
                    }
                    masterLayout.removeAllViews();
                    masterLayout.addView(masterScroll);
                }

                @Override
                public void onFail(ArrayList<GetNews.Link> e) {
                    TextView fail = new TextView(getApplicationContext());
                    fail.setTextSize(getFontSize());
                    fail.setTypeface(getTypeface());
                    fail.setTextColor(textColor);
                    fail.setText(R.string.news_load_failed);
                    fail.setGravity(Gravity.CENTER);
                    mAppView.getDragNavigation().setContent(fail);
                }
            }).execute("");
        }
        return masterLayout;
    }

    private void parseAndLoad(File f) {
        Sheet s=getSheet(f);
        if(s!=null) {
            classes = getClasses(s);
            messages = getMessages(s);
            day = getDay(s);
//            Log.i("Messages",messages.toString());
            if (classes != null) {
                teachers = getTeacherSchudleForClasses(classes);
                initStageB();
            }
        }
    }

    private void share(String st) {
        Intent s = new Intent(Intent.ACTION_SEND);
        s.putExtra(Intent.EXTRA_TEXT, st);
        s.setType("text/plain");
        s.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(Intent.createChooser(s, "Share With"));
    }

    private void setStudentMode(StudentClass c) {
        currentClass = c;
        main.setText(textColor, c.name, day);
        displayLessonViews(scheduleForClass(c));
    }

    private void setTeacherMode(Teacher t) {
        currentTeacher = t;
        main.setText(textColor, t.mainName.split(" ")[0], day);
        displayLessonViews(scheduleForTeacher(t));
    }

    private void displayLessonViews(ArrayList<LessonView> lessonViews) {
        lessonViewHolder.removeAllViews();
        for (int l = 0; l < lessonViews.size(); l++) {
            lessonViewHolder.addView(lessonViews.get(l));
        }
    }

    private int getFontSize() {
        return sp.getInt(Values.fontSizeNumber, Values.fontSizeDefault);
    }

    private String scheduleForClassString(StudentClass fclass, boolean showTime) {
        String allsubj = "";
        for (int s = 0; s < fclass.subjects.size(); s++) {
            String before;
            if (showTime) {
                before = "(" + getRealTimeForHourNumber(fclass.subjects.get(s).hour) + ") " + fclass.subjects.get(s).hour + ". ";
            } else {
                before = fclass.subjects.get(s).hour + ". ";
            }
            String total = before + fclass.subjects.get(s).name;
            if (fclass.subjects.get(s).name != null && !fclass.subjects.get(s).name.equals("")) {
                allsubj += total + "\n";
            }
        }
        return allsubj;
    }

    private Typeface getTypeface() {
        return Typeface.createFromAsset(getAssets(), Values.fontName);
    }

    private ArrayList<LessonView> scheduleForClass(final StudentClass fclass) {
        ArrayList<LessonView> lessons = new ArrayList<>();
        for (int s = 0; s < fclass.subjects.size(); s++) {
            if (getBreak(fclass.subjects.get(s).hour - 1) != -1) {
                final LessonView breakt = new LessonView(getApplicationContext(), generateCoaster(Values.classCoasterColor), generateCoaster(Values.classCoasterMarkColor), -1, "הפסקה", getBreak(fclass.subjects.get(s).hour - 1) + " דקות", "");
                if (fclass.subjects.get(s).name != null && !fclass.subjects.get(s).name.equals("")) {
                    lessons.add(breakt);
                }
                if (!breakTime) {
                    breakt.setVisibility(View.GONE);
                } else {
                    breakt.setVisibility(View.VISIBLE);
                }
                TunnelHub.breakTimeTunnle.addReceiver(new Tunnel.OnTunnel<Boolean>() {
                    @Override
                    public void onReceive(Boolean response) {
                        if (!response) {
                            breakt.setVisibility(View.GONE);
                        } else {
                            breakt.setVisibility(View.VISIBLE);
                        }
                    }
                });
            }
            StudentClass.Subject.Time classTime = getTimeForLesson(fclass.subjects.get(s).hour);
            boolean isCurrent = false;
            Calendar c = Calendar.getInstance();
            int minute = c.get(Calendar.MINUTE);
            int hour = c.get(Calendar.HOUR_OF_DAY);
            if (minuteOfDay(hour, minute) > minuteOfDay(classTime.startH, classTime.startM) && minuteOfDay(hour, minute) <= minuteOfDay(classTime.finishH, classTime.finishM)) {
                isCurrent = true;
            }
            String txt = getRealTimeForHourNumber(fclass.subjects.get(s).hour) + "-" + getRealEndTimeForHourNumber(fclass.subjects.get(s).hour);
            String tcnm = (fclass.subjects.get(s).fullName.substring(fclass.subjects.get(s).fullName.indexOf("\n") + 1).trim().split("\\r?\\n")[0]).split(",")[0];
            LessonView subject = new LessonView(getApplicationContext(), generateCoaster(Values.classCoasterColor), generateCoaster(Values.classCoasterMarkColor), fclass.subjects.get(s).hour, fclass.subjects.get(s).name.replaceAll(",", "/"), txt, tcnm);
            if (isCurrent) subject.mark();
            if (fclass.subjects.get(s).name != null && !fclass.subjects.get(s).name.equals("")) {
                lessons.add(subject);
            }
        }
        return lessons;
    }

    private ArrayList<LessonView> scheduleForTeacher(final Teacher fclass) {
        ArrayList<LessonView> lessons = new ArrayList<>();
        for (int h = 0; h <= 12; h++) {
            ArrayList<Teacher.Lesson> currentLesson = new ArrayList<>();
            String currentText = "";
            boolean isCurrent = false;
            String lessonName = "";
            for (int s = 0; s < fclass.teaching.size(); s++) {
                if (fclass.teaching.get(s).hour == h) {
                    StudentClass.Subject.Time classTime = getTimeForLesson(h);
                    Calendar ca = Calendar.getInstance();
                    int minute = ca.get(Calendar.MINUTE);
                    int hour = ca.get(Calendar.HOUR_OF_DAY);
                    if (minuteOfDay(hour, minute) >= minuteOfDay(classTime.startH, classTime.startM) && minuteOfDay(hour, minute) <= minuteOfDay(classTime.finishH, classTime.finishM)) {
                        isCurrent = true;
                    }
                    if (currentText.equals("")) {
                        currentText += fclass.teaching.get(s).className;
                    } else {
                        currentText += ", " + fclass.teaching.get(s).className;
                    }
                    lessonName = fclass.teaching.get(s).lessonName;
                    currentLesson.add(fclass.teaching.get(s));
                }
            }
            if (currentLesson.size() > 1) {
                if (getGrade(currentLesson) != -1) {
                    currentText = getGrade(getGrade(currentLesson));
                }
            }
            if (currentLesson.size() >= 1) {
                if (getBreak(h - 1) != -1) {
                    final LessonView breakt = new LessonView(getApplicationContext(), generateCoaster(Values.classCoasterColor), generateCoaster(Values.classCoasterMarkColor), -1, "הפסקה", getBreak(h - 1) + " דקות", "");
                    lessons.add(breakt);
                    if (!breakTime) {
                        breakt.setVisibility(View.GONE);
                    } else {
                        breakt.setVisibility(View.VISIBLE);
                    }
                    TunnelHub.breakTimeTunnle.addReceiver(new Tunnel.OnTunnel<Boolean>() {
                        @Override
                        public void onReceive(Boolean response) {
                            if (!response) {
                                breakt.setVisibility(View.GONE);
                            } else {
                                breakt.setVisibility(View.VISIBLE);
                            }
                        }
                    });
                }
                String txt = getRealTimeForHourNumber(h) + "-" + getRealEndTimeForHourNumber(h);
                LessonView lesson = new LessonView(getApplicationContext(), generateCoaster(Values.classCoasterColor), generateCoaster(Values.classCoasterMarkColor), h, currentText, txt, lessonName);
                if (isCurrent) lesson.mark();
                lessons.add(lesson);
            }
        }
        return lessons;
    }
}