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
    private BroadcastReceiver stopReceiver;
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("DNDService","Started!");
        stopReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                sendBroadcast(new Intent(Main.KILL_DND));
                unregisterReceiver(this);
                stopSelf();
            }
        };
        registerReceiver(stopReceiver, new IntentFilter(Main.KILL_DND_SERVICE));
        startDND(this);
        return START_STICKY;
    }
    static void startDND(Context context){
            context.sendBroadcast(new Intent(Main.KILL_DND));
            IntentFilter o = new IntentFilter();
            o.addAction(Intent.ACTION_TIME_TICK);
            o.addAction(Main.KILL_DND);
            context.registerReceiver(new Main.DNDReceiver(), o);
    }
}
