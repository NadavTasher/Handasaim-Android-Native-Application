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
import nadav.tasher.lightool.graphics.views.appview.AppView;
import nadav.tasher.lightool.graphics.views.appview.navigation.corner.Corner;
import nadav.tasher.lightool.graphics.views.appview.navigation.corner.CornerView;
import nadav.tasher.lightool.info.Device;
import nadav.tasher.lightool.parts.Peer;
import nadav.tasher.lightool.parts.Tower;

public class HomeActivity extends Activity {
    private Tower<Boolean> showBreaks = new Tower<>();
    private Tower<Boolean> showMessages = new Tower<>();
    private Tower<AppView.Gradient> color = new Tower<>();
    private int combinedColor = generateCombinedColor();
    private Tower<Integer> textSize = new Tower<>();
    private Tower<Integer> textColor = new Tower<>();
    private Classroom currentClass;
    private Teacher currentTeacher;
    private Corner icon, info;
    private MessageBar messageBar;
    private CornerView cornerView;
    private LinearLayout scheduleLayout, lessonViewHolder;
    private AppView mAppView;
    private Schedule schedule;
    private HorizontalScrollView menuDrawer;
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
        textColor.tell(sp.getInt(Values.fontColor, Values.fontColorDefault));
        textSize.tell(sp.getInt(Values.fontSizeNumber, Values.fontSizeDefault));
        showMessages.tell(sp.getBoolean(Values.messages, Values.messagesDefault));
        showBreaks.tell(sp.getBoolean(Values.breakTime, Values.breakTimeDefault));
        color.tell(new AppView.Gradient(sp.getInt(Values.colorA, Values.defaultColorA), sp.getInt(Values.colorB, Values.defaultColorB)));
        combinedColor = generateCombinedColor();
        if (mAppView != null) mAppView.setBackgroundColor(color.getLast());
        getWindow().setNavigationBarColor(combinedColor);
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

    private int generateCombinedColor() {
        if (color.getLast() != null) {
            int redA = Color.red(color.getLast().getColorTop());
            int greenA = Color.green(color.getLast().getColorTop());
            int blueA = Color.blue(color.getLast().getColorTop());
            int redB = Color.red(color.getLast().getColorBottom());
            int greenB = Color.green(color.getLast().getColorBottom());
            int blueB = Color.blue(color.getLast().getColorBottom());
            int combineRed = redA - (redA - redB) / 2, combineGreen = greenA - (greenA - greenB) / 2, combineBlue = blueA - (blueA - blueB) / 2;
            return Color.rgb(combineRed, combineGreen, combineBlue);
        } else {
            return 0;
        }
    }

    private void initStageB() {
        mAppView = new AppView(getApplicationContext(), Values.navColor);
        mAppView.setColorChangeNavigation(false);
        mAppView.setWindow(getWindow());
        cornerView = new CornerView(getApplicationContext());
        mAppView.setNavigationView(cornerView);
        icon = new Corner(getApplicationContext(), Device.screenX(getApplicationContext()) / 5, Color.TRANSPARENT);
        icon.setDrawable(getDrawable(R.drawable.ic_icon), 0.85);
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
                mAppView.getDrawer().emptyContent();
                if (menuDrawer != null) {
                    mAppView.getDrawer().setContent(menuDrawer);
                    mAppView.getDrawer().open(false, ((double) menuDrawer.getLayoutParams().height / (double) Device.screenY(getApplicationContext())));
                }
            }

            @Override
            public void onClose() {
                mAppView.getDrawer().close(false);
            }

