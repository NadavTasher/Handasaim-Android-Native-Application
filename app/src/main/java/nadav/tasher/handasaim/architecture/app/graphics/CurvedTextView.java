package nadav.tasher.handasaim.architecture.app.graphics;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Typeface;
import android.view.View;

public class CurvedTextView extends View {
    private Path circle;
    private Paint tPaint;
    private Paint cPaint;
    private String text;
    private float textSize;
    private int textColor, sizeX, sizeY, radius;

    public CurvedTextView(Context c) {
        super(c);
        this.text = "Example";
        circle = new Path();
        int sizeX = 100, sizeY = 100, radius = 50, textColor = Color.WHITE, textSize = 20;
        circle.addCircle(((sizeX - radius * 2) / 2) + radius, ((sizeY - radius * 2) / 2) + radius, radius, Path.Direction.CW);
        cPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        cPaint.setStyle(Paint.Style.STROKE);
        cPaint.setColor(Color.LTGRAY);
        cPaint.setStrokeWidth(3);
        tPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        tPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        tPaint.setColor(textColor);
        tPaint.setTextSize(textSize);
        tPaint.setTypeface(Typeface.createFromAsset(c.getAssets(), Values.fontName));
    }

    public CurvedTextView(Context context, String text, float textSize, int textColor, int sizeX, int sizeY, int radius) {
        super(context);
        this.text = text;
        this.textSize = textSize;
        this.textColor = textColor;
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.radius = radius;
        init();
    }

    public void setText(String s) {
        this.text = s;
        init();
    }

    private void init() {
        circle = new Path();
        circle.addCircle(((sizeX - radius * 2) / 2) + radius, ((sizeY - radius * 2) / 2) + radius, radius, Path.Direction.CW);
        cPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        cPaint.setStyle(Paint.Style.STROKE);
        cPaint.setColor(Color.LTGRAY);
        cPaint.setStrokeWidth(3);
        tPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        tPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        tPaint.setColor(textColor);
        tPaint.setTextSize(textSize);
        tPaint.setTypeface(Typeface.createFromAsset(getContext().getAssets(), Values.fontName));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawTextOnPath(text, circle, 0, 0, tPaint);
        invalidate();
    }
}