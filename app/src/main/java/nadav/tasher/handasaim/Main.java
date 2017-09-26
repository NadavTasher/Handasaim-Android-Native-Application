package nadav.tasher.handasaim;

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
    private final int color = Color.parseColor("#336699");
    private final String serviceProvider = "http://handasaim.co.il";
    private final String service = "http://handasaim.co.il/2017/06/13/%D7%9E%D7%A2%D7%A8%D7%9B%D7%AA-%D7%95%D7%A9%D7%99%D7%A0%D7%95%D7%99%D7%99%D7%9D/";

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
        ProgressBar pb = new ProgressBar(this);
        pb.setIndeterminate(true);
        ll.addView(pb);
        setContentView(ll);
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
        getWindow().setStatusBarColor(color + 0x333333);
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
        navbarAll.setBackgroundColor(color + 0x333333);
        navbarAll.setOrientation(LinearLayout.HORIZONTAL);
        navbarAll.setGravity(Gravity.START|Gravity.CENTER_VERTICAL);
        nutIcon.setLayoutParams(nutParms);
        nutIcon.setImageDrawable(getDrawable(R.drawable.ic_icon));
        anim.setDuration(750);
        anim.setRepeatMode(ObjectAnimator.RESTART);
        anim.setRepeatCount(ObjectAnimator.INFINITE);
        anim.start();
        navbarAll.addView(nutIcon);
        navParms.gravity = Gravity.START;
        navbarAll.setLayoutParams(navParms);
        sall.addView(navbarAll);
        LinearLayout navSliderview=new LinearLayout(this);
        navSliderview.setGravity(Gravity.START);
        navSliderview.setOrientation(LinearLayout.HORIZONTAL);
        HorizontalScrollView navSliderviewscroll=new HorizontalScrollView(this);
        navSliderviewscroll.addView(navSliderview);
        navbarAll.addView(navSliderviewscroll);
        LinearLayout timeswitch=new LinearLayout(this);
        timeswitch.setBackground(getDrawable(R.drawable.back));
        timeswitch.setLayoutParams(new LinearLayout.LayoutParams(Light.Device.screenX(getApplicationContext())/4, Light.Device.screenY(getApplicationContext())/12));
        ImageView clock_ic=new ImageView(getApplicationContext());
        clock_ic.setLayoutParams(new LinearLayout.LayoutParams(Light.Device.screenY(getApplicationContext())/20,Light.Device.screenY(getApplicationContext())/20));
        clock_ic.setImageDrawable(getDrawable(R.drawable.ic_clock));
        timeswitch.addView(clock_ic);
        timeswitch.setPadding(20,20,20,20);
        Switch sw=new Switch(this);
        sw.setText(null);
        sw.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        timeswitch.setGravity(Gravity.CENTER);
        timeswitch.setOrientation(LinearLayout.HORIZONTAL);
        timeswitch.addView(sw);

        navSliderview.addView(timeswitch);
        LinearLayout share=new LinearLayout(this);
        share.setOrientation(LinearLayout.HORIZONTAL);
        share.setGravity(Gravity.CENTER);
        ImageButton sr=new ImageButton(this);
        sr.setImageDrawable(getDrawable(android.R.drawable.ic_menu_share));
        sr.setBackgroundColor(Color.TRANSPARENT);
        share.setBackground(getDrawable(R.drawable.back));
        sr.setLayoutParams(new LinearLayout.LayoutParams(Light.Device.screenY(getApplicationContext())/20,Light.Device.screenY(getApplicationContext())/20));
        share.addView(sr);
        sr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                share(currentClass.name+"\n"+hourSystemForClassString(currentClass,sp.getBoolean("show_time",true)));
            }
        });
        share.setLayoutParams(new LinearLayout.LayoutParams(Light.Device.screenX(getApplicationContext())/4, Light.Device.screenY(getApplicationContext())/12));
        navSliderview.addView(share);
        int selectedClass = 0;
        if (sp.getString("favorite_class", null) != null) {
            if(classes!=null) {
                for (int fc = 0; fc < classes.size(); fc++) {
                    if (sp.getString("favorite_class", "").equals(classes.get(fc).name)) {
                        selectedClass = fc;
                        break;
                    }
                }
            }else{
                popup("Downloaded Excel File Is Corrupted");
            }
        }
        ScrollView sv = new ScrollView(this);
        sv.addView(all);
        sall.addView(sv);
        final LinearLayout hsplace = new LinearLayout(this);
        hsplace.setGravity(Gravity.CENTER);
        hsplace.setOrientation(LinearLayout.VERTICAL);
        hsplace.setPadding(20,20,20,20);
        all.addView(hsplace);
        sw.setChecked(sp.getBoolean("show_time",true));
        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                sp.edit().putBoolean("show_time",b).commit();
                showHS(currentClass,hsplace,classes,b);
            }
        });
        if(classes!=null)
        showHS(classes.get(selectedClass), hsplace, classes,sp.getBoolean("show_time",true));
        setContentView(sall);
    }
    private void share(String st) {
        Intent s=new Intent(Intent.ACTION_SEND);
        s.putExtra(Intent.EXTRA_TEXT, st);
        s.setType("text/plain");
        s.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(Intent.createChooser(s, "Share With"));
    }
    private Class currentClass;
    private void showHS(final Class c, final LinearLayout hsplace, final ArrayList<Class> classes, final boolean showTime) {
        currentClass=c;
        hsplace.removeAllViews();
        final Typeface custom_font = Typeface.createFromAsset(getAssets(), "gisha.ttf");
        final Button className = new Button(this);
        className.setTextSize((float) 35);
//        className.setBackgroundColor(Color.TRANSPARENT);
        className.setGravity(Gravity.CENTER);
        className.setBackground(getDrawable(R.drawable.back));
        className.setText(c.name);
        className.setTextColor(Color.WHITE);
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
                        cls.setTextSize((float) 32);
                        cls.setGravity(Gravity.CENTER);
                        cls.setText(classes.get(cs).name);
                        cls.setTextColor(Color.WHITE);
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
                                showHS(classes.get(finalCs), hsplace, classes,showTime);
                                dialog.dismiss();
                            }
                        });
                    }
                }
                dialog.show();
            }
        });
        hsplace.addView(className);
        hsplace.addView(hourSystemForClass(c,showTime));
    }

    private LinearLayout hourSystemForClass(Class fclass,boolean showTime) {
        LinearLayout all = new LinearLayout(this);
        all.setGravity(Gravity.START | Gravity.CENTER_HORIZONTAL);
        all.setOrientation(LinearLayout.VERTICAL);
        Typeface custom_font = Typeface.createFromAsset(getAssets(), "gisha.ttf");
        for (int s = 0; s < fclass.classes.size(); s++) {
            TextView subj = new TextView(this);
            String before;
            if(showTime){
                before="("+getRealTimeForHourNumber(fclass.classes.get(s).hour)+") "+fclass.classes.get(s).hour + ". ";
            }else{
                before=fclass.classes.get(s).hour + ". ";
            }
            String total=before+ fclass.classes.get(s).name;
            subj.setText(total);
            subj.setGravity(Gravity.START);
            subj.setTextSize((float) 30);
            subj.setTextColor(Color.WHITE);
            subj.setTypeface(custom_font);
            if (fclass.classes.get(s).name != null && !fclass.classes.get(s).name.equals("")) {
                all.addView(subj);
            }
        }
        return all;
    }
    private String hourSystemForClassString(Class fclass,boolean showTime) {
        String allsubj="";
        for (int s = 0; s < fclass.classes.size(); s++) {
            String before;
            if(showTime){
                before="("+getRealTimeForHourNumber(fclass.classes.get(s).hour)+") "+fclass.classes.get(s).hour + ". ";
            }else{
                before=fclass.classes.get(s).hour + ". ";
            }
            String total=before+ fclass.classes.get(s).name;
            if (fclass.classes.get(s).name != null && !fclass.classes.get(s).name.equals("")) {
                allsubj+=total+"\n";
            }
        }
        return allsubj;
    }
    private String getRealTimeForHourNumber(int hour){
        switch(hour){
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
    private void openApp() {
        new GetLink(service, new GetLink.GotLink() {
            @Override
            public void onLinkGet(String link) {
                if(link!=null) {
                    Log.i("LINK", link);
                    new Light.Net.NetFile.FileDownloader(link, new File(getApplicationContext().getFilesDir(), "hs.xls"), new Light.Net.NetFile.FileDownloader.OnDownload() {
                        @Override
                        public void onFinish(File file, boolean b) {
                            if (b) {
                                ArrayList<Class> classes = readExcelFile(file);

                                if (classes != null) {
                                    for (int cl = 0; cl < classes.size(); cl++) {
                                        for (int su = 0; su < classes.get(cl).classes.size(); su++) {
                                            Log.i(classes.get(cl).name + " " + classes.get(cl).classes.get(su).hour, classes.get(cl).classes.get(su).name);
                                        }
                                    }
                                    view(classes);
                                }
                            }else{
                                popup("Failed To Download Excel File");
                            }
                        }

                        @Override
                        public void onProgressChanged(File file, int i) {
                        }
                    }).execute();
                }else{
                    popup("Could Not Fetch Link, Please Try Disconnecting From Wi-Fi");
                }
            }

            @Override
            public void onFail(String e) {
                popup(e);
            }
        }).execute();
    }

    private ArrayList<Class> readExcelFile(File f){
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
class getStringURL extends AsyncTask<String, Integer, String> {

    @Override
    protected String doInBackground(String... params) {
        String result = "";
        try {
            URL url = new URL(params[0]);
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
            in.close();
        } catch (MalformedURLException e) {
            return null;
        } catch (IOException e) {
            return null;
        }
        return result;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
    }
}