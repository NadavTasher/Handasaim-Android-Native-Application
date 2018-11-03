package nadav.tasher.handasaim.architecture.app.graphics;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;

import nadav.tasher.handasaim.R;
import nadav.tasher.handasaim.architecture.app.Center;
import nadav.tasher.handasaim.architecture.app.Theme;
import nadav.tasher.handasaim.architecture.appcore.AppCore;
import nadav.tasher.handasaim.architecture.appcore.components.Subject;
import nadav.tasher.lightool.graphics.views.ExpandingView;
import nadav.tasher.lightool.graphics.views.Utils;
import nadav.tasher.lightool.info.Device;

public class LessonView extends ExpandingView {
    private RatioView topText;
    private ImageView currentMark;
    private ArrayList<RatioView> texts = new ArrayList<>();
    private ArrayList<Subject> subjects;
    private Subject subject;
    private Theme currentTheme;
    private int breakHour = -1;


    public LessonView(Context context, Theme theme, Subject subject) {
        super(context);
        this.subject = subject;
        this.currentTheme = theme;
        init();
    }

    public LessonView(Context context, Theme theme, ArrayList<Subject> subjects) {
        super(context);
        this.subjects = subjects;
        this.currentTheme = theme;
        init();
    }

    public LessonView(Context context, Theme theme, int breakHour) {
        super(context);
        this.currentTheme = theme;
        this.breakHour = breakHour;
        init();
    }

    public void refresh() {
        String text = "";
        if (subject != null) {
            text = subject.getHour() + ". " + subject.getName();
            if (currentTheme.showRemainingTime && Center.inRange(Center.currentMinute(), AppCore.getSchool().getStartingMinute(subject), AppCore.getSchool().getEndingMinute(subject))) {
                text += AppCore.Utils.DIVIDER;
                text += (AppCore.getSchool().getEndingMinute(subject) - Center.currentMinute());
                text += " ";
                text += getContext().getResources().getString(R.string.interface_minutes_short);
            }
        } else {
            if (subjects != null) {
                if (!subjects.isEmpty()) {
                    text = subjects.get(0).getHour() + ". " + subjects.get(0).getName();
                    if (currentTheme.showRemainingTime && Center.inRange(Center.currentMinute(), AppCore.getSchool().getEndingMinute(breakHour - 1), AppCore.getSchool().getStartingMinute(breakHour))) {
                        text += AppCore.Utils.DIVIDER;
                        text += (AppCore.getSchool().getEndingMinute(subjects.get(0)) - Center.currentMinute());
                        text += " ";
                        text += getContext().getResources().getString(R.string.interface_minutes_short);
                    }
                }
            } else {
                text = getContext().getResources().getString(R.string.interface_break);
                if (currentTheme.showRemainingTime && Center.inRange(Center.currentMinute(), AppCore.getSchool().getEndingMinute(breakHour - 1), AppCore.getSchool().getStartingMinute(breakHour))) {
                    text += AppCore.Utils.DIVIDER;
                    text += (AppCore.getSchool().getStartingMinute(breakHour) - Center.currentMinute());
                    text += " ";
                    text += getContext().getResources().getString(R.string.interface_minutes_short);
                }
            }
        }
        topText.setText(text);
        refreshCurrent();
    }

    private void refreshCurrent() {
        boolean currentLesson = false;
        if (subject != null) {
            currentLesson = Center.inRange(Center.currentMinute(), AppCore.getSchool().getStartingMinute(subject), AppCore.getSchool().getEndingMinute(subject));
        } else {
            if (subjects != null) {
                if (!subjects.isEmpty()) {
                    currentLesson = Center.inRange(Center.currentMinute(), AppCore.getSchool().getStartingMinute(subjects.get(0)), AppCore.getSchool().getEndingMinute(subjects.get(0)));
                }
            } else {
                currentLesson = Center.inRange(Center.currentMinute(), AppCore.getSchool().getEndingMinute(breakHour - 1), AppCore.getSchool().getStartingMinute(breakHour));
            }
        }
        if (currentLesson) {
            currentMark.setImageDrawable(Center.getCurrentMark(getContext(), currentMark));
        } else {
            currentMark.setImageDrawable(null);
        }
    }

    private Drawable initBackground() {
        int markID;
        boolean markSpecial = false;
        if (subject != null) {
            markSpecial = currentTheme.markPrehours && subject.getHour() == 0;
        } else {
            if (subjects != null) {
                if (!subjects.isEmpty()) {
                    markSpecial = currentTheme.markPrehours && subjects.get(0).getHour() == 0;
                }
            } else {
                markSpecial = false;
            }
        }
        if (markSpecial) {
            markID = R.color.coaster_special_bright;
        } else {
            markID = R.color.coaster_bright;
        }
        return Utils.getCoaster(getContext().getResources().getColor(markID), 32, 5);
    }

