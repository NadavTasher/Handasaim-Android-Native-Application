package nadav.tasher.handasaim.tools.architecture;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import nadav.tasher.handasaim.services.DNDService;
import nadav.tasher.handasaim.services.PushService;
import nadav.tasher.handasaim.services.RefreshService;
import nadav.tasher.handasaim.values.Values;

public class Starter {

    static final int REFRESH_ID = 102;
    static final int PUSH_ID = 103;

    public static void beginDND(Context c) {
        try {
            c.sendBroadcast(new Intent(Values.KILL_DND_SERVICE));
            c.startService(new Intent(c, DNDService.class));
        } catch (IllegalStateException ignored) {
        }
    }

    public static void startRefresh(Context c) {
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M) {
            if (!isJobServiceOn(c, REFRESH_ID)) {
                ComponentName serviceComponent = new ComponentName(c, RefreshService.class);
                JobInfo.Builder builder = new JobInfo.Builder(REFRESH_ID, serviceComponent);
                builder.setMinimumLatency(Values.refreshLoop);
                JobScheduler jobScheduler = c.getSystemService(JobScheduler.class);
                if (jobScheduler != null) {
                    //                Log.i("RefreshService", "Scheduled");
                    jobScheduler.schedule(builder.build());
                }
            }
        }
    }

    public static void startPush(Context c) {
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M) {
            if (!isJobServiceOn(c, PUSH_ID)) {
                ComponentName serviceComponent = new ComponentName(c, PushService.class);
                JobInfo.Builder builder = new JobInfo.Builder(PUSH_ID, serviceComponent);
                builder.setMinimumLatency(Values.refreshLoop);
                JobScheduler jobScheduler = c.getSystemService(JobScheduler.class);
                if (jobScheduler != null) {
                    //                Log.i("PushService", "Scheduled");
                    jobScheduler.schedule(builder.build());
                }
            }
        }
    }

    public static void scheduleJobs(Context c) {
        startPush(c);
        startRefresh(c);
    }

    public static boolean isJobServiceOn(Context context, int id) {
        JobScheduler scheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        boolean hasBeenScheduled = false;
        if (scheduler != null) {
            for (JobInfo jobInfo : scheduler.getAllPendingJobs()) {
                if (jobInfo.getId() == id) {
                    hasBeenScheduled = true;
                    break;
                }
            }
        }
        return hasBeenScheduled;
    }

}
