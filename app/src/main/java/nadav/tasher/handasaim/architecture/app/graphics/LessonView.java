package nadav.tasher.handasaim.architecture.app.graphics;

import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
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

public class LessonView extends LinearLayout{
    static final String rtlMark = "\u200F";
    private String topText;
    private ArrayList<String> bottomText;
    private int schoolHour;
    private TextView top, time;
    private LinearLayout topLayout, bottomLayout, bottomTextLayout;
    private Tower<Integer> textColor=new Tower<>(),textSize=new Tower<>();
    private Tower<AppView.Gradient> color=new Tower<>();

    public LessonView(Context c) {
        super(c);
    }

    public LessonView(Context context, int schoolHour, String topText, ArrayList<String> bottomText) {
        super(context);
        this.topText = rtlMark + topText;
        if(bottomText!=null) {
            this.bottomText = rtl(bottomText);
        }
        this.schoolHour = schoolHour;
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

    private void initBackground(){
        Calendar c = Calendar.getInstance();
        int minute = c.get(Calendar.MINUTE);
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minuteOfDay=(hour*60)+minute;
        if(minuteOfDay>=AppCore.getStartMinute(schoolHour)&&minuteOfDay<AppCore.getEndMinute(schoolHour)){
            setBackground(Center.getCoaster(Values.classCoasterMarkColor, 32));
        }else {
            setBackground(Center.getCoaster(Values.classCoasterColor, 32));
        }
    }

    private void init() {
        setOrientation(VERTICAL);
        setLayoutDirection(LAYOUT_DIRECTION_RTL);
        setGravity(Gravity.CENTER);
        setPadding(20, 10, 20, 10);
        setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Device.screenY(getContext()) / 10));
        initBackground();
        if (schoolHour < 0) {
            top = getText(topText, 1);
        } else {
            top = getText(schoolHour + ". " + topText, 1);
        }
        time = getText(AppCore.convertMinuteToTime(AppCore.getStartMinute(schoolHour)) + " - " + AppCore.convertMinuteToTime(AppCore.getEndMinute(schoolHour)), 0.8);
        bottomTextLayout = new LinearLayout(getContext());
        topLayout = new LinearLayout(getContext());
        bottomLayout = new LinearLayout(getContext());
        topLayout.setOrientation(LinearLayout.HORIZONTAL);
        bottomLayout.setOrientation(LinearLayout.HORIZONTAL);
        bottomTextLayout.setOrientation(LinearLayout.VERTICAL);
        topLayout.setLayoutDirection(LAYOUT_DIRECTION_RTL);
        bottomLayout.setLayoutDirection(LAYOUT_DIRECTION_RTL);
        bottomTextLayout.setLayoutDirection(LAYOUT_DIRECTION_RTL);
        time.setGravity(Gravity.CENTER);
        top.setGravity(Gravity.CENTER_VERTICAL | Gravity.START);
        top.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        time.setLayoutParams(new LinearLayout.LayoutParams(Device.screenX(getContext())/2 -getPaddingLeft(), ViewGroup.LayoutParams.MATCH_PARENT));
        bottomTextLayout.setLayoutParams(new LinearLayout.LayoutParams(Device.screenX(getContext())/2-getPaddingRight(), ViewGroup.LayoutParams.MATCH_PARENT));
        topLayout.addView(top);
        bottomLayout.addView(bottomTextLayout);
        bottomLayout.addView(time);

        if(bottomText!=null) {
            if (bottomText.size() == 1) {
                TextView bottomTextView = getText(bottomText.get(0), 0.8);
                bottomTextView.setGravity(Gravity.CENTER);
                bottomTextView.setEllipsize(TextUtils.TruncateAt.END);
                bottomTextLayout.addView(bottomTextView);
            } else {
                for (int i = 0; i < bottomText.size(); i++) {
                    final TextView bottomTextView = getText(i + ". " + bottomText.get(i), 0.6);
                    bottomTextView.setGravity(Gravity.CENTER_VERTICAL | Gravity.START);
                    bottomTextView.setEllipsize(TextUtils.TruncateAt.END);
                    bottomTextView.setBackground(Center.getCoaster(Center.getColorA(getContext()), 16));
                    color.addPeer(new Peer<AppView.Gradient>(new Peer.OnPeer<AppView.Gradient>() {
                        @Override
                        public boolean onPeer(AppView.Gradient gradient) {
                            bottomTextView.setBackground(Center.getCoaster(Center.getColorA(getContext()), 16));
                            return false;
                        }
                    }));
                    bottomTextView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Device.screenY(getContext()) / 16));
                    bottomTextLayout.addView(bottomTextView);
                }
            }
        }

        addView(topLayout);
        addView(bottomLayout);
        //        Log.i("LessonView",""+Device.screenY(getContext()) / 7);
    }

    private TextView getText(String text, final double textSizeRatio) {
        final TextView tv = new TextView(getContext());
        tv.setTextSize((int) ((double) Center.getFontSize(getContext()) * textSizeRatio));
        tv.setTypeface(Center.getTypeface(getContext()));
        tv.setTextColor(Center.getTextColor(getContext()));
        tv.setSingleLine(true);
        // TODO register in towers
        textSize.addPeer(new Peer<Integer>(new Peer.OnPeer<Integer>() {
            @Override
            public boolean onPeer(Integer integer) {
                tv.setTextSize((int)((double)integer*textSizeRatio));
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
