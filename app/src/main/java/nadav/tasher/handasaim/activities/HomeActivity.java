package nadav.tasher.handasaim.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
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
import nadav.tasher.handasaim.architecture.app.PreferenceManager;
import nadav.tasher.handasaim.architecture.app.graphics.LessonView;
import nadav.tasher.handasaim.architecture.app.graphics.MessageBar;
import nadav.tasher.handasaim.architecture.app.graphics.RatioView;
import nadav.tasher.handasaim.architecture.appcore.AppCore;
import nadav.tasher.handasaim.architecture.appcore.components.Classroom;
import nadav.tasher.handasaim.architecture.appcore.components.Schedule;
import nadav.tasher.handasaim.architecture.appcore.components.Subject;
import nadav.tasher.handasaim.architecture.appcore.components.Teacher;
import nadav.tasher.handasaim.values.Filters;
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
    private Corner info;
    private MessageBar messageBar;
    private LinearLayout lessonViewHolder;
    private AppView mAppView;
    private Schedule schedule;
    private HorizontalScrollView menuDrawer;
    private ScrollView settings, switcher, share, code, about;

    private int drawerPadding = 10;

    private PreferenceManager pm;

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
    }

    private void refreshTheme() {
        Theme currentTheme = new Theme();
        currentTheme.textColor = pm.getUserManager().get(R.string.preferences_user_color_text, getResources().getColor(R.color.default_text));
        currentTheme.textSize = pm.getUserManager().get(R.string.preferences_user_size_text, getResources().getInteger(R.integer.default_font));
        currentTheme.showMessages = pm.getUserManager().get(R.string.preferences_user_display_messages, getResources().getBoolean(R.bool.default_display_messages));
        currentTheme.showBreaks = pm.getUserManager().get(R.string.preferences_user_display_breaks, getResources().getBoolean(R.bool.default_display_breaks));
        currentTheme.colorTop = pm.getUserManager().get(R.string.preferences_user_color_top, getResources().getColor(R.color.default_top));
        currentTheme.colorBottom = pm.getUserManager().get(R.string.preferences_user_color_bottom, getResources().getColor(R.color.default_bottom));
        currentTheme.colorMix = generateCombinedColor(currentTheme.colorTop, currentTheme.colorBottom);
        theme.tell(currentTheme);
        Log.i("COlorA", String.format("#%06X", (0xFFFFFF & currentTheme.colorTop)));
        Log.i("COlorA", String.format("#%06X", (0xFFFFFF & currentTheme.colorBottom)));
    }

    public void go() {
        String file = pm.getCoreManager().getFile();
        if (file != null) {
            parseAndLoad(new File(file));
        } else {
            Center.exit(this, SplashActivity.class);
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
                pm.getKeyManager().loadKey(key.getText().toString().toUpperCase());
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
        refreshTheme();
        mAppView = new AppView(this);
        mAppView.setDrawNavigation(false);
        mAppView.getDrawer().getDrawerView().setBackground(Utils.getCoaster(getResources().getColor(R.color.drawer_color), 30, 10));
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
        mAppView.getCornerView().setBottomRight(info);
        mAppView.getScrolly().setOnScroll(new AppView.Scrolly.OnScroll() {
            @Override
            public void onScroll(int i, int i1, int i2, int i3) {
                setCornerColor(mAppView.getScrolly());
            }
        });
        final LinearLayout scheduleLayout = new LinearLayout(getApplicationContext());
        scheduleLayout.setGravity(Gravity.START | Gravity.CENTER_HORIZONTAL);
        scheduleLayout.setOrientation(LinearLayout.VERTICAL);
        scheduleLayout.setPadding(10, 10, 10, 10);
        messageBar = new MessageBar(this);
        messageBar.setMessages(schedule.getMessages());
        messageBar.setOnMessage(new MessageBar.OnMessage() {
            @Override
            public void onMessage(String message, int index) {
                ScrollView scrollView = new ScrollView(getApplicationContext());
                scrollView.setFillViewport(true);
                scrollView.setOverScrollMode(ScrollView.OVER_SCROLL_NEVER);
                scrollView.setVerticalScrollBarEnabled(false);
                RatioView messageView = new RatioView(getApplicationContext(), 0.8);
                messageView.setPadding(20, 20, 20, 20);
                messageView.setTypeface(Center.getTypeface(getApplicationContext()));
                messageView.setTextSize(theme.getLast().textSize);
                messageView.setTextColor(theme.getLast().textColor);
                messageView.setGravity(Gravity.CENTER);
                messageView.setText(message);
                messageView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                scrollView.addView(messageView);
                messageView.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                double percent = ((double) (messageView.getMeasuredHeight() + (2 * drawerPadding))) / ((double) Device.screenY(getApplicationContext()));
                mAppView.getDrawer().setContent(scrollView);
                mAppView.getDrawer().open(((percent) > 0.7) ? 0.7 : percent);
            }
        });
        messageBar.start();
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
                setCornerColor(mAppView.getScrolly());
                messageBar.setTextColor(theme.textColor);
                messageBar.setTextSize(theme.textSize);
                if (theme.showMessages && schedule.getMessages().size() != 0) {
                    messageBar.setVisibility(View.VISIBLE);
                } else {
                    messageBar.setVisibility(View.GONE);
                }
                return false;
            }
        }));
        assembleDrawers();
        loadContent();
        setContentView(mAppView);
        refreshTheme();
    }

    private void loadContent() {
        if (pm.getCoreManager().getMode() != null) {
            if (pm.getCoreManager().getMode().equals(getResources().getString(R.string.core_mode_teacher))) {
                if (pm.getKeyManager().isKeyLoaded(R.string.preferences_keys_type_teachers)) {
                    Teacher favoriteTeacher = getFavoriteTeacher();
                    if (favoriteTeacher != null) {
                        setTeacherMode(favoriteTeacher);
                    } else {
                        loadFavoriteClassroom();
                    }
                } else {
                    loadFavoriteClassroom();
                }
            } else {
                loadFavoriteClassroom();
            }
        } else {
            loadFavoriteClassroom();
        }
    }

    private void loadFavoriteClassroom() {
        Classroom favoriteClassroom = getFavoriteClassroom();
        if (favoriteClassroom != null) {
            setStudentMode(favoriteClassroom);
        } else {
            Center.exit(this, SplashActivity.class);
        }
    }

    private void assembleDrawers() {
        assembleMenuDrawer();
        settings = getSettings();
        share = getShare();
        switcher = getSwitcher();
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
        final FrameLayout shareIcon, classroomIcon, icon, refreshIcon, settingsIcon;
        shareIcon = generateImageView(R.drawable.ic_share, size);
        classroomIcon = generateImageView(R.drawable.ic_class, size);
        icon = generateImageView(R.drawable.ic_icon, size);
        refreshIcon = generateImageView(R.drawable.ic_reload, size);
        settingsIcon = generateImageView(R.drawable.ic_gear, size);
        settingsIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDrawer(settings);
            }
        });
        classroomIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDrawer(switcher);
            }
        });
        refreshIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Center.exit(HomeActivity.this, SplashActivity.class);
            }
        });
        shareIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDrawer(share);
            }
        });
        icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                aboutPopup();
            }
        });
        menu.addView(shareIcon);
        menu.addView(classroomIcon);
        menu.addView(icon);
        menu.addView(refreshIcon);
        menu.addView(settingsIcon);
        menu.setPadding(10, 10, 10, 10);
        menu.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, size + menu.getPaddingBottom() + menu.getPaddingTop()));
        menuDrawer.setLayoutParams(new LinearLayout.LayoutParams(Device.screenX(getApplicationContext()), size + menu.getPaddingBottom() + menu.getPaddingTop()));
        menuDrawer.addView(menu);
    }

    private FrameLayout generateImageView(int drawable, int size) {
        FrameLayout fl = new FrameLayout(getApplicationContext());
        fl.setPadding(20, 20, 20, 20);
        fl.setForegroundGravity(Gravity.CENTER);
        fl.setLayoutParams(new LinearLayout.LayoutParams(size, size));
        final ImageView iv = new ImageView(getApplicationContext());
        iv.setImageDrawable(getDrawable(drawable));
        iv.setScaleType(ImageView.ScaleType.FIT_XY);
        iv.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        fl.addView(iv);
        return fl;
    }

    private void setCornerColor(AppView.Scrolly s) {
        //        Log.i("Scroll",String.valueOf(s.getScrollY()));
        if (s.getScrollY() != 0) {
            if (s.getChildAt(s.getChildCount() - 1).getBottom() - (s.getHeight() + s.getScrollY()) == 0) {
                info.setColorAlpha(128);
                info.setColor(theme.getLast().colorTop);
            } else {
                info.setColorAlpha(255);
                info.setColor(theme.getLast().colorMix);
            }
        } else {
            info.setColorAlpha(255);
            info.setColor(theme.getLast().colorMix);
        }
    }

    private void openDrawer(View v) {
        mAppView.getDrawer().setContent(v);
        v.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        double percent = ((double) (v.getMeasuredHeight() + (2 * drawerPadding))) / ((double) Device.screenY(getApplicationContext()));
        mAppView.getDrawer().open(((percent) > 0.7) ? 0.7 : percent);
    }

    private Classroom getFavoriteClassroom() {
        if (!schedule.getClassrooms().isEmpty()) {
            int selectedClass = 0;
            String favoriteClassroomName = pm.getUserManager().get(R.string.preferences_user_favorite_classroom, null);
            if (favoriteClassroomName != null) {
                for (int fc = 0; fc < schedule.getClassrooms().size(); fc++) {
                    if (favoriteClassroomName.equals(schedule.getClassrooms().get(fc).getName())) {
                        return schedule.getClassrooms().get(fc);
                    }
                }
                return schedule.getClassrooms().get(selectedClass);
            } else {
                return schedule.getClassrooms().get(selectedClass);
            }
        } else {
            // Empty Schedule - Return null
            return null;
        }
    }

    private Teacher getFavoriteTeacher() {
        if (!schedule.getTeachers().isEmpty()) {
            int selectedTeacher = 0;
            String favoriteTeacherName = pm.getUserManager().get(R.string.preferences_user_favorite_classroom, null);
            if (favoriteTeacherName != null) {
                for (int fc = 0; fc < schedule.getTeachers().size(); fc++) {
                    if (favoriteTeacherName.equals(schedule.getTeachers().get(fc).getName())) {
                        return schedule.getTeachers().get(fc);
                    }
                }
                return schedule.getTeachers().get(selectedTeacher);
            } else {
                return schedule.getTeachers().get(selectedTeacher);
            }
        } else {
            // Empty Schedule - Return null
            return null;
        }
    }

    private ScrollView getShare() {
        ScrollView sv = new ScrollView(getApplicationContext());
        sv.setOverScrollMode(ScrollView.OVER_SCROLL_NEVER);
        sv.setFillViewport(true);
        LinearLayout shareView = new LinearLayout(getApplicationContext());
        shareView.setGravity(Gravity.CENTER);
        shareView.setOrientation(LinearLayout.VERTICAL);
        shareView.setPadding(20, 20, 20, 20);
        final Switch shareMessageSwitch = new Switch(getApplicationContext());
        shareMessageSwitch.setPadding(10, 0, 10, 0);
        shareMessageSwitch.setText(R.string.messages_switch);
        shareMessageSwitch.setTypeface(Center.getTypeface(getApplicationContext()));
        shareMessageSwitch.setTextSize((int) (double) (theme.getLast().textSize * 0.8));
        shareMessageSwitch.setTextColor(theme.getLast().textColor);
        shareMessageSwitch.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        shareMessageSwitch.setEnabled(!schedule.getMessages().isEmpty());
        final Switch shareGradeSwitch = new Switch(getApplicationContext());
        shareGradeSwitch.setPadding(10, 0, 10, 0);
        shareGradeSwitch.setText(R.string.grade_switch);
        shareGradeSwitch.setTypeface(Center.getTypeface(getApplicationContext()));
        shareGradeSwitch.setTextSize((int) (double) (theme.getLast().textSize * 0.8));
        shareGradeSwitch.setTextColor(theme.getLast().textColor);
        shareGradeSwitch.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        final Button shareB = new Button(getApplicationContext());
        shareB.setText(R.string.share);
        shareB.setBackground(Utils.getCoaster(theme.getLast().colorMix, 16, 10));
        shareB.setTextColor(theme.getLast().textColor);
        shareB.setTextSize((int) (double) (theme.getLast().textSize * 0.7));
        shareB.setTypeface(Center.getTypeface(getApplicationContext()));
        shareB.setAllCaps(false);
        shareB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StringBuilder message = new StringBuilder();
                if (shareGradeSwitch.isChecked()) {
                    message.append("יום").append(" ").append(schedule.getDay()).append(":").append("\n").append("\n");
                    int grade = currentClass.getGrade();
                    for (Classroom classroom : schedule.getClassrooms()) {
                        if (classroom.getGrade() == grade) {
                            message.append(classroom.getName()).append(":").append("\n");
                            message.append(scheduleForClassString(classroom)).append("\n");
                        }
                    }
                } else {
                    message.append(currentClass.getName()).append(" (").append(schedule.getDay()).append(")").append("\n");
                    message.append(scheduleForClassString(currentClass)).append("\n");
                }
                if (shareMessageSwitch.isChecked()) {
                    message.append("הודעות:\n");
                    for (int i = 0; i < schedule.getMessages().size(); i++) {
                        message.append(i + 1).append(". ").append(schedule.getMessages().get(i)).append("\n");
                    }
                }
                share(message.toString());
            }
        });
        shareView.addView(shareMessageSwitch);
        shareView.addView(shareGradeSwitch);
        shareView.addView(shareB);
        theme.addPeer(new Peer<>(new Peer.OnPeer<Theme>() {
            @Override
            public boolean onPeer(Theme theme) {
                shareB.setTextSize((int) ((double) (theme.textSize * 0.7)));
                shareB.setTextColor(theme.textColor);
                shareGradeSwitch.setTextSize((int) ((double) (theme.textSize * 0.8)));
                shareGradeSwitch.setTextColor(theme.textColor);
                shareMessageSwitch.setTextSize((int) ((double) (theme.textSize * 0.8)));
                shareMessageSwitch.setTextColor(theme.textColor);
                return false;
            }
        }));
        sv.addView(shareView);
        return sv;
    }

    private ScrollView getSettings() {
        ScrollView sv = new ScrollView(getApplicationContext());
        sv.setPadding(10, 10, 10, 10);
        LinearLayout settings = new LinearLayout(getApplicationContext());
        settings.setOrientation(LinearLayout.VERTICAL);
        settings.setGravity(Gravity.START);
        settings.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        settings.setPadding(20, 20, 20, 20);
        final Switch devSwitch = new Switch(getApplicationContext()), displayMessagesSwitch = new Switch(getApplicationContext()), refreshSwitch = new Switch(getApplicationContext()), displayBreaksSwitch = new Switch(getApplicationContext()), pushSwitch = new Switch(getApplicationContext());
        pushSwitch.setText(R.string.live_messages);
        displayMessagesSwitch.setText(R.string.schedule_messages);
        refreshSwitch.setText(R.string.schedule_notification);
        displayBreaksSwitch.setText(R.string.show_breaks);
        devSwitch.setText(R.string.developer_mode);
        pushSwitch.setChecked(pm.getUserManager().get(R.string.preferences_user_service_push, getResources().getBoolean(R.bool.default_service_push)));
        refreshSwitch.setChecked(pm.getUserManager().get(R.string.preferences_user_service_refresh, getResources().getBoolean(R.bool.default_service_refresh)));
        displayMessagesSwitch.setChecked(pm.getUserManager().get(R.string.preferences_user_display_messages, getResources().getBoolean(R.bool.default_display_messages)));
        displayBreaksSwitch.setChecked(pm.getUserManager().get(R.string.preferences_user_display_messages, getResources().getBoolean(R.bool.default_display_messages)));
        devSwitch.setChecked(pm.getUserManager().get(R.string.preferences_user_mode_developer, getResources().getBoolean(R.bool.default_mode_developer)));
        pushSwitch.setTypeface(Center.getTypeface(getApplicationContext()));
        refreshSwitch.setTypeface(Center.getTypeface(getApplicationContext()));
        displayBreaksSwitch.setTypeface(Center.getTypeface(getApplicationContext()));
        devSwitch.setTypeface(Center.getTypeface(getApplicationContext()));
        displayMessagesSwitch.setTypeface(Center.getTypeface(getApplicationContext()));
        displayBreaksSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                pm.getUserManager().set(R.string.preferences_user_display_breaks, isChecked);
                refreshTheme();
            }
        });
        displayMessagesSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                pm.getUserManager().set(R.string.preferences_user_display_messages, isChecked);
                refreshTheme();
            }
        });
        pushSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                pm.getUserManager().set(R.string.preferences_user_service_push, isChecked);
            }
        });
        refreshSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                pm.getUserManager().set(R.string.preferences_user_service_refresh, isChecked);
            }
        });
        devSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                pm.getUserManager().set(R.string.preferences_user_mode_developer, isChecked);
            }
        });
        final TextView explainTextSize = new TextView(getApplicationContext());
        explainTextSize.setText(R.string.choose_text_size);
        explainTextSize.setTypeface(Center.getTypeface(getApplicationContext()));
        explainTextSize.setGravity(Gravity.CENTER);
        final TextView explainTextColor = new TextView(getApplicationContext());
        explainTextColor.setText(R.string.choose_text_color);
        explainTextColor.setTypeface(Center.getTypeface(getApplicationContext()));
        explainTextColor.setGravity(Gravity.CENTER);
        final TextView explainColorA = new TextView(getApplicationContext());
        explainColorA.setText(R.string.choose_first_color);
        explainColorA.setTypeface(Center.getTypeface(getApplicationContext()));
        explainColorA.setGravity(Gravity.CENTER);
        final TextView explainColorB = new TextView(getApplicationContext());
        explainColorB.setText(R.string.choose_second_color);
        explainColorB.setTypeface(Center.getTypeface(getApplicationContext()));
        explainColorB.setGravity(Gravity.CENTER);
        SeekBar fontSizeSeekBar = new SeekBar(getApplicationContext());
        fontSizeSeekBar.setMax(70);
        fontSizeSeekBar.setProgress(pm.getUserManager().get(R.string.preferences_user_size_text, getResources().getInteger(R.integer.default_font)));
        fontSizeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    pm.getUserManager().set(R.string.preferences_user_size_text, progress);
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
                pm.getUserManager().set(R.string.preferences_user_color_text, color);
                refreshTheme();
            }
        });
        ColorPicker colorTopPicker = new ColorPicker(getApplicationContext());
        colorTopPicker.setColor(theme.getLast().colorTop);
        colorTopPicker.setOnColor(new ColorPicker.OnColorChanged() {
            @Override
            public void onColorChange(int color) {
                pm.getUserManager().set(R.string.preferences_user_color_top, color);
                refreshTheme();
            }
        });
        ColorPicker colorBottomPicker = new ColorPicker(getApplicationContext());
        colorBottomPicker.setColor(theme.getLast().colorBottom);
        colorBottomPicker.setOnColor(new ColorPicker.OnColorChanged() {
            @Override
            public void onColorChange(int color) {
                pm.getUserManager().set(R.string.preferences_user_color_bottom, color);
                refreshTheme();
            }
        });
        textColorPicker.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Device.screenY(getApplicationContext()) / 15));
        colorTopPicker.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Device.screenY(getApplicationContext()) / 15));
        colorBottomPicker.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Device.screenY(getApplicationContext()) / 15));
        theme.addPeer(new Peer<>(new Peer.OnPeer<Theme>() {
            @Override
            public boolean onPeer(Theme theme) {
                pushSwitch.setTextSize((float) (theme.textSize / 1.5));
                displayBreaksSwitch.setTextSize((float) (theme.textSize / 1.5));
                refreshSwitch.setTextSize((float) (theme.textSize / 1.5));
                displayMessagesSwitch.setTextSize((float) (theme.textSize / 1.5));
                devSwitch.setTextSize((float) (theme.textSize / 1.5));
                explainColorA.setTextSize((float) (theme.textSize / 1.5));
                explainColorB.setTextSize((float) (theme.textSize / 1.5));
                explainTextColor.setTextSize((float) (theme.textSize / 1.5));
                explainTextSize.setTextSize((float) (theme.textSize / 1.5));
                pushSwitch.setTextColor(theme.textColor);
                displayBreaksSwitch.setTextColor(theme.textColor);
                refreshSwitch.setTextColor(theme.textColor);
                displayMessagesSwitch.setTextColor(theme.textColor);
                devSwitch.setTextColor(theme.textColor);
                explainColorA.setTextColor(theme.textColor);
                explainColorB.setTextColor(theme.textColor);
                explainTextColor.setTextColor(theme.textColor);
                explainTextSize.setTextColor(theme.textColor);
                return false;
            }
        }));
        settings.addView(displayMessagesSwitch);
        settings.addView(displayBreaksSwitch);
        settings.addView(pushSwitch);
        settings.addView(refreshSwitch);
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

    // TODO register in theme tower
    private ScrollView getSwitcher() {
        LinearLayout all = new LinearLayout(getApplicationContext());
        all.setOrientation(LinearLayout.VERTICAL);
        all.setGravity(Gravity.CENTER);
        all.setPadding(20, 20, 20, 20);
        LinearLayout students = new LinearLayout(getApplicationContext());
        students.setPadding(10, 10, 10, 10);
        students.setOrientation(LinearLayout.VERTICAL);
        students.setGravity(Gravity.CENTER);
        LinearLayout teachersv = new LinearLayout(getApplicationContext());
        teachersv.setPadding(10, 10, 10, 10);
        teachersv.setOrientation(LinearLayout.VERTICAL);
        teachersv.setGravity(Gravity.CENTER);
        students.setPadding(10, 10, 10, 10);
        teachersv.setPadding(10, 10, 10, 10);
        if (!pm.getKeyManager().isKeyLoaded(R.string.preferences_keys_type_teachers)) {
            students.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            all.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        }
        ArrayList<LinearLayout> groups = new ArrayList<>();
        for (final Classroom cr : schedule.getClassrooms()) {
            int grade = cr.getGrade() - Classroom.NINTH_GRADE;
            if (grade < 0) {
                grade = 0;
            }
            Button cls = new Button(getApplicationContext());
            cls.setTextSize(theme.getLast().textSize);
            cls.setGravity(Gravity.CENTER);
            cls.setText(cr.getName());
            cls.setTextColor(theme.getLast().textColor);
            cls.setBackground(Utils.getCoaster(theme.getLast().colorMix, 20, 5));
            cls.setTypeface(Center.getTypeface(getApplicationContext()));
            cls.setLayoutParams(new LinearLayout.LayoutParams(Device.screenX(getApplicationContext()) / 5, (Device.screenY(getApplicationContext()) / 12)));
            cls.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setStudentMode(cr);
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
        if (pm.getKeyManager().isKeyLoaded(R.string.preferences_keys_type_teachers)) {
            for (final Teacher tc : schedule.getTeachers()) {
                Button cls = new Button(getApplicationContext());
                cls.setTextSize(theme.getLast().textSize);
                cls.setGravity(Gravity.CENTER);
                cls.setText(tc.getName());
                cls.setTextColor(theme.getLast().textColor);
                cls.setBackground(Utils.getCoaster(theme.getLast().colorMix, 20, 5));
                cls.setPadding(10, 0, 10, 0);
                cls.setTypeface(Center.getTypeface(getApplicationContext()));
                cls.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (Device.screenY(getApplicationContext()) / 12)));
                teachersv.addView(cls);
                cls.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        setTeacherMode(tc);
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
        // Write to preferences
        pm.getUserManager().set(R.string.preferences_user_favorite_classroom, c.getName());
        pm.getServicesManager().setChannel(c.getGrade());
        // Display corner info
        LinearLayout infoText = new LinearLayout(getApplicationContext());
        infoText.setOrientation(LinearLayout.VERTICAL);
        infoText.setGravity(Gravity.CENTER);
        final RatioView name, day;
        name = new RatioView(getApplicationContext(), 0.9);
        day = new RatioView(getApplicationContext(), 0.7);
        name.setText(c.getName());
        name.setTextColor(theme.getLast().textColor);
        name.setTextSize(theme.getLast().textSize);
        name.setTypeface(Center.getTypeface(getApplicationContext()));
        name.setGravity(Gravity.CENTER);
        day.setText(schedule.getDay());
        day.setTextColor(theme.getLast().textColor);
        day.setTextSize(theme.getLast().textSize);
        day.setTypeface(Center.getTypeface(getApplicationContext()));
        day.setGravity(Gravity.CENTER);
        infoText.addView(name);
        infoText.addView(day);
        info.setView(infoText, 0.8);
        theme.addPeer(new Peer<>(new Peer.OnPeer<Theme>() {
            @Override
            public boolean onPeer(Theme theme) {
                name.setTextSize(theme.textSize);
                name.setTextColor(theme.textColor);
                day.setTextSize(theme.textSize);
                day.setTextColor(theme.textColor);
                return false;
            }
        }));
        displayLessonViews(scheduleForClass(c));
        refreshTheme();
    }

    private void setTeacherMode(Teacher t) {
        currentTeacher = t;
        // Write to preferences
        pm.getUserManager().set(R.string.preferences_user_favorite_teacher, t.getName());
        pm.getServicesManager().setChannel(Classroom.UNKNOWN_GRADE);
        // Write info to corner
        LinearLayout infoText = new LinearLayout(getApplicationContext());
        infoText.setOrientation(LinearLayout.VERTICAL);
        infoText.setGravity(Gravity.CENTER);
        final RatioView name, day;
        name = new RatioView(getApplicationContext(), 0.9);
        day = new RatioView(getApplicationContext(), 0.7);
        name.setText(t.getName());
        name.setTextColor(theme.getLast().textColor);
        name.setTextSize(theme.getLast().textSize);
        name.setTypeface(Center.getTypeface(getApplicationContext()));
        name.setGravity(Gravity.CENTER);
        day.setText(schedule.getDay());
        day.setTextColor(theme.getLast().textColor);
        day.setTextSize(theme.getLast().textSize);
        day.setTypeface(Center.getTypeface(getApplicationContext()));
        day.setGravity(Gravity.CENTER);
        infoText.addView(name);
        infoText.addView(day);
        info.setView(infoText, 0.8);
        theme.addPeer(new Peer<>(new Peer.OnPeer<Theme>() {
            @Override
            public boolean onPeer(Theme theme) {
                name.setTextSize(theme.textSize);
                name.setTextColor(theme.textColor);
                day.setTextSize(theme.textSize);
                day.setTextColor(theme.textColor);
                return false;
            }
        }));
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
                theme.addPeer(new Peer<>(new Peer.OnPeer<Theme>() {
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
                    theme.addPeer(new Peer<>(new Peer.OnPeer<Theme>() {
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

    @Override
    public void onBackPressed() {
        if (mAppView.getDrawer().isOpen()) {
            mAppView.getDrawer().close();
        } else {
            finish();
        }
    }

    private class Theme {
        private int textSize, textColor;
        private int colorTop, colorBottom, colorMix;
        private boolean showBreaks, showMessages;
    }
}
