package nadav.tasher.handasaim.architecture.app.graphics;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
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

public class LessonView extends FrameLayout {
    public static final int MARK_TYPE_NORMAL = 0;
    public static final int MARK_TYPE_PRESSED = 1;
    public static final int MARK_TYPE_SPECIAL_NORMAL = 2;
    public static final int MARK_TYPE_SPECIAL_PRESSED = 3;
    private Activity activity;
    private RatioView topView, timeView;
    private int markType;
    private ArrayList<String> bottomTexts;
    private ArrayList<RatioView> texts = new ArrayList<>();
    private Subject subject;
    private Theme currentTheme;
    private BroadcastReceiver timeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null && intent.getAction() != null && intent.getAction().equals(Intent.ACTION_TIME_TICK)) {
                refreshTopText();
            }
        }
    };

    public LessonView(Activity activity, Subject subject, Theme theme) {
        super(activity);
        this.activity = activity;
        this.subject = subject;
        this.currentTheme = theme;
        init();
    }

    public LessonView(Activity activity, int breakHour, Theme theme) {
        super(activity);
        this.activity = activity;
        this.currentTheme = theme;
    }

    private void refreshTopText() {
        String topText = subject.getName();
        if (currentTheme.showRemainingTime) {
            topText += AppCore.Utils.DIVIDER;
            topText += AppCore.getSchool().getEndingMinute(subject) - AppCore.getSchool().getStartingMinute(subject) - (AppCore.getSchool().getEndingMinute(subject) - Center.currentMinute());
            topText += getContext().getResources().getString(R.string.interface_minutes);
        }
        topView.setText(topText);
    }

    private Drawable initBackground() {
        int markID = R.color.coaster_bright;
        if (Center.inRange(Center.currentMinute(), AppCore.getSchool().getStartingMinute(subject), AppCore.getSchool().getEndingMinute(subject)))
            switch (markType) {
                case MARK_TYPE_NORMAL:
                    markID = R.color.coaster_bright;
                    break;
                case MARK_TYPE_PRESSED:
                    markID = R.color.coaster_bright;
                    break;
                case MARK_TYPE_SPECIAL_NORMAL:
                    markID = R.color.coaster_special_bright;
                    break;
                case MARK_TYPE_SPECIAL_PRESSED:
                    markID = R.color.coaster_special_dark;
                    break;
            }
        return Utils.getCoaster(getContext().getResources().getColor(markID), 32, 5);
    }

    private void init() {
        setLayoutDirection(LAYOUT_DIRECTION_RTL);
        // Setup text view
        topView = getText(1);
        timeView = getText(Center.generateTime(subject.getHour()), 0.8);
        topView.setPadding(20, 0, 20, 0);
        timeView.setTextDirection(TEXT_DIRECTION_LTR);
        texts.add(topView);
        texts.add(timeView);
        // Continue setup
        topView.setGravity(Gravity.CENTER_VERTICAL | Gravity.START);
        timeView.setGravity(Gravity.CENTER);
        topView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Device.screenY(getContext()) / 12));
        timeView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Device.screenY(getContext()) / 13, 1));
        // Bottom setup
        LinearLayout bottomLayout = new LinearLayout(getContext());
        bottomLayout.setGravity(Gravity.CENTER);
        bottomLayout.setOrientation(LinearLayout.HORIZONTAL);
        bottomLayout.setLayoutDirection(LAYOUT_DIRECTION_RTL);
        bottomLayout.addView(getTexts(bottomTexts));
        bottomLayout.addView(timeView);
        ExpandingView ev = new ExpandingView(getContext());
        ev.setDuration(200);
        ev.setBackground(initBackground());
        ev.setPadding(20, 25);
        ev.setTop(topView);
        ev.setBottom(bottomLayout);
        addView(ev);
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
            text.setTypeface(Center.getTypeface(getContext()), Typeface.ITALIC);
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
        tv.setTypeface(Center.getTypeface(getContext()));
        tv.setTextColor(Center.getTextColor(getContext()));
        tv.setTextDirection(TEXT_DIRECTION_RTL);
        tv.setSingleLine(true);
        return tv;
    }

    private RatioView getText(final double textSizeRatio) {
        RatioView tv = new RatioView(getContext(), textSizeRatio);
        tv.setTextSize(Center.getFontSize(getContext()));
        tv.setTypeface(Center.getTypeface(getContext()));
        tv.setTextColor(Center.getTextColor(getContext()));
        tv.setTextDirection(TEXT_DIRECTION_RTL);
        tv.setSingleLine(true);
        return tv;
    }
}
