package nadav.tasher.handasaim.activities;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;

import nadav.tasher.handasaim.R;
import nadav.tasher.handasaim.architecture.app.Center;
import nadav.tasher.handasaim.architecture.app.PreferenceManager;
import nadav.tasher.handasaim.architecture.app.Theme;
import nadav.tasher.handasaim.architecture.app.graphics.LessonView;
import nadav.tasher.handasaim.architecture.app.graphics.MessageBar;
import nadav.tasher.handasaim.architecture.app.graphics.RatioView;
import nadav.tasher.handasaim.architecture.appcore.AppCore;
import nadav.tasher.handasaim.architecture.appcore.components.Classroom;
import nadav.tasher.handasaim.architecture.appcore.components.Schedule;
import nadav.tasher.handasaim.architecture.appcore.components.Subject;
import nadav.tasher.handasaim.architecture.appcore.components.Teacher;
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
    private Corner info;
    private MessageBar messageBar;
    private LinearLayout lessonViewHolder;
    private AppView mAppView;
    private Schedule schedule;
    private HorizontalScrollView menu;
    private ScrollView settings, switcher, share, code, about;
    private int scheduleIndex = 0;
    private int drawerPadding = 30;

    private PreferenceManager pm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initVars();
        loadSchedule();
    }

    @Override
    public Context getApplicationContext() {
        return this;
    }

    private void initVars() {
        pm = new PreferenceManager(getApplicationContext());
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        if (getIntent().getExtras() != null && getIntent().getExtras().containsKey(getResources().getString(R.string.schedule_index))) {
            scheduleIndex = getIntent().getExtras().getInt(getResources().getString(R.string.schedule_index), 0);
        }
    }

    private void refreshTheme() {
        theme.tell(Center.getTheme(getApplicationContext()));
//        Log.i("ColorA", String.format("#%06X", (0xFFFFFF & currentTheme.colorTop)));
//        Log.i("ColorB", String.format("#%06X", (0xFFFFFF & currentTheme.colorBottom)));
//        Log.i("ColorMenu", String.format("#%06X", (0xFFFFFF & currentTheme.menuColor)));
    }

    private void refreshCorner() {
        if (pm.getUserManager().get(R.string.preferences_user_corner_location, getResources().getString(R.string.corner_location_right)).equals(getResources().getString(R.string.corner_location_right))) {
            mAppView.getCornerView().setBottomLeft(null);
            mAppView.getCornerView().setBottomRight(info);
        } else {
            mAppView.getCornerView().setBottomRight(null);
            mAppView.getCornerView().setBottomLeft(info);
        }
    }

    private void loadSchedule() {
        try {
            schedule = pm.getCoreManager().getSchedule(scheduleIndex);
            loadUI();
        } catch (Exception e) {
            Center.exit(this, SplashActivity.class);
        }
    }

    private void loadUI() {
        refreshTheme();
        Log.i("Theme", theme.getLast().toString());
        mAppView = new AppView(this);
        mAppView.setDrawNavigation(false);
        mAppView.getDrawer().setAnimationTime(200);
        info = new Corner(getApplicationContext(), (int) (Device.screenX(getApplicationContext()) / 4.5), Color.TRANSPARENT);
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
                    openDrawer(menu);
                }
            }
        });
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
                mAppView.getDrawer().getDrawerView().setBackground(Utils.getCoaster(theme.menuColor, 48, 10));
                mAppView.getDrawer().setPadding(drawerPadding, drawerPadding, drawerPadding, drawerPadding);
