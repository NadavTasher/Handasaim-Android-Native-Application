package nadav.tasher.handasaim;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ExecutionException;

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
    private void view(ArrayList<Class> classes){
        getWindow().setStatusBarColor(color + 0x333333);
        final LinearLayout sall = new LinearLayout(this);
        final LinearLayout all = new LinearLayout(this);
        final LinearLayout navbarAll = new LinearLayout(this);
        final ImageView nutIcon = new ImageView(this);
        final int screenY = Light.Device.screenY(this);
        final int nutSize = (screenY / 8) - screenY / 30;
        final ObjectAnimator anim = ObjectAnimator.ofFloat(nutIcon, View.TRANSLATION_X, Light.Animations.VIBRATE_SMALL);
        final int navY = screenY / 8;
        final LinearLayout.LayoutParams nutParms = new LinearLayout.LayoutParams(nutSize, nutSize);
        final LinearLayout.LayoutParams navParms = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, navY);
        all.setOrientation(LinearLayout.VERTICAL);
        all.setGravity(Gravity.START|Gravity.CENTER_HORIZONTAL);
        all.setBackgroundColor(color);
        sall.setOrientation(LinearLayout.VERTICAL);
        sall.setGravity(Gravity.START|Gravity.CENTER_HORIZONTAL);
        sall.setBackgroundColor(color);
        navbarAll.setBackgroundColor(color + 0x333333);
        navbarAll.setOrientation(LinearLayout.HORIZONTAL);
        navbarAll.setGravity(Gravity.CENTER);
        nutIcon.setLayoutParams(nutParms);
        nutIcon.setImageDrawable(getDrawable(R.drawable.ic_icon));
        anim.setDuration(1500);
        anim.setRepeatMode(ObjectAnimator.RESTART);
        anim.setRepeatCount(ObjectAnimator.INFINITE);
        anim.start();
        navbarAll.addView(nutIcon);
        navParms.gravity = Gravity.START;
        navbarAll.setLayoutParams(navParms);
        sall.addView(navbarAll);
        final SharedPreferences sp=getPreferences(Context.MODE_PRIVATE);
        int selectedClass=0;
        if(sp.getString("favorite_class",null)!=null){
            for(int fc=0;fc<classes.size();fc++){
                if(sp.getString("favorite_class","").equals(classes.get(fc).name)){
                    selectedClass=fc;
                    break;
                }
            }
        }
//        Spinner chooser=new Spinner(this);
//        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item, classNames){
//            @Override
//            public View getView(int position, View convertView, ViewGroup parent) {
//                View v =super.getDropDownView(position, convertView, parent);
//
//                Typeface externalFont=Typeface.createFromAsset(getAssets(), "gisha.ttf");
//                ((TextView) v).setTypeface(externalFont);
//                ((TextView) v).setTextColor(Color.WHITE);
//                ((TextView) v).setTextSize((float)30);
//                ((TextView) v).setGravity(Gravity.START);
//                v.setLayoutDirection(TextView.LAYOUT_DIRECTION_RTL);
//                return v;
//            }
//        };
//        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
//        chooser.setAdapter(spinnerArrayAdapter);
//        chooser.setSelection(selectedClass);
//        final int selectedFinal=selectedClass;
//        chooser.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                if(i!=selectedFinal){
//                    sp.edit().putString("favorite_class",classNames.get(i)).commit();
//                    startApp();
//                }
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//            }
//        });
        ScrollView sv=new ScrollView(this);
        sv.addView(all);
        sall.addView(sv);
