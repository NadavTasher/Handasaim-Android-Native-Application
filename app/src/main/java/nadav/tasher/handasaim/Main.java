package nadav.tasher.handasaim;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import nadav.tasher.lightool.Light;

public class Main extends Activity {
    private final int color = Color.parseColor("#336699");
    private final String serviceProvider="http://handasaim.co.il";
    private final String service="http://handasaim.co.il/2017/06/13/%D7%9E%D7%A2%D7%A8%D7%9B%D7%AA-%D7%95%D7%A9%D7%99%D7%A0%D7%95%D7%99%D7%99%D7%9D/";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startApp();
    }
    private void splash() {
        final Window window=getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(color);
        window.setNavigationBarColor(color);
        LinearLayout ll=new LinearLayout(this);
        ll.setGravity(Gravity.CENTER);
        ll.setOrientation(LinearLayout.VERTICAL);
        ll.setBackgroundColor(color);
        ImageView icon=new ImageView(this);
        icon.setImageDrawable(getDrawable(R.drawable.ic_icon));
        int is=(int) (Light.Device.screenX(getApplicationContext()) * 0.8);
        icon.setLayoutParams(new LinearLayout.LayoutParams(is, is));
        ll.addView(icon);
        ProgressBar pb=new ProgressBar(this);
        pb.setIndeterminate(true);
        ll.addView(pb);
        setContentView(ll);
    }
    private void checkInternet(){
        if(Light.Device.isOnline(getApplicationContext())){
            new Light.Net.Pinger(5000, new Light.Net.Pinger.OnEnd() {
                @Override
                public void onPing(String s, boolean b) {
                    if(s.equals(serviceProvider)&&b){
                        openApp();
                    }else if(s.equals(serviceProvider)&&!b){
                        popupRetry("Server Error: No Response From Service Provider.");
                    }
                }
            }).execute(serviceProvider);
        }else{
            popupRetry("No Internet Connection.");
        }
    }
    private void startApp(){
        splash();
        checkInternet();
    }
    void popupRetry(String text) {
        AlertDialog.Builder pop=new AlertDialog.Builder(this);
        pop.setCancelable(true);
        pop.setMessage("Error: " + text);
        pop.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
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
    private void openApp(){

    }
}
