package nadav.tasher.handasaim.architecture.app.graphics;

import android.app.Activity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import nadav.tasher.handasaim.R;
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
//        message.setPadding(20, 0, 20, 0);
        message.setSingleLine();
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), message.getText().toString(), Toast.LENGTH_LONG).show();
            }
        });
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
            @Override
            public void run() {
                a.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (!messages.isEmpty()) {
                            if (currentIndex + 1 < messages.size()) {
                                currentIndex++;
                            } else {
                                currentIndex = 0;
                            }
                            message.setText(messages.get(currentIndex));
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