//                mAppView.getDrawer().getDrawerView().setPadding(0, drawerPadding, 0, drawerPadding);
                if (theme.showMessages && schedule.getMessages().size() != 0) {
                    messageBar.setVisibility(View.VISIBLE);
                } else {
                    messageBar.setVisibility(View.GONE);
                }
                return false;
            }
        }));
        refreshCorner();
        loadDrawers();
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

    private void loadDrawers() {
        menu = assembleMenuDrawer();
        settings = assembleSettings();
        share = assembleShare();
        switcher = assembleSwitcher();
        code = assembleCode();
        about = assembleAbout();
    }

    private void setStudentMode(Classroom c) {
        currentClass = c;
        // Write to preferences
        pm.getUserManager().set(R.string.preferences_user_favorite_classroom, c.getName());
        pm.getCoreManager().setMode(R.string.core_mode_student);
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
        name.setGravity(Gravity.CENTER);
        day.setText(schedule.getDay());
        day.setTextColor(theme.getLast().textColor);
        day.setTextSize(theme.getLast().textSize);
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
        // Write to preferences
        pm.getUserManager().set(R.string.preferences_user_favorite_teacher, t.getName());
        pm.getCoreManager().setMode(R.string.core_mode_teacher);
        // Write info to corner
        LinearLayout infoText = new LinearLayout(getApplicationContext());
        infoText.setOrientation(LinearLayout.VERTICAL);
        infoText.setGravity(Gravity.CENTER);
        final RatioView name, day;
        name = new RatioView(getApplicationContext(), 0.9);
        day = new RatioView(getApplicationContext(), 0.7);
        name.setText(Center.trimName(t.getName().split("\\s")[0]));
        name.setTextColor(theme.getLast().textColor);
        name.setTextSize(theme.getLast().textSize);
        name.setGravity(Gravity.CENTER);
        day.setText(schedule.getDay());
        day.setTextColor(theme.getLast().textColor);
        day.setTextSize(theme.getLast().textSize);
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
                for (LessonView l : lessonViews) {
                    l.setTheme(theme);
                }
                return false;
            }
        }));
        for (LessonView l : lessonViews) {
            lessonViewHolder.addView(l);
        }
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
        v.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        //        Utils.measure(v);
        v.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        double percent = ((double) v.getMeasuredHeight() + 2 * (double) drawerPadding) / ((double) Device.screenY(getApplicationContext()));
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
            String favoriteTeacherName = pm.getUserManager().get(R.string.preferences_user_favorite_teacher, null);
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

    private ScrollView assembleShare() {
        ScrollView sv = new ScrollView(getApplicationContext());
        sv.setOverScrollMode(ScrollView.OVER_SCROLL_NEVER);
        sv.setFillViewport(true);
        LinearLayout shareView = new LinearLayout(getApplicationContext());
        shareView.setGravity(Gravity.CENTER);
        shareView.setOrientation(LinearLayout.VERTICAL);
        shareView.setPadding(20, 20, 20, 20);
        final Switch shareMessageSwitch = new Switch(getApplicationContext());
        shareMessageSwitch.setPadding(10, 0, 10, 0);
        shareMessageSwitch.setText(R.string.interface_share_messages);
        shareMessageSwitch.setTextSize((int) (double) (theme.getLast().textSize * 0.8));
        shareMessageSwitch.setTextColor(theme.getLast().textColor);
        shareMessageSwitch.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        shareMessageSwitch.setEnabled(!schedule.getMessages().isEmpty());
        final Switch shareGradeSwitch = new Switch(getApplicationContext());
        shareGradeSwitch.setPadding(10, 0, 10, 0);
        shareGradeSwitch.setText(R.string.interface_share_grade);
        shareGradeSwitch.setTextSize((int) (double) (theme.getLast().textSize * 0.8));
        shareGradeSwitch.setTextColor(theme.getLast().textColor);
        shareGradeSwitch.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        final Button shareB = new Button(getApplicationContext());
        shareB.setText(R.string.interface_share_button);
        shareB.setBackground(Utils.getCoaster(theme.getLast().colorMix, 16, 10));
        shareB.setTextColor(theme.getLast().textColor);
        shareB.setTextSize((int) (double) (theme.getLast().textSize * 0.7));
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
                            message.append(classroomToString(classroom)).append("\n");
                        }
                    }
                } else {
                    message.append(currentClass.getName()).append(" (").append(schedule.getDay()).append(")").append("\n");
                    message.append(classroomToString(currentClass)).append("\n");
                }
                if (shareMessageSwitch.isChecked()) {
                    message.append("הודעות:\n");
                    for (int i = 0; i < schedule.getMessages().size(); i++) {
                        message.append(i + 1).append(". ").append(schedule.getMessages().get(i)).append("\n");
                    }
                }
                Center.share(getApplicationContext(), message.toString());
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

    private ScrollView assembleSettings() {
        ScrollView sv = new ScrollView(getApplicationContext());
//        sv.setPadding(10, 10, 10, 10);
        LinearLayout settings = new LinearLayout(getApplicationContext());
        settings.setOrientation(LinearLayout.VERTICAL);
        settings.setGravity(Gravity.START);
        settings.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        settings.setPadding(20, 20, 20, 20);
        final Switch cornerLocation = new Switch(getApplicationContext()), displayRemainingTime = new Switch(getApplicationContext()), markPrehourSwitch = new Switch(getApplicationContext()), displayMessagesSwitch = new Switch(getApplicationContext()), displayBreaksSwitch = new Switch(getApplicationContext());
        markPrehourSwitch.setText(R.string.interface_settings_mark_prehour);
        displayMessagesSwitch.setText(R.string.interface_settings_messages);
        displayBreaksSwitch.setText(R.string.interface_settings_breaks);
        displayRemainingTime.setText(R.string.interface_settings_remaining_time);
        markPrehourSwitch.setChecked(pm.getUserManager().get(R.string.preferences_user_mark_prehour, getResources().getBoolean(R.bool.default_mark_prehour)));
        displayMessagesSwitch.setChecked(pm.getUserManager().get(R.string.preferences_user_display_messages, getResources().getBoolean(R.bool.default_display_messages)));
        displayBreaksSwitch.setChecked(pm.getUserManager().get(R.string.preferences_user_display_breaks, getResources().getBoolean(R.bool.default_display_messages)));
        displayRemainingTime.setChecked(pm.getUserManager().get(R.string.preferences_user_display_remaining_time, getResources().getBoolean(R.bool.default_display_remaining_time)));
        cornerLocation.setChecked(pm.getUserManager().get(R.string.preferences_user_corner_location, getResources().getString(R.string.corner_location_right)).equals(getResources().getString(R.string.corner_location_right)));
        markPrehourSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                pm.getUserManager().set(R.string.preferences_user_mark_prehour, isChecked);
                refreshTheme();
                loadContent();
            }
        });
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
        displayRemainingTime.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                pm.getUserManager().set(R.string.preferences_user_display_remaining_time, isChecked);
                refreshTheme();
            }
        });
        if (cornerLocation.isChecked()) {
            cornerLocation.setText(getResources().getString(R.string.interface_corner_choose_right));
        } else {
            cornerLocation.setText(getResources().getString(R.string.interface_corner_choose_left));
        }
        cornerLocation.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    compoundButton.setText(getResources().getString(R.string.interface_corner_choose_right));
                    pm.getUserManager().set(R.string.preferences_user_corner_location, getResources().getString(R.string.corner_location_right));
                } else {
                    compoundButton.setText(getResources().getString(R.string.interface_corner_choose_left));
                    pm.getUserManager().set(R.string.preferences_user_corner_location, getResources().getString(R.string.corner_location_left));
                }
                refreshCorner();
            }
        });
        final TextView explainTextSize = new TextView(getApplicationContext());
        explainTextSize.setText(R.string.interface_settings_choose_size_text);
        explainTextSize.setGravity(Gravity.CENTER);
        final TextView explainTextColor = new TextView(getApplicationContext());
        explainTextColor.setText(R.string.interface_settings_choose_color_text);
        explainTextColor.setGravity(Gravity.CENTER);
        final TextView explainColorA = new TextView(getApplicationContext());
        explainColorA.setText(R.string.interface_settings_choose_color_top);
        explainColorA.setGravity(Gravity.CENTER);
        final TextView explainColorB = new TextView(getApplicationContext());
        explainColorB.setText(R.string.interface_settings_choose_color_bottom);
        explainColorB.setGravity(Gravity.CENTER);
        final TextView explainColorMenu = new TextView(getApplicationContext());
        explainColorMenu.setText(R.string.interface_settings_choose_color_menu);
        explainColorMenu.setGravity(Gravity.CENTER);
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
        ColorPicker menuColorPicker = new ColorPicker(getApplicationContext());
        menuColorPicker.setColor(theme.getLast().menuColor);
        menuColorPicker.setOnColor(new ColorPicker.OnColorChanged() {
            @Override
            public void onColorChange(int color) {
                pm.getUserManager().set(R.string.preferences_user_color_menu, color);
                refreshTheme();
            }
        });
        textColorPicker.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Device.screenY(getApplicationContext()) / 25));
        colorTopPicker.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Device.screenY(getApplicationContext()) / 25));
        colorBottomPicker.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Device.screenY(getApplicationContext()) / 25));
        menuColorPicker.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Device.screenY(getApplicationContext()) / 25));
        theme.addPeer(new Peer<>(new Peer.OnPeer<Theme>() {
            @Override
            public boolean onPeer(Theme theme) {
                displayBreaksSwitch.setTextSize((float) (theme.textSize / 1.5));
                displayMessagesSwitch.setTextSize((float) (theme.textSize / 1.5));
                cornerLocation.setTextSize((float) (theme.textSize / 1.5));
                explainColorA.setTextSize((float) (theme.textSize / 1.5));
                explainColorB.setTextSize((float) (theme.textSize / 1.5));
                explainTextColor.setTextSize((float) (theme.textSize / 1.5));
                explainTextSize.setTextSize((float) (theme.textSize / 1.5));
                explainColorMenu.setTextSize((float) (theme.textSize / 1.5));
                markPrehourSwitch.setTextSize((float) (theme.textSize / 1.5));
                displayRemainingTime.setTextSize((float) (theme.textSize / 1.5));
                displayBreaksSwitch.setTextColor(theme.textColor);
                displayMessagesSwitch.setTextColor(theme.textColor);
                displayRemainingTime.setTextColor(theme.textColor);
                markPrehourSwitch.setTextColor(theme.textColor);
                cornerLocation.setTextColor(theme.textColor);
                explainColorA.setTextColor(theme.textColor);
                explainColorB.setTextColor(theme.textColor);
                explainTextColor.setTextColor(theme.textColor);
                explainTextSize.setTextColor(theme.textColor);
                explainColorMenu.setTextColor(theme.textColor);
                return false;
            }
        }));
        settings.addView(displayMessagesSwitch);
        settings.addView(displayBreaksSwitch);
        settings.addView(displayRemainingTime);
        settings.addView(markPrehourSwitch);
        settings.addView(cornerLocation);
        settings.addView(explainTextSize);
        settings.addView(fontSizeSeekBar);
        settings.addView(explainTextColor);
        settings.addView(textColorPicker);
        settings.addView(explainColorA);
        settings.addView(colorTopPicker);
        settings.addView(explainColorB);
        settings.addView(colorBottomPicker);
        settings.addView(explainColorMenu);
        settings.addView(menuColorPicker);
        sv.addView(settings);
        sv.setVerticalScrollBarEnabled(false);
        return sv;
    }

    private ScrollView assembleSwitcher() {
        LinearLayout all = new LinearLayout(getApplicationContext());
        all.setOrientation(LinearLayout.VERTICAL);
        all.setGravity(Gravity.CENTER);
        all.setPadding(20, 0, 20, 0);
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
            final Button cls = new Button(getApplicationContext());
            cls.setGravity(Gravity.CENTER);
            cls.setText(cr.getName());
            cls.setTextColor(theme.getLast().textColor);
            cls.setBackground(Utils.getCoaster(theme.getLast().colorMix, 20, 5));
            cls.setTextSize(theme.getLast().textSize);
            cls.setLayoutParams(new LinearLayout.LayoutParams(Device.screenX(getApplicationContext()) / 5, (Device.screenY(getApplicationContext()) / 12)));
            cls.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mAppView.getDrawer().close();
                    setStudentMode(cr);
                }
            });
            theme.addPeer(new Peer<>(new Peer.OnPeer<Theme>() {
                @Override
                public boolean onPeer(Theme theme) {
                    cls.setTextColor(theme.textColor);
                    cls.setBackground(Utils.getCoaster(theme.colorMix, 20, 5));
                    cls.setTextSize(theme.textSize);
                    cls.setLayoutParams(new LinearLayout.LayoutParams(Device.screenX(getApplicationContext()) / 5, (Device.screenY(getApplicationContext()) / 12)));
                    return false;
                }
            }));
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
                final Button cls = new Button(getApplicationContext());
                cls.setTextSize(theme.getLast().textSize);
                cls.setGravity(Gravity.CENTER);
                cls.setText(tc.getName());
                cls.setTextColor(theme.getLast().textColor);
                cls.setBackground(Utils.getCoaster(theme.getLast().colorMix, 20, 5));
                cls.setPadding(10, 0, 10, 0);
                cls.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (Device.screenY(getApplicationContext()) / 12)));
                teachersv.addView(cls);
                cls.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        setTeacherMode(tc);
                    }
                });
                theme.addPeer(new Peer<>(new Peer.OnPeer<Theme>() {
                    @Override
                    public boolean onPeer(Theme theme) {
                        cls.setTextColor(theme.textColor);
                        cls.setBackground(Utils.getCoaster(theme.colorMix, 20, 5));
                        cls.setTextSize(theme.textSize);
                        cls.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (Device.screenY(getApplicationContext()) / 12)));
                        return false;
                    }
                }));
            }
            all.addView(teachersv);
        }
        ScrollView sv = new ScrollView(getApplicationContext());
        sv.addView(all);
        sv.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        sv.setFillViewport(true);
        return sv;
    }

    private ScrollView assembleCode() {
        ScrollView sv = new ScrollView(getApplicationContext());
        sv.setOverScrollMode(ScrollView.OVER_SCROLL_NEVER);
        sv.setVerticalScrollBarEnabled(false);
        sv.setFillViewport(true);
        LinearLayout ll = Center.getCodeEntering(this);
        sv.addView(ll);
        return sv;
    }

    private ScrollView assembleAbout() {
        ScrollView sv = new ScrollView(getApplicationContext());
        sv.setOverScrollMode(ScrollView.OVER_SCROLL_NEVER);
        sv.setFillViewport(true);
        LinearLayout aboutView = new LinearLayout(getApplicationContext());
        aboutView.setGravity(Gravity.CENTER);
        aboutView.setOrientation(LinearLayout.VERTICAL);
        aboutView.setPadding(20, 20, 20, 20);
        RatioView title = new RatioView(getApplicationContext(), 0.9);
        RatioView message = new RatioView(getApplicationContext(), 0.7);
        RatioView enterCodes = new RatioView(getApplicationContext(), 0.7);
        enterCodes.setText(R.string.interface_enter_codes);
        title.setText(R.string.app_name);
        String messageText = "Made By Nadav Tasher.\nVersion: " +
                Device.getVersionName(getApplicationContext(), getApplicationContext().getPackageName()) +
                " (" +
                Device.getVersionCode(getApplicationContext(), getApplicationContext().getPackageName()) +
                ")" +
                "\n" +
                "AppCore v" +
                AppCore.APPCORE_VERSION;
        message.setText(messageText);
        title.setTextSize(theme.getLast().textSize);
        title.setTextColor(theme.getLast().textColor);
        title.setGravity(Gravity.CENTER);
        title.setPadding(0, 30, 0, 30);
        message.setTextSize(theme.getLast().textSize);
        message.setTextColor(theme.getLast().textColor);
        message.setGravity(Gravity.CENTER);
        message.setPadding(0, 30, 0, 30);
        enterCodes.setTextSize(theme.getLast().textSize);
        enterCodes.setTextColor(theme.getLast().textColor);
        enterCodes.setGravity(Gravity.CENTER);
        enterCodes.setBackground(Utils.getCoaster(getResources().getColor(R.color.coaster_bright), 20, 10));
        enterCodes.setPadding(0, 40, 0, 40);
        enterCodes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDrawer(code);
            }
        });
        aboutView.addView(title);
        aboutView.addView(message);
        aboutView.addView(enterCodes);
        sv.addView(aboutView);
        return sv;
    }

    private HorizontalScrollView assembleMenuDrawer() {
        HorizontalScrollView menuDrawer = new HorizontalScrollView(getApplicationContext());
        menuDrawer.setOverScrollMode(ScrollView.OVER_SCROLL_NEVER);
        menuDrawer.setHorizontalScrollBarEnabled(false);
        LinearLayout menu = new LinearLayout(getApplicationContext());
        menu.setOrientation(LinearLayout.HORIZONTAL);
        menu.setGravity(Gravity.CENTER);
        menuDrawer.setFillViewport(true);
        final int size = (int) (Device.screenX(getApplicationContext()) / 6.5);
        final FrameLayout shareIcon, classroomIcon, icon, timetraverIcon, settingsIcon;
        shareIcon = generateImageView(R.drawable.ic_share, size);
        classroomIcon = generateImageView(R.drawable.ic_class, size);
        icon = generateImageView(R.drawable.ic_icon, size);
        timetraverIcon = generateImageView(R.drawable.ic_timetravel, size);
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
        timetraverIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Center.exit(HomeActivity.this, TimeTravelActivity.class);
            }
        });
        timetraverIcon.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Center.exit(HomeActivity.this, SplashActivity.class);
                return false;
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
                openDrawer(about);
            }
        });
        menu.addView(shareIcon);
        menu.addView(classroomIcon);
        menu.addView(icon);
        menu.addView(timetraverIcon);
        menu.addView(settingsIcon);
        menu.setPadding(10, 10, 10, 10);
        menu.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        menuDrawer.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        menuDrawer.addView(menu);
        return menuDrawer;
    }

    private FrameLayout generateImageView(int drawable, int size) {
        FrameLayout fl = new FrameLayout(getApplicationContext());
        fl.setPadding(20, 20, 20, 20);
        fl.setForegroundGravity(Gravity.CENTER);
        fl.setLayoutParams(new LinearLayout.LayoutParams(size, size, 1));
        final ImageView iv = new ImageView(getApplicationContext());
        iv.setImageDrawable(getDrawable(drawable));
        //        iv.setScaleType(ImageView.ScaleType.FIT_XY);
        iv.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        fl.addView(iv);
        return fl;
    }

    private String classroomToString(Classroom classroom) {
        StringBuilder export = new StringBuilder();
        for (Subject subject : classroom.getSubjects()) {
            String text = subject.getHour() + ". " + subject.getName();
            if (subject.getName() != null && !subject.getName().isEmpty()) {
                export.append(text).append("\n");
            }
        }
        return export.toString();
    }

    private ArrayList<LessonView> scheduleForClass(final Classroom classroom) {
        ArrayList<LessonView> views = new ArrayList<>();
        for (Subject s : classroom.getSubjects()) {
            if (s.getName() != null && !s.getName().isEmpty()) {
                if (AppCore.getSchool().getBreakLength(s.getHour() - 1, s.getHour()) > 0) {
                    final LessonView breakView = new LessonView(this, theme.getLast(), s.getHour());
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
                views.add(new LessonView(this, theme.getLast(), s));
            }
        }
        return views;
    }

    private ArrayList<LessonView> scheduleForTeacher(final Teacher teacher) {
        ArrayList<LessonView> views = new ArrayList<>();
        for (int h = 0; h <= 12; h++) {
            ArrayList<Subject> subjects = new ArrayList<>();
            for (Subject subject : teacher.getSubjects()) {
                if (subject.getHour() == h) {
                    subjects.add(subject);
                }
            }
            if (!subjects.isEmpty()) {
                if (AppCore.getSchool().getBreakLength(h - 1, h) > 0) {
                    final LessonView breakView = new LessonView(this, theme.getLast(), h);
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
//                LessonView subject = new LessonView(getApplicationContext(), markType, h + ". " + subjectName + " (" + grades + ")", generateTime(h), classroomNames);
                views.add(new LessonView(this, theme.getLast(), subjects));
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
}
