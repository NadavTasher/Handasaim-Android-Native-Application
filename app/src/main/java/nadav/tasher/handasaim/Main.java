package nadav.tasher.handasaim;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import nadav.tasher.lightool.Light;

public class Main extends Activity {
    private final int color = Color.parseColor("#445566");
    private final int secolor = color+0x333333;
    private final String serviceProvider = "http://handasaim.co.il";
    private final String service = "http://handasaim.co.il/2017/06/13/%D7%9E%D7%A2%D7%A8%D7%9B%D7%AA-%D7%95%D7%A9%D7%99%D7%A0%D7%95%D7%99%D7%99%D7%9D/";
    private String day;
    private Class currentClass;
    private int textColor = Color.WHITE;
    private ProgressBar pb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startApp();
    }

    private void splash() {
        final Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(color);
        window.setNavigationBarColor(color);
        LinearLayout ll = new LinearLayout(this);
        ll.setGravity(Gravity.CENTER);
        ll.setOrientation(LinearLayout.VERTICAL);
        ll.setBackgroundColor(color);
        ImageView icon = new ImageView(this);
        icon.setImageDrawable(getDrawable(R.drawable.ic_icon));
        int is = (int) (Light.Device.screenX(getApplicationContext()) * 0.8);
        icon.setLayoutParams(new LinearLayout.LayoutParams(is, is));
        ll.addView(icon);
        pb = new ProgressBar(this);
        pb.setIndeterminate(true);
        ll.addView(pb);
        setContentView(ll);
    }

    private void welcome(final ArrayList<Class> classes, final boolean renew) {
        final SharedPreferences sp = getPreferences(Context.MODE_PRIVATE);
        LinearLayout part1 = new LinearLayout(this);
        final LinearLayout part2 = new LinearLayout(this);
        final LinearLayout part3 = new LinearLayout(this);
        part1.setGravity(Gravity.CENTER);
        part2.setGravity(Gravity.CENTER);
        part3.setGravity(Gravity.CENTER);
        part1.setOrientation(LinearLayout.VERTICAL);
        part2.setOrientation(LinearLayout.VERTICAL);
        part3.setOrientation(LinearLayout.VERTICAL);
        part1.setBackgroundColor(color);
        part2.setBackgroundColor(color);
        part3.setBackgroundColor(color);
        final Typeface custom_font = Typeface.createFromAsset(getAssets(), "gisha.ttf");
        //part1
        ImageView icon = new ImageView(this);
        final Button setup = new Button(this);
        final TextView welcome = new TextView(this);
        setup.setTypeface(custom_font);
        welcome.setTypeface(custom_font);
        setup.setText(R.string.set);
        setup.setAlpha(0);
        setup.setBackgroundColor(Color.TRANSPARENT);
        setup.setTextSize((float) 30);
        welcome.setAlpha(0);
        welcome.setGravity(Gravity.CENTER);
        welcome.setTextSize((float) 29);
        welcome.setTextColor(Color.WHITE);
        welcome.setText(R.string.wlc);
        icon.setImageDrawable(getDrawable(R.drawable.ic_icon));
        int is = (int) (Light.Device.screenX(getApplicationContext()) * 0.8);
        icon.setLayoutParams(new LinearLayout.LayoutParams(is, is));
        ObjectAnimator iconSlide = ObjectAnimator.ofFloat(icon, View.TRANSLATION_X, -Light.Device.screenX(getApplicationContext()), 0);
        iconSlide.setDuration(1000);
        iconSlide.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                ObjectAnimator buttonAn = ObjectAnimator.ofFloat(setup, View.ALPHA, Light.Animations.INVISIBLE_TO_VISIBLE);
                buttonAn.setDuration(500);
                buttonAn.start();
                ObjectAnimator welAn = ObjectAnimator.ofFloat(welcome, View.ALPHA, Light.Animations.INVISIBLE_TO_VISIBLE);
                welAn.setDuration(500);
                welAn.start();

            }

            @Override
            public void onAnimationCancel(Animator animator) {
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
            }
        });
        iconSlide.start();
        part1.addView(welcome);
        part1.addView(icon);
        part1.addView(setup);
        if(!renew)
        setContentView(part1);
        setup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setContentView(part2);
            }
        });
        //part2
        TextView selClas = new TextView(this);
        ScrollView clascroll = new ScrollView(this);
        final RadioGroup classs = new RadioGroup(this);
        clascroll.addView(classs);
        Button next = new Button(this);
        next.setTypeface(custom_font);
        selClas.setTypeface(custom_font);
        next.setBackgroundColor(Color.TRANSPARENT);
        next.setTextSize((float) 30);
        selClas.setGravity(Gravity.CENTER);
        selClas.setTextSize((float) 29);
        selClas.setTextColor(Color.WHITE);
        selClas.setText(R.string.scla);
        next.setText(R.string.nxt);
        part2.addView(selClas);
        part2.addView(clascroll);
        part2.addView(next);
        classs.setGravity(Gravity.CENTER);
        clascroll.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Light.Device.screenY(getApplicationContext()) / 2));
        for (int c = 0; c < classes.size(); c++) {
            RadioButton rb = new RadioButton(this);
            rb.setText(classes.get(c).name);
            rb.setTextSize((float) 30);
            rb.setTypeface(custom_font);
            rb.setGravity(Gravity.CENTER);
            rb.setId(c);
            rb.setLayoutParams(new RadioGroup.LayoutParams(Light.Device.screenX(getApplicationContext()) / 10 * 8, ViewGroup.LayoutParams.WRAP_CONTENT));
            classs.addView(rb);
        }
        classs.check(0);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sp.edit().putString("favorite_class", classes.get(classs.getCheckedRadioButtonId()).name).commit();
                setContentView(part3);
            }
        });
        //part3
        TextView spclSet = new TextView(this);
        Button done = new Button(this);
        done.setTypeface(custom_font);
        spclSet.setTypeface(custom_font);
        done.setBackgroundColor(Color.TRANSPARENT);
        done.setTextSize((float) 30);
        spclSet.setGravity(Gravity.CENTER);
        spclSet.setTextSize((float) 29);
        spclSet.setTextColor(Color.WHITE);
        spclSet.setText(R.string.spclstt);
        done.setText(R.string.dn);
        final Switch showTimes = new Switch(this);
        showTimes.setChecked(false);
        showTimes.setText(R.string.sct);
        showTimes.setTextSize((float) 30);
        showTimes.setTypeface(custom_font);
        showTimes.setLayoutParams(new LinearLayout.LayoutParams(Light.Device.screenX(getApplicationContext()) / 10 * 8, ViewGroup.LayoutParams.WRAP_CONTENT));
        final Switch textCo = new Switch(this);
        textCo.setChecked(true);
        textCo.setText(R.string.white);
        textCo.setTextSize((float) 30);
        textCo.setTypeface(custom_font);
        textCo.setLayoutParams(new LinearLayout.LayoutParams(Light.Device.screenX(getApplicationContext()) / 10 * 8, ViewGroup.LayoutParams.WRAP_CONTENT));
        textCo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    textCo.setText(R.string.white);
                    textCo.setTextColor(Color.WHITE);
                } else {
                    textCo.setText(R.string.black);
                    textCo.setTextColor(Color.BLACK);
                }
            }
        });
        final Switch showBreaks = new Switch(this);
        showBreaks.setChecked(true);
        showBreaks.setText(R.string.showbrk);
        showBreaks.setTextSize((float) 30);
        showBreaks.setTypeface(custom_font);
        showBreaks.setLayoutParams(new LinearLayout.LayoutParams(Light.Device.screenX(getApplicationContext()) / 10 * 8, ViewGroup.LayoutParams.WRAP_CONTENT));
        part3.addView(spclSet);
        part3.addView(showTimes);
        part3.addView(showBreaks);
        part3.addView(textCo);
        part3.addView(done);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sp.edit().putBoolean("show_time", showTimes.isChecked()).commit();
                sp.edit().putBoolean("fontWhite", textCo.isChecked()).commit();
                sp.edit().putBoolean("breaks", textCo.isChecked()).commit();
                sp.edit().putInt("last_recorded_version_code", Light.Device.getVersionCode(getApplicationContext(),getPackageName())).commit();
                sp.edit().putBoolean("first", false).commit();
                if(!renew) {
                    new Light.Net.Pinger(2000, new Light.Net.Pinger.OnEnd() {
                        @Override
                        public void onPing(String s, boolean b) {
                            if (b) {
                                ArrayList<Light.Net.PHP.Post.PHPParameter> parms = new ArrayList<>();
                                parms.add(new Light.Net.PHP.Post.PHPParameter("install", "true"));
                                new Light.Net.PHP.Post("http://handasaim.thepuzik.com/install.php", parms, new Light.Net.PHP.Post.OnPost() {
                                    @Override
                                    public void onPost(String s) {
                                        Log.i("COUNT", s);
                                        view(classes);
                                    }
                                }).execute();
                            } else {
                                view(classes);
                            }
                        }
                    }).execute("http://handasaim.thepuzik.com");
                }else{
                    view(classes);
                }
                //                view(classes);
            }
        });
        if(renew){
            spclSet.setText(R.string.rnew);
            setContentView(part3);
        }
    }

    private void checkInternet() {
        if (Light.Device.isOnline(getApplicationContext())) {
            new Light.Net.Pinger(5000, new Light.Net.Pinger.OnEnd() {
                @Override
                public void onPing(String s, boolean b) {
                    if (s.equals(serviceProvider) && b) {
                        openApp();
                    } else if (s.equals(serviceProvider) && !b) {
                        popup("Server Error: No Response From Service Provider.");
                    }
                }
            }).execute(serviceProvider);
        } else {
            popup("No Internet Connection.");
        }
    }

    private void startApp() {
        splash();
        checkInternet();
    }

    void popup(String text) {
        AlertDialog.Builder pop = new AlertDialog.Builder(this);
        pop.setCancelable(true);
        pop.setMessage(text);
        pop.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                startApp();
            }
        });
        pop.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                startApp();
            }
        });
        pop.show();
    }

    private void view(final ArrayList<Class> classes) {
        final SharedPreferences sp = getPreferences(Context.MODE_PRIVATE);
        getWindow().setStatusBarColor(secolor);
        final LinearLayout sall = new LinearLayout(this);
        final LinearLayout all = new LinearLayout(this);
        final LinearLayout navbarAll = new LinearLayout(this);
        final ImageView nutIcon = new ImageView(this);
        final int screenY = Light.Device.screenY(this);
        final int nutSize = (screenY / 8) - screenY / 30;
        final ObjectAnimator anim = ObjectAnimator.ofFloat(nutIcon, View.TRANSLATION_Y, Light.Animations.JUMP_SMALL);
        final int navY = screenY / 8;
        final LinearLayout.LayoutParams nutParms = new LinearLayout.LayoutParams(nutSize, nutSize);
        final LinearLayout.LayoutParams navParms = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, navY);
        all.setOrientation(LinearLayout.VERTICAL);
        all.setGravity(Gravity.START | Gravity.CENTER_HORIZONTAL);
        all.setBackgroundColor(color);
        sall.setOrientation(LinearLayout.VERTICAL);
        sall.setGravity(Gravity.START | Gravity.CENTER_HORIZONTAL);
        sall.setBackgroundColor(color);
        navbarAll.setBackgroundColor(secolor);
        navbarAll.setOrientation(LinearLayout.HORIZONTAL);
        navbarAll.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
        nutIcon.setLayoutParams(nutParms);
        nutIcon.setImageDrawable(getDrawable(R.drawable.ic_icon));
        nutIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder ab=new AlertDialog.Builder(Main.this);
                ab.setTitle(R.string.app_name);
                ab.setMessage("This App Was Made By NadavTasher, Sept. 2017\nVersion: "+ Light.Device.getVersionName(getApplicationContext(),getPackageName())+"\nBuild: "+ Light.Device.getVersionCode(getApplicationContext(),getPackageName()));
                ab.setCancelable(true);
                ab.setPositiveButton("Close",null);
                ab.show();
            }
        });
        anim.setDuration(750);
        anim.setRepeatMode(ObjectAnimator.RESTART);
        anim.setRepeatCount(ObjectAnimator.INFINITE);
        anim.start();
        navbarAll.addView(nutIcon);
        navParms.gravity = Gravity.START;
        navbarAll.setLayoutParams(navParms);
        sall.addView(navbarAll);
        LinearLayout navSliderview = new LinearLayout(this);
        navSliderview.setGravity(Gravity.START);
        navSliderview.setOrientation(LinearLayout.HORIZONTAL);
        HorizontalScrollView navSliderviewscroll = new HorizontalScrollView(this);
        navSliderviewscroll.addView(navSliderview);
        navbarAll.addView(navSliderviewscroll);
        LinearLayout timeswitch = new LinearLayout(this);
        timeswitch.setBackground(getDrawable(R.drawable.back));
        timeswitch.setLayoutParams(new LinearLayout.LayoutParams(Light.Device.screenX(getApplicationContext()) / 4, Light.Device.screenY(getApplicationContext()) / 12));
        ImageView clock_ic = new ImageView(getApplicationContext());
        clock_ic.setLayoutParams(new LinearLayout.LayoutParams(Light.Device.screenY(getApplicationContext()) / 20, Light.Device.screenY(getApplicationContext()) / 20));
        clock_ic.setImageDrawable(getDrawable(R.drawable.ic_clock));
        timeswitch.addView(clock_ic);
        timeswitch.setPadding(20, 20, 20, 20);
        Switch sw = new Switch(this);
        sw.setText(null);
        sw.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        timeswitch.setGravity(Gravity.CENTER);
        timeswitch.setOrientation(LinearLayout.HORIZONTAL);
        timeswitch.addView(sw);
        navSliderview.addView(timeswitch);


        LinearLayout breakswitch = new LinearLayout(this);
        breakswitch.setBackground(getDrawable(R.drawable.back));
        breakswitch.setLayoutParams(new LinearLayout.LayoutParams(Light.Device.screenX(getApplicationContext()) / 4, Light.Device.screenY(getApplicationContext()) / 12));
        ImageView breakswitch_ic = new ImageView(getApplicationContext());
        breakswitch_ic.setLayoutParams(new LinearLayout.LayoutParams(Light.Device.screenY(getApplicationContext()) / 20, Light.Device.screenY(getApplicationContext()) / 20));
        breakswitch_ic.setImageDrawable(getDrawable(R.drawable.ic_breaktime));
        breakswitch.addView(breakswitch_ic);
        breakswitch.setPadding(20, 20, 20, 20);
        Switch swb = new Switch(this);
        swb.setText(null);
        swb.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        breakswitch.setGravity(Gravity.CENTER);
        breakswitch.setOrientation(LinearLayout.HORIZONTAL);
        breakswitch.addView(swb);
        navSliderview.addView(breakswitch);

        LinearLayout share = new LinearLayout(this);
        share.setOrientation(LinearLayout.HORIZONTAL);
        share.setGravity(Gravity.CENTER);
        ImageButton sr = new ImageButton(this);
        sr.setImageDrawable(getDrawable(R.drawable.ic_share));
        sr.setBackgroundColor(Color.TRANSPARENT);
        share.setBackground(getDrawable(R.drawable.back));
        sr.setLayoutParams(new LinearLayout.LayoutParams(Light.Device.screenY(getApplicationContext()) / 20, Light.Device.screenY(getApplicationContext()) / 20));
        share.addView(sr);
        sr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                share(currentClass.name + "\n" + hourSystemForClassString(currentClass, sp.getBoolean("show_time", true)));
            }
        });
        share.setLayoutParams(new LinearLayout.LayoutParams(Light.Device.screenX(getApplicationContext()) / 4, Light.Device.screenY(getApplicationContext()) / 12));
        navSliderview.addView(share);
        LinearLayout fontS = new LinearLayout(this);
        fontS.setOrientation(LinearLayout.HORIZONTAL);
        fontS.setGravity(Gravity.CENTER);
        fontS.setBackground(getDrawable(R.drawable.back));
        fontS.setLayoutParams(new LinearLayout.LayoutParams(Light.Device.screenX(getApplicationContext()) / 4, Light.Device.screenY(getApplicationContext()) / 12));
        ImageButton minus = new ImageButton(this);
        final TextView size = new TextView(this);
        ImageButton plus = new ImageButton(this);
        size.setTextColor(Color.WHITE);
        size.setText(String.valueOf(sp.getInt("font", 32)));
        plus.setImageDrawable(getDrawable(R.drawable.ic_plus));
        minus.setImageDrawable(getDrawable(R.drawable.ic_minus));
        plus.setBackgroundColor(Color.TRANSPARENT);
        minus.setBackgroundColor(Color.TRANSPARENT);
        LinearLayout.LayoutParams buttonp = new LinearLayout.LayoutParams(Light.Device.screenY(getApplicationContext()) / 25, Light.Device.screenY(getApplicationContext()) / 25);
        plus.setLayoutParams(buttonp);
        minus.setLayoutParams(buttonp);
        size.setTextSize((float) 25);
        fontS.addView(minus);
        fontS.addView(size);
        fontS.addView(plus);
        navSliderview.addView(fontS);
        LinearLayout colorText = new LinearLayout(this);
        colorText.setOrientation(LinearLayout.HORIZONTAL);
        colorText.setGravity(Gravity.CENTER);
        colorText.setBackground(getDrawable(R.drawable.back));
        colorText.setLayoutParams(new LinearLayout.LayoutParams(Light.Device.screenX(getApplicationContext()) / 4, Light.Device.screenY(getApplicationContext()) / 12));
        final ImageButton switchc = new ImageButton(this);
        switchc.setBackgroundColor(Color.TRANSPARENT);
        LinearLayout.LayoutParams colorTextp = new LinearLayout.LayoutParams(Light.Device.screenY(getApplicationContext()) / 15, Light.Device.screenY(getApplicationContext()) / 15);
        switchc.setLayoutParams(colorTextp);
        if (sp.getBoolean("fontWhite", true)) {
            switchc.setImageDrawable(getDrawable(R.drawable.ic_white));
            textColor = Color.WHITE;
        } else {
            switchc.setImageDrawable(getDrawable(R.drawable.ic_black));
            textColor = Color.BLACK;
        }
        colorText.addView(switchc);
        navSliderview.addView(colorText);
        int selectedClass = 0;
        if (sp.getString("favorite_class", null) != null) {
            if (classes != null) {
                for (int fc = 0; fc < classes.size(); fc++) {
                    if (sp.getString("favorite_class", "").equals(classes.get(fc).name)) {
                        selectedClass = fc;
                        break;
                    }
                }
            } else {
                popup("Downloaded Excel File Is Corrupted");
            }
        }
        ScrollView sv = new ScrollView(this);
        sv.addView(all);
        sall.addView(sv);
        final LinearLayout hsplace = new LinearLayout(this);
        hsplace.setGravity(Gravity.CENTER);
        hsplace.setOrientation(LinearLayout.VERTICAL);
        hsplace.setPadding(20, 20, 20, 20);
        all.addView(hsplace);
        sw.setChecked(sp.getBoolean("show_time", false));
        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                sp.edit().putBoolean("show_time", b).commit();
                showHS(currentClass, hsplace, classes, b, sp.getInt("font", 32),sp.getBoolean("breaks", true));
            }
        });
        swb.setChecked(sp.getBoolean("breaks", true));
        swb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                sp.edit().putBoolean("breaks", b).commit();
                showHS(currentClass, hsplace, classes, sp.getBoolean("show_time", true), sp.getInt("font", 32),b);
            }
        });
        switchc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sp.edit().putBoolean("fontWhite", !sp.getBoolean("fontWhite", true)).commit();
                if (sp.getBoolean("fontWhite", true)) {
                    switchc.setImageDrawable(getDrawable(R.drawable.ic_white));
                    textColor = Color.WHITE;
                } else {
                    switchc.setImageDrawable(getDrawable(R.drawable.ic_black));
                    textColor = Color.BLACK;
                }
                showHS(currentClass, hsplace, classes, sp.getBoolean("show_time", false), sp.getInt("font", 32),sp.getBoolean("breaks", true));
            }
        });
        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int fontSize = sp.getInt("font", 32);
                fontSize++;
                if (fontSize <= 50) {
                    sp.edit().putInt("font", fontSize).commit();
                    showHS(currentClass, hsplace, classes, sp.getBoolean("show_time", false), fontSize,sp.getBoolean("breaks", true));
                    size.setText(String.valueOf(fontSize));
                }
            }
        });
        minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int fontSize = sp.getInt("font", 32);
                fontSize--;
                if (fontSize >= 1) {
                    sp.edit().putInt("font", fontSize).commit();
                    showHS(currentClass, hsplace, classes, sp.getBoolean("show_time", false), fontSize,sp.getBoolean("breaks", true));
                    size.setText(String.valueOf(fontSize));
                }
            }
        });
        if (classes != null)
            showHS(classes.get(selectedClass), hsplace, classes, sp.getBoolean("show_time", true), sp.getInt("font", 32),sp.getBoolean("breaks", true));
        setContentView(sall);
    }

    private void share(String st) {
        Intent s = new Intent(Intent.ACTION_SEND);
        s.putExtra(Intent.EXTRA_TEXT, st);
        s.setType("text/plain");
        s.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(Intent.createChooser(s, "Share With"));
    }

    private void showHS(final Class c, final LinearLayout hsplace, final ArrayList<Class> classes, final boolean showTime, final int fontSize, final boolean breakTimes) {
        currentClass = c;
        hsplace.removeAllViews();
        final Typeface custom_font = Typeface.createFromAsset(getAssets(), "gisha.ttf");
        final Button className = new Button(this);
        className.setTextSize((float) fontSize);
        //        className.setBackgroundColor(Color.TRANSPARENT);
        className.setGravity(Gravity.CENTER);
        className.setBackground(getDrawable(R.drawable.back));
        className.setText(c.name + " (" + day + ")");
        className.setTextColor(textColor);
        className.setTypeface(custom_font);
        className.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LinearLayout classesll = new LinearLayout(getApplicationContext());
                classesll.setGravity(Gravity.CENTER);
                classesll.setOrientation(LinearLayout.VERTICAL);
                final Dialog dialog = new Dialog(Main.this);
                dialog.setCancelable(true);
                ScrollView classesllss = new ScrollView(getApplicationContext());
                classesllss.addView(classesll);
                dialog.setContentView(classesllss);
                for (int cs = 0; cs < classes.size(); cs++) {
                    if (classes.get(cs) != c) {
                        Button cls = new Button(getApplicationContext());
                        cls.setTextSize((float) fontSize);
                        cls.setGravity(Gravity.CENTER);
                        cls.setText(classes.get(cs).name);
                        cls.setTextColor(textColor);
                        cls.setBackgroundColor(Color.TRANSPARENT);
                        cls.setTypeface(custom_font);
                        cls.setLayoutParams(new LinearLayout.LayoutParams((int) (Light.Device.screenX(getApplicationContext()) * 0.8), (Light.Device.screenY(getApplicationContext()) / 8)));
                        classesll.addView(cls);
                        final int finalCs = cs;
                        cls.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                final SharedPreferences sp = getPreferences(Context.MODE_PRIVATE);
                                sp.edit().putString("favorite_class", classes.get(finalCs).name).commit();
                                showHS(classes.get(finalCs), hsplace, classes, showTime, fontSize,breakTimes);
                                dialog.dismiss();
                            }
                        });
                    }
                }
                dialog.show();
            }
        });
        hsplace.addView(className);
        hsplace.addView(hourSystemForClass(c, showTime, fontSize,breakTimes));
    }

    private LinearLayout hourSystemForClass(final Class fclass, boolean showTime, int fontSize,boolean breakTimes) {
        LinearLayout all = new LinearLayout(this);
        all.setGravity(Gravity.START | Gravity.CENTER_HORIZONTAL);
        all.setOrientation(LinearLayout.VERTICAL);
        all.setPadding(10, 10, 10, 10);
        final Typeface custom_font = Typeface.createFromAsset(getAssets(), "gisha.ttf");
        for (int s = 0; s < fclass.classes.size(); s++) {
            if(getBreak(fclass.classes.get(s).hour-1)!=-1&&breakTimes){
                Button breakt = new Button(this);
                breakt.setText("הפסקה, "+getBreak(fclass.classes.get(s).hour-1)+" דקות");
                breakt.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
                breakt.setTextSize((float) fontSize - 2);
                breakt.setTextColor(textColor);
                breakt.setBackground(getDrawable(R.drawable.button));
                breakt.setPadding(20, 20, 20, 20);
                breakt.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Light.Device.screenY(getApplicationContext()) / 10));
                breakt.setTypeface(custom_font);
                breakt.setAllCaps(false);
                if (fclass.classes.get(s).name != null && !fclass.classes.get(s).name.equals("")) {
                    all.addView(breakt);
                }
                final int finalS1 = s;
                breakt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final Dialog dialog = new Dialog(Main.this);
                        dialog.setCancelable(true);
                        LinearLayout di = new LinearLayout(getApplicationContext());
                        TextView subjName = new TextView(getApplicationContext());
                        TextView hours = new TextView(getApplicationContext());
                        TextView fullInfo = new TextView(getApplicationContext());
                        subjName.setText(R.string.brkk);
                        //fclass.classes.get(s)
                        hours.setText(getRealEndTimeForHourNumber(fclass.classes.get(finalS1).hour-1) + "-" + getRealTimeForHourNumber(fclass.classes.get(finalS1).hour));
                        String fulltext=getBreak(fclass.classes.get(finalS1).hour-1)+" Minutes";
                        fullInfo.setText(fulltext);
                        di.setGravity(Gravity.CENTER);
                        di.setOrientation(LinearLayout.VERTICAL);
                        di.addView(subjName);
                        di.addView(hours);
                        di.addView(fullInfo);
                        subjName.setTextColor(textColor);
                        hours.setTextColor(textColor);
                        fullInfo.setTextColor(textColor);
                        subjName.setTextSize((float) 30);
                        hours.setTextSize((float) 30);
                        fullInfo.setTextSize((float) 30);
                        subjName.setGravity(Gravity.CENTER);
                        hours.setGravity(Gravity.CENTER);
                        fullInfo.setGravity(Gravity.CENTER);
                        subjName.setTypeface(custom_font, Typeface.BOLD);
                        hours.setTypeface(custom_font);
                        fullInfo.setTypeface(custom_font);
                        Button close = new Button(getApplicationContext());
                        close.setText(R.string.close);
                        close.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialog.dismiss();
                            }
                        });
                        close.setBackgroundColor(Color.TRANSPARENT);
                        close.setTextColor(textColor);
                        di.addView(close);
                        dialog.setContentView(di);
                        dialog.show();
                    }
                });
            }
            Button subj = new Button(this);
            String before;
            if (showTime) {
                before = "(" + getRealTimeForHourNumber(fclass.classes.get(s).hour) + ") " + fclass.classes.get(s).hour + ". ";
            } else {
                before = fclass.classes.get(s).hour + ". ";
            }
            String total = before + fclass.classes.get(s).name;
            subj.setText(total);
            subj.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
            subj.setTextSize((float) fontSize - 2);
            subj.setTextColor(textColor);
            subj.setBackground(getDrawable(R.drawable.button));
            subj.setPadding(20, 20, 20, 20);
            subj.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Light.Device.screenY(getApplicationContext()) / 10));
            subj.setTypeface(custom_font);
            final int finalS = s;
            subj.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final Dialog dialog = new Dialog(Main.this);
                    dialog.setCancelable(true);
                    LinearLayout di = new LinearLayout(getApplicationContext());
                    TextView subjName = new TextView(getApplicationContext());
                    TextView hours = new TextView(getApplicationContext());
                    TextView fullInfo = new TextView(getApplicationContext());
                    subjName.setText(fclass.classes.get(finalS).name);
                    //fclass.classes.get(s)
                    hours.setText(getRealTimeForHourNumber(fclass.classes.get(finalS).hour) + "-" + getRealEndTimeForHourNumber(fclass.classes.get(finalS).hour));
                    fullInfo.setText(fclass.classes.get(finalS).fullName);
                    di.setGravity(Gravity.CENTER);
                    di.setOrientation(LinearLayout.VERTICAL);
                    di.addView(subjName);
                    di.addView(hours);
                    di.addView(fullInfo);
                    subjName.setTextColor(textColor);
                    hours.setTextColor(textColor);
                    fullInfo.setTextColor(textColor);
                    subjName.setTextSize((float) 30);
                    hours.setTextSize((float) 30);
                    fullInfo.setTextSize((float) 30);
                    subjName.setGravity(Gravity.CENTER);
                    hours.setGravity(Gravity.CENTER);
                    fullInfo.setGravity(Gravity.CENTER);
                    subjName.setTypeface(custom_font, Typeface.BOLD);
                    hours.setTypeface(custom_font);
                    fullInfo.setTypeface(custom_font);
                    Button close = new Button(getApplicationContext());
                    close.setText(R.string.close);
                    close.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                        }
                    });
                    close.setBackgroundColor(Color.TRANSPARENT);
                    close.setTextColor(textColor);
                    di.addView(close);
                    dialog.setContentView(di);
                    dialog.show();
                }
            });
            if (fclass.classes.get(s).name != null && !fclass.classes.get(s).name.equals("")) {
                all.addView(subj);
            }
        }
        return all;
    }

    private String hourSystemForClassString(Class fclass, boolean showTime) {
        String allsubj = "";
        for (int s = 0; s < fclass.classes.size(); s++) {
            String before;
            if (showTime) {
                before = "(" + getRealTimeForHourNumber(fclass.classes.get(s).hour) + ") " + fclass.classes.get(s).hour + ". ";
            } else {
                before = fclass.classes.get(s).hour + ". ";
            }
            String total = before + fclass.classes.get(s).name;
            if (fclass.classes.get(s).name != null && !fclass.classes.get(s).name.equals("")) {
                allsubj += total + "\n";
            }
        }
        return allsubj;
    }

    private String getRealTimeForHourNumber(int hour) {
        switch (hour) {
            case 0:
                return "07:45";
            case 1:
                return "08:30";
            case 2:
                return "09:15";
            case 3:
                return "10:15";
            case 4:
                return "11:00";
            case 5:
                return "12:10";
            case 6:
                return "12:55";
            case 7:
                return "13:50";
            case 8:
                return "14:35";
            case 9:
                return "15:25";
            case 10:
                return "16:10";
            case 11:
                return "17:00";
            case 12:
                return "17:45";
        }
        return null;
    }

    private String getRealEndTimeForHourNumber(int hour) {
        switch (hour) {
            case 0:
                return "08:30";
            case 1:
                return "09:15";
            case 2:
                return "10:00";
            case 3:
                return "11:00";
            case 4:
                return "11:45";
            case 5:
                return "12:55";
            case 6:
                return "13:40";
            case 7:
                return "14:35";
            case 8:
                return "15:20";
            case 9:
                return "16:10";
            case 10:
                return "16:55";
            case 11:
                return "17:45";
            case 12:
                return "18:30";
        }
        return null;
    }
    private int getBreak(int washour){
        switch (washour){
            case 2:
                return 15;
            case 4:
                return 25;
            case 6:
                return 10;
            case 8:
                return 5;
            case 10:
                return 5;
        }
        return -1;
    }

    private void openApp() {
        final SharedPreferences sp = getPreferences(Context.MODE_PRIVATE);
        new GetLink(service, new GetLink.GotLink() {
            @Override
            public void onLinkGet(String link) {
                if (link != null) {
                    Log.i("LINK", link);
                    new Light.Net.NetFile.FileDownloader(link, new File(getApplicationContext().getFilesDir(), "hs.xls"), new Light.Net.NetFile.FileDownloader.OnDownload() {
                        @Override
                        public void onFinish(File file, boolean b) {
                            if (b) {
                                ArrayList<Class> classes = readExcelFile(file);
                                day = readExcelDay(file);
                                if (classes != null) {
                                    for (int cl = 0; cl < classes.size(); cl++) {
                                        for (int su = 0; su < classes.get(cl).classes.size(); su++) {
                                            Log.i(classes.get(cl).name + " " + classes.get(cl).classes.get(su).hour, classes.get(cl).classes.get(su).name);
                                        }
                                    }
                                    if (!sp.getBoolean("first", true)) {
                                        if(sp.getInt("last_recorded_version_code",0)!= Light.Device.getVersionCode(getApplicationContext(),getPackageName())){
                                            welcome(classes,true);
                                        }else {
                                            view(classes);
                                        }
                                    } else {
                                        welcome(classes,false);
                                    }
                                }
                            } else {
                                popup("Failed To Download Excel File");
                            }
                        }

                        @Override
                        public void onProgressChanged(File file, int i) {
                            Log.i("Downloader","Progress "+i);
                            pb.setIndeterminate(false);
                            pb.setMax(100);
                            pb.setProgress(i);
                        }
                    }).execute();
                } else {
                    popup("Could Not Fetch Link, Please Try Disconnecting From Wi-Fi");
                }
            }

            @Override
            public void onFail(String e) {
                popup(e);
            }
        }).execute();
    }

    private ArrayList<Class> readExcelFile(File f) {
        try {
            ArrayList<Class> classes = new ArrayList<>();
            POIFSFileSystem myFileSystem = new POIFSFileSystem(new FileInputStream(f));
            Workbook myWorkBook = new HSSFWorkbook(myFileSystem);
            Sheet mySheet = myWorkBook.getSheetAt(0);
            int rows = mySheet.getLastRowNum();
            int cols = mySheet.getRow(1).getLastCellNum();
            for (int c = 1; c < cols; c++) {
                ArrayList<Subject> subs = new ArrayList<>();
                for (int r = 2; r < rows; r++) {
                    Row row = mySheet.getRow(r);
                    subs.add(new Subject(r - 2, row.getCell(c).getStringCellValue().split("\\r?\\n")[0], row.getCell(c).getStringCellValue()));
                }
                classes.add(new Class(mySheet.getRow(1).getCell(c).getStringCellValue(), subs));
            }
            return classes;
        } catch (Exception e) {
            return null;
        }
    }

    private String readExcelDay(File f) {
        try {
            POIFSFileSystem myFileSystem = new POIFSFileSystem(new FileInputStream(f));
            Workbook myWorkBook = new HSSFWorkbook(myFileSystem);
            Sheet mySheet = myWorkBook.getSheetAt(0);
            int rows = mySheet.getLastRowNum();
            String day = mySheet.getRow(0).getCell(0).getStringCellValue();
            return day;
        } catch (Exception e) {
            return null;
        }
    }

    class Class {
        public String name;
        public ArrayList<Subject> classes;

        public Class(String name, ArrayList<Subject> classes) {
            this.name = name;
            this.classes = classes;
        }
    }
    class Subject {
        public int hour;
        public String name, fullName;

        public Subject(int hour, String name, String fullName) {
            this.hour = hour;
            this.name = name;
            this.fullName = fullName;
        }
    }
}
class GetLink extends AsyncTask<String, String, String> {
    private String ser;
    private GotLink gotlink;
    private boolean success;

    public GetLink(String service, GotLink gl) {
        ser = service;
        gotlink = gl;
    }

    @Override
    protected String doInBackground(String... strings) {
        try {
            Document docu = Jsoup.connect(ser).get();
            Elements doc = docu.select("a");
            String file = null;
            for (int i = 0; i < doc.size(); i++) {
                if (doc.get(i).attr("href").endsWith(".xls")) {
                    file = doc.get(i).attr("href");
                    break;
                }
            }
            success = true;
            return file;
        } catch (IOException e) {
            success = false;
            return e.getMessage();
        }
    }

    @Override
    protected void onPostExecute(String s) {
        if (success) {
            gotlink.onLinkGet(s);
        } else {
            gotlink.onFail(s);
        }
    }

    public interface GotLink {
        void onLinkGet(String link);

        void onFail(String e);
    }
}