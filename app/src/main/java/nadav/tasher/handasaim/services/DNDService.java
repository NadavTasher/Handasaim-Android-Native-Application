package nadav.tasher.handasaim.services;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import nadav.tasher.handasaim.services.receivers.DNDReceiver;
import nadav.tasher.handasaim.values.Values;

public class DNDService extends Service {
    private BroadcastReceiver stopReceiver, dndReceiver;

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //        Log.i("DNDService","Started!");
        stopReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (dndReceiver != null) {
                    unregisterReceiver(dndReceiver);
                }
                sendBroadcast(new Intent(Values.KILL_DND));
                unregisterReceiver(this);
                stopSelf();
            }
        };
        registerReceiver(stopReceiver, new IntentFilter(Values.KILL_DND_SERVICE));
        startDND();
        return START_STICKY;
    }

    void startDND() {
        sendBroadcast(new Intent(Values.KILL_DND));
        IntentFilter o = new IntentFilter();
        o.addAction(Intent.ACTION_TIME_TICK);
        o.addAction(Values.KILL_DND);
        dndReceiver = new DNDReceiver();
        registerReceiver(dndReceiver, o);
    }

    @Override
    public void onDestroy() {
        sendBroadcast(new Intent(Values.KILL_DND));
    }
}
