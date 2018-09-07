package nadav.tasher.handasaim.architecture.app.graphics;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import java.util.ArrayList;

import nadav.tasher.handasaim.R;
import nadav.tasher.handasaim.architecture.app.Center;
import nadav.tasher.lightool.graphics.views.ExpandingView;
import nadav.tasher.lightool.graphics.views.Utils;
import nadav.tasher.lightool.info.Device;

public class LessonView extends FrameLayout {
    public static final int MARK_TYPE_NORMAL = 0;
    public static final int MARK_TYPE_PRESSED = 1;
    public static final int MARK_TYPE_SPECIAL_NORMAL = 2;
    public static final int MARK_TYPE_SPECIAL_PRESSED = 3;
    static final String rtlMark = "\u200F";
    private String topText, timeText;
    private int markType = MARK_TYPE_NORMAL;
    private ArrayList<String> bottomTexts = new ArrayList<>();
    private ArrayList<RatioView> texts = new ArrayList<>();

    public LessonView(Context context, int markType, String topText, String timeText, ArrayList<String> bottomTexts) {
        super(context);
        this.markType = markType;
        this.topText = topText;
        this.timeText = timeText;
        this.bottomTexts = bottomTexts;
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
        int markID = R.color.coaster_bright;
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
        RatioView topView = getText(topText, 1);
        RatioView timeView = getText(timeText, 0.8);
        topView.setPadding(20, 0, 20, 0);
        timeView.setTextDirection(TEXT_DIRECTION_LTR);
        texts.add(topView);
        texts.add(timeView);
        // Continue setup
        topView.setGravity(Gravity.CENTER_VERTICAL | Gravity.START);
        timeView.setGravity(Gravity.CENTER);
        topView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Device.screenY(getContext()) / 10));
        timeView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Device.screenY(getContext()) / 11, 1));
        // Bottom setup
        LinearLayout bottomLayout = new LinearLayout(getContext());
        bottomLayout.setGravity(Gravity.CENTER);
        bottomLayout.setOrientation(LinearLayout.HORIZONTAL);
        bottomLayout.setLayoutDirection(LAYOUT_DIRECTION_RTL);
        bottomLayout.addView(getTexts(bottomTexts));
        bottomLayout.addView(timeView);
        ExpandingView ev = new ExpandingView(getContext());
        ev.setBackground(initBackground());
        ev.setPadding(20, 25);
        ev.setTop(topView);
        ev.setBottom(bottomLayout);
        addView(ev);
    }

    public void setTextColor(int color) {
        for (int t = 0; t < texts.size(); t++) {
            RatioView current = texts.get(t);
            current.setTextColor(color);
        }
    }

    public void setTextSize(int size) {
        for (int t = 0; t < texts.size(); t++) {
            RatioView current = texts.get(t);
            current.setTextSize(size);
        }
    }

    private View getTexts(ArrayList<String> strings) {
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
}
