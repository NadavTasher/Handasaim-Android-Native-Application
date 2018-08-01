package nadav.tasher.handasaim.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

import nadav.tasher.handasaim.R;
import nadav.tasher.handasaim.architecture.app.Center;
import nadav.tasher.handasaim.architecture.app.KeyManager;
import nadav.tasher.handasaim.architecture.app.graphics.LessonView;
import nadav.tasher.handasaim.architecture.app.graphics.MessageBar;
import nadav.tasher.handasaim.architecture.appcore.AppCore;
import nadav.tasher.handasaim.architecture.appcore.components.Classroom;
import nadav.tasher.handasaim.architecture.appcore.components.Schedule;
import nadav.tasher.handasaim.architecture.appcore.components.Subject;
import nadav.tasher.handasaim.architecture.appcore.components.Teacher;
import nadav.tasher.handasaim.tools.specific.GetNews;
import nadav.tasher.handasaim.values.Filters;
import nadav.tasher.handasaim.values.Values;
import nadav.tasher.lightool.graphics.views.ColorPicker;
import nadav.tasher.lightool.graphics.views.Utils;
import nadav.tasher.lightool.graphics.views.appview.AppView;
import nadav.tasher.lightool.graphics.views.appview.navigation.corner.Corner;
import nadav.tasher.lightool.info.Device;
import nadav.tasher.lightool.parts.Peer;
import nadav.tasher.lightool.parts.Tower;

public class HomeActivity extends Activity {
    private Tower<Theme> theme = new Tower<>();

    private Classroom currentClass;
    private Teacher currentTeacher;
    private Corner icon, info;
    private MessageBar messageBar;
    private LinearLayout lessonViewHolder;
    private LinearLayout shareDrawer;
    private FrameLayout newsDrawer;
    private AppView mAppView;
    private Schedule schedule;
    private HorizontalScrollView menuDrawer;
    private ScrollView settingsDrawer, classDrawer;

    private int drawerPadding = 10;

    private SharedPreferences sp;
    private KeyManager keyManager;

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
        sp = getSharedPreferences(Values.prefName, MODE_PRIVATE);
        keyManager = new KeyManager(getApplicationContext());
    }

    private void refreshTheme() {
        Theme currentTheme = new Theme();
        currentTheme.textColor = sp.getInt(Values.fontColor, Values.fontColorDefault);
        currentTheme.textSize = sp.getInt(Values.fontSizeNumber, Values.fontSizeDefault);
        currentTheme.showMessages = sp.getBoolean(Values.messages, Values.messagesDefault);
        currentTheme.showBreaks = sp.getBoolean(Values.breakTime, Values.breakTimeDefault);
        currentTheme.colorTop = sp.getInt(Values.colorA, Values.defaultColorA);
        currentTheme.colorBottom = sp.getInt(Values.colorB, Values.defaultColorB);
        currentTheme.colorMix = generateCombinedColor(currentTheme.colorTop, currentTheme.colorBottom);
        theme.tell(currentTheme);
    }

    public void go() {
        String file = sp.getString(Values.scheduleFile, null);
        if (file != null) {
            parseAndLoad(new File(file));
        }
    }

    // TODO move to drawer
    private void popupKeyEntering() {
        AlertDialog.Builder pop = new AlertDialog.Builder(this);
        pop.setCancelable(true);
        pop.setTitle("Unlock Features");
        pop.setMessage("Enter The Unlock Key You Got To Unlock Special Features.");
        final EditText key = new EditText(getApplicationContext());
        key.setFilters(new InputFilter[]{
                Filters.codeFilter,
                new InputFilter.AllCaps()
        });
        FrameLayout f = new FrameLayout(getApplicationContext());
        f.setPadding(50, 10, 50, 10);
        f.addView(key);
        key.setHint("Unlock Key");
        pop.setView(f);
        key.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        pop.setPositiveButton("Unlock", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                keyManager.loadKey(key.getText().toString().toUpperCase());
            }
        });
        pop.setNegativeButton("Close", null);
        pop.show();
    }

    // TODO move to drawer
    private void aboutPopup() {
        AlertDialog.Builder ab = new AlertDialog.Builder(this);
        ab.setTitle(R.string.app_name);
        ab.setMessage("Made By Nadav Tasher.\nVersion: " + Device.getVersionName(getApplicationContext(), getApplicationContext().getPackageName()) + " (" + Device.getVersionCode(getApplicationContext(), getApplicationContext().getPackageName()) + ")\nAppCore v" + AppCore.APPCORE_VERSION);
        ab.setCancelable(true);
        ab.setPositiveButton("Close", null);
        ab.setNegativeButton("Enter Code", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                popupKeyEntering();
            }
        });
        ab.show();
    }

    private int generateCombinedColor(int colorTop, int colorBottom) {
        int redA = Color.red(colorTop);
        int greenA = Color.green(colorTop);
        int blueA = Color.blue(colorTop);
        int redB = Color.red(colorBottom);
        int greenB = Color.green(colorBottom);
        int blueB = Color.blue(colorBottom);
        int combineRed = redA - (redA - redB) / 2, combineGreen = greenA - (greenA - greenB) / 2, combineBlue = blueA - (blueA - blueB) / 2;
        return Color.rgb(combineRed, combineGreen, combineBlue);
    }

    private void initStageB() {
        mAppView = new AppView(this);
        mAppView.setDrawNavigation(false);
        mAppView.getDrawer().getDrawerView().setBackground(Utils.getCoaster(Center.alpha(255, Values.classCoasterColor), 30, 10));
        icon = new Corner(getApplicationContext(), Device.screenX(getApplicationContext()) / 5, Color.TRANSPARENT);
        ImageView iconImageView = new ImageView(getApplicationContext());
        iconImageView.setImageDrawable(getDrawable(R.drawable.ic_icon));
        icon.setView(iconImageView, 0.8);
        icon.addOnState(new Corner.OnState() {
            @Override
            public void onOpen() {
            }

            @Override
            public void onClose() {
            }

            @Override
            public void onBoth(boolean b) {
                aboutPopup();
            }
        });
        info = new Corner(getApplicationContext(), Device.screenX(getApplicationContext()) / 5, Color.TRANSPARENT);
        info.addOnState(new Corner.OnState() {
            @Override
            public void onOpen() {
            }

            @Override
            public void onClose() {
            }

            @Override
            public void onBoth(boolean b) {
                if (mAppView.getDrawer().isOpen()) {
                    mAppView.getDrawer().close();
                } else {
                    mAppView.getDrawer().emptyContent();
                    if (menuDrawer != null) {
                        mAppView.getDrawer().setContent(menuDrawer);
                        mAppView.getDrawer().open(((double) (menuDrawer.getLayoutParams().height + 2 * drawerPadding) / (double) Device.screenY(getApplicationContext())));
                    }
                }
            }
        });
        mAppView.getCornerView().setBottomLeft(icon);
        mAppView.getCornerView().setBottomRight(info);
        mAppView.getScrolly().setOnScroll(new AppView.Scrolly.OnScroll() {
            @Override
            public void onScroll(int i, int i1, int i2, int i3) {
                setCornerColors(mAppView.getScrolly());
            }
        });
        LinearLayout scheduleLayout = new LinearLayout(getApplicationContext());
        scheduleLayout.setGravity(Gravity.START | Gravity.CENTER_HORIZONTAL);
        scheduleLayout.setOrientation(LinearLayout.VERTICAL);
        scheduleLayout.setPadding(10, 10, 10, 10);
        messageBar = new MessageBar(this, schedule.getMessages(), mAppView.getDrawer());
        messageBar.start();
        //       TODO textColor.addPeer(messageBar.getTextColorPeer());
        scheduleLayout.addView(messageBar);
        lessonViewHolder = new LinearLayout(getApplicationContext());
        lessonViewHolder.setGravity(Gravity.START | Gravity.CENTER_HORIZONTAL);
        lessonViewHolder.setOrientation(LinearLayout.VERTICAL);
        scheduleLayout.addView(lessonViewHolder);
        mAppView.getScrolly().setView(scheduleLayout);
        theme.addPeer(new Peer<>(new Peer.OnPeer<Theme>() {
            @Override
            public boolean onPeer(Theme theme) {
                if (mAppView != null)
                    mAppView.setBackgroundColor(new AppView.Gradient(theme.colorTop, theme.colorBottom));
                getWindow().setNavigationBarColor(theme.colorMix);
                setCornerColors(mAppView.getScrolly());
                if (theme.showMessages && schedule.getMessages().size() != 0) {
                    messageBar.setVisibility(View.VISIBLE);
                } else {
                    messageBar.setVisibility(View.GONE);
                }
                return false;
            }
        }));
        setContentView(mAppView);
        refreshTheme();
        assembleDrawers();
        if (getFavoriteClass() != null) setStudentMode(getFavoriteClass());
        refreshTheme();
    }

    private void assembleDrawers() {
        assembleMenuDrawer();
        assembleSettingsDrawer();
        //        assembleShareDrawer();
        //        assembleNewsDrawer();
        assembleClassDrawer();
    }

    private void assembleSettingsDrawer() {
        settingsDrawer = getSettings();
    }

    private void assembleShareDrawer() {
        shareDrawer = getShare();
    }

    private void assembleNewsDrawer() {
        newsDrawer = getNews();
    }

    private void assembleClassDrawer() {
        classDrawer = getSwitcher();
    }

    private void assembleMenuDrawer() {
        menuDrawer = new HorizontalScrollView(getApplicationContext());
        menuDrawer.setOverScrollMode(ScrollView.OVER_SCROLL_NEVER);
        menuDrawer.setHorizontalScrollBarEnabled(false);
        LinearLayout menu = new LinearLayout(getApplicationContext());
        menu.setOrientation(LinearLayout.HORIZONTAL);
        menu.setGravity(Gravity.CENTER);
        menuDrawer.setFillViewport(true);
        final int size = Device.screenY(getApplicationContext()) / 13;
        FrameLayout share, classroom, news, refresh, settings;
        share = generateImageView(R.drawable.ic_share, size);
        classroom = generateImageView(R.drawable.ic_class, size);
        news = generateImageView(R.drawable.ic_news, size);
        refresh = generateImageView(R.drawable.ic_reload, size);
        settings = generateImageView(R.drawable.ic_gear, size);
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAppView.getDrawer().setContent(settingsDrawer);
                mAppView.getDrawer().open(0.8);
            }
        });
        classroom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAppView.getDrawer().setContent(classDrawer);
                mAppView.getDrawer().open(0.8);
            }
        });
        menu.addView(share);
        menu.addView(classroom);
        menu.addView(news);
        menu.addView(refresh);
        menu.addView(settings);
        menu.setPadding(10, 10, 10, 10);
        menu.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, size + menu.getPaddingBottom() + menu.getPaddingTop()));
        menuDrawer.setLayoutParams(new LinearLayout.LayoutParams(Device.screenX(getApplicationContext()), size + menu.getPaddingBottom() + menu.getPaddingTop()));
        menuDrawer.addView(menu);
    }

    private FrameLayout generateImageView(int drawable, int size) {
        FrameLayout fl = new FrameLayout(getApplicationContext());
        fl.setPadding(20, 0, 20, 0);
        fl.setForegroundGravity(Gravity.CENTER);
        fl.setLayoutParams(new LinearLayout.LayoutParams(size, size));
        final ImageView iv = new ImageView(getApplicationContext());
        iv.setImageDrawable(getDrawable(drawable));
        iv.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        fl.addView(iv);
        return fl;
    }

    private void setCornerColors(AppView.Scrolly s) {
        if (s.getChildAt(s.getChildCount() - 1).getBottom() - (s.getHeight() + s.getScrollY()) == 0) {
            icon.setColorAlpha(128);
            info.setColorAlpha(128);
            icon.setColor(theme.getLast().colorTop);
            info.setColor(theme.getLast().colorTop);
        } else {
            info.setColorAlpha(255);
            icon.setColorAlpha(255);
            icon.setColor(theme.getLast().colorMix);
            info.setColor(theme.getLast().colorMix);
        }
    }

    private Classroom getFavoriteClass() {
        int selectedClass = 0;
        if (sp.getString(Values.favoriteClass, null) != null) {
            if (!schedule.getClassrooms().isEmpty()) {
                for (int fc = 0; fc < schedule.getClassrooms().size(); fc++) {
                    if (sp.getString(Values.favoriteClass, "").equals(schedule.getClassrooms().get(fc).getName())) {
                        return schedule.getClassrooms().get(fc);
                    }
                }
                return schedule.getClassrooms().get(selectedClass);
            } else {
                return null;
            }
        } else {
            return schedule.getClassrooms().get(selectedClass);
        }
    }

    private Teacher getFavoriteTeacher() {
        int selectedTeacher = 0;
        if (sp.getString(Values.favoriteTeacher, null) != null) {
            if (!schedule.getTeachers().isEmpty()) {
                for (int fc = 0; fc < schedule.getTeachers().size(); fc++) {
                    if (sp.getString(Values.favoriteTeacher, "").equals(schedule.getTeachers().get(fc).getName())) {
                        return schedule.getTeachers().get(fc);
                    }
                }
                return schedule.getTeachers().get(selectedTeacher);
            } else {
                return null;
            }
        } else {
            return schedule.getTeachers().get(selectedTeacher);
        }
    }
    //    private ArrayList<Squircle> getSquircles(int size) {
    //        ArrayList<Squircle> squircles = new ArrayList<>();
    //        final Squircle reload = new Squircle(getApplicationContext(), size, colorA);
    //        reload.setDrawable(getApplicationContext().getDrawable(R.drawable.ic_reload));
    //        reload.addOnState(new Squircle.OnState() {
    //
    //            @Override
    //            public void onOpen() {
    //            }
    //
    //            @Override
    //            public void onClose() {
    //            }
    //
    //            @Override
    //            public void onBoth(boolean isOpened) {
    //                Center.exit(HomeActivity.this,SplashActivity.class);
    //                // TODO RETURNTO splash
    //            }
    //        });
    //        squircles.add(reload);
    //        final Squircle news = new Squircle(getApplicationContext(), size, colorA);
    //        news.setDrawable(getApplicationContext().getDrawable(R.drawable.ic_news));
    //        final FrameLayout newsContent = getNews();
    //        news.addOnState(new Squircle.OnState() {
    //            @Override
    //            public void onOpen() {
    //                mAppView.getDrag().emptyContent();
    //                mAppView.getDrag().open(false);
    //                mAppView.getDrag().setContent(newsContent);
    //            }
    //
    //            @Override
    //            public void onClose() {
    //                if (mAppView.getDrag().isOpen()) {
    //                    if (mAppView.getDrag().getContent() == newsContent) {
    //                        mAppView.getDrag().close(true);
    //                    } else {
    //                        news.setState(true);
    //                        this.onOpen();
    //                    }
    //                } else {
    //                    news.setState(true);
    //                    this.onOpen();
    //                }
    //            }
    //
    //            @Override
    //            public void onBoth(boolean isOpened) {
    //            }
    //        });
    //        squircles.add(news);
    //        final Squircle choose = new Squircle(getApplicationContext(), size, colorA);
    //        choose.setDrawable(getApplicationContext().getDrawable(R.drawable.ic_class));
    //        final ScrollView chooseContent = getSwitcher();
    //        choose.addOnState(new Squircle.OnState() {
    //            @Override
    //            public void onOpen() {
    //                mAppView.getDrag().emptyContent();
    //                mAppView.getDrag().open(false);
    //                mAppView.getDrag().setContent(chooseContent);
    //            }
    //
    //            @Override
    //            public void onClose() {
    //                if (mAppView.getDrag().isOpen()) {
    //                    if (mAppView.getDrag().getContent() == chooseContent) {
    //                        mAppView.getDrag().close(true);
    //                    } else {
    //                        choose.setState(true);
    //                        this.onOpen();
    //                    }
    //                } else {
    //                    choose.setState(true);
    //                    this.onOpen();
    //                }
    //            }
    //
    //            @Override
    //            public void onBoth(boolean isOpened) {
    //            }
    //        });
    //        squircles.add(choose);
    //        final Squircle share = new Squircle(getApplicationContext(), size, colorA);
    //        share.setDrawable(getApplicationContext().getDrawable(R.drawable.ic_share));
    //        share.addOnState(new Squircle.OnState() {
    //
    //            private LinearLayout shareContent;
    //
    //            @Override
    //            public void onOpen() {
    //                shareContent = getShare();
    //                mAppView.getDrag().emptyContent();
    //                mAppView.getDrag().open(false);
    //                mAppView.getDrag().setContent(shareContent);
    //            }
    //
    //            @Override
    //            public void onClose() {
    //                if (mAppView.getDrag().isOpen()) {
    //                    if (mAppView.getDrag().getContent() == shareContent) {
    //                        mAppView.getDrag().close(true);
    //                    } else {
    //                        share.setState(true);
    //                        this.onOpen();
    //                    }
    //                } else {
    //                    share.setState(true);
    //                    this.onOpen();
    //                }
    //            }
    //
    //            @Override
    //            public void onBoth(boolean isOpened) {
    //            }
    //        });
    //        squircles.add(share);
    //        final Squircle settings = new Squircle(getApplicationContext(), size, colorA);
    //        settings.setDrawable(getApplicationContext().getDrawable(R.drawable.ic_gear));
    //        final ScrollView settingsContent = getSettings();
    //        settings.addOnState(new Squircle.OnState() {
    //
    //            @Override
    //            public void onOpen() {
    //                mAppView.getDrag().emptyContent();
    //                mAppView.getDrag().open(false);
    //                mAppView.getDrag().setContent(settingsContent);
    //            }
    //
    //            @Override
    //            public void onClose() {
    //                if (mAppView.getDrag().isOpen()) {
    //                    if (mAppView.getDrag().getContent() == settingsContent) {
    //                        mAppView.getDrag().close(true);
    //                    } else {
    //                        settings.setState(true);
    //                        this.onOpen();
    //                    }
    //                } else {
    //                    settings.setState(true);
    //                    this.onOpen();
    //                }
    //            }
    //
    //            @Override
    //            public void onBoth(boolean isOpened) {
    //            }
    //        });
    //        squircles.add(settings);
    //        final Squircle devPanel = new Squircle(getApplicationContext(), size, colorA);
    //        devPanel.setDrawable(getApplicationContext().getDrawable(R.drawable.ic_developer));
    //        devPanel.addOnState(new Squircle.OnState() {
    //
    //            @Override
    //            public void onOpen() {
    //            }
    //
    //            @Override
    //            public void onClose() {
    //            }
    //
    //            @Override
    //            public void onBoth(boolean isOpened) {
    //                if (keyManager.isKeyLoaded(KeyManager.TYPE_BETA)) {
    //                    // TODO GOTO developer
    //                } else {
    //                    Toast.makeText(getApplicationContext(), "Beta Key Not Installed.\nFor Now, A Beta Key Must Be Installed To Enter The Developer Console.", Toast.LENGTH_LONG).show();
    //                }
    //            }
    //        });
    //        if (sp.getBoolean(Values.devMode, Values.devModeDefault)) {
    //            squircles.add(devPanel);
    //        }
    //        TowerHub.colorAChangeTunnle.addPeer(new Peer<>(new Peer.OnPeer<Integer>() {
    //            @Override
    //            public boolean onPeer(Integer integer) {
    //                news.setColor(integer);
    //                choose.setColor(integer);
    //                share.setColor(integer);
    //                settings.setColor(integer);
    //                devPanel.setColor(integer);
    //                return true;
    //            }
    //        }));
    //        return squircles;
    //    }

    private LinearLayout getShare() {
        LinearLayout shareView = new LinearLayout(getApplicationContext());
        shareView.setGravity(Gravity.CENTER);
        shareView.setOrientation(LinearLayout.VERTICAL);
        TextView shareTitle = new TextView(getApplicationContext());
        shareTitle.setTextSize(getFontSize());
        shareTitle.setTypeface(getTypeface());
        shareTitle.setTextColor(theme.getLast().textColor);
        shareTitle.setText(R.string.share_menu);
        shareTitle.setGravity(Gravity.CENTER);
        final Switch shareMessageSwitch = new Switch(getApplicationContext());
        shareMessageSwitch.setPadding(10, 0, 10, 0);
        shareMessageSwitch.setText(R.string.messages_switch);
        shareMessageSwitch.setTypeface(getTypeface());
        shareMessageSwitch.setTextSize(getFontSize() - 4);
        shareMessageSwitch.setTextColor(theme.getLast().textColor);
        shareMessageSwitch.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        shareMessageSwitch.setEnabled(!schedule.getMessages().isEmpty());
        Button shareB = new Button(getApplicationContext());
        String shareText = getApplicationContext().getString(R.string.share) + " " + currentClass.getName();
        shareB.setText(shareText);
        shareB.setBackground(Utils.getCoaster(theme.getLast().colorMix, 16, 10));
        shareB.setTextColor(theme.getLast().textColor);
        shareB.setTextSize(getFontSize() - 7);
        shareB.setTypeface(getTypeface());
        shareB.setAllCaps(false);
        shareB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StringBuilder message = new StringBuilder();
                if (shareMessageSwitch.isChecked()) {
                    message = new StringBuilder("Messages:\n");
                    for (int i = 0; i < schedule.getMessages().size(); i++) {
                        message.append(i + 1).append(". ").append(schedule.getMessages().get(i)).append("\n");
                    }
                }
                share(currentClass.getName() + " (" + schedule.getDay() + ")" + "\n" + scheduleForClassString(currentClass) + message);
            }
        });
        shareView.addView(shareTitle);
        shareView.addView(shareMessageSwitch);
        shareView.addView(shareB);
        return shareView;
    }

    private ScrollView getSettings() {
        ScrollView sv = new ScrollView(getApplicationContext());
        sv.setPadding(10, 10, 10, 10);
        LinearLayout settings = new LinearLayout(getApplicationContext());
        settings.setOrientation(LinearLayout.VERTICAL);
        settings.setGravity(Gravity.START);
        settings.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        final Switch devSwitch = new Switch(getApplicationContext()), messageSwitch = new Switch(getApplicationContext()), newScheduleSwitch = new Switch(getApplicationContext()), breakTimeSwitch = new Switch(getApplicationContext()), pushSwitch = new Switch(getApplicationContext());
        pushSwitch.setText(R.string.live_messages);
        messageSwitch.setText(R.string.schedule_messages);
        newScheduleSwitch.setText(R.string.schedule_notification);
        breakTimeSwitch.setText(R.string.show_breaks);
        devSwitch.setText(R.string.developer_mode);
        pushSwitch.setChecked(sp.getBoolean(Values.pushService, Values.pushDefault));
        messageSwitch.setChecked(sp.getBoolean(Values.messages, Values.messagesDefault));
        newScheduleSwitch.setChecked(sp.getBoolean(Values.scheduleService, Values.scheduleDefault));
        breakTimeSwitch.setChecked(sp.getBoolean(Values.breakTime, Values.breakTimeDefault));
        devSwitch.setChecked(sp.getBoolean(Values.devMode, Values.devModeDefault));
        pushSwitch.setTypeface(getTypeface());
        newScheduleSwitch.setTypeface(getTypeface());
        breakTimeSwitch.setTypeface(getTypeface());
        devSwitch.setTypeface(getTypeface());
        messageSwitch.setTypeface(getTypeface());
        breakTimeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                sp.edit().putBoolean(Values.breakTime, isChecked).apply();
                refreshTheme();
            }
        });
        messageSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                sp.edit().putBoolean(Values.messages, isChecked).apply();
                refreshTheme();
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
                sp.edit().putBoolean(Values.devMode, isChecked).apply();
            }
        });
        final TextView explainTextSize = new TextView(getApplicationContext());
        explainTextSize.setText(R.string.choose_text_size);
        explainTextSize.setTypeface(getTypeface());
        explainTextSize.setGravity(Gravity.CENTER);
        final TextView explainTextColor = new TextView(getApplicationContext());
        explainTextColor.setText(R.string.choose_text_color);
        explainTextColor.setTypeface(getTypeface());
        explainTextColor.setGravity(Gravity.CENTER);
        final TextView explainColorA = new TextView(getApplicationContext());
        explainColorA.setText(R.string.choose_first_color);
        explainColorA.setTypeface(getTypeface());
        explainColorA.setGravity(Gravity.CENTER);
        final TextView explainColorB = new TextView(getApplicationContext());
        explainColorB.setText(R.string.choose_second_color);
        explainColorB.setTypeface(getTypeface());
        explainColorB.setGravity(Gravity.CENTER);
        SeekBar fontSizeSeekBar = new SeekBar(getApplicationContext());
        fontSizeSeekBar.setMax(70);
        fontSizeSeekBar.setProgress(sp.getInt(Values.fontSizeNumber, Values.fontSizeDefault));
        fontSizeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    sp.edit().putInt(Values.fontSizeNumber, progress).apply();
                    refreshTheme();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        ColorPicker textColorPicker = new ColorPicker(getApplicationContext());
        textColorPicker.setColor(theme.getLast().textColor);
        textColorPicker.setOnColor(new ColorPicker.OnColorChanged() {
            @Override
            public void onColorChange(int color) {
                sp.edit().putInt(Values.fontColor, color).apply();
                refreshTheme();
            }
        });
        ColorPicker colorTopPicker = new ColorPicker(getApplicationContext());
        colorTopPicker.setColor(theme.getLast().colorTop);
        colorTopPicker.setOnColor(new ColorPicker.OnColorChanged() {
            @Override
            public void onColorChange(int color) {
                //                Log.i("Red", Color.red(color) + "");
                //                Log.i("Gre", Color.green(color) + "");
                //                Log.i("Blu", Color.blue(color) + "");
                sp.edit().putInt(Values.colorA, color).apply();
                refreshTheme();
            }
        });
        ColorPicker colorBottomPicker = new ColorPicker(getApplicationContext());
        colorBottomPicker.setColor(theme.getLast().colorBottom);
        colorBottomPicker.setOnColor(new ColorPicker.OnColorChanged() {
            @Override
            public void onColorChange(int color) {
                sp.edit().putInt(Values.colorB, color).apply();
                refreshTheme();
            }
        });
        textColorPicker.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Device.screenY(getApplicationContext()) / 12));
        colorTopPicker.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Device.screenY(getApplicationContext()) / 12));
        colorBottomPicker.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Device.screenY(getApplicationContext()) / 12));
        theme.addPeer(new Peer<Theme>(new Peer.OnPeer<Theme>() {
            @Override
            public boolean onPeer(Theme theme) {
                pushSwitch.setTextSize((float) (theme.textSize / 1.5));
                breakTimeSwitch.setTextSize((float) (theme.textSize / 1.5));
                newScheduleSwitch.setTextSize((float) (theme.textSize / 1.5));
                messageSwitch.setTextSize((float) (theme.textSize / 1.5));
                devSwitch.setTextSize((float) (theme.textSize / 1.5));
                explainColorA.setTextSize((float) (theme.textSize / 1.5));
                explainColorB.setTextSize((float) (theme.textSize / 1.5));
                explainTextColor.setTextSize((float) (theme.textSize / 1.5));
                explainTextSize.setTextSize((float) (theme.textSize / 1.5));
                pushSwitch.setTextColor(theme.textColor);
                breakTimeSwitch.setTextColor(theme.textColor);
                newScheduleSwitch.setTextColor(theme.textColor);
                messageSwitch.setTextColor(theme.textColor);
                devSwitch.setTextColor(theme.textColor);
                explainColorA.setTextColor(theme.textColor);
                explainColorB.setTextColor(theme.textColor);
                explainTextColor.setTextColor(theme.textColor);
                explainTextSize.setTextColor(theme.textColor);
                return false;
            }
        }));
        settings.addView(messageSwitch);
        settings.addView(newScheduleSwitch);
        settings.addView(pushSwitch);
        settings.addView(breakTimeSwitch);
        settings.addView(explainTextSize);
        settings.addView(fontSizeSeekBar);
        settings.addView(explainTextColor);
        settings.addView(textColorPicker);
        settings.addView(explainColorA);
        settings.addView(colorTopPicker);
        settings.addView(explainColorB);
        settings.addView(colorBottomPicker);
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
                // TODO RETURNTO splash
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
        LinearLayout all = new LinearLayout(getApplicationContext());
        all.setOrientation(LinearLayout.VERTICAL);
        all.setGravity(Gravity.CENTER);
        LinearLayout students = new LinearLayout(getApplicationContext());
        students.setPadding(10, 10, 10, 10);
        students.setOrientation(LinearLayout.VERTICAL);
        students.setGravity(Gravity.CENTER);
        LinearLayout teachersv = new LinearLayout(getApplicationContext());
        teachersv.setPadding(10, 10, 10, 10);
        teachersv.setOrientation(LinearLayout.VERTICAL);
        teachersv.setGravity(Gravity.CENTER);
        TextView studentsTitle = new TextView(getApplicationContext());
        studentsTitle.setText(R.string.students_text);
        studentsTitle.setTypeface(getTypeface());
        studentsTitle.setTextSize(getFontSize() - 5);
        studentsTitle.setTextColor(theme.getLast().textColor);
        studentsTitle.setGravity(Gravity.CENTER);
        TextView teachersTitle = new TextView(getApplicationContext());
        teachersTitle.setText(R.string.teachers_text);
        teachersTitle.setTypeface(getTypeface());
        teachersTitle.setTextSize(getFontSize() - 5);
        teachersTitle.setTextColor(theme.getLast().textColor);
        teachersTitle.setGravity(Gravity.CENTER);
        all.addView(studentsTitle);
        students.setPadding(10, 10, 10, 10);
        teachersv.setPadding(10, 10, 10, 10);
        if (!keyManager.isKeyLoaded(KeyManager.TYPE_TEACHER_MODE)) {
            students.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            all.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        }
        ArrayList<LinearLayout> groups = new ArrayList<>();
        for (int cs = 0; cs < schedule.getClassrooms().size(); cs++) {
            int grade = schedule.getClassrooms().get(cs).getGrade() - Classroom.NINTH_GRADE;
            if (grade < 0) {
                grade = 0;
            }
            Button cls = new Button(getApplicationContext());
            cls.setTextSize((float) getFontSize());
            cls.setGravity(Gravity.CENTER);
            cls.setText(schedule.getClassrooms().get(cs).getName());
            cls.setTextColor(theme.getLast().textColor);
            cls.setBackground(Utils.getCoaster(theme.getLast().colorMix, 20, 5));
            //            cls.setPadding(10, 0, 10, 0);
            cls.setTypeface(getTypeface());
            cls.setLayoutParams(new LinearLayout.LayoutParams(Device.screenX(getApplicationContext()) / 5, (Device.screenY(getApplicationContext()) / 12)));
            final int finalCs = cs;
            cls.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    sp.edit().putString(Values.favoriteClass, schedule.getClassrooms().get(finalCs).getName()).apply();
                    setStudentMode(schedule.getClassrooms().get(finalCs));
                }
            });
            if (groups.size() > grade && groups.get(grade) != null) {
                groups.get(grade).addView(cls);
            } else {
                LinearLayout c = new LinearLayout(getApplicationContext());
                c.setOrientation(LinearLayout.HORIZONTAL);
                c.setGravity(Gravity.CENTER);
                groups.add(grade, c);
                groups.get(grade).addView(cls);
            }
        }
        for (int i = 0; i < groups.size(); i++) {
            students.addView(groups.get(i));
        }
        all.addView(students);
        if (keyManager.isKeyLoaded(KeyManager.TYPE_TEACHER_MODE)) {
            all.addView(teachersTitle);
            for (int cs = 0; cs < schedule.getTeachers().size(); cs++) {
                Button cls = new Button(getApplicationContext());
                cls.setTextSize((float) getFontSize());
                cls.setGravity(Gravity.CENTER);
                cls.setText(schedule.getTeachers().get(cs).getName());
                cls.setTextColor(theme.getLast().textColor);
                cls.setBackground(Utils.getCoaster(theme.getLast().colorMix, 20, 5));
                cls.setPadding(10, 0, 10, 0);
                cls.setTypeface(getTypeface());
                cls.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (Device.screenY(getApplicationContext()) / 12)));
                teachersv.addView(cls);
                final int finalCs = cs;
                cls.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        sp.edit().putString(Values.favoriteTeacher, schedule.getTeachers().get(finalCs).getName()).apply();
                        setTeacherMode(schedule.getTeachers().get(finalCs));
                    }
                });
            }
            all.addView(teachersv);
        }
        ScrollView sv = new ScrollView(getApplicationContext());
        sv.addView(all);
        sv.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        sv.setFillViewport(true);
        return sv;
    }

    private Drawable generateCoaster(int color) {
        GradientDrawable gd = (GradientDrawable) getApplicationContext().getDrawable(R.drawable.rounded_rect);
        if (gd != null) {
            gd.setColor(color);
        }
        return gd;
    }

    @Override
    public void onBackPressed() {
        if (mAppView.getDrawer().isOpen()) {
            mAppView.getDrawer().close();
        } else {
            finish();
        }
    }

    private FrameLayout getNews() {
        final FrameLayout masterLayout = new FrameLayout(getApplicationContext());
        masterLayout.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        TextView load = new TextView(getApplicationContext());
        load.setTextSize(getFontSize());
        load.setTypeface(getTypeface());
        load.setTextColor(theme.getLast().textColor);
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
        newsTitle.setTextColor(theme.getLast().textColor);
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
                        cls.setTextColor(theme.getLast().textColor);
                        cls.setEllipsize(TextUtils.TruncateAt.END);
                        cls.setLines(2);
                        //                            cls.setBackgroundColor(Color.TRANSPARENT);
                        cls.setBackground(Utils.getCoaster(theme.getLast().colorMix, 16, 10));
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
                                    getApplicationContext().startActivity(i);
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
                    fail.setTextColor(theme.getLast().textColor);
                    fail.setText(R.string.news_load_failed);
                    fail.setGravity(Gravity.CENTER);
                    //                    mAppView.getDrag().setContent(fail);
                }
            }).execute("");
        }
        return masterLayout;
    }

    private void parseAndLoad(File f) {
        schedule = AppCore.getSchedule(f);
        initStageB();
    }

    private void share(String st) {
        Intent s = new Intent(Intent.ACTION_SEND);
        s.putExtra(Intent.EXTRA_TEXT, st);
        s.setType("text/plain");
        s.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getApplicationContext().startActivity(Intent.createChooser(s, "Share With"));
    }

    private void setStudentMode(Classroom c) {
        currentClass = c;
        // TODO
        //        info.setText(textColor, getFontSize(), c.getName(), day);
        //        info.setText(0.9, new Corner.TextPiece(c.getName(), 0.9, textColor.getLast()), new Corner.TextPiece(schedule.getDay(), 0.65, textColor.getLast()));
        displayLessonViews(scheduleForClass(c));
        refreshTheme();
    }

    private void setTeacherMode(Teacher t) {
        currentTeacher = t;
        // TODO
        //        info.setText(textColor, getFontSize(), t.mainName.split(" ")[0], day);
        //        info.setText(0.9, new Corner.TextPiece(t.getName().split(" ")[0], 0.9, textColor.getLast()), new Corner.TextPiece(schedule.getDay(), 0.65, textColor.getLast()));
        displayLessonViews(scheduleForTeacher(t));
        refreshTheme();
    }

    private void displayLessonViews(final ArrayList<LessonView> lessonViews) {
        lessonViewHolder.removeAllViews();
        theme.addPeer(new Peer<>(new Peer.OnPeer<Theme>() {
            @Override
            public boolean onPeer(Theme theme) {
                for (int l = 0; l < lessonViews.size(); l++) {
                    LessonView lessonView = lessonViews.get(l);
                    lessonView.setTextColor(theme.textColor);
                    lessonView.setTextSize(theme.textSize);
                    lessonView.setButtonColor(theme.colorTop);
                }
                return false;
            }
        }));
        for (int l = 0; l < lessonViews.size(); l++) {
            lessonViewHolder.addView(lessonViews.get(l));
        }
    }

    private int getFontSize() {
        return sp.getInt(Values.fontSizeNumber, Values.fontSizeDefault);
    }

    private String scheduleForClassString(Classroom classroom) {
        StringBuilder export = new StringBuilder();
        for (Subject subject : classroom.getSubjects()) {
            String text = subject.getSchoolHour() + ". " + subject.getName();
            if (subject.getName() != null && !subject.getName().isEmpty()) {
                export.append(text).append("\n");
            }
        }
        return export.toString();
    }

    private Typeface getTypeface() {
        return Typeface.createFromAsset(getApplicationContext().getAssets(), Values.fontName);
    }

    private ArrayList<LessonView> scheduleForClass(final Classroom classroom) {
        ArrayList<LessonView> views = new ArrayList<>();
        for (Subject s : classroom.getSubjects()) {
            if (AppCore.getBreak(s.getSchoolHour() - 1, s.getSchoolHour()) > 0) {
                final LessonView breakView = new LessonView(getApplicationContext(), s.getSchoolHour() - 1, s.getSchoolHour(), "הפסקה");
                if (theme.getLast().showBreaks) {
                    breakView.setVisibility(View.VISIBLE);
                } else {
                    breakView.setVisibility(View.GONE);
                }
                theme.addPeer(new Peer<Theme>(new Peer.OnPeer<Theme>() {
                    @Override
                    public boolean onPeer(Theme theme) {
                        if (theme.showBreaks) {
                            breakView.setVisibility(View.VISIBLE);
                        } else {
                            breakView.setVisibility(View.GONE);
                        }
                        return false;
                    }
                }));
                if (s.getName() != null && !s.getName().isEmpty()) {
                    views.add(breakView);
                }
            }
            LessonView subject = new LessonView(getApplicationContext(), s.getSchoolHour(), s.getName(), s.getTeacherNames());
            if (s.getName() != null && !s.getName().isEmpty()) {
                views.add(subject);
            }
        }
        return views;
    }

    private ArrayList<LessonView> scheduleForTeacher(final Teacher teacher) {
        ArrayList<LessonView> views = new ArrayList<>();
        ArrayList<Subject> teaching = teacher.getSubjects();
        for (int h = 0; h <= 12; h++) {
            String grades;
            ArrayList<Classroom> classrooms = new ArrayList<>();
            for (int s = 0; s < teaching.size(); s++) {
                if (teaching.get(s).getSchoolHour() == h) {
                    classrooms.add(teaching.get(s).getClassroom());
                }
            }
            grades = AppCore.getGrades(classrooms);
            if (!classrooms.isEmpty()) {
                if (AppCore.getBreak(h - 1, h) > 0) {
                    final LessonView breakView = new LessonView(getApplicationContext(), h - 1, h, "הפסקה");
                    if (theme.getLast().showBreaks) {
                        breakView.setVisibility(View.VISIBLE);
                    } else {
                        breakView.setVisibility(View.GONE);
                    }
                    theme.addPeer(new Peer<Theme>(new Peer.OnPeer<Theme>() {
                        @Override
                        public boolean onPeer(Theme theme) {
                            if (theme.showBreaks) {
                                breakView.setVisibility(View.VISIBLE);
                            } else {
                                breakView.setVisibility(View.GONE);
                            }
                            return false;
                        }
                    }));
                    views.add(breakView);
                }
                LessonView subject = new LessonView(getApplicationContext(), h, grades, new ArrayList<>(Collections.singletonList(classrooms.get(h).getName())));
                views.add(subject);
            }
        }
        return views;
    }

    private class Theme {
        private int textSize, textColor;
        private int colorTop, colorBottom, colorMix;
        private boolean showBreaks, showMessages;
    }
}
