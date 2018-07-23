package nadav.tasher.handasaim.architecture.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;

import nadav.tasher.handasaim.R;
import nadav.tasher.handasaim.values.Values;

import static nadav.tasher.handasaim.values.Values.fontColor;
import static nadav.tasher.handasaim.values.Values.fontColorDefault;

public class Center {
    public static int getFontSize(Context c) {
        SharedPreferences sp = c.getSharedPreferences(Values.prefName, Context.MODE_PRIVATE);
        return sp.getInt(Values.fontSizeNumber, Values.fontSizeDefault);
    }

    public static int getColorA(Context c) {
        SharedPreferences sp = c.getSharedPreferences(Values.prefName, Context.MODE_PRIVATE);
        return sp.getInt(Values.colorA, Values.defaultColorA);
    }

    public static int getColorB(Context c) {
        SharedPreferences sp = c.getSharedPreferences(Values.prefName, Context.MODE_PRIVATE);
        return sp.getInt(Values.colorB, Values.defaultColorB);
    }

    public static int getTextColor(Context c) {
        SharedPreferences sp = c.getSharedPreferences(Values.prefName, Context.MODE_PRIVATE);
        return sp.getInt(Values.fontColor, Values.fontColorDefault);
    }

    public static Drawable getGradient(Context c) {
        return new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, new int[]{
                getColorA(c),
                getColorB(c)
        });
    }

    public static int getCombinedColor(Context c){
        int colorA= Center.getColorA(c);
        int colorB= Center.getColorB(c);
        int redA = Color.red(colorA);
        int greenA = Color.green(colorA);
        int blueA = Color.blue(colorA);
        int redB = Color.red(colorB);
        int greenB = Color.green(colorB);
        int blueB = Color.blue(colorB);
        int alphaA = Color.alpha(colorA);
        int alphaB = Color.alpha(colorB);
        int combineRed = redA - (redA - redB) / 2, combineGreen = greenA - (greenA - greenB) / 2, combineBlue = blueA - (blueA - blueB) / 2;
        return Color.rgb(combineRed, combineGreen, combineBlue);
    }

    public static int alpha(int alpha, int color){
        return Color.argb(alpha,Color.red(color),Color.green(color),Color.blue(color));
    }

    public static Typeface getTypeface(Context c) {
        return Typeface.createFromAsset(c.getAssets(), Values.fontName);
    }

    public static void installColors(Context c) {
        SharedPreferences sp = c.getSharedPreferences(Values.prefName, Context.MODE_PRIVATE);
        sp.edit().putInt(Values.colorA, Values.defaultColorA).apply();
        sp.edit().putInt(Values.colorB, Values.defaultColorB).apply();
    }

    public static void enter(Activity c, Class a){
        if(c.hasWindowFocus()){
            c.startActivity(new Intent(c,a));
            c.overridePendingTransition(R.anim.out, R.anim.in);
            c.finish();
        }
    }

    public static void exit(Activity c,Class a){
        if(c.hasWindowFocus()){
            c.startActivity(new Intent(c,a));
            c.overridePendingTransition(R.anim.back_out, R.anim.back_in);
            c.finish();
        }
    }

    public static void writeDefaults(Context c) {
        SharedPreferences.Editor se = c.getSharedPreferences(Values.prefName,Context.MODE_PRIVATE).edit();
        se.putInt(Values.fontSizeNumber, Values.fontSizeDefault);
        se.putBoolean(Values.pushService, Values.pushDefault);
        se.putBoolean(Values.breakTime, Values.breakTimeDefault);
        se.putInt(fontColor, fontColorDefault);
        se.putInt(Values.colorA, Values.defaultColorA);
        se.putInt(Values.colorB, Values.defaultColorB);
        se.putBoolean(Values.firstLaunch, false);
        se.apply();
    }
}