//        all.addView(chooser);
        LinearLayout hsplace=new LinearLayout(this);
        hsplace.setGravity(Gravity.CENTER);
        hsplace.setOrientation(LinearLayout.VERTICAL);
        all.addView(hsplace);
        showHS(classes.get(selectedClass),hsplace,classes);
        setContentView(sall);
    }
    private void showHS(final Class c, final LinearLayout hsplace, final ArrayList<Class> classes){
        hsplace.removeAllViews();
        final Typeface custom_font = Typeface.createFromAsset(getAssets(),  "gisha.ttf");
        final Button className=new Button(this);
        className.setTextSize((float)35);
        className.setGravity(Gravity.CENTER);
        className.setText(c.name);
        className.setTypeface(custom_font);
        final ArrayList<String> classNames=new ArrayList<>();
        for(int i=0;i<classes.size();i++){
            classNames.add(classes.get(i).name);
        }
        className.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LinearLayout classesll=new LinearLayout(getApplicationContext());
                classesll.setGravity(Gravity.CENTER);
                classesll.setOrientation(LinearLayout.VERTICAL);
                final Dialog dialog=new Dialog(Main.this);
                dialog.setCancelable(true);
                ScrollView classesllss=new ScrollView(getApplicationContext());
                classesllss.addView(classesll);
                dialog.setContentView(classesllss);
                for(int cs=0;cs<classes.size();cs++){
                    if(classes.get(cs)!=c){
                        Button cls=new Button(getApplicationContext());
                        cls.setTextSize((float)35);
                        cls.setGravity(Gravity.CENTER);
                        cls.setText(classes.get(cs).name);
                        cls.setTypeface(custom_font);
                        classesll.addView(cls);
                        final int finalCs = cs;
                        cls.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                final SharedPreferences sp=getPreferences(Context.MODE_PRIVATE);
                                sp.edit().putString("favorite_class",classes.get(finalCs).name).commit();
                                showHS(classes.get(finalCs),hsplace,classes);
                                dialog.dismiss();
                            }
                        });
                    }
                }
                dialog.show();
            }
        });
        hsplace.addView(className);
        hsplace.addView(hourSystemForClass(c));
    }
    private LinearLayout hourSystemForClass(Class fclass){
        LinearLayout all=new LinearLayout(this);
        all.setGravity(Gravity.START|Gravity.CENTER_HORIZONTAL);
        all.setOrientation(LinearLayout.VERTICAL);
        Typeface custom_font = Typeface.createFromAsset(getAssets(),  "gisha.ttf");
        for(int s=0;s<fclass.classes.size();s++){
            TextView subj=new TextView(this);
            subj.setText(fclass.classes.get(s).hour+". "+fclass.classes.get(s).name);
            subj.setGravity(Gravity.START);
            subj.setTextSize((float)35);
            subj.setTextColor(Color.WHITE);
            subj.setTypeface(custom_font);
            if(fclass.classes.get(s).name!=null&& !fclass.classes.get(s).name.equals("")) {
                all.addView(subj);
            }
        }
        return all;
    }
    private void openApp() {

        new GetLink(service, new GetLink.GotLink() {
            @Override
            public void onLinkGet(String link) {
                Log.i("LINK",link);
                new Light.Net.NetFile.FileDownloader(link, new File(getApplicationContext().getFilesDir(), "hs.xls"), new Light.Net.NetFile.FileDownloader.OnDownload() {
                    @Override
                    public void onFinish(File file, boolean b) {
                        if(b){
                            ArrayList<Class> classes=readExcelFile(file);
                            if(classes!=null) {
                                for (int cl = 0; cl < classes.size(); cl++) {
                                    for (int su = 0; su < classes.get(cl).classes.size(); su++) {
                                        Log.i(classes.get(cl).name+" "+classes.get(cl).classes.get(su).hour, classes.get(cl).classes.get(su).name);
                                    }
                                }
                            }
                            view(classes);
                        }
                    }

                    @Override
                    public void onProgressChanged(File file, int i) {
                    }
                }).execute();
            }

            @Override
            public void onFail(String e) {
                popup(e);
            }
        }).execute();
    }

    private ArrayList<Class> readExcelFile(File f) {
        try {
            ArrayList<Class> classes=new ArrayList<>();
            POIFSFileSystem myFileSystem = new POIFSFileSystem(new FileInputStream(f));
            Workbook myWorkBook = new HSSFWorkbook(myFileSystem);
            Sheet mySheet = myWorkBook.getSheetAt(0);
            int rows=mySheet.getLastRowNum();
            int cols=mySheet.getRow(1).getLastCellNum();
            for(int c=1;c<cols;c++){
                ArrayList<Subject> subs=new ArrayList<>();
                for(int r=2;r<rows;r++){
                    Row row=mySheet.getRow(r);
                    subs.add(new Subject(r-2,row.getCell(c).getStringCellValue().split("\\r?\\n")[0],row.getCell(c).getStringCellValue()));
                }
                classes.add(new Class(mySheet.getRow(1).getCell(c).getStringCellValue(),subs));
            }
            return classes;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
class Class{
    public String name;
    public ArrayList<Subject> classes;
    public Class(String name, ArrayList<Subject> classes){
        this.name=name;
        this.classes=classes;
    }
}
    class Subject{
        public int hour;
        public String name,fullName;
        public Subject(int hour,String name,String fullName){
            this.hour=hour;
            this.name=name;
            this.fullName=fullName;
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
                if (doc.get(i).attr("href").contains(".xsls") || doc.get(i).attr("href").contains(".xls")) {
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