            @Override
            public void onBoth(boolean b) {
            }
        });
        color.addPeer(new Peer<AppView.Gradient>(new Peer.OnPeer<AppView.Gradient>() {
            @Override
            public boolean onPeer(AppView.Gradient integer) {
                setCornerColors(mAppView.getScrolly());
                return false;
            }
        }));
        color.addPeer(new Peer<AppView.Gradient>(new Peer.OnPeer<AppView.Gradient>() {
            @Override
            public boolean onPeer(AppView.Gradient integer) {
                setCornerColors(mAppView.getScrolly());
                return false;
            }
        }));
        textColor.addPeer(icon.getTextColorPeer());
        textColor.addPeer(info.getTextColorPeer());
        textSize.addPeer(icon.getTextSizePeer());
        textSize.addPeer(info.getTextSizePeer());
        cornerView.setBottomLeft(icon);
        cornerView.setBottomRight(info);
        mAppView.getScrolly().setOnScroll(new AppView.Scrolly.OnScroll() {
            @Override
            public void onScroll(int i, int i1, int i2, int i3) {
                setCornerColors(mAppView.getScrolly());
            }
        });
        scheduleLayout = new LinearLayout(getApplicationContext());
        scheduleLayout.setGravity(Gravity.START | Gravity.CENTER_HORIZONTAL);
        scheduleLayout.setOrientation(LinearLayout.VERTICAL);
        scheduleLayout.setPadding(10, 10, 10, 10);
        messageBar = new MessageBar(this, schedule.getMessages(), mAppView.getDrawer());
        messageBar.start();
        showMessages.addPeer(new Peer<Boolean>(new Peer.OnPeer<Boolean>() {
            @Override
            public boolean onPeer(Boolean aBoolean) {
                if (aBoolean && schedule.getMessages().size() != 0) {
                    messageBar.setVisibility(View.VISIBLE);
                } else {
                    messageBar.setVisibility(View.GONE);
                }
                return false;
            }
        }));
        scheduleLayout.addView(messageBar);
        lessonViewHolder = new LinearLayout(getApplicationContext());
        lessonViewHolder.setGravity(Gravity.START | Gravity.CENTER_HORIZONTAL);
        lessonViewHolder.setOrientation(LinearLayout.VERTICAL);
        scheduleLayout.addView(lessonViewHolder);
        mAppView.setContent(scheduleLayout);
        setContentView(mAppView);
        refreshTheme();

        assembleDrawers();
        if (getFavoriteClass() != null) setStudentMode(getFavoriteClass());

    }

    private void assembleDrawers() {
        assembleMenuDrawer();
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
        FrameLayout share, classroom, news, predict, refresh, settings;
        share = generateImageView(R.drawable.ic_share, size);
        classroom = generateImageView(R.drawable.ic_class, size);
        news = generateImageView(R.drawable.ic_news, size);
        predict = generateImageView(R.drawable.ic_notification, size);
        refresh = generateImageView(R.drawable.ic_reload, size);
        settings = generateImageView(R.drawable.ic_gear, size);
        menu.addView(share);
        menu.addView(classroom);
        menu.addView(news);
        menu.addView(predict);
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
        ImageView iv = new ImageView(getApplicationContext());
        iv.setImageDrawable(getDrawable(drawable));
        iv.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        fl.addView(iv);
        return fl;
    }

    private void setCornerColors(AppView.Scrolly s) {
        if (s.getChildAt(s.getChildCount() - 1).getBottom() - (s.getHeight() + s.getScrollY()) == 0) {
            icon.setColorAlpha(128);
            info.setColorAlpha(128);
            icon.setColor(color.getLast().getColorTop());
            info.setColor(color.getLast().getColorTop());
        } else {
            info.setColorAlpha(255);
            icon.setColorAlpha(255);
            icon.setColor(combinedColor);
            info.setColor(combinedColor);
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
        shareTitle.setTextColor(textColor.getLast());
        shareTitle.setText(R.string.share_menu);
        shareTitle.setGravity(Gravity.CENTER);
        final Switch shareTimeSwitch = new Switch(getApplicationContext());
        shareTimeSwitch.setPadding(10, 0, 10, 0);
        shareTimeSwitch.setText(R.string.lesson_time);
        shareTimeSwitch.setTypeface(getTypeface());
        shareTimeSwitch.setTextSize(getFontSize() - 4);
        shareTimeSwitch.setTextColor(textColor.getLast());
        shareTimeSwitch.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        final Switch shareMessageSwitch = new Switch(getApplicationContext());
        shareMessageSwitch.setPadding(10, 0, 10, 0);
        shareMessageSwitch.setText(R.string.messages_switch);
        shareMessageSwitch.setTypeface(getTypeface());
        shareMessageSwitch.setTextSize(getFontSize() - 4);
        shareMessageSwitch.setTextColor(textColor.getLast());
        shareMessageSwitch.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        shareMessageSwitch.setEnabled(!schedule.getMessages().isEmpty());
        Button shareB = new Button(getApplicationContext());
        String shareText = getApplicationContext().getString(R.string.share) + " " + currentClass.getName();
        shareB.setText(shareText);
        shareB.setBackground(generateCoaster(combinedColor));
        shareB.setTextColor(textColor.getLast());
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
                share(currentClass.getName() + " (" + schedule.getDay() + ")" + "\n" + scheduleForClassString(currentClass, shareTimeSwitch.isChecked()) + message);
            }
        });
        shareView.addView(shareTitle);
        shareView.addView(shareTimeSwitch);
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
        pushSwitch.setTextSize((float) (getFontSize() / 1.5));
        messageSwitch.setTextSize((float) (getFontSize() / 1.5));
        breakTimeSwitch.setTextSize((float) (getFontSize() / 1.5));
        newScheduleSwitch.setTextSize((float) (getFontSize() / 1.5));
        devSwitch.setTextSize((float) (getFontSize() / 1.5));
        pushSwitch.setTypeface(getTypeface());
        newScheduleSwitch.setTypeface(getTypeface());
        breakTimeSwitch.setTypeface(getTypeface());
        devSwitch.setTypeface(getTypeface());
        messageSwitch.setTypeface(getTypeface());
        newScheduleSwitch.setTextColor(textColor.getLast());
        messageSwitch.setTextColor(textColor.getLast());
        pushSwitch.setTextColor(textColor.getLast());
        breakTimeSwitch.setTextColor(textColor.getLast());
        devSwitch.setTextColor(textColor.getLast());
        breakTimeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                sp.edit().putBoolean(Values.breakTime, isChecked).apply();
                showBreaks.tell(isChecked);
            }
        });
        messageSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                sp.edit().putBoolean(Values.messages, isChecked).apply();
                showMessages.tell(isChecked);
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
                if (isChecked) {
                    devModeConfirm(devSwitch);
                } else {
                    if (sp.getBoolean(Values.devMode, Values.devModeDefault)) {
                        sp.edit().putBoolean(Values.devMode, false).apply();
                        // TODO RETURNTO splash
                    }
                }
            }
        });
        final TextView explainTextSize = new TextView(getApplicationContext());
        explainTextSize.setText(R.string.choose_text_size);
        explainTextSize.setTypeface(getTypeface());
        explainTextSize.setTextSize((float) (getFontSize() / 1.5));
        explainTextSize.setTextColor(textColor.getLast());
        explainTextSize.setGravity(Gravity.CENTER);
        final TextView explainTextColor = new TextView(getApplicationContext());
        explainTextColor.setText(R.string.choose_text_color);
        explainTextColor.setTypeface(getTypeface());
        explainTextColor.setTextSize((float) (getFontSize() / 1.5));
        explainTextColor.setTextColor(textColor.getLast());
        explainTextColor.setGravity(Gravity.CENTER);
        final TextView explainColorA = new TextView(getApplicationContext());
        explainColorA.setText(R.string.choose_first_color);
        explainColorA.setTypeface(getTypeface());
        explainColorA.setTextSize((float) (getFontSize() / 1.5));
        explainColorA.setTextColor(textColor.getLast());
        explainColorA.setGravity(Gravity.CENTER);
        final TextView explainColorB = new TextView(getApplicationContext());
        explainColorB.setText(R.string.choose_second_color);
        explainColorB.setTypeface(getTypeface());
        explainColorB.setTextSize((float) (getFontSize() / 1.5));
        explainColorB.setTextColor(textColor.getLast());
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
        ColorPicker textColorPicker = new ColorPicker(getApplicationContext(), textColor.getLast());
        textColorPicker.setOnColorChanged(new ColorPicker.OnColorChanged() {
            @Override
            public void onColorChange(int color) {
                sp.edit().putInt(Values.fontColor, color).apply();
                refreshTheme();
            }
        });
        ColorPicker colorApicker = new ColorPicker(getApplicationContext(), color.getLast().getColorTop());
        colorApicker.setOnColorChanged(new ColorPicker.OnColorChanged() {
            @Override
            public void onColorChange(int color) {
                //                Log.i("Red", Color.red(color) + "");
                //                Log.i("Gre", Color.green(color) + "");
                //                Log.i("Blu", Color.blue(color) + "");
                sp.edit().putInt(Values.colorA, color).apply();
                refreshTheme();
            }
        });
        ColorPicker colorBpicker = new ColorPicker(getApplicationContext(), color.getLast().getColorBottom());
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
        textSize.addPeer(new Peer<>(new Peer.OnPeer<Integer>() {
            @Override
            public boolean onPeer(Integer response) {
                pushSwitch.setTextSize((float) (response / 1.5));
                breakTimeSwitch.setTextSize((float) (response / 1.5));
                newScheduleSwitch.setTextSize((float) (response / 1.5));
                messageSwitch.setTextSize((float) (response / 1.5));
                devSwitch.setTextSize((float) (response / 1.5));
                explainColorA.setTextSize((float) (response / 1.5));
                explainColorB.setTextSize((float) (response / 1.5));
                explainTextColor.setTextSize((float) (response / 1.5));
                explainTextSize.setTextSize((float) (response / 1.5));
                return true;
            }
        }));
        textColor.addPeer(new Peer<>(new Peer.OnPeer<Integer>() {
            @Override
            public boolean onPeer(Integer response) {
                pushSwitch.setTextColor(response);
                breakTimeSwitch.setTextColor(response);
                newScheduleSwitch.setTextColor(response);
                messageSwitch.setTextColor(response);
                devSwitch.setTextColor(response);
                explainColorA.setTextColor(response);
                explainColorB.setTextColor(response);
                explainTextColor.setTextColor(response);
                explainTextSize.setTextColor(response);
                return true;
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
        studentsTitle.setTextColor(textColor.getLast());
        studentsTitle.setGravity(Gravity.CENTER);
        TextView teachersTitle = new TextView(getApplicationContext());
        teachersTitle.setText(R.string.teachers_text);
        teachersTitle.setTypeface(getTypeface());
        teachersTitle.setTextSize(getFontSize() - 5);
        teachersTitle.setTextColor(textColor.getLast());
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
            int grade = schedule.getClassrooms().get(cs).getGrade();
            Button cls = new Button(getApplicationContext());
            cls.setTextSize((float) getFontSize());
            cls.setGravity(Gravity.CENTER);
            cls.setText(schedule.getClassrooms().get(cs).getName());
            cls.setTextColor(textColor.getLast());
            cls.setBackground(generateCoaster(combinedColor));
            cls.setPadding(10, 0, 10, 0);
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
                cls.setTextColor(textColor.getLast());
                cls.setBackground(generateCoaster(combinedColor));
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
            mAppView.getDrawer().close(true);
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
        load.setTextColor(textColor.getLast());
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
        newsTitle.setTextColor(textColor.getLast());
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
                        cls.setTextColor(textColor.getLast());
                        cls.setEllipsize(TextUtils.TruncateAt.END);
                        cls.setLines(2);
                        //                            cls.setBackgroundColor(Color.TRANSPARENT);
                        cls.setBackground(generateCoaster(combinedColor));
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
                    fail.setTextColor(textColor.getLast());
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
        info.setText(0.9, new Corner.TextPiece(c.getName(), 0.9, textColor.getLast()), new Corner.TextPiece(schedule.getDay(), 0.65, textColor.getLast()));
        displayLessonViews(scheduleForClass(c));
    }

    private void setTeacherMode(Teacher t) {
        currentTeacher = t;
        // TODO
        //        info.setText(textColor, getFontSize(), t.mainName.split(" ")[0], day);
        info.setText(0.9, new Corner.TextPiece(t.getName().split(" ")[0], 0.9, textColor.getLast()), new Corner.TextPiece(schedule.getDay(), 0.65, textColor.getLast()));
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

    private String scheduleForClassString(Classroom fclass, boolean showTime) {
        StringBuilder allsubj = new StringBuilder();
        //        for (int s = 0; s < fclass.getSubjects().size(); s++) {
        //            String before;
        //            if (showTime) {
        //                before = "(" + getRealTimeForHourNumber(fclass.getSubjects().get(s).getHour()) + ") " + fclass.getSubjects().get(s).getHour() + ". ";
        //            } else {
        //                before = fclass.getSubjects().get(s).getHour() + ". ";
        //            }
        //            String total = before + fclass.getSubjects().get(s).getName();
        //            if (fclass.getSubjects().get(s).getName() != null && !fclass.getSubjects().get(s).getName().equals("")) {
        //                allsubj.append(total).append("\n");
        //            }
        //        }
        return allsubj.toString();
    }

    private Typeface getTypeface() {
        return Typeface.createFromAsset(getApplicationContext().getAssets(), Values.fontName);
    }

    private ArrayList<LessonView> scheduleForClass(final Classroom classroom) {
        ArrayList<LessonView> views = new ArrayList<>();
        for (Subject s : classroom.getSubjects()) {
            if (AppCore.getBreak(s.getSchoolHour() - 1, s.getSchoolHour()) > 0) {
                final LessonView breakView = new LessonView(getApplicationContext(), s.getSchoolHour(), "הפסקה", null);
                if (showBreaks.getLast()) {
                    breakView.setVisibility(View.VISIBLE);
                } else {
                    breakView.setVisibility(View.GONE);
                }
                showBreaks.addPeer(new Peer<Boolean>(new Peer.OnPeer<Boolean>() {
                    @Override
                    public boolean onPeer(Boolean aBoolean) {
                        if (aBoolean) {
                            breakView.setVisibility(View.VISIBLE);
                        } else {
                            breakView.setVisibility(View.GONE);
                        }
                        return false;
                    }
                }));
                if (s.getName() != null && !s.getName().equals("")) {
                    views.add(breakView);
                }
            }
            LessonView subject = new LessonView(getApplicationContext(), s.getSchoolHour(), s.getName(), s.getTeacherNames());
            if (s.getName() != null && !s.getName().equals("")) {
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
                    final LessonView breakView = new LessonView(getApplicationContext(), h, "הפסקה", null);
                    if (showBreaks.getLast()) {
                        breakView.setVisibility(View.VISIBLE);
                    } else {
                        breakView.setVisibility(View.GONE);
                    }
                    showBreaks.addPeer(new Peer<Boolean>(new Peer.OnPeer<Boolean>() {
                        @Override
                        public boolean onPeer(Boolean aBoolean) {
                            if (aBoolean) {
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
}
