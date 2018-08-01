package nadav.tasher.handasaim.architecture.app.graphics;

import android.content.Context;
import android.widget.TextView;

public class RatioView extends TextView {
    private double ratio=1;
    public RatioView(Context context, double ratio) {
        super(context);
        this.ratio=ratio;
    }

    @Override
    public void setTextSize(float size){
        super.setTextSize((int)((double)size*ratio));
    }
}