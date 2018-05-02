package nadav.tasher.handasaim.tools.graphics;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import nadav.tasher.handasaim.activities.Main;
import nadav.tasher.handasaim.tools.TowerHub;
import nadav.tasher.lightool.communication.Tunnel;
import nadav.tasher.lightool.info.Device;

public class LessonView  extends LinearLayout {
    static final String rtlMark = "\u200F";
    private String ln, tm, tc;
    private int num;
    private TextView lessonTv, timeTv, teacherTv;
    private LinearLayout top, bottom;
    private Drawable back, pressed;

    public LessonView(Context c) {
        super(c);
    }

    public LessonView(Context context, Drawable d, Drawable p, int number, String lessonName, String times, String teacher) {
        super(context);
        ln = rtlMark + lessonName;
        tm = rtlMark + times;
        tc = rtlMark + teacher;
        num = number;
        back = d;
        pressed = p;
        init();
    }

    public void mark() {
        setBackground(pressed);
    }

    private void init() {
        setOrientation(VERTICAL);
        setLayoutDirection(LAYOUT_DIRECTION_RTL);
        setGravity(Gravity.CENTER);
        lessonTv = new TextView(getContext());
        timeTv = new TextView(getContext());
        teacherTv = new TextView(getContext());
        top = new LinearLayout(getContext());
        bottom = new LinearLayout(getContext());
        top.setOrientation(LinearLayout.HORIZONTAL);
        bottom.setOrientation(LinearLayout.HORIZONTAL);
        top.setLayoutDirection(LAYOUT_DIRECTION_RTL);
        bottom.setLayoutDirection(LAYOUT_DIRECTION_RTL);
        lessonTv.setTextSize(Main.getFontSize(getContext()));
        teacherTv.setTextSize((float) (Main.getFontSize(getContext()) * 0.8));
        timeTv.setTextSize((float) (Main.getFontSize(getContext()) * 0.8));
        top.addView(lessonTv);
        bottom.addView(teacherTv);
        bottom.addView(timeTv);
        if (num != -1) {
            String tx = num + ". " + ln;
            lessonTv.setText(tx);
        } else {
            lessonTv.setText(ln);
        }
        timeTv.setText(tm);
        teacherTv.setText(tc);
        lessonTv.setTextColor(Main.getTextColor(getContext()));
        teacherTv.setTextColor(Main.getTextColor(getContext()));
        timeTv.setTextColor(Main.getTextColor(getContext()));
        timeTv.setTypeface(Main.getTypeface(getContext()));
        lessonTv.setTypeface(Main.getTypeface(getContext()));
        teacherTv.setTypeface(Main.getTypeface(getContext()));
        teacherTv.setSingleLine(true);
        timeTv.setSingleLine(true);
        lessonTv.setSingleLine(true);
        teacherTv.setEllipsize(TextUtils.TruncateAt.END);
        teacherTv.setSingleLine();
        //                lessonTv.setEllipsize(TextUtils.TruncateAt.END);
        lessonTv.setSingleLine();
        timeTv.setGravity(Gravity.CENTER);
        teacherTv.setGravity(Gravity.CENTER);
        addView(top);
        addView(bottom);
        setBackground(back);
        setPadding(20, 10, 20, 10);
        teacherTv.setLayoutParams(new LayoutParams(Device.screenX(getContext()) / 2 - getPaddingRight(), ViewGroup.LayoutParams.WRAP_CONTENT));
        timeTv.setLayoutParams(new LayoutParams(Device.screenX(getContext()) / 2 - getPaddingLeft(), ViewGroup.LayoutParams.WRAP_CONTENT));
        setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Device.screenY(getContext()) / 7));
        TowerHub.textColorChangeTunnle.addReceiver(new Tunnel.OnTunnel<Integer>() {
            @Override
            public void onReceive(Integer response) {
                lessonTv.setTextColor(response);
                teacherTv.setTextColor(response);
                timeTv.setTextColor(response);
            }
        });
        TowerHub.fontSizeChangeTunnle.addReceiver(new Tunnel.OnTunnel<Integer>() {
            @Override
            public void onReceive(Integer response) {
                lessonTv.setTextSize(response);
                teacherTv.setTextSize((float) (response * 0.8));
                timeTv.setTextSize((float) (response * 0.8));
            }
        });
    }
}
