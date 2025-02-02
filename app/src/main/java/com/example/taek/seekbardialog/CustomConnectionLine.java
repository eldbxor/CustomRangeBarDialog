package com.example.taek.seekbardialog;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.TypedValue;


/**
 * Created by Taek on 2018-01-31.
 */

public class CustomConnectionLine {
    // Member Variables ////////////////////////////////////////////////////////

    private final Paint mPaint;

    private final float mY;

    // Constructor /////////////////////////////////////////////////////////////

    /**
     * Constructor for connecting line
     *
     * @param ctx                  the context for the line
     * @param y                    the y co-ordinate for the line
     * @param connectingLineWeight the weight of the line
     * @param connectingLineColor  the color of the line
     */
    public CustomConnectionLine(Context ctx, float y, float connectingLineWeight,
                          int connectingLineColor) {

        final Resources res = ctx.getResources();

        float connectingLineWeight1 = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                connectingLineWeight,
                res.getDisplayMetrics());

        // Initialize the paint, set values
        mPaint = new Paint();
        mPaint.setColor(connectingLineColor);
        mPaint.setStrokeWidth(connectingLineWeight1);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setAntiAlias(true);

        mY = y;
    }

    // Package-Private Methods /////////////////////////////////////////////////

    /**
     * Draw the connecting line between the two thumbs in rangebar.
     *
     * @param canvas     the Canvas to draw to
     * @param leftThumb  the left thumb
     * @param rightThumb the right thumb
     */
    public void draw(Canvas canvas, CustomPinView leftThumb, CustomPinView rightThumb) {
        canvas.drawLine(leftThumb.getX(), mY, rightThumb.getX(), mY, mPaint);
    }

    /**
     * Draw the connecting line between for single slider.
     *
     * @param canvas     the Canvas to draw to
     * @param rightThumb the right thumb
     * @param leftMargin the left margin
     */
    public void draw(Canvas canvas, float leftMargin, CustomPinView rightThumb) {
        canvas.drawLine(leftMargin, mY, rightThumb.getX(), mY, mPaint);
    }
}
