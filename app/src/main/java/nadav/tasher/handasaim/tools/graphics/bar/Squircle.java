package nadav.tasher.handasaim.tools.graphics.bar;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import nadav.tasher.handasaim.activities.Main;
import nadav.tasher.handasaim.tools.TunnelHub;
import nadav.tasher.handasaim.values.Values;
import nadav.tasher.lightool.communication.Tunnel;

public class Squircle extends FrameLayout{
    private int imageXY,contentXY, maxXY,color;
    private OnState onstate;
    private boolean isOpened = false;
    private LinearLayout inside;

    public Squircle(Context context,int xy,int backColor) {
        super(context);
        this.maxXY = xy;
        init();
        setColor(backColor);

    }

    private void init() {
        OnClickListener onClickListener=new OnClickListener() {
            @Override
            public void onClick(View v) {
                isOpened = !isOpened;
                if (isOpened) {
                    if (onstate != null) onstate.onOpen();
                } else {
                    if (onstate != null) onstate.onClose();
                }
                if (onstate != null) onstate.onBoth(isOpened);
            }
        };
        contentXY= (int) (maxXY*0.9);
        imageXY= (int) (maxXY*0.6);
        inside=new LinearLayout(getContext());
        inside.setOrientation(LinearLayout.VERTICAL);
        inside.setGravity(Gravity.CENTER);
        inside.setLayoutParams(new LayoutParams(maxXY,maxXY));
        setLayoutParams(new LayoutParams(maxXY, maxXY));
        setOnClickListener(onClickListener);
        TunnelHub.colorAChangeTunnle.addReceiver(new Tunnel.OnTunnel<Integer>() {
            @Override
            public void onReceive(Integer integer) {
                setColor(integer);
            }
        });
        addView(inside);
    }

    private void squircle() {
        int radii=32;
        float[] rad=new float[]{radii,radii,radii,radii,radii,radii,radii,radii};
        ShapeDrawable oval = new ShapeDrawable(new RoundRectShape(rad,null,null));
        oval.setIntrinsicHeight(maxXY);
        oval.setIntrinsicWidth(maxXY);
        oval.getPaint().setColor(color);
        setBackground(oval);
    }

    public int getContentXY(){
        return contentXY;
    }

    public int getMaxXY(){
        return maxXY;
    }

    public void setColor(int color){
        this.color= Color.argb(Values.squircleAlpha,Color.red(color),Color.green(color),Color.blue(color));
        squircle();
    }

    public void setDrawable(Drawable d){
        inside.removeAllViews();
        ImageView iv=new ImageView(getContext());
        iv.setLayoutParams(new LinearLayout.LayoutParams(imageXY,imageXY));
        iv.setImageDrawable(d);
        inside.addView(iv);
    }

    public void setText(int color,String upper, String lower) {
        if (upper.length() > 4) {
            upper = upper.substring(0, 4);
        }
        inside.removeAllViews();
        inside.addView(getTextView(upper, Main.getFontSize(getContext()) + 4, maxXY,color));
        inside.addView(getTextView(lower, Main.getFontSize(getContext()) - 10, contentXY,color));
    }

    public void setState(boolean state){
        isOpened=state;
    }

    private TextView getTextView(String t, int s, int par,int textColor) {
        final TextView v = new TextView(getContext());
        v.setTextColor(textColor);
        v.setTextSize(s);
        v.setText(t);
        v.setGravity(Gravity.CENTER);
        v.setTypeface(Main.getTypeface(getContext()));
        v.setLayoutParams(new LinearLayout.LayoutParams(par, par / 2));
        TunnelHub.textColorChangeTunnle.addReceiver(new Tunnel.OnTunnel<Integer>() {
            @Override
            public void onReceive(Integer response) {
                v.setTextColor(response);
            }
        });
        return v;
    }

    public void setOnState(OnState osc) {
        onstate = osc;
    }

    public interface OnState {
        void onOpen();

        void onClose();

        void onBoth(boolean isOpened);
    }
    public static class Holder extends LinearLayout{

        private int size;
        private Squircle squircle;

        public Holder(Context context,int size,Squircle squircle) {
            super(context);
            this.size=size;
            this.squircle=squircle;
            init();
        }

        private void init(){
            double pad=0.05;
            int padding=(int)(size*pad);
            setOrientation(VERTICAL);
            setGravity(Gravity.CENTER);
            setLayoutParams(new LinearLayout.LayoutParams(size+2*padding,size+2*padding));
            setPadding(padding,padding,padding,padding);
            addView(squircle);
        }
    }
}
