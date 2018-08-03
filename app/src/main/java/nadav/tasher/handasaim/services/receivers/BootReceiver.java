package nadav.tasher.handasaim.services.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import nadav.tasher.handasaim.architecture.app.Starter;
import nadav.tasher.handasaim.services.BackgroundService;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent!=null&&intent.getAction()!=null) {
            if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
                BackgroundService.scheduleJob(context);
            }
        }
    }
}
