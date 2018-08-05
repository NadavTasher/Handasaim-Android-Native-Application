package nadav.tasher.handasaim.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;

import nadav.tasher.handasaim.R;
import nadav.tasher.handasaim.architecture.app.Center;
import nadav.tasher.handasaim.architecture.app.PreferenceManager;
import nadav.tasher.handasaim.architecture.app.graphics.RatioView;
import nadav.tasher.handasaim.tools.specific.NewsFetcher;
import nadav.tasher.handasaim.values.Egg;
import nadav.tasher.lightool.graphics.views.ExpandingView;
import nadav.tasher.lightool.graphics.views.Utils;
import nadav.tasher.lightool.info.Device;

public class NewsActivity extends Activity {

    private static final int waitTime = 10;
    private boolean started = false;
    private PreferenceManager pm;
    private Handler mHandler;

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
        getWindow().setNavigationBarColor(Center.getColorTop(getApplicationContext()));
        getWindow().setStatusBarColor(Center.getColorTop(getApplicationContext()));
        // Setup ScrollView
        ScrollView scrollView = new ScrollView(getApplicationContext());
        scrollView.setBackgroundColor(Center.getColorTop(getApplicationContext()));
        // Setup LinearLayout
        final LinearLayout fullScreen = new LinearLayout(getApplicationContext());
        fullScreen.setOrientation(LinearLayout.VERTICAL);
        fullScreen.setGravity(Gravity.CENTER);
        // Setup EasterEgg
        TextView mEggTop = getText(getResources().getString(R.string.interface_did_you_know), 1);
        final TextView mEggBottom = getText(Egg.dispenseEgg(Egg.TYPE_FACT), 0.9);
        mEggBottom.setPadding(0, 30, 0, 30);
        final ExpandingView mEggView = new ExpandingView(getApplicationContext(), Utils.getCoaster(getResources().getColor(R.color.coaster_bright), 32, 10), 500, Device.screenY(getApplicationContext()) / 8, mEggTop, mEggBottom);
        fullScreen.addView(mEggView);
        new NewsFetcher(getString(R.string.provider_internal_news), new NewsFetcher.OnFinish() {
            @Override
            public void onNewsFetch(ArrayList<NewsFetcher.Article> articles) {
                for (final NewsFetcher.Article article : articles) {
                    TextView title = getText(article.getTitle(), 1);
                    TextView button = getText(getResources().getString(R.string.interface_open_in_browser), 0.8);
                    button.setBackground(Utils.getCoaster(getResources().getColor(R.color.default_bottom), 20, 20));
                    button.setPadding(50,50,50,50);
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(article.getUrl())));
                        }
                    });
                    ExpandingView mArticleView = new ExpandingView(getApplicationContext(), Utils.getCoaster(getResources().getColor(R.color.coaster_bright), 32, 10), 500, Device.screenY(getApplicationContext()) / 8, title, button);
                    fullScreen.addView(mArticleView);
                }
            }

            @Override
            public void onFail() {
                if (!started) {
                    started = true;
                    Center.enter(NewsActivity.this, HomeActivity.class);
                }
            }
        }).execute();
        scrollView.addView(fullScreen);
        setContentView(scrollView);
        mHandler = new Handler();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!started) {
                    started = true;
                    Center.enter(NewsActivity.this, HomeActivity.class);
                }
            }
        }, 1000 * waitTime);
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
}
