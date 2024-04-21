package maps;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.widget.LinearLayout;

public class TransparentPanel extends LinearLayout {

    Paint innerPaint, borderPaint;

    public TransparentPanel(Context context) {
        super(context);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        this.setPadding(5, 5, 5, 5);

        innerPaint = new Paint();
        innerPaint.setARGB(180, 50, 200, 50);

        borderPaint = new Paint();
        borderPaint.setARGB(255, 255, 255, 255);
        borderPaint.setAntiAlias(true);
        borderPaint.setStyle(Style.STROKE);
        borderPaint.setStrokeWidth(2);

        RectF drawRect = new RectF();
        drawRect.set(0, 0, getMeasuredWidth(), 75);

        canvas.drawRoundRect(drawRect, 5, 5, innerPaint);
        canvas.drawRoundRect(drawRect, 5, 5, borderPaint);

        super.dispatchDraw(canvas);
    }

}
