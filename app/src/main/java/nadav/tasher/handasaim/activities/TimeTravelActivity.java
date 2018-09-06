package nadav.tasher.handasaim.activities;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.LinearLayout;

import org.json.JSONArray;

import nadav.tasher.handasaim.architecture.app.Center;

public class TimeTravelActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadUI();
    }

    private void loadUI() {
        getWindow().setStatusBarColor(Center.getColorTop(getApplicationContext()));
        getWindow().setNavigationBarColor(Center.getColorBottom(getApplicationContext()));
        final LinearLayout ll = new LinearLayout(getApplicationContext());
        ll.setGravity(Gravity.CENTER);
        ll.setOrientation(LinearLayout.VERTICAL);
        ll.setBackground(generateGradient(Center.getColorTop(getApplicationContext()), Center.getColorBottom(getApplicationContext())));
        JSONArray scheduleArray
        setContentView(ll);
    }

    private Drawable generateGradient(int colorA, int colorB) {
        return new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, new int[]{
                colorA,
                colorB
        });
    }
}
