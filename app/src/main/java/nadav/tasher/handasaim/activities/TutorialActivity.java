package nadav.tasher.handasaim.activities;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

import nadav.tasher.handasaim.R;
import nadav.tasher.handasaim.architecture.app.Center;
import nadav.tasher.handasaim.architecture.app.PreferenceManager;
import nadav.tasher.handasaim.architecture.app.graphics.LessonView;
import nadav.tasher.handasaim.architecture.app.graphics.RatioView;
import nadav.tasher.lightool.graphics.views.Utils;
import nadav.tasher.lightool.graphics.views.appview.navigation.corner.Corner;
import nadav.tasher.lightool.graphics.views.appview.navigation.corner.CornerView;
import nadav.tasher.lightool.info.Device;

import static nadav.tasher.handasaim.architecture.app.Center.generateTime;

public class TutorialActivity extends Activity {

    private PreferenceManager pm;
    private FrameLayout content;
    private Button next, back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initVars();
        go();
    }

    @Override
    public Context getApplicationContext() {
        return this;
    }

    private void initVars() {
        pm = new PreferenceManager(getApplicationContext());
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    private void go() {
        // Tutorial Screen
        getWindow().setNavigationBarColor(Center.getColorTop(getApplicationContext()));
        getWindow().setStatusBarColor(Center.getColorTop(getApplicationContext()));
        LinearLayout fullScreen = new LinearLayout(getApplicationContext());
        LinearLayout buttonLayout = new LinearLayout(getApplicationContext());
        content = new FrameLayout(getApplicationContext());
        content.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 11));
        content.setPadding(40, 0, 40, 0);
        fullScreen.setOrientation(LinearLayout.VERTICAL);
        fullScreen.setGravity(Gravity.BOTTOM);
        fullScreen.setBackgroundColor(Center.getColorTop(getApplicationContext()));
        buttonLayout.setOrientation(LinearLayout.HORIZONTAL);
        buttonLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Device.screenY(getApplicationContext()) / 12, 1));
        back = new Button(getApplicationContext());
        next = new Button(getApplicationContext());
        back.setTextSize((float) (Center.getFontSize(getApplicationContext()) / 1.5));
        next.setTextSize((float) (Center.getFontSize(getApplicationContext()) / 1.5));
        back.setTextColor(Center.getTextColor(getApplicationContext()));
        next.setTextColor(Center.getTextColor(getApplicationContext()));
        back.setBackground(null);
        next.setBackground(null);
        back.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1));
        next.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1));
        back.setTypeface(Center.getTypeface(getApplicationContext()));
        next.setTypeface(Center.getTypeface(getApplicationContext()));
        back.setText(R.string.interface_back);
        next.setText(R.string.interface_next);
        buttonLayout.addView(back);
        buttonLayout.addView(next);
        fullScreen.addView(content);
        fullScreen.addView(buttonLayout);
        initScreen1();
        setContentView(fullScreen);
    }

    private void initScreen1() {
        TextView welcomeTitle = new TextView(getApplicationContext());
        welcomeTitle.setText(R.string.interface_tutorial_welcome);
        welcomeTitle.setTypeface(Center.getTypeface(getApplicationContext()));
        welcomeTitle.setTextSize(Center.getFontSize(getApplicationContext()));
        welcomeTitle.setGravity(Gravity.CENTER);
        welcomeTitle.setTextColor(Center.getTextColor(getApplicationContext()));
        TextView explainationMessage = new TextView(getApplicationContext());
        explainationMessage.setText(R.string.interface_tutorial_explanation);
        explainationMessage.setTypeface(Center.getTypeface(getApplicationContext()));
        explainationMessage.setTextSize((float) (Center.getFontSize(getApplicationContext()) / 1.5));
        explainationMessage.setGravity(Gravity.CENTER);
        explainationMessage.setTextColor(Center.getTextColor(getApplicationContext()));
        back.setVisibility(View.GONE);
        LinearLayout screen1 = new LinearLayout(getApplicationContext());
        screen1.setOrientation(LinearLayout.VERTICAL);
        screen1.setGravity(Gravity.CENTER);
        screen1.addView(welcomeTitle);
        screen1.addView(explainationMessage);
        content.removeAllViews();
        content.addView(screen1);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetButtons();
                initScreen2();
            }
        });
    }

    private void initScreen2() {
        TextView explanationLessonView = new TextView(getApplicationContext());
        explanationLessonView.setText(R.string.interface_tutorial_lessonview_explanation);
        explanationLessonView.setTypeface(Center.getTypeface(getApplicationContext()));
        explanationLessonView.setTextSize((float) (Center.getFontSize(getApplicationContext()) / 1.5));
        explanationLessonView.setGravity(Gravity.CENTER);
        explanationLessonView.setTextColor(Center.getTextColor(getApplicationContext()));
        explanationLessonView.setPadding(0, 40, 0, 40);
        LessonView mLessonView = new LessonView(getApplicationContext(), LessonView.MARK_TYPE_NORMAL, getResources().getString(R.string.interface_tutorial_lessonview_example_top), generateTime(1), new ArrayList<String>(Arrays.asList(getResources().getString(R.string.interface_tutorial_lessonview_example_bottom1), getResources().getString(R.string.interface_tutorial_lessonview_example_bottom2))));
        LinearLayout screen2 = new LinearLayout(getApplicationContext());
        screen2.setOrientation(LinearLayout.VERTICAL);
        screen2.setGravity(Gravity.CENTER);
        screen2.addView(explanationLessonView);
        screen2.addView(mLessonView);
        content.removeAllViews();
        content.addView(screen2);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetButtons();
                initScreen1();
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetButtons();
                initScreen3();
            }
        });
    }

    private void initScreen3() {
        TextView explanationCorner = new TextView(getApplicationContext());
        explanationCorner.setText(R.string.interface_tutorial_corner_explanation);
        explanationCorner.setTypeface(Center.getTypeface(getApplicationContext()));
        explanationCorner.setTextSize((float) (Center.getFontSize(getApplicationContext()) / 1.5));
        explanationCorner.setGravity(Gravity.CENTER);
        explanationCorner.setTextColor(Center.getTextColor(getApplicationContext()));
        explanationCorner.setPadding(0, 40, 0, 40);
        // Corner Setup
        CornerView cornerView = new CornerView(getApplicationContext());
        cornerView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        Corner corner = new Corner(getApplicationContext(), Device.screenX(getApplicationContext()) / 5, Center.getCombinedColor(getApplicationContext()));
        cornerView.setBottomRight(corner);
        LinearLayout infoText = new LinearLayout(getApplicationContext());
        infoText.setOrientation(LinearLayout.VERTICAL);
        infoText.setGravity(Gravity.CENTER);
        final RatioView name, day;
        name = new RatioView(getApplicationContext(), 0.9);
        day = new RatioView(getApplicationContext(), 0.7);
        name.setText(R.string.interface_tutorial_corner_example_top);
        name.setTextColor(Center.getTextColor(getApplicationContext()));
        name.setTextSize(Center.getFontSize(getApplicationContext()));
        name.setTypeface(Center.getTypeface(getApplicationContext()));
        name.setGravity(Gravity.CENTER);
        day.setText(R.string.interface_tutorial_corner_example_bottom);
        day.setTextColor(Center.getTextColor(getApplicationContext()));
        day.setTextSize(Center.getFontSize(getApplicationContext()));
        day.setTypeface(Center.getTypeface(getApplicationContext()));
        day.setGravity(Gravity.CENTER);
        infoText.addView(name);
        infoText.addView(day);
        corner.setView(infoText, 0.8);
        // Corner Setup
        TextView explanationCornerLocation = new TextView(getApplicationContext());
        explanationCornerLocation.setText(R.string.interface_tutorial_corner_choose_location);
        explanationCornerLocation.setTypeface(Center.getTypeface(getApplicationContext()));
        explanationCornerLocation.setTextSize((float) (Center.getFontSize(getApplicationContext()) / 1.5));
        explanationCornerLocation.setGravity(Gravity.CENTER);
        explanationCornerLocation.setTextColor(Center.getTextColor(getApplicationContext()));
        explanationCornerLocation.setPadding(0, 40, 0, 40);
        Switch cornerLocation = new Switch(getApplicationContext());
        cornerLocation.setTypeface(Center.getTypeface(getApplicationContext()));
        cornerLocation.setTextSize((float) (Center.getFontSize(getApplicationContext()) / 1.5));
        //        cornerLocation.setGravity(Gravity.CENTER);
        cornerLocation.setLayoutParams(new LinearLayout.LayoutParams((int) (Device.screenX(getApplicationContext()) / 1.5), ViewGroup.LayoutParams.WRAP_CONTENT));
        cornerLocation.setTextColor(Center.getTextColor(getApplicationContext()));
        cornerLocation.setChecked(pm.getUserManager().get(R.string.preferences_user_corner_location, getResources().getString(R.string.corner_location_right)).equals(getResources().getString(R.string.corner_location_right)));
        if (cornerLocation.isChecked()) {
            cornerLocation.setText(getResources().getString(R.string.interface_corner_choose_right));
        } else {
            cornerLocation.setText(getResources().getString(R.string.interface_corner_choose_left));
        }
        cornerLocation.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    compoundButton.setText(getResources().getString(R.string.interface_corner_choose_right));
                    pm.getUserManager().set(R.string.preferences_user_corner_location, getResources().getString(R.string.corner_location_right));
                } else {
                    compoundButton.setText(getResources().getString(R.string.interface_corner_choose_left));
                    pm.getUserManager().set(R.string.preferences_user_corner_location, getResources().getString(R.string.corner_location_left));
                }
            }
        });
        LinearLayout screen3 = new LinearLayout(getApplicationContext());
        screen3.setOrientation(LinearLayout.VERTICAL);
        screen3.setGravity(Gravity.CENTER);
        screen3.addView(explanationCorner);
        screen3.addView(cornerView);
        screen3.addView(explanationCornerLocation);
        screen3.addView(cornerLocation);
        content.removeAllViews();
        content.addView(screen3);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetButtons();
                initScreen2();
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetButtons();
                initScreen4();
            }
        });
    }

    private void initScreen4() {
        next.setText(R.string.interface_finish);
        TextView doneTitle = new TextView(getApplicationContext());
        doneTitle.setText(R.string.interface_tutorial_title_done);
        doneTitle.setTypeface(Center.getTypeface(getApplicationContext()));
        doneTitle.setTextSize(Center.getFontSize(getApplicationContext()));
        doneTitle.setGravity(Gravity.CENTER);
        doneTitle.setTextColor(Center.getTextColor(getApplicationContext()));
        TextView explanationDone = new TextView(getApplicationContext());
        explanationDone.setText(R.string.interface_tutorial_explanation_done);
        explanationDone.setTypeface(Center.getTypeface(getApplicationContext()));
        explanationDone.setTextSize((float) (Center.getFontSize(getApplicationContext()) / 1.5));
        explanationDone.setGravity(Gravity.CENTER);
        explanationDone.setTextColor(Center.getTextColor(getApplicationContext()));
        explanationDone.setPadding(0, 40, 0, 40);
        Button enterCodes = new Button(getApplicationContext());
        enterCodes.setBackground(Utils.getCoaster(getResources().getColor(R.color.coaster_bright), 20, 10));
        enterCodes.setText(R.string.interface_enter_codes);
        enterCodes.setAllCaps(false);
        enterCodes.setLayoutParams(new LinearLayout.LayoutParams((int) (Device.screenX(getApplicationContext()) / 1.5), ViewGroup.LayoutParams.WRAP_CONTENT));
        enterCodes.setTypeface(Center.getTypeface(getApplicationContext()));
        enterCodes.setTextSize((float) (Center.getFontSize(getApplicationContext()) / 1.6));
        enterCodes.setGravity(Gravity.CENTER);
        enterCodes.setTextColor(Center.getTextColor(getApplicationContext()));
        enterCodes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetButtons();
                initScreenCodes();
            }
        });
        LinearLayout screen4 = new LinearLayout(getApplicationContext());
        screen4.setOrientation(LinearLayout.VERTICAL);
        screen4.setGravity(Gravity.CENTER);
        screen4.addView(doneTitle);
        screen4.addView(explanationDone);
        screen4.addView(enterCodes);
        content.removeAllViews();
        content.addView(screen4);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetButtons();
                initScreen3();
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetButtons();
                pm.getUserManager().set(R.string.preferences_user_launch_first, false);
                Center.enter(TutorialActivity.this, HomeActivity.class);
            }
        });
    }

    private void initScreenCodes() {
        next.setVisibility(View.GONE);
        LinearLayout screen4 = new LinearLayout(getApplicationContext());
        screen4.setOrientation(LinearLayout.VERTICAL);
        screen4.setGravity(Gravity.CENTER);
        screen4.addView(Center.getCodeEntering(getApplicationContext()));
        content.removeAllViews();
        content.addView(screen4);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetButtons();
                initScreen4();
            }
        });
    }

    private void resetButtons() {
        back.setVisibility(View.VISIBLE);
        next.setVisibility(View.VISIBLE);
        back.setText(R.string.interface_back);
        next.setText(R.string.interface_next);
    }
}
