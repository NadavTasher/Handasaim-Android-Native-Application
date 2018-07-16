package nadav.tasher.handasaim.architecture.app.graphics;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;

import nadav.tasher.handasaim.architecture.app.Center;
import nadav.tasher.handasaim.architecture.appcore.AppCore;
import nadav.tasher.handasaim.values.Values;
import nadav.tasher.lightool.graphics.views.appview.AppView;
import nadav.tasher.lightool.info.Device;
import nadav.tasher.lightool.parts.Peer;
import nadav.tasher.lightool.parts.Tower;

public class LessonView extends LinearLayout {
    static final String rtlMark = "\u200F";
    private String topText;
    private ArrayList<String> bottomText;
    private int schoolHour, hour1, hour2;
    private TextView top, time;
    private LinearLayout topLayout, bottomLayout, bottomTextLayout;
    private Tower<Integer> textColor = new Tower<>(), textSize = new Tower<>();
    private Tower<AppView.Gradient> color = new Tower<>();
    private boolean isOpened = false, isAnimating = false;

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

    public Tower<AppView.Gradient> getColor() {
        return color;
    }

    public Tower<Integer> getTextSize() {
        return textSize;
    }

    public Tower<Integer> getTextColor() {
        return textColor;
    }

    private ArrayList<String> rtl(ArrayList<String> input) {
        ArrayList<String> output = new ArrayList<>();
        for (String s : input) {
            output.add(rtlMark + s);
        }
        return output;
    }

    private void initBackground() {
        Calendar c = Calendar.getInstance();
        int minute = c.get(Calendar.MINUTE);
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minuteOfDay = (hour * 60) + minute;
        if (schoolHour < 0) {
            if (minuteOfDay >= AppCore.getEndMinute(hour1) && minuteOfDay < AppCore.getStartMinute(hour2)) {
                setBackground(Center.getCoaster(Values.classCoasterMarkColor, 32));
            } else {
                setBackground(Center.getCoaster(Values.classCoasterColor, 32));
            }
        } else {
            if (minuteOfDay >= AppCore.getStartMinute(schoolHour) && minuteOfDay < AppCore.getEndMinute(schoolHour)) {
                setBackground(Center.getCoaster(Values.classCoasterMarkColor, 32));
            } else {
                setBackground(Center.getCoaster(Values.classCoasterColor, 32));
            }
        }
    }

    private void init() {
        initBackground();
        setOrientation(VERTICAL);
        setLayoutDirection(LAYOUT_DIRECTION_RTL);
        setGravity(Gravity.START);
        setPadding(30, 20, 30, 20);
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
        bottomLayout.setGravity(Gravity.CENTER);
        topLayout.setOrientation(LinearLayout.HORIZONTAL);
        bottomLayout.setOrientation(LinearLayout.HORIZONTAL);
        bottomTextLayout.setOrientation(LinearLayout.VERTICAL);
        topLayout.setLayoutDirection(LAYOUT_DIRECTION_RTL);
        bottomLayout.setLayoutDirection(LAYOUT_DIRECTION_RTL);
        bottomTextLayout.setLayoutDirection(LAYOUT_DIRECTION_RTL);
        time.setGravity(Gravity.CENTER);
        top.setGravity(Gravity.CENTER_VERTICAL | Gravity.START);
        topLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Device.screenY(getContext()) / 10));
        top.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        time.setLayoutParams(new LinearLayout.LayoutParams(Device.screenX(getContext()) / 2 - getPaddingLeft(), ViewGroup.LayoutParams.MATCH_PARENT));
        bottomTextLayout.setLayoutParams(new LinearLayout.LayoutParams(Device.screenX(getContext()) / 2 - getPaddingRight(), ViewGroup.LayoutParams.MATCH_PARENT));
        topLayout.addView(top);
        bottomLayout.addView(bottomTextLayout);
        bottomLayout.addView(time);
        if (bottomText != null) {
            if (bottomText.size() == 1) {
                TextView bottomTextView = getText(bottomText.get(0), 0.8);
                bottomTextView.setGravity(Gravity.CENTER);
                bottomTextView.setEllipsize(TextUtils.TruncateAt.END);
                bottomTextLayout.addView(bottomTextView);
            } else {
                for (int i = 0; i < bottomText.size(); i++) {
                    final TextView bottomTextView = getText((i + 1) + ". " + bottomText.get(i), 0.6);
                    bottomTextView.setGravity(Gravity.CENTER_VERTICAL | Gravity.START);
                    bottomTextView.setEllipsize(TextUtils.TruncateAt.END);
                    bottomTextView.setBackground(Center.getCoaster(Center.alpha(128, Center.getColorA(getContext())), 16));
                    bottomTextView.setPadding(30, 0, 30, 0);
                    color.addPeer(new Peer<AppView.Gradient>(new Peer.OnPeer<AppView.Gradient>() {
                        @Override
                        public boolean onPeer(AppView.Gradient gradient) {
                            bottomTextView.setBackground(Center.getCoaster(Center.alpha(128, Center.getColorA(getContext())), 16));
                            return false;
                        }
                    }));
                    bottomTextView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Device.screenY(getContext()) / 16));
                    bottomTextLayout.addView(bottomTextView);
                }
            }
        }
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isAnimating) {
                    if (!isOpened) {
                        animate(true, 700);
                    } else {
                        animate(false, 700);
                    }
                    isOpened = !isOpened;
                }
            }
        });
        setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, topLayout.getLayoutParams().height + getPaddingTop() + getPaddingBottom()));
        addView(topLayout);
        addView(bottomLayout);
    }

    private void animate(final boolean open, int duration) {
        bottomLayout.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        int first, second;
        if (open) {
            first = topLayout.getHeight() + getPaddingTop() + getPaddingBottom();
            second = topLayout.getHeight() + bottomLayout.getMeasuredHeight() + getPaddingBottom() + getPaddingTop();
        } else {
            first = topLayout.getHeight() + bottomLayout.getMeasuredHeight() + getPaddingBottom() + getPaddingTop();
            second = topLayout.getHeight() + getPaddingTop() + getPaddingBottom();
        }
        ValueAnimator animator = ValueAnimator.ofInt(first, second);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int val = (Integer) animation.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = getLayoutParams();
                layoutParams.height = val;
                setLayoutParams(layoutParams);
            }
        });
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                isAnimating = true;
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                isAnimating = false;
            }

            @Override
            public void onAnimationCancel(Animator animator) {
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
            }
        });
        animator.setDuration(duration);
        animator.start();
    }

    private TextView getText(String text, final double textSizeRatio) {
        final TextView tv = new TextView(getContext());
        tv.setText(text);
        tv.setTextSize((int) ((double) Center.getFontSize(getContext()) * textSizeRatio));
        tv.setTypeface(Center.getTypeface(getContext()));
        tv.setTextColor(Center.getTextColor(getContext()));
        tv.setTextDirection(TEXT_DIRECTION_RTL);
        tv.setSingleLine(true);
        // TODO register in towers
        textSize.addPeer(new Peer<Integer>(new Peer.OnPeer<Integer>() {
            @Override
            public boolean onPeer(Integer integer) {
                tv.setTextSize((int) ((double) integer * textSizeRatio));
                return false;
            }
        }));
        textColor.addPeer(new Peer<Integer>(new Peer.OnPeer<Integer>() {
            @Override
            public boolean onPeer(Integer integer) {
                tv.setTextColor(integer);
                return false;
            }
        }));
        return tv;
    }
}
