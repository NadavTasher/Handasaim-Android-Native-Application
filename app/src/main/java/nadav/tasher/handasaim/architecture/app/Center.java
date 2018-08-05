package nadav.tasher.handasaim.architecture.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.text.InputFilter;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import nadav.tasher.handasaim.R;
import nadav.tasher.handasaim.architecture.app.graphics.RatioView;
import nadav.tasher.handasaim.values.Filters;
import nadav.tasher.lightool.graphics.views.Utils;

public class Center {
    public static int getFontSize(Context c) {
        return new PreferenceManager(c).getUserManager().get(R.string.preferences_user_size_text, c.getResources().getInteger(R.integer.default_font));
    }

    public static int getColorTop(Context c) {
        return new PreferenceManager(c).getUserManager().get(R.string.preferences_user_color_top, c.getResources().getColor(R.color.default_top));
    }

    public static int getColorBottom(Context c) {
        return new PreferenceManager(c).getUserManager().get(R.string.preferences_user_color_bottom, c.getResources().getColor(R.color.default_bottom));
    }

    public static int getTextColor(Context c) {
        return new PreferenceManager(c).getUserManager().get(R.string.preferences_user_color_text, c.getResources().getColor(R.color.default_text));
    }

    public static Drawable getGradient(Context c) {
        return new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, new int[]{
                getColorTop(c),
                getColorBottom(c)
        });
    }

    public static int getCombinedColor(Context c) {
        int colorA = getColorTop(c);
        int colorB = getColorBottom(c);
        int redA = Color.red(colorA);
        int greenA = Color.green(colorA);
        int blueA = Color.blue(colorA);
        int redB = Color.red(colorB);
        int greenB = Color.green(colorB);
        int blueB = Color.blue(colorB);
        int combineRed = redA - (redA - redB) / 2, combineGreen = greenA - (greenA - greenB) / 2, combineBlue = blueA - (blueA - blueB) / 2;
        return Color.rgb(combineRed, combineGreen, combineBlue);
    }

    public static int alpha(int alpha, int color) {
        return Color.argb(alpha, Color.red(color), Color.green(color), Color.blue(color));
    }

    public static Typeface getTypeface(Context c) {
        return Typeface.createFromAsset(c.getAssets(), c.getResources().getString(R.string.font_name));
    }

    public static LinearLayout getCodeEntering(Context context) {
        final PreferenceManager pm = new PreferenceManager(context);
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setGravity(Gravity.CENTER);
        layout.setPadding(40,40,40,40);
        RatioView codeTitle = new RatioView(context, 1);
        codeTitle.setText(R.string.interface_codes_title);
        codeTitle.setTypeface(getTypeface(context));
        codeTitle.setTextSize(getFontSize(context));
        codeTitle.setGravity(Gravity.CENTER);
        codeTitle.setTextColor(getTextColor(context));
        RatioView codeExplanation = new RatioView(context, 0.66);
        codeExplanation.setText(R.string.interface_codes_message);
        codeExplanation.setTypeface(getTypeface(context));
        codeExplanation.setTextSize(getFontSize(context));
        codeExplanation.setGravity(Gravity.CENTER);
        codeExplanation.setTextColor(getTextColor(context));
        codeExplanation.setPadding(0, 20, 0, 20);
        final EditText key = new EditText(context);
        key.setSingleLine(true);
        key.setHint(R.string.interface_codes_hint);
        key.setFilters(new InputFilter[]{
                Filters.codeFilter,
                new InputFilter.AllCaps()
        });
        FrameLayout keyHolder = new FrameLayout(context);
        keyHolder.setPadding(50, 10, 50, 10);
        keyHolder.addView(key);
        RatioView installCode = new RatioView(context, 0.66);
        installCode.setText(R.string.interface_codes_install);
        installCode.setTypeface(getTypeface(context));
        installCode.setTextSize(getFontSize(context));
        installCode.setGravity(Gravity.CENTER);
        installCode.setTextColor(getTextColor(context));
        installCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pm.getKeyManager().loadKey(key.getText().toString().toUpperCase());
            }
        });
        installCode.setBackground(Utils.getCoaster(context.getResources().getColor(R.color.coaster_bright), 20, 10));
        installCode.setPadding(40, 40, 40, 40);

        layout.addView(codeTitle);
        layout.addView(codeExplanation);
        layout.addView(keyHolder);
        layout.addView(installCode);
        return layout;
    }

    public static void enter(Activity c, Class a) {
        if (c.hasWindowFocus()) {
            c.startActivity(new Intent(c, a));
            c.overridePendingTransition(R.anim.out, R.anim.in);
            c.finish();
        }
    }

    public static void exit(Activity c, Class a) {
        if (c.hasWindowFocus()) {
            c.startActivity(new Intent(c, a));
            c.overridePendingTransition(R.anim.back_out, R.anim.back_in);
            c.finish();
        }
    }
}
