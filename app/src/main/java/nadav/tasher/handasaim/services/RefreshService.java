package nadav.tasher.handasaim.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.Random;

import nadav.tasher.handasaim.R;
import nadav.tasher.handasaim.activities.Main;
import nadav.tasher.handasaim.tools.architecture.Starter;
import nadav.tasher.handasaim.tools.specific.GetLink;
import nadav.tasher.handasaim.values.Values;
import nadav.tasher.lightool.communication.network.Ping;

public class RefreshService extends JobService {

    private SharedPreferences sp;

    @Override
    public boolean onStartJob(final JobParameters params) {
        //        Log.i("RefreshService","RefreshService");
        Log.i("RefreshService", "Refreshing...");
        sp = getSharedPreferences(Values.prefName, Context.MODE_PRIVATE);
        if (sp.getBoolean(Values.scheduleService, Values.scheduleDefault)) {
            new Ping(Values.scheduleProvider, 5000, new Ping.OnEnd() {
                @Override
                public void onPing(boolean b) {
                    if (b) {
                        //                        Log.i("Current Value",sp.getString(Values.latestFileDateRefresher,"None"));
                        Log.i("Refresh","Refreshing");
                        checkForSchedule(sp, params);
                    } else {
                        jobFinished(params, true);
                    }
                }
            }).execute();
        }
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }

    private void checkForSchedule(final SharedPreferences sp, final JobParameters params) {
        new GetLink(Values.scheduleProvider, new GetLink.GotLink() {

            @Override
            public void onLinkGet(String link) {
                if (link != null) {
                    String date = link.split("/")[link.split("/").length - 1].split("\\.")[0];
                    if (!sp.getString(Values.latestFileDateRefresher, "").equals(date)) {
                        sp.edit().putString(Values.latestFileDateRefresher, date).commit();
                        showNotification();
                    }
                    Starter.startRefresh(getApplicationContext());
                    jobFinished(params, false);
                } else {
                    jobFinished(params, true);
                }
            }

            @Override
            public void onFail(String e) {
                jobFinished(params, true);
            }
        }).execute();
    }

    private void showNotification() {
        Notification.Builder mBuilder =
                new Notification.Builder(this)
                        .setSmallIcon(R.drawable.ic_notification)
                        .setContentTitle("New Schedule")
                        .setContentText("A New Schedule Has Been Uploaded. Click To Open App.")
                        .setDefaults(Notification.DEFAULT_ALL);
        NotificationManager mNotifyMgr =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Intent resultIntent = new Intent(this, Main.class);
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        this,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setAutoCancel(true);
        mBuilder.setContentIntent(resultPendingIntent);
        mBuilder.setShowWhen(true);
        if (mNotifyMgr != null)
            mNotifyMgr.notify(new Random().nextInt(1000), mBuilder.build());
    }
}
