package nadav.tasher.handasaim.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;

import nadav.tasher.handasaim.R;
import nadav.tasher.handasaim.architecture.app.Center;
import nadav.tasher.handasaim.tools.online.PictureLoader;
import nadav.tasher.handasaim.tools.specific.GetNews;
import nadav.tasher.handasaim.values.Egg;
import nadav.tasher.lightool.graphics.views.Utils;
import nadav.tasher.lightool.info.Device;

public class NewsActivity extends Activity {

    private boolean started = false;
    private static final int waitTime = 10;

    private SharedPreferences sp;
    private KeyManager keyManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initVars();
        go();
    }

    @Override
    public Context getApplicationContext(){
        return this;
    }

    private void initVars(){
        sp=getSharedPreferences(Values.prefName,MODE_PRIVATE);
        keyManager=new KeyManager(getApplicationContext());
    }


    private void go() {
        
        getWindow().setStatusBarColor(Center.getColorA(getApplicationContext()));
        getWindow().setNavigationBarColor(Center.getColorB(getApplicationContext()));

            final LinearLayout full = new LinearLayout(getApplicationContext());
            full.setGravity(Gravity.CENTER);
            full.setOrientation(LinearLayout.VERTICAL);
            full.setPadding(10, 10, 10, 10);
            LinearLayout newsAll = new LinearLayout(getApplicationContext());
            newsAll.setGravity(Gravity.CENTER);
            final LinearLayout loadingTView = new LinearLayout(getApplicationContext());
            loadingTView.setGravity(Gravity.CENTER);
            loadingTView.setOrientation(LinearLayout.VERTICAL);
            //            loadingTView.setBackground(getDrawable(R.drawable.rounded_rect));
            final TextView loadingText = new TextView(getApplicationContext()), egg = new TextView(getApplicationContext());
            loadingText.setGravity(Gravity.CENTER);
            loadingText.setText(R.string.loading_text);
            loadingText.setTextColor(Color.LTGRAY);
            loadingText.setTypeface(Center.getTypeface(getApplicationContext()));
            loadingText.setTextSize(Center.getFontSize(getApplicationContext()) + 4);
            loadingTView.addView(loadingText);
            loadingTView.setPadding(20, 20, 20, 20);
            egg.setGravity(Gravity.CENTER);
            egg.setText(Egg.dispenseEgg(Egg.TYPE_BOTH));
            egg.setTextColor(Color.LTGRAY);
            egg.setTypeface(Center.getTypeface(getApplicationContext()));
            egg.setTextSize(Center.getFontSize(getApplicationContext()) - 8);
            //            egg.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            loadingTView.addView(egg);
            loadingTView.setGravity(Gravity.CENTER);
            //            loadingTView.setBackgroundColor(Color.BLACK);
            loadingTView.setLayoutParams(new LinearLayout.LayoutParams((int) (Device.screenX(getApplicationContext()) * 0.8), ViewGroup.LayoutParams.MATCH_PARENT));
            newsAll.setLayoutParams(new ScrollView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            full.addView(loadingTView);
            //            newsAll.setBackgroundColor(Color.GREEN);
            final LinearLayout news = new LinearLayout(getApplicationContext());
            news.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.START);
            newsAll.setOrientation(LinearLayout.VERTICAL);
            news.setOrientation(LinearLayout.VERTICAL);
            //        news.setAlpha(0.5f);
            newsAll.addView(news);
            final ScrollView newsAllSV = new ScrollView(getApplicationContext());
            //            full.setBackgroundColor(Center.getColorB(getApplicationContext()));
            full.setBackground(Center.getGradient(getApplicationContext()));
            newsAllSV.addView(newsAll);
            newsAllSV.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            full.addView(newsAllSV);
            full.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            news.setVisibility(View.GONE);
            setContentView(full);
            new GetNews(Values.serviceProvider, new GetNews.GotNews() {
                @Override
                public void onNewsGet(final ArrayList<GetNews.Link> link) {
                    loadingTView.setVisibility(View.GONE);
                    news.setVisibility(View.VISIBLE);
                    for (int n = 0; n < link.size(); n++) {
                        final LinearLayout nt = new LinearLayout(getApplicationContext());
                        nt.setOrientation(LinearLayout.VERTICAL);
                        nt.setGravity(Gravity.CENTER);
                        Button newtopic = new Button(getApplicationContext());
                        nt.addView(newtopic);
                        nt.setPadding(10, 10, 10, 10);
                        nt.setBackground(Utils.getCoaster( Values.classCoasterColor,32,10));
                        newtopic.setText(link.get(n).name);
                        newtopic.setEllipsize(TextUtils.TruncateAt.END);
                        newtopic.setTextColor(Center.getTextColor(getApplicationContext()));
                        newtopic.setTextSize(Center.getFontSize(getApplicationContext()) - 10);
                        newtopic.setPadding(20, 10, 20, 10);
                        newtopic.setEllipsize(TextUtils.TruncateAt.END);
                        newtopic.setLines(2);
                        newtopic.setBackground(null);
                        newtopic.setTypeface(Center.getTypeface(getApplicationContext()));
                        if (!link.get(n).imgurl.equals("") && link.get(n).imgurl != null) {
                            final int finalN1 = n;
                            new PictureLoader(link.get(n).imgurl, new PictureLoader.GotImage() {

                                @Override
                                public void onGet(Bitmap image) {
                                    ImageView imageView = new ImageView(getApplicationContext());
                                    imageView.setImageBitmap(image);
                                    imageView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Device.screenY(getApplicationContext()) / 3));
                                    imageView.setPadding(20, 20, 20, 40);
                                    imageView.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            String url = link.get(finalN1).url;
                                            Intent i = new Intent(Intent.ACTION_VIEW);
                                            i.setData(Uri.parse(url));
                                            getApplicationContext().startActivity(i);
                                        }
                                    });
                                    if (image != null) nt.addView(imageView);
                                }
                            }).execute();
                        }
                        newtopic.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Device.screenY(getApplicationContext()) / 8));
                        final int finalN = n;
                        newtopic.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String url = link.get(finalN).url;
                                Intent i = new Intent(Intent.ACTION_VIEW);
                                i.setData(Uri.parse(url));
                                getApplicationContext().startActivity(i);
                            }
                        });
                        news.addView(nt);
                        newsAllSV.setScrollY(0);
                    }
                }

                @Override
                public void onFail(ArrayList<GetNews.Link> e) {
                    if (!started) {
                        started = true;
                        // TODO start main
                    }
                }
            }).execute();
            new CountDownTimer((Values.waitTime + 1) * 1000, 1000) {

                @Override
                public void onTick(long millisUntilFinished) {
                    if (millisUntilFinished <= 2000) {
                        // TODO Start Main
                    }
                }

                @Override
                public void onFinish() {
                    //                            waiting.setVisibility(View.GONE);
                    //                            nextButton.setVisibility(View.VISIBLE);
                }
            }.start();

    }
}
