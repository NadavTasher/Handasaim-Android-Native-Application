package nadav.tasher.handasaim;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

public class DNDService extends Service {
    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
    private BroadcastReceiver stopReceiver,dndReceiver;
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("DNDService","Started!");
        stopReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(dndReceiver!=null){
                    unregisterReceiver(dndReceiver);
                }
                sendBroadcast(new Intent(Main.KILL_DND));
                unregisterReceiver(this);
                stopSelf();
            }
        };
        registerReceiver(stopReceiver, new IntentFilter(Main.KILL_DND_SERVICE));
        startDND();
        return START_STICKY;
    }
    void startDND(){
            if(dndReceiver!=null){
                unregisterReceiver(dndReceiver);
            }
            sendBroadcast(new Intent(Main.KILL_DND));
            IntentFilter o = new IntentFilter();
            o.addAction(Intent.ACTION_TIME_TICK);
            o.addAction(Main.KILL_DND);
            dndReceiver=new Main.DNDReceiver();
            registerReceiver(dndReceiver, o);
    }
}