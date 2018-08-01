package nadav.tasher.handasaim.architecture.app.graphics;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;

import nadav.tasher.handasaim.architecture.app.Center;
import nadav.tasher.handasaim.architecture.appcore.AppCore;
import nadav.tasher.handasaim.values.Values;
import nadav.tasher.lightool.graphics.views.ExpandingView;
import nadav.tasher.lightool.graphics.views.Utils;
import nadav.tasher.lightool.info.Device;

public class LessonView extends FrameLayout {
    static final String rtlMark = "\u200F";
    private String topText;
    private ArrayList<String> bottomText;
    private int schoolHour, hour1, hour2;
    private RatioView top, time;
    private ArrayList<RatioView> bottomTextViews=new ArrayList<>();
    private LinearLayout topLayout, bottomTextLayout, bottomLayout;

    private ExpandingView expandingView;

    public LessonView(Context c) {
        super(c);
    }

    public LessonView(Context context, int schoolHour, String topText, ArrayList<String> bottomText) {
        super(context);
        this.topText = rtlMark + topText;
        if (bottomText != null) {
            this.bottomText = rtl(bottomText);
        }
        this.schoolHour = schoolHour;
        init();
    }

    public LessonView(Context context, int hour1, int hour2, String topText) {
        super(context);
        this.topText = rtlMark + topText;
        this.schoolHour = -1;
        this.hour1 = hour1;
        this.hour2 = hour2;
        this.time = getText(AppCore.convertMinuteToTime(AppCore.getStartMinute(hour2)) + " - " + AppCore.convertMinuteToTime(AppCore.getEndMinute(hour1)), 0.8);
        init();
    }

    private ArrayList<String> rtl(ArrayList<String> input) {
        ArrayList<String> output = new ArrayList<>();
        for (String s : input) {
            output.add(rtlMark + s);
        }
        return output;
    }

    private Drawable initBackground() {
        Calendar c = Calendar.getInstance();
        int minute = c.get(Calendar.MINUTE);
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minuteOfDay = (hour * 60) + minute;
        if (schoolHour < 0) {
            if (minuteOfDay >= AppCore.getEndMinute(hour1) && minuteOfDay < AppCore.getStartMinute(hour2)) {
                return Utils.getCoaster(Values.classCoasterMarkColor, 32, 5);
            } else {
                return Utils.getCoaster(Values.classCoasterColor, 32, 5);
            }
        } else {
            if (minuteOfDay >= AppCore.getStartMinute(schoolHour) && minuteOfDay < AppCore.getEndMinute(schoolHour)) {
                return Utils.getCoaster(Values.classCoasterMarkColor, 32, 5);
            } else {
                return Utils.getCoaster(Values.classCoasterColor, 32, 5);
            }
        }
    }

    private void init() {
        setLayoutDirection(LAYOUT_DIRECTION_RTL);
        if (schoolHour < 0) {
            top = getText(topText, 1);
        } else {
            top = getText(schoolHour + ". " + topText, 1);
        }
        if (time == null)
            time = getText(AppCore.convertMinuteToTime(AppCore.getEndMinute(schoolHour)) + " - " + AppCore.convertMinuteToTime(AppCore.getStartMinute(schoolHour)), 0.8);
        bottomTextLayout = new LinearLayout(getContext());
        topLayout = new LinearLayout(getContext());
        bottomLayout = new LinearLayout(getContext());
        topLayout.setGravity(Gravity.CENTER);
        bottomTextLayout.setGravity(Gravity.CENTER);
        bottomLayout.setGravity(Gravity.CENTER);
        topLayout.setOrientation(LinearLayout.HORIZONTAL);
        bottomLayout.setOrientation(LinearLayout.HORIZONTAL);
        bottomTextLayout.setOrientation(LinearLayout.VERTICAL);
        topLayout.setLayoutDirection(LAYOUT_DIRECTION_RTL);
        bottomLayout.setLayoutDirection(LAYOUT_DIRECTION_RTL);
        bottomTextLayout.setLayoutDirection(LAYOUT_DIRECTION_RTL);
        time.setGravity(Gravity.CENTER);
        top.setGravity(Gravity.CENTER_VERTICAL | Gravity.START);
        top.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        topLayout.addView(top);
        bottomLayout.setPadding(0, 10, 0, 10);
        bottomTextLayout.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1));
        time.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1));
        bottomLayout.addView(bottomTextLayout);
        bottomLayout.addView(time);
        if (bottomText != null) {
            if (bottomText.size() == 1) {
                RatioView bottomTextView = getText(bottomText.get(0), 0.8);
                bottomTextView.setGravity(Gravity.CENTER);
                bottomTextView.setEllipsize(TextUtils.TruncateAt.END);
                bottomTextViews.add(bottomTextView);
                bottomTextLayout.addView(bottomTextView);
            } else {
                for (int i = 0; i < bottomText.size(); i++) {
                    RatioView bottomTextView = getText((i + 1) + ". " + bottomText.get(i), 0.6);
                    bottomTextView.setGravity(Gravity.CENTER_VERTICAL | Gravity.START);
                    bottomTextView.setEllipsize(TextUtils.TruncateAt.END);
                    bottomTextView.setBackground(Utils.getCoaster(Center.alpha(128, Center.getColorA(getContext())), 16, 10));
                    bottomTextView.setPadding(40, 0, 30, 0);
                    bottomTextView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Device.screenY(getContext()) / 16));
                    bottomTextViews.add(bottomTextView);
                    bottomTextLayout.addView(bottomTextView);
                }
            }
        }
        bottomLayout.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        if (bottomLayout.getMeasuredHeight() < Device.screenY(getContext()) / 10)
            bottomLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Device.screenY(getContext()) / 10));
        expandingView = new ExpandingView(getContext(), initBackground(), 500, Device.screenY(getContext()) / 10, topLayout, bottomLayout);
        addView(expandingView);
    }

    public void setButtonColor(int color){
        if(bottomTextViews.size()>1) {
            for (int t = 0; t < bottomTextViews.size(); t++) {
                RatioView current = bottomTextViews.get(t);
                current.setBackground(Utils.getCoaster(Center.alpha(128, color), 16, 10));
                current.setPadding(40, 0, 30, 0);
            }
        }
    }

    public void setTextColor(int color){
        top.setTextColor(color);
        time.setTextColor(color);
        for(int t=0;t<bottomTextViews.size();t++){
            RatioView current=bottomTextViews.get(t);
            current.setTextColor(color);
        }
    }

    public void setTextSize(int size){
        top.setTextSize(size);
        time.setTextSize(size);
        for(int t=0;t<bottomTextViews.size();t++){
            RatioView current=bottomTextViews.get(t);
            current.setTextSize(size);
        }
    }

    private RatioView getText(String text, final double textSizeRatio) {
        RatioView tv = new RatioView(getContext(),textSizeRatio);
        tv.setText(text);
        tv.setTextSize(Center.getFontSize(getContext()));
        tv.setTypeface(Center.getTypeface(getContext()));
        tv.setTextColor(Center.getTextColor(getContext()));
        tv.setTextDirection(TEXT_DIRECTION_RTL);
        tv.setSingleLine(true);
        return tv;
    }
}
