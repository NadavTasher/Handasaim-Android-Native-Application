package nadav.tasher.handasaim.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;

import nadav.tasher.handasaim.R;
import nadav.tasher.handasaim.architecture.app.Center;
import nadav.tasher.handasaim.architecture.app.NewsFetcher;
import nadav.tasher.handasaim.architecture.app.graphics.RatioView;
import nadav.tasher.handasaim.values.Egg;
import nadav.tasher.lightool.graphics.views.ExpandingView;
import nadav.tasher.lightool.graphics.views.Utils;
import nadav.tasher.lightool.info.Device;

public class NewsActivity extends Activity {

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
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    private void go() {
        getWindow().setStatusBarColor(Center.getColorTop(getApplicationContext()));
        getWindow().setNavigationBarColor(Center.getColorBottom(getApplicationContext()));
        // Setup ScrollView
        ScrollView scrollView = new ScrollView(getApplicationContext());
        scrollView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) (Device.screenY(getApplicationContext()) * 0.85)));
        // Setup LinearLayouts
        final LinearLayout fullScreen = new LinearLayout(getApplicationContext());
        fullScreen.setOrientation(LinearLayout.VERTICAL);
        fullScreen.setGravity(Gravity.CENTER);
        fullScreen.setBackground(Center.getGradient(getApplicationContext()));
        final LinearLayout newsLayout = new LinearLayout(getApplicationContext());
        newsLayout.setOrientation(LinearLayout.VERTICAL);
        newsLayout.setGravity(Gravity.CENTER);
        final LinearLayout bottomNavigation = new LinearLayout(getApplicationContext());
        bottomNavigation.setOrientation(LinearLayout.HORIZONTAL);
        bottomNavigation.setGravity(Gravity.CENTER);
        bottomNavigation.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Device.screenY(getApplicationContext()) / 12));
        // Setup SKIP Button
        Button skipButton = new Button(getApplicationContext());
        skipButton.setAllCaps(false);
        skipButton.setText(R.string.interface_skip);
        skipButton.setTextSize(Center.getFontSize(getApplicationContext()));
        skipButton.setTextColor(Center.getTextColor(getApplicationContext()));
        skipButton.setBackground(Utils.getCoaster(getResources().getColor(R.color.coaster_bright), 32, 10));
        skipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Center.enter(NewsActivity.this, HomeActivity.class);
            }
        });
        skipButton.setPadding(20, 20, 20, 20);
        skipButton.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        bottomNavigation.addView(skipButton);
        // Setup EasterEgg
        TextView mEggTop = getText(getResources().getString(R.string.interface_did_you_know), 1);
        mEggTop.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Device.screenY(getApplicationContext()) / 12));
        final TextView mEggBottom = getText(Egg.dispenseEgg(Egg.TYPE_FACT), 0.7);
        mEggBottom.setPadding(0, 30, 0, 30);
        final ExpandingView mEggView = new ExpandingView(getApplicationContext());
        mEggView.setBackground(Utils.getCoaster(getResources().getColor(R.color.coaster_bright), 32, 10));
        mEggView.setTop(mEggTop);
        mEggView.setBottom(mEggBottom);
        newsLayout.addView(mEggView);
        new NewsFetcher(getString(R.string.provider_internal_news), new NewsFetcher.OnFinish() {
            @Override
            public void onNewsFetch(ArrayList<NewsFetcher.Article> articles) {
                for (final NewsFetcher.Article article : articles) {
                    TextView title = getText(article.getTitle(), 0.8);
                    title.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Device.screenY(getApplicationContext()) / 12));
                    TextView button = getText(getResources().getString(R.string.interface_open_in_browser), 0.7);
                    button.setPadding(50, 50, 50, 50);
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(article.getUrl())));
                        }
                    });
                    ExpandingView mArticleView = new ExpandingView(getApplicationContext());
                    mArticleView.setBackground(Utils.getCoaster(getResources().getColor(R.color.coaster_bright), 32, 10));
                    mArticleView.setTop(title);
                    mArticleView.setBottom(button);
                    newsLayout.addView(mArticleView);
                }
            }

            @Override
            public void onFail() {
            }
        }).execute();
        scrollView.addView(newsLayout);
        fullScreen.addView(scrollView);
        fullScreen.addView(bottomNavigation);
        setContentView(fullScreen);
    }

    private RatioView getText(String text, double ratio) {
        RatioView mRatio = new RatioView(getApplicationContext(), ratio);
        mRatio.setTextColor(Center.getTextColor(getApplicationContext()));
        mRatio.setTextSize(Center.getFontSize(getApplicationContext()));
        mRatio.setGravity(Gravity.CENTER);
        mRatio.setText(text);
        return mRatio;
    }
}
