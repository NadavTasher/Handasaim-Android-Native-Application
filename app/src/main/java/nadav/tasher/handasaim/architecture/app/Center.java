package nadav.tasher.handasaim.architecture.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;

import nadav.tasher.handasaim.R;

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
