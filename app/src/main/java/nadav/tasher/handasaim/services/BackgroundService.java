package nadav.tasher.handasaim.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import org.json.JSONArray;
import org.json.JSONObject;

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

public class BackgroundService extends JobService {

    private static final int ID = 102;
    private PreferenceManager pm;
    private boolean pushFinished = false, refreshFinished = false;

    public static void reschedule(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Device.isJobServiceScheduled(context, ID)) {
                ComponentName serviceComponent = new ComponentName(context, BackgroundService.class);
                JobInfo.Builder builder = new JobInfo.Builder(ID, serviceComponent);
                builder.setMinimumLatency(context.getResources().getInteger(R.integer.background_loop_time));
                JobScheduler jobScheduler = context.getSystemService(JobScheduler.class);
                if (jobScheduler != null) {
                    jobScheduler.schedule(builder.build());
                }
            }
        }
    }

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        // Setup the PreferenceManager.
        pm = new PreferenceManager(getApplicationContext());
        // Check if Push Service is enabled.
        if (pm.getUserManager().get(R.string.preferences_user_service_push, getResources().getBoolean(R.bool.default_service_push))) {
            checkForNewPushes(jobParameters);
        }
        // Check if Refresh Service is enabled.
        if (pm.getUserManager().get(R.string.preferences_user_service_refresh, getResources().getBoolean(R.bool.default_service_refresh))) {
            checkForNewSchedule(jobParameters);
        }
        return !(pushFinished && refreshFinished);
    }

    private void checkForNewPushes(final JobParameters jobParameters) {
        JSONArray filters = new JSONArray();
        int channel = pm.getServicesManager().getChannel(0);
        if (channel != 0) {
            filters.put(0);
        }
        filters.put(channel);
        new Requester(new Request.Builder().url(getResources().getString(R.string.provider_external_push)).post(new MultipartBody.Builder().addFormDataPart("filter", filters.toString()).build()).build(), new Requester.Callback() {
            @Override
            public void onCall(Response serverResponse) {
                if (serverResponse.isSuccessful()) {
                    if (serverResponse.body() != null) {
                        try {
                            JSONObject response = new JSONObject(serverResponse.body().string());
                            if (response.getString(getResources().getString(R.string.push_response_parameter_mode)).equals(getResources().getString(R.string.push_response_parameter_mode_client))) {
                                if (response.has(getResources().getString(R.string.push_response_parameter_approved)) && response.getBoolean(getResources().getString(R.string.push_response_parameter_approved))) {
                                    // Client Mode & Approved -> Scan Pushes
                                    JSONArray pushes = response.getJSONArray(getResources().getString(R.string.push_response_parameter_pushes));
                                    for (int p = 0; p < pushes.length(); p++) {
                                        JSONObject push = pushes.getJSONObject(p);
                                        String pushId = push.getString(getResources().getString(R.string.push_response_parameter_push_id));
                                        if (!pm.getServicesManager().getPushDisplayedAlready(pushId)) {
                                            // Push Not Displayed Yet, Write It To Preferences And Display.
                                            pm.getServicesManager().setPushDisplayedAlready(pushId);
                                            // Display Notification
                                            String titleBuilder =
                                                    "(" +
                                                            push.getString(getResources().getString(R.string.push_response_parameter_push_sender)) +
                                                            ')' +
                                                            ' ' +
                                                            push.getString(getResources().getString(R.string.push_response_parameter_push_title)) +
                                                            ':';
                                            inform(titleBuilder, push.getString(getResources().getString(R.string.push_response_parameter_push_message)));
                                        }
                                    }
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                pushFinished = true;
                done(jobParameters);
            }
        }).execute();
    }

    private void checkForNewSchedule(final JobParameters jobParameters) {
        new LinkFetcher(getResources().getString(R.string.provider_internal_schedule), new LinkFetcher.OnFinish() {
            @Override
            public void onLinkFetch(String link) {
                if (!pm.getServicesManager().getScheduleNotifiedAlready(link)) {
                    pm.getServicesManager().setScheduleNotifiedAlready(link);
                    inform(getResources().getString(R.string.refresh_text_title), getResources().getString(R.string.refresh_text_message));
                }
                refreshFinished = true;
                done(jobParameters);
            }

            @Override
            public void onFail() {
                refreshFinished = true;
                done(jobParameters);
            }
        }).execute();
    }

    private void done(JobParameters parameters) {
        if (pushFinished && refreshFinished) jobFinished(parameters, false);
        reschedule(getApplicationContext());
    }

    private void inform(String title, String message) {
        Notification.Builder mBuilder = new Notification.Builder(this)
                .setSmallIcon(R.drawable.ic_class)
                .setContentTitle(title)
                .setContentText(message)
                .setShowWhen(true)
                .setAutoCancel(true)
                .setContentIntent(PendingIntent.getActivity(
                        this,
                        0,
                        new Intent(this, SplashActivity.class),
                        PendingIntent.FLAG_UPDATE_CURRENT
                ))
                .setDefaults(Notification.DEFAULT_ALL);
        NotificationManager mManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        //        Intent mIntent = new Intent(this, SplashActivity.class);
        //        mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (mManager != null) {
            if (Build.VERSION.SDK_INT >= 26) {
                mManager.createNotificationChannel(new NotificationChannel(getString(R.string.app_name), getString(R.string.app_name), NotificationManager.IMPORTANCE_DEFAULT));
                mBuilder.setChannelId(getString(R.string.app_name));
            }
            mManager.notify(getString(R.string.app_name), new Random().nextInt(1000), mBuilder.build());
        }
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return !(pushFinished && refreshFinished);
    }
}
