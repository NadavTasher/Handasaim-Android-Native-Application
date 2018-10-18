package nadav.tasher.handasaim.architecture.app.graphics;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import nadav.tasher.handasaim.R;
import nadav.tasher.handasaim.architecture.app.Center;
import nadav.tasher.lightool.graphics.views.Utils;
import nadav.tasher.lightool.info.Device;

public class MessageBar extends LinearLayout {

    private ArrayList<String> messages;
    private RatioView message;
    private int currentIndex = 0;
    private Timer timer;
    private Activity a;

    public MessageBar(Activity context) {
        super(context.getApplicationContext());
        this.a = context;
        init();
    }

    private void init() {
        setOrientation(HORIZONTAL);
        setGravity(Gravity.CENTER);
        setBackground(Utils.getCoaster(getContext().getResources().getColor(R.color.coaster_bright), 32, 5));
        setPadding(20, 10, 20, 10);
        setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Device.screenY(getContext()) / 10));
        message = new RatioView(getContext(), 0.65);
        message.setPadding(20, 0, 20, 0);
        message.setSingleLine();
        message.setTypeface(Center.getTypeface(getContext()));
        addView(message);
        make();
    }

    public void setMessages(ArrayList<String> messages) {
        this.messages = messages;
        currentIndex = 0;
    }

    public void setTextColor(int color) {
        message.setTextColor(color);
    }

    public void setTextSize(int size) {
        message.setTextSize(size);
    }

    private void make() {
        timer = new Timer();
        timer.schedule(new TimerTask() {

            private Handler handler;

            private void appear() {
                ObjectAnimator appear = ObjectAnimator.ofFloat(message, View.ALPHA, 0, 1);
                appear.setDuration(1000);
                appear.start();
            }

            private void disappear() {
                ObjectAnimator disappear = ObjectAnimator.ofFloat(message, View.ALPHA, 1, 0);
                disappear.setDuration(1000);
                disappear.start();
            }

            @Override
            public void run() {
                a.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (messages.size() > 1) {
                            if (messages.size() > currentIndex) {
                                message.setText(messages.get(currentIndex));
                            }
                            appear();
                            handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                public void run() {
                                    disappear();
                                    if (currentIndex < messages.size() - 1) {
                                        currentIndex++;
                                    } else {
                                        currentIndex = 0;
                                    }
                                }
                            }, 3000);
                        } else if (messages.size() == 1) {
                            if (messages.size() > currentIndex && !message.getText().toString().equals(messages.get(currentIndex))) {
                                message.setText(messages.get(currentIndex));
                                appear();
                            }
                        }
                    }
                });
            }
        }, 0, 5 * 1000);
    }

    public void stop() {
        timer.cancel();
    }
}
