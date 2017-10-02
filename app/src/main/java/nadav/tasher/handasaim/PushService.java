package nadav.tasher.handasaim;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import java.util.Timer;
import java.util.TimerTask;

public class PushService extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
    private BroadcastReceiver stopReceiver;
    @Override
    public void onCreate() {
        stopReceiver=new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                unregisterReceiver(this);
                stopSelf();
            }
        };
        registerReceiver(stopReceiver,new IntentFilter(Main.STOP_SERVICE));
        listenForPush();
    }
    private void listenForPush(){
        Timer timer=new Timer("HandasaimPushService", true);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {

            }
        }, 0, 1000*60*2);
    }
}
