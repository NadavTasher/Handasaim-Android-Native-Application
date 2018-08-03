package nadav.tasher.handasaim.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.os.Build;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Random;

import nadav.tasher.handasaim.R;
import nadav.tasher.handasaim.activities.SplashActivity;
import nadav.tasher.handasaim.architecture.app.PreferenceManager;
import nadav.tasher.handasaim.tools.specific.GetLink;
import nadav.tasher.lightool.communication.OnFinish;
import nadav.tasher.lightool.communication.SessionStatus;
import nadav.tasher.lightool.communication.network.Ping;
import nadav.tasher.lightool.communication.network.request.Post;
import nadav.tasher.lightool.communication.network.request.RequestParameter;

public class BackgroundService extends JobService {

    private PreferenceManager pm;
    private boolean pushFinished = false, refreshFinished = false;

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
        new Ping(getResources().getString(R.string.provider_external), getResources().getInteger(R.integer.ping_timeout), new Ping.OnEnd() {
            @Override
            public void onPing(boolean b) {
                if (b) {
                    JSONArray filters = new JSONArray();
                    int channel = pm.getServicesManager().getChannel(0);
                    if (channel != 0) {
                        filters.put(0);
                    }
                    filters.put(channel);
                    new Post(getResources().getString(R.string.provider_external_push), new RequestParameter[]{new RequestParameter(getResources().getString(R.string.push_request_parameter_filter), filters.toString())}, new OnFinish() {
                        @Override
                        public void onFinish(SessionStatus sessionStatus) {
                            if (sessionStatus.getStatus() == SessionStatus.FINISHED_SUCCESS) {
                                if (sessionStatus.getExtra() != null) {
                                    if (!sessionStatus.getExtra().isEmpty()) {
                                        try {
                                            JSONObject response = new JSONObject(sessionStatus.getExtra());
                                            if (response.getString(getResources().getString(R.string.push_response_parameter_mode)).equals(getResources().getString(R.string.push_response_parameter_mode_client))) {
                                                if (response.getBoolean(getResources().getString(R.string.push_response_parameter_approved))) {
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
                            }
                            pushFinished = true;
                            done(jobParameters);
                        }
                    }).execute();
                } else {
                    pushFinished = true;
                    done(jobParameters);
                }
            }
        }).execute();
    }

    private void checkForNewSchedule(final JobParameters jobParameters) {
        new Ping(getResources().getString(R.string.provider_internal), getResources().getInteger(R.integer.ping_timeout), new Ping.OnEnd() {
            @Override
            public void onPing(boolean b) {
                if (b) {
                    new GetLink(getResources().getString(R.string.provider_internal_schedule), new GetLink.GotLink() {
                        @Override
                        public void onLinkGet(String link) {
                            if (link != null) {
                                String date = link.split("/")[link.split("/").length - 1].split("\\.")[0];
                                if (!pm.getServicesManager().getScheduleNotifiedAlready(date)) {
                                    pm.getServicesManager().setScheduleNotifiedAlready(date);
                                    inform("", "");
                                }
                                refreshFinished = true;
                                done(jobParameters);
                            }
                        }

                        @Override
                        public void onFail(String e) {
                            refreshFinished = true;
                            done(jobParameters);
                        }
                    }).execute();
                } else {
                    refreshFinished = true;
                    done(jobParameters);
                }
            }
        }).execute();
    }

    private void done(JobParameters parameters) {
        if (pushFinished && refreshFinished) jobFinished(parameters, false);
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
