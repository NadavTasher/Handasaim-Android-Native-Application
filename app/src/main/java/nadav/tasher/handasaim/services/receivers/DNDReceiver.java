package nadav.tasher.handasaim.services.receivers;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.provider.Settings;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

import nadav.tasher.handasaim.R;
import nadav.tasher.handasaim.architecture.StudentClass;
import nadav.tasher.handasaim.values.Values;

import static nadav.tasher.handasaim.tools.architecture.AppCore.getTimeForLesson;
import static nadav.tasher.handasaim.tools.architecture.AppCore.readExcelFile;
import static nadav.tasher.handasaim.tools.architecture.AppCore.readExcelFileXLSX;

public class DNDReceiver extends BroadcastReceiver{
    private boolean isAlive = true;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() != null) {
            if (intent.getAction().equals(Values.KILL_DND)) {
                if (isAlive) {
                    isAlive = false;
                    try {
                        context.unregisterReceiver(this);
                    } catch (IllegalArgumentException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        final SharedPreferences sp = context.getSharedPreferences(Values.prefName, Context.MODE_PRIVATE);
        if (sp.getBoolean(Values.autoMute, false)) {
            if (Build.VERSION.SDK_INT >= 23) {
                NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                boolean granted = false;
                if (nm != null) {
                    granted = nm.isNotificationPolicyAccessGranted();
                }
                Calendar c = Calendar.getInstance();
                if (c.get(Calendar.DAY_OF_WEEK) != Calendar.FRIDAY && c.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY) {
                    if (granted) {
                        checkTime(context, sp);
                    } else {
                        sendNoPermissionNotification(context);
                    }
                }
            }
        }
    }

    void checkTime(Context context, SharedPreferences sp) {
        Calendar c = Calendar.getInstance();
        File excel = new File(context.getFilesDir(), sp.getString(Values.latestFileName, Values.latestFileNameDefault));
        String name = sp.getString(Values.latestFileName, "");
        ArrayList<StudentClass.Subject.Time> classTimes = new ArrayList<>();
        ArrayList<StudentClass> classes;
        if (!name.equals("")) {
            if (name.endsWith(".xlsx")) {
                classes = readExcelFileXLSX(excel);
            } else {
                classes = readExcelFile(excel);
            }
        } else {
            classes = new ArrayList<>();
        }
        if (sp.getString(Values.favoriteClass, null) != null) {
            if (classes != null) {
                for (int fc = 0; fc < classes.size(); fc++) {
                    if (sp.getString(Values.favoriteClass, "").equals(classes.get(fc).name)) {
                        ArrayList<StudentClass.Subject> subjects = classes.get(fc).subjects;
                        for (int sub = 0; sub < subjects.size(); sub++) {
                            classTimes.add(getTimeForLesson(subjects.get(sub).hour));
                        }
                        break;
                    }
                }
            }
        }
        for (int ct = 0; ct < classTimes.size(); ct++) {
            StudentClass.Subject.Time classTime = classTimes.get(ct);
            if (c.get(Calendar.MINUTE) == classTime.startM && c.get(Calendar.HOUR_OF_DAY) == classTime.startH) {
                NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                if (mNotificationManager != null) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        mNotificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_PRIORITY);
                    }
                }
            } else if (c.get(Calendar.MINUTE) == classTime.finishM && c.get(Calendar.HOUR_OF_DAY) == classTime.finishH) {
                NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                if (mNotificationManager != null) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        mNotificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALL);
                    }
                }
            }
        }
    }

    void sendNoPermissionNotification(Context c) {
        NotificationManager manager = (NotificationManager) c.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            notification = new Notification.Builder(c).setSmallIcon(R.mipmap.ic_launcher).setContentTitle(c.getResources().getString(R.string.app_name) + " Warning").setContentText("The app does not have 'Do Not Disturb' permissions.").setContentIntent(PendingIntent.getActivity(c, 0, new Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS), PendingIntent.FLAG_UPDATE_CURRENT)).build();
        }
        if (manager != null) {
            manager.notify(new Random().nextInt(100), notification);
        }
    }
}
