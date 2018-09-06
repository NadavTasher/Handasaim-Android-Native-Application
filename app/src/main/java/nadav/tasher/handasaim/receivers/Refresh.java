package nadav.tasher.handasaim.receivers;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Random;

import nadav.tasher.handasaim.R;
import nadav.tasher.handasaim.activities.SplashActivity;
import nadav.tasher.handasaim.architecture.app.LinkFetcher;
import nadav.tasher.handasaim.architecture.app.PreferenceManager;
import nadav.tasher.lightool.communication.network.Requester;
import nadav.tasher.lightool.info.Device;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.Response;

public class Refresh extends BroadcastReceiver {

    private static final String ACTION = "nadav.tasher.handasaim.REFRESH";
    private static final int ID = 102;
    private PreferenceManager pm;

    public static void reschedule(Context context) {
//        Log.i("Receiver","here2");
        Calendar cal = Calendar.getInstance();
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent broadcastIntent = new Intent(context, Refresh.class);
        PendingIntent broadcastPendingIntent =
                PendingIntent.getBroadcast(context,
                        ID,
                        broadcastIntent,
                        PendingIntent.FLAG_CANCEL_CURRENT);
        if (am != null) {
            am.setRepeating(
                    AlarmManager.RTC_WAKEUP,
                    cal.getTimeInMillis(),
                    context.getResources().getInteger(R.integer.background_loop_time),
                    broadcastPendingIntent
            );
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        start(context);
    }

    private void checkForNewPushes(final Context context) {
        try {
            JSONArray filters = new JSONArray();
            int channel = pm.getServicesManager().getChannel(0);
            if (channel != 0) {
                filters.put(0);
            }
            filters.put(channel);
            new Requester(new Request.Builder().url(context.getResources().getString(R.string.provider_external_push)).post(new MultipartBody.Builder().setType(MultipartBody.FORM).addFormDataPart("filter", filters.toString()).build()), new Requester.Callback() {
                @Override
                public void onCall(Response serverResponse) {
                    try {
                        if (serverResponse != null) {
                            if (serverResponse.isSuccessful()) {
                                if (serverResponse.body() != null) {
                                    try {
                                        JSONObject response = new JSONObject(serverResponse.body().string());
                                        if (response.getString(context.getResources().getString(R.string.push_response_parameter_mode)).equals(context.getResources().getString(R.string.push_response_parameter_mode_client))) {
                                            if (response.has(context.getResources().getString(R.string.push_response_parameter_approved)) && response.getBoolean(context.getResources().getString(R.string.push_response_parameter_approved))) {
                                                // Client Mode & Approved -> Scan Pushes
                                                JSONArray pushes = response.getJSONArray(context.getResources().getString(R.string.push_response_parameter_pushes));
                                                for (int p = 0; p < pushes.length(); p++) {
                                                    JSONObject push = pushes.getJSONObject(p);
                                                    String pushId = push.getString(context.getResources().getString(R.string.push_response_parameter_push_id));
                                                    if (!pm.getServicesManager().getPushDisplayedAlready(pushId)) {
                                                        // Push Not Displayed Yet, Write It To Preferences And Display.
                                                        pm.getServicesManager().setPushDisplayedAlready(pushId);
                                                        // Display Notification
                                                        String titleBuilder =
                                                                "(" +
                                                                        push.getString(context.getResources().getString(R.string.push_response_parameter_push_sender)) +
                                                                        ')' +
                                                                        ' ' +
                                                                        push.getString(context.getResources().getString(R.string.push_response_parameter_push_title)) +
                                                                        ':';
                                                        inform(context, titleBuilder, push.getString(context.getResources().getString(R.string.push_response_parameter_push_message)));
                                                    }
                                                }
                                            }
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void start(Context context) {
//        Log.i("Receiver","here");
        // Setup the PreferenceManager.
        pm = new PreferenceManager(context);
        // Check if device is online
        if (Device.isOnline(context)) {
            // Check if Push Service is enabled.
            if (pm.getUserManager().get(R.string.preferences_user_service_push, context.getResources().getBoolean(R.bool.default_service_push))) {
                checkForNewPushes(context);
            }
            // Check if Refresh Service is enabled.
            if (pm.getUserManager().get(R.string.preferences_user_service_refresh, context.getResources().getBoolean(R.bool.default_service_refresh))) {
                checkForNewSchedule(context);
            }
        }
    }

    private void checkForNewSchedule(final Context context) {
        try {
            new LinkFetcher(context.getResources().getString(R.string.provider_internal_schedule_page), context.getResources().getString(R.string.provider_internal_schedule_page_fallback), new LinkFetcher.OnFinish() {
                @Override
                public void onLinkFetch(String link) {
                    if (!pm.getServicesManager().getScheduleNotifiedAlready(link)) {
                        pm.getServicesManager().setScheduleNotifiedAlready(link);
                        inform(context, context.getResources().getString(R.string.refresh_text_title), context.getResources().getString(R.string.refresh_text_message));
                    }
                }

                @Override
                public void onFail() {
                }
            }).execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void inform(Context context, String title, String message) {
        Notification.Builder mBuilder = new Notification.Builder(context)
                .setSmallIcon(R.drawable.ic_class)
                .setContentTitle(title)
                .setContentText(message)
                .setShowWhen(true)
                .setAutoCancel(true)
                .setContentIntent(PendingIntent.getActivity(
                        context,
                        0,
                        new Intent(context, SplashActivity.class),
                        PendingIntent.FLAG_UPDATE_CURRENT
                ))
                .setDefaults(Notification.DEFAULT_ALL);
        NotificationManager mManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        //        Intent mIntent = new Intent(this, SplashActivity.class);
        //        mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (mManager != null) {
            if (Build.VERSION.SDK_INT >= 26) {
                mManager.createNotificationChannel(new NotificationChannel(context.getString(R.string.app_name), context.getString(R.string.app_name), NotificationManager.IMPORTANCE_DEFAULT));
                mBuilder.setChannelId(context.getString(R.string.app_name));
            }
            mManager.notify(context.getString(R.string.app_name), new Random().nextInt(1000), mBuilder.build());
        }
    }
}
