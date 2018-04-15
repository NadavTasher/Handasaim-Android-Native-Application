package nadav.tasher.handasaim.tools.graphics.bar;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import java.util.ArrayList;

public class Bar extends LinearLayout{

    private HorizontalScrollView hsv;
    private LinearLayout squircles;
    private Squircle mainSquircle;
    private ArrayList<Squircle> squircleList;
    private boolean isOpen=false;

    public Bar(Context context,Squircle main) {
        super(context);
        this.mainSquircle=main;
        makeMain();
        init();
    }

    private void makeMain(){
        mainSquircle.setOnState(new Squircle.OnState() {
            @Override
            public void onOpen() {
                open();
            }

            @Override
            public void onClose() {
                close();
            }

            @Override
            public void onBoth(boolean isOpened) {
            }
        });
    }

    private void init(){
        squircleList=new ArrayList<>();
        squircles=new LinearLayout(getContext());
        squircles.setOrientation(HORIZONTAL);
        squircles.setGravity(Gravity.CENTER);
        hsv=new HorizontalScrollView(getContext());
        setOrientation(HORIZONTAL);
        setGravity(Gravity.CENTER_VERTICAL|Gravity.START);
        setLayoutDirection(LAYOUT_DIRECTION_RTL);
        addView(new Squircle.Holder(getContext(),mainSquircle.getMaxXY(),mainSquircle));
        hsv.addView(squircles);
        hsv.setOverScrollMode(ScrollView.OVER_SCROLL_NEVER);
        hsv.setHorizontalScrollBarEnabled(false);
        hsv.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        addView(hsv);
        close();
    }

    public void addSquircle(Squircle squircle){
        squircleList.add(squircle);
        squircles.addView(new Squircle.Holder(getContext(),mainSquircle.getMaxXY(),squircle));
    }

    public void addSquircles(Squircle[] squircle){
        for (Squircle s:squircle) {
            addSquircle(s);
        }

    }

    public void addSquircles(ArrayList<Squircle> squircle){
        for (Squircle s:squircle) {
            addSquircle(s);
        }

    }

    public boolean isOpen(){
        return isOpen;
    }

    public void open(){
        isOpen=true;
        hsv.setVisibility(View.VISIBLE);
    }

    public void close(){
        isOpen=false;
        hsv.setVisibility(View.GONE);
    }
}
