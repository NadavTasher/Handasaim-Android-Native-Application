package nadav.tasher.handasaim;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.support.annotation.IntDef;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Random;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import nadav.tasher.lightool.Light;

public class PushService extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
    private BroadcastReceiver stopReceiver;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        stopReceiver=new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                unregisterReceiver(this);
                stopSelf();
            }
        };
        registerReceiver(stopReceiver,new IntentFilter(Main.STOP_SERVICE));
        listenForPush();
        return START_STICKY;
    }
    private void listenForPush(){
        final SharedPreferences sp = getSharedPreferences("app",Context.MODE_PRIVATE);
        Timer timer=new Timer("HandasaimPushService", true);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                ArrayList<Light.Net.PHP.Post.PHPParameter> parameters= new ArrayList<>();
                parameters.add(new Light.Net.PHP.Post.PHPParameter("get",""));
                final Light.Net.PHP.Post p=new Light.Net.PHP.Post(Main.pushProvider, parameters, new Light.Net.PHP.Post.OnPost() {
                    @Override
                    public void onPost(String s) {
                        try {
                            Log.i("HandasaimPushService","Loop");
                            JSONObject mainObject=new JSONObject(s);
                            boolean success=mainObject.getBoolean("success");
                            if(success){
                                JSONArray pushesArray=mainObject.getJSONArray("pushes");
                                for(int pA=0;pA<pushesArray.length();pA++){
                                    JSONObject push=pushesArray.getJSONObject(pA);
                                    String text=push.getString("data");
                                    String id=push.getString("id");
                                    JSONObject time=push.getJSONObject("time");
                                    String timeH=time.getString("h");
                                    String timeM=time.getString("m");
                                    if(!sp.getBoolean(id,false)){
                                        sendNotification(text,timeH,timeM);
                                        Log.i("HandasaimPushService","Notify");
                                        sp.edit().putBoolean(id,true).commit();
                                    }
                                }
                            }
                        } catch (JSONException e) {
                            Log.e("HandasaimPushService","Failed To Get, Skipping Loop.");
                        }
                    }
                });
                if(Light.Device.isOnline(getApplicationContext())){
                    new Light.Net.Pinger(5000, new Light.Net.Pinger.OnEnd() {
                        @Override
                        public void onPing(String s, boolean b) {
                            if(b){
                                p.execute("");
                            }else{
                                Log.i("HandasaimPushService","Failed To Connect To Server");
                            }
                        }
                    }).execute("http://handasaim.thepuzik.com");
                }
            }
        }, 0, 1000*60*2);
    }
    private void sendNotification(String text,String timeH,String timeM){
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Calendar mCalendar = new GregorianCalendar();
        TimeZone mTimeZone = mCalendar.getTimeZone();
        int mGMTOffset = mTimeZone.getRawOffset();
//        timeH+=TimeUnit.HOURS.convert(mGMTOffset, TimeUnit.MILLISECONDS);
        Notification notification = new NotificationCompat.Builder(getApplicationContext())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(getResources().getString(R.string.app_name)+" at "+timeH+":"+timeM+"GMT")
                .setContentText(text)
                .setDefaults(Notification.DEFAULT_SOUND|Notification.DEFAULT_VIBRATE)
                .build();
        manager.notify(new Random().nextInt(100),notification);
    }
}
