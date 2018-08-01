package nadav.tasher.handasaim.architecture.app.graphics;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;

import nadav.tasher.handasaim.values.Values;
import nadav.tasher.lightool.graphics.views.Utils;
import nadav.tasher.lightool.graphics.views.appview.navigation.drawer.Drawer;
import nadav.tasher.lightool.info.Device;

public class MessageBar extends LinearLayout {

    private ArrayList<String> messages;
    private RatioView message;
    private int currentIndex = 0;
    private boolean alive = true;
    private Thread animate;
    private OnMessage onMessage;
    private Activity a;

    public MessageBar(Activity context, ArrayList<String> messages, Drawer drag) {
        super(context.getApplicationContext());
        this.a = context;
        this.messages = messages;
        init();
    }

    private void init() {
        setOrientation(HORIZONTAL);
        setGravity(Gravity.CENTER);
        setBackground(Utils.getCoaster(Values.messageColor, 32, 5));
        setPadding(20, 10, 20, 10);
        setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Device.screenY(getContext()) / 10));
        message = new RatioView(getContext(), 0.65);
        message.setSingleLine();
        message.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(onMessage!=null){
                    String text=null;
                    if(messages!=null&&messages.size()>0)text=messages.get(currentIndex);
                    onMessage.onMessage(text,currentIndex);
                }
            }
        });
        addView(message);
        make();
    }

    public void setOnMessage(OnMessage onMessage) {
        this.onMessage = onMessage;
    }

    private void make() {
        animate = new Thread(new Runnable() {

            void disappear() {
                ObjectAnimator disappear = ObjectAnimator.ofFloat(message, View.ALPHA, 1, 0);
                disappear.setDuration(1000);
                disappear.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        next();
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {
                    }
                });
                disappear.start();
            }

            void go() {
                appear();
            }

            void next() {
                if (currentIndex < messages.size() - 1) {
                    currentIndex++;
                } else {
                    currentIndex = 0;
                }
                a.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        message.setText(messages.get(currentIndex));
                    }
                });
                go();
            }

            void appear() {
                ObjectAnimator appear = ObjectAnimator.ofFloat(message, View.ALPHA, 0, 1);
                appear.setDuration(1000);
                appear.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        pause();
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {
                    }
                });
                appear.start();
            }

            void pause() {
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        disappear();
                    }
                }, 1000);
            }

            @Override
            public void run() {
                a.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        go();
                    }
                });
            }
        });
    }

    public void start() {
        if (animate != null)
            animate.start();
    }

    public void stop() {
        alive = false;
    }

    public interface OnMessage{
        void onMessage(String message,int index);
    }
}
