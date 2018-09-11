package nadav.tasher.handasaim.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;

import nadav.tasher.handasaim.R;
import nadav.tasher.handasaim.architecture.app.Center;
import nadav.tasher.handasaim.architecture.app.PreferenceManager;
import nadav.tasher.handasaim.architecture.app.graphics.RatioView;
import nadav.tasher.handasaim.architecture.appcore.components.Schedule;
import nadav.tasher.lightool.graphics.views.ExpandingView;
import nadav.tasher.lightool.graphics.views.Utils;
import nadav.tasher.lightool.info.Device;

public class TimeTravelActivity extends Activity {

    private PreferenceManager pm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initVars();
        loadUI();
    }

    private void initVars() {
        pm = new PreferenceManager(getApplicationContext());
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    // TODO add a refresh button

    private void loadUI() {
        getWindow().setStatusBarColor(Center.getColorTop(getApplicationContext()));
        getWindow().setNavigationBarColor(Center.getColorBottom(getApplicationContext()));
        final LinearLayout full = new LinearLayout(getApplicationContext());
        final LinearLayout schedulesLayout = new LinearLayout(getApplicationContext());
        full.setGravity(Gravity.CENTER);
        schedulesLayout.setGravity(Gravity.CENTER);
        full.setOrientation(LinearLayout.VERTICAL);
        schedulesLayout.setOrientation(LinearLayout.VERTICAL);
        TextView clearButton = getText("Clear Time-Travel", 0.8);
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pm.getCoreManager().clearSchedules();
                Center.exit(TimeTravelActivity.this, SplashActivity.class);
            }
        });
        full.setBackground(generateGradient(Center.getColorTop(getApplicationContext()), Center.getColorBottom(getApplicationContext())));
        full.addView(getText(getResources().getString(R.string.interface_time_travel_title), 1.1));
        full.addView(clearButton);
        ArrayList<Schedule> schedules = pm.getCoreManager().getSchedules();
        for (int i = 0; i < schedules.size(); i++) {
            schedulesLayout.addView(getTimeTraver(schedules.get(i), i));
        }
        ScrollView scheduleScroll = new ScrollView(getApplicationContext());
        scheduleScroll.addView(schedulesLayout);
        full.addView(scheduleScroll);
        setContentView(full);
    }

    private RatioView getText(String text, double ratio) {
        RatioView mRatio = new RatioView(getApplicationContext(), ratio);
        mRatio.setTypeface(Center.getTypeface(getApplicationContext()));
        mRatio.setTextColor(Center.getTextColor(getApplicationContext()));
        mRatio.setTextSize(Center.getFontSize(getApplicationContext()));
        mRatio.setGravity(Gravity.CENTER);
        mRatio.setText(text);
        return mRatio;
    }

    private ExpandingView getTimeTraver(Schedule schedule, final int index) {
        TextView title = getText(((index == 0) ? "Latest" : schedule.getName()) + " (" + schedule.getDay() + ")", 1);
        title.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Device.screenY(getApplicationContext()) / 10));
        TextView button = getText(getResources().getString(R.string.interface_time_travel_button), 0.8);
        button.setBackground(Utils.getCoaster(Center.getColorBottom(getApplicationContext()), 20, 20));
        button.setPadding(50, 50, 50, 50);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TimeTravelActivity.this, HomeActivity.class);
                intent.putExtra(getResources().getString(R.string.schedule_index), index);
                Center.enter(TimeTravelActivity.this, intent);
            }
        });
        ExpandingView expandingView = new ExpandingView(getApplicationContext());
        expandingView.setBackground(Utils.getCoaster(getResources().getColor(R.color.coaster_bright), 32, 10));
        expandingView.setTop(title);
        expandingView.setBottom(button);
        return expandingView;
    }

    private Drawable generateGradient(int colorA, int colorB) {
        return new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, new int[]{
                colorA,
                colorB
        });
    }

    @Override
    public void onBackPressed() {
        Center.exit(this, HomeActivity.class);
        super.onBackPressed();
    }
}
