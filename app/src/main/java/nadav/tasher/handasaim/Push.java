package nadav.tasher.handasaim;

import android.app.Service;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

import nadav.tasher.lightool.Light;

public class Push extends JobService {

    @Override
    public boolean onStartJob(final JobParameters params) {
//        Toast.makeText(getApplicationContext(),"HELLO",Toast.LENGTH_LONG).show();
        Log.i("Hello","HIo");
        Main.startPush(getApplicationContext());
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }
}
