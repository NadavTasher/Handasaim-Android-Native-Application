package nadav.tasher.handasaim;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        final SharedPreferences sp = context.getSharedPreferences("app", Context.MODE_PRIVATE);
        if (sp.getBoolean("push", false)) {
            context.startService(new Intent(context, PushService.class));
        }
        Main.beginDND(context);
    }
}
