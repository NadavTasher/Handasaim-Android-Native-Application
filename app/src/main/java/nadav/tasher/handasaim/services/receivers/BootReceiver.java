package nadav.tasher.handasaim.services.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import nadav.tasher.handasaim.tools.architecture.Starter;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Starter.scheduleJobs(context);
    }
}
