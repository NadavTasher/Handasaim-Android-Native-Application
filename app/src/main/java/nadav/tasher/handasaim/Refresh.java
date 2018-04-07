package nadav.tasher.handasaim;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Random;

import nadav.tasher.lightool.Net;

public class Refresh extends JobService {

    static int getDay(int day, int month, int year) {
        int m = 31;
        int y = 12 * m;
        return year * y + month * m + day;
    }

    @Override
    public boolean onStartJob(final JobParameters params) {
        final SharedPreferences sp = getSharedPreferences(Main.Values.prefName, Context.MODE_PRIVATE);
        if (sp.getBoolean(Main.Values.pushService, Main.Values.pushDefault)) {
            new Net.Pinger(Main.Values.puzProvider, 5000, new Net.Pinger.OnEnd() {
                @Override
                public void onPing(boolean b) {
                    if (b) checkForPushes(sp);
                }
            }).execute();
        }
        if (sp.getBoolean(Main.Values.scheduleService, Main.Values.scheduleDefault)) {
            new Net.Pinger(Main.Values.scheduleProvider, 5000, new Net.Pinger.OnEnd() {
                @Override
                public void onPing(boolean b) {
                    if (b) checkForSchedule(sp);
                }
            }).execute();
        }
        Main.startRefresh(getApplicationContext());
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }

    private void checkForSchedule(final SharedPreferences sp) {
        new Main.GetLink(Main.Values.scheduleProvider, new Main.GetLink.GotLink() {

            @Override
            public void onLinkGet(String link) {
                if (link != null) {
                    String date = link.split("/")[link.split("/").length - 1].split("\\.")[0];
                    if (!sp.getString(Main.Values.latestFileDateRefresher, "").equals(date)) {
                        sp.edit().putString(Main.Values.latestFileDateRefresher, date).commit();
                        showNewScheduleNotification();
                    }
                }
            }

            @Override
            public void onFail(String e) {
            }
        }).execute();
    }

    private void showNewScheduleNotification() {
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

    private void checkForPushes(final SharedPreferences sp) {
        new Main.FileReader(Main.Values.pushProvider, new Main.FileReader.OnRead() {
            @Override
            public void done(String s) {
                if (s != null) {
                    try {
                        ArrayList<PushItem> tm = new ArrayList<>();
                        JSONObject reader = new JSONObject(s);
                        Iterator<String> types = reader.keys();
                        while (types.hasNext()) {
                            String name = types.next();
                            try {
                                PushItem i = new PushItem();
                                JSONObject uo = reader.getJSONObject(name);
                                i.id = Integer.parseInt(name);
                                i.text = uo.getString("text");
                                i.subtext = uo.getString("sub");
                                i.value = uo.getString("todo").replaceAll("\\[[a-z]+]", "");
                                String action = uo.getString("todo").split("]")[0].replaceAll("\\[|]", "");
                                i.action = PushItem.actionForString(action);
                                i.d = uo.getInt("d");
                                i.m = uo.getInt("m");
                                i.y = uo.getInt("y");
                                tm.add(i);
                            } catch (JSONException ignored) {
                            }
                        }
                        Calendar c = Calendar.getInstance();
                        for (int n = 0; n < tm.size(); n++) {
                            PushItem it = tm.get(n);
                            int cdate = getDay(c.get(Calendar.DAY_OF_MONTH), c.get(Calendar.MONTH) + 1, c.get(Calendar.YEAR));
                            int wdate = getDay(it.d, it.m, it.y);
                            boolean dated = cdate <= wdate;
                            if (!sp.getBoolean(Main.Values.pushID + it.id, false) && dated) {
                                sp.edit().putBoolean(Main.Values.pushID + it.id, true).apply();
                                showNotification(it);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).execute();
    }

    private void showNotification(PushItem pi) {
        Notification.Builder mBuilder =
                new Notification.Builder(this)
                        .setSmallIcon(R.drawable.ic_notification)
                        .setContentTitle(pi.text)
                        .setContentText(pi.subtext)
                        .setDefaults(Notification.DEFAULT_ALL);
        NotificationManager mNotifyMgr =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Intent resultIntent = new Intent();
        switch (pi.action) {
            case PushItem.URL:
                resultIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(pi.value));
                break;
            case PushItem.TOAST:
                resultIntent = new Intent(this, Main.class);
                resultIntent.putExtra("toast", pi.value);
                break;
            case PushItem.POPUP:
                resultIntent = new Intent(this, Main.class);
                resultIntent.putExtra("popup", pi.value);
                break;
            case PushItem.NONE:
                break;
        }
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
        mNotifyMgr.notify(new Random().nextInt(1000), mBuilder.build());
    }

    static class PushItem {
        static final int URL = 0;
        static final int POPUP = 1;
        static final int TOAST = 2;
        static final int NONE = -1;
        String text, subtext, value;
        int id, action, d, m, y;

        static int actionForString(String a) {
            switch (a) {
                case "url":
                    return URL;
                case "popup":
                    return POPUP;
                case "toast":
                    return TOAST;
                case "none":
                    return NONE;
                default:
                    return NONE;
            }
        }
    }
}