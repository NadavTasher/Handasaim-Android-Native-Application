package nadav.tasher.handasaim.architecture.app;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import nadav.tasher.handasaim.services.PushService;
import nadav.tasher.handasaim.services.RefreshService;

public class Starter {

    private static final int REFRESH_ID = 102;
    private static final int PUSH_ID = 103;

    public static void startRefresh(Context c) {
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M) {
            if (!isJobServiceOn(c, REFRESH_ID)) {
                Log.i("Starter","Refresh Scheduling");
                ComponentName serviceComponent = new ComponentName(c, RefreshService.class);
                JobInfo.Builder builder = new JobInfo.Builder(REFRESH_ID, serviceComponent);
                builder.setMinimumLatency(Values.refreshLoop);
                JobScheduler jobScheduler = c.getSystemService(JobScheduler.class);
                if (jobScheduler != null) {
                    jobScheduler.schedule(builder.build());
                    Log.i("Starter","Refresh Scheduled");

                }
            }
        }
    }

    public static void startPush(Context c) {
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M) {
            if (!isJobServiceOn(c, PUSH_ID)) {
                Log.i("Starter","Push Scheduling");
                ComponentName serviceComponent = new ComponentName(c, PushService.class);
                JobInfo.Builder builder = new JobInfo.Builder(PUSH_ID, serviceComponent);
                builder.setMinimumLatency(Values.refreshLoop);
                JobScheduler jobScheduler = c.getSystemService(JobScheduler.class);
                if (jobScheduler != null) {
                    jobScheduler.schedule(builder.build());
                    Log.i("Starter","Push Scheduled");
                }
            }
        }
    }

    public static void scheduleJobs(Context c) {
        Log.i("Starter","Scheduled");
        startPush(c);
        startRefresh(c);
    }

    private static boolean isJobServiceOn(Context context, int id) {
        JobScheduler scheduler = context.getSystemService(JobScheduler.class);
        boolean hasBeenScheduled = false;
        if (scheduler != null) {
            Log.i("Starter","Searching For Service");
            for (JobInfo jobInfo : scheduler.getAllPendingJobs()) {
                if (jobInfo.getId() == id) {
                    hasBeenScheduled = true;
                    Log.i("Starter","Found! "+jobInfo.toString());
                    break;
                }
            }
        }
        return hasBeenScheduled;
    }

}