    private void init() {
        topText = getText(1);
        LinearLayout bottomLayout = new LinearLayout(getContext());
        bottomLayout.setGravity(Gravity.CENTER);
        bottomLayout.setOrientation(LinearLayout.HORIZONTAL);
        bottomLayout.setLayoutDirection(LAYOUT_DIRECTION_RTL);
        setLayoutDirection(LAYOUT_DIRECTION_RTL);
        FrameLayout topView = new FrameLayout(getContext());
        topView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Device.screenY(getContext()) / 12));
        LinearLayout currentMarkHolder = new LinearLayout(getContext());
        currentMarkHolder.setGravity(Gravity.END | Gravity.TOP);
        currentMarkHolder.setPadding(10, 10, 10, 10);
        currentMark = new ImageView(getContext());
        currentMark.setLayoutParams(new LinearLayout.LayoutParams(Device.screenY(getContext()) / 60, Device.screenY(getContext()) / 60));
        currentMarkHolder.addView(currentMark);
        topView.addView(topText);
        topView.addView(currentMarkHolder);
        RatioView timeText;
        if (subject != null) {
            timeText = getText(Center.generateTime(subject.getHour()), 0.8);
            bottomLayout.addView(getTexts(subject.getTeacherNames()));
        } else {
            if (subjects != null) {
                if (!subjects.isEmpty()) {
                    timeText = getText(Center.generateTime(subjects.get(0).getHour()), 0.8);
                    ArrayList<String> names = new ArrayList<>();
                    for (Subject subject : subjects) {
                        names.add(subject.getClassroom().getName());
                    }
                    bottomLayout.addView(getTexts(names));
                } else {
                    timeText = getText(0.8);
                    bottomLayout.addView(getTexts(new ArrayList<String>()));
                }
            } else {
                RatioView minutesView = getText(AppCore.getSchool().getBreakLength(breakHour - 1, breakHour) + " " + getContext().getResources().getString(R.string.interface_minutes_long), 0.8);
                minutesView.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1));
                minutesView.setGravity(Gravity.CENTER);
                texts.add(minutesView);
                timeText = getText(Center.generateBreakTime(breakHour - 1, breakHour), 0.8);
                bottomLayout.addView(minutesView);
            }
        }
        timeText.setTextDirection(TEXT_DIRECTION_LTR);
        timeText.setGravity(Gravity.CENTER);
        timeText.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Device.screenY(getContext()) / 13, 1));
        bottomLayout.addView(timeText);
        texts.add(timeText);
        topText.setPadding(20, 0, 20, 0);
        topText.setGravity(Gravity.CENTER_VERTICAL | Gravity.START);
        topText.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        texts.add(topText);
        setDuration(200);
        setBackground(initBackground());
        setPadding(20, 25);
        setTop(topView);
        setBottom(bottomLayout);
        refresh();
        getContext().registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                refresh();
            }
        }, new IntentFilter(Intent.ACTION_TIME_TICK));
    }

    public void setTheme(Theme theme) {
        this.currentTheme = theme;
        for (RatioView text : texts) text.setTextColor(theme.textColor);
        for (RatioView text : texts) text.setTextSize(theme.textSize);
    }

    private LinearLayout getTexts(ArrayList<String> strings) {
        LinearLayout textsLayout = new LinearLayout(getContext());
        textsLayout.setGravity(Gravity.CENTER);
        textsLayout.setOrientation(LinearLayout.VERTICAL);
        textsLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
        for (String teacher : strings) {
            RatioView text = getText(teacher, 0.8);
            text.setGravity(Gravity.CENTER);
            text.setEllipsize(TextUtils.TruncateAt.END);
            text.setPadding(30, 0, 30, 0);
            texts.add(text);
            textsLayout.addView(text);
        }
        return textsLayout;
    }

    private RatioView getText(String text, final double textSizeRatio) {
        RatioView tv = new RatioView(getContext(), textSizeRatio);
        tv.setText(text);
        tv.setTextSize(Center.getFontSize(getContext()));
        tv.setTextColor(Center.getTextColor(getContext()));
        tv.setTextDirection(TEXT_DIRECTION_RTL);
        tv.setSingleLine(true);
        return tv;
    }

    private RatioView getText(final double textSizeRatio) {
        RatioView tv = new RatioView(getContext(), textSizeRatio);
        tv.setTextSize(Center.getFontSize(getContext()));
        tv.setTextColor(Center.getTextColor(getContext()));
        tv.setTextDirection(TEXT_DIRECTION_RTL);
        tv.setSingleLine(true);
        return tv;
    }
}
