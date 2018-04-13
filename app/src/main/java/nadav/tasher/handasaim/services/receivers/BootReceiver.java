package nadav.tasher.handasaim.services.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import nadav.tasher.handasaim.activities.Main;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) Main.beginDND(context);
        Main.scheduleJobs(context);
    }
}
