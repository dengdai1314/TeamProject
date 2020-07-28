package com.test.netwallpaper.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.view.View;

import java.util.Calendar;

/**
 * @author DoubleTick
 * @description
 * @date 2020/7/24
 */
class TextClockView extends View {

    /**
     * 全局画笔
     */
//    private val mPaint = createPaint()
//    private val mHelperPaint = createPaint(color = Color.RED)
    private Float mWidth = -1.0f;
    private Float mHeight = null;

    private Float mHourR = null;
    private Float mMinuteR = null;
    private Float mSecondR = null;

    private Float mHourDeg = null;
    private Float mMinuteDeg = null;
    private Float mSecondDeg = null;

    private ValueAnimator mAnimator = null;

    public TextClockView(Context context) {
        super(context);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
//        mWidth = (getMeasuredWidth() - getPaddingLeft() - getPaddingRight());
//        mHeight = (getMeasuredHeight() - getPaddingTop() - getPaddingBottom());

        mHourR = mWidth * 0.143f;
        mMinuteR = mWidth * 0.35f;
        mSecondR = mWidth * 0.35f;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(canvas == null) {
            canvas.drawColor(Color.BLACK);
            canvas.save();
            canvas.translate(mWidth / 2, mHeight / 2);

            //                canvas.drawLine(0f,0f,mWidth,0f,mHelperPaint);

            canvas.restore();
            //                return canvas;
            return;
        }
    }

    private void  drawCenterInfo(Canvas canvas){
        Calendar calendar = Calendar .getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

//        mPaint.
    }

//    private Paint createPaint(String colorString,int color = Color.White){}
}
