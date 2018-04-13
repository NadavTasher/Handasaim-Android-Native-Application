package nadav.tasher.handasaim.services.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import nadav.tasher.handasaim.tools.architecture.Starter;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) Starter.beginDND(context);
        Starter.scheduleJobs(context);
    }
}
