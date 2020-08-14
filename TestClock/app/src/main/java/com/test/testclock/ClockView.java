package com.test.testclock;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

import androidx.annotation.Nullable;

/**
 * @author DoubleTick
 * @description
 * @date 2020/8/14
 */
public class ClockView extends View {

    private Context mContext;
    private Paint mPaint;
    private int mRadius;
    private int mCircleX;
    private int mCircleY;
    private boolean mIsNight;
    private float mTotalSecond;

    private float mSecondDegree;//秒针度数
    private float mMinDegree;//分针度数
    private float mHourDegree;//时针度数
    private Timer mTimer = new Timer();
    private TimerTask task = new TimerTask() {
        @Override
        public void run() {
            if(mSecondDegree == 360){
                mSecondDegree = 0;
            }
            if (mMinDegree == 360) {
                mMinDegree = 0;
            }
            if (mHourDegree == 360) {
                mHourDegree = 0;
            }
            mSecondDegree = mSecondDegree + 6;//秒针
            mMinDegree = mMinDegree + 0.1f;//分针
            mHourDegree = mHourDegree + 1.0f/240;//时针
            //invalidate方法和postInvalidate方法都是用于进行View的刷新，
            // invalidate方法应用在UI线程中，而postInvalidate方法应用在非UI线程中，
            // 用于将线程切换到UI线程，postInvalidate方法最后调用的也是invalidate方法。
            postInvalidate();
        }
    };

    /**
     *开启定时器
     */
    public void start() {
        mTimer.schedule(task,0,1000);
    }

    public ClockView(Context context) {
        super(context);
        this.mContext = context;
        initPaint();
    }

    public ClockView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        initPaint();
    }

    public ClockView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        initPaint();
    }

    public ClockView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.mContext = context;
        initPaint();
    }

    private void initPaint(){
        mPaint = new Paint();
        mPaint.setAntiAlias(true);//开启抗锯齿
        mPaint.setColor(Color.BLACK);
        mPaint.setStyle(Paint.Style.STROKE);//描边
        mPaint.setStrokeWidth(0);
        mPaint.setTextSize(25);
        Paint.FontMetrics fontMetrics = mPaint.getFontMetrics();
        float ascent = fontMetrics.ascent;
        float bottom = fontMetrics.bottom;
        float descent = fontMetrics.descent;
        float leading = fontMetrics.leading;
        float top = fontMetrics.top;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawDial(canvas);
        drawPointer(canvas);
    }

    private void drawDial(Canvas canvas){
        canvas.save();
        //圆形边框
        mPaint.setStrokeWidth(2);
        mRadius = getWidth()/4;
        mCircleX = getWidth()/2;
        mCircleY = getHeight()/2;

        canvas.drawCircle(mCircleX,mCircleY,mRadius,mPaint);
        //圆心
        mPaint.setStrokeWidth(5);
        canvas.drawPoint(mCircleX,mCircleY,mPaint);

        canvas.save();
        //画刻度
        //设置刻度线线宽
        mPaint.setStrokeWidth(2);
        //将坐标原点移到圆心处
        canvas.translate(mCircleX,mCircleY);
        for(int i=0;i<360;i++){
            if (i % 30 == 0) {//长刻度
                canvas.drawLine(mRadius - 25, 0,mRadius, 0, mPaint);
            } else if (i % 6 == 0) {//中刻度*/
                canvas.drawLine(mRadius - 14, 0,mRadius, 0, mPaint);
            } else {//短刻度
                canvas.drawLine(mRadius - 9, 0,mRadius, 0, mPaint);
            }
            canvas.rotate(1);
        }
        canvas.restore();

        canvas.save();
        //画刻度文字
        canvas.translate(mCircleX,mCircleY);
        for (int i=0;i<12;i++){
            if (i == 0) {
                drawNum(canvas, i * 30, 12 + "", mPaint);
            } else {
                drawNum(canvas, i * 30, i + "", mPaint);
            }
        }
        canvas.restore();
    }

    private void drawPointer(Canvas canvas){
        canvas.translate(getWidth()/2,getHeight()/2);
        //秒针
        canvas.save();
        mPaint.setColor(Color.RED);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(2);
        canvas.rotate(mSecondDegree);
        //其实坐标点（0,0）终点坐标（0，-190），这里的190为秒针长度
        canvas.drawLine(0, 0, 0,
                -190, mPaint);
        canvas.restore();
        //分针
        canvas.save();
        mPaint.setColor(Color.BLACK);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(4);
        canvas.rotate(mMinDegree);
        canvas.drawLine(0, 0, 0,
                -130, mPaint);
        canvas.restore();
        //时针
        canvas.save();
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(7);
        canvas.rotate(mHourDegree);
        canvas.drawLine(0, 0, 0,
                -90, mPaint);
        canvas.restore();
    }

    /**
     * 画刻度文字
     * @param canvas 画布
     * @param degree 数字与12点之间的夹角
     * @param text   要画的数字
     * @param paint  画笔
     */
    private void drawNum(Canvas canvas,int degree,String text,Paint paint){
        Rect textBound = new Rect();//创建一个矩形
        paint.getTextBounds(text, 0, text.length(), textBound);
        canvas.rotate(degree);
        canvas.translate(0, 50 - mRadius);//这里的50是坐标中心距离时钟最外边框的距离，当然你可以根据需要适当调节
        canvas.rotate(-degree);
        canvas.drawText(text, -textBound.width() / 2,
                textBound.height() / 2, paint);
        canvas.rotate(degree);
        canvas.translate(0, mRadius - 50);
        canvas.rotate(-degree);
    }

    //1:30:30 时针的角度为1*30 = 30度；分针的角度为30*6 = 180度；秒针的角度为30*6=180度；
    public void setTime(int hour,int min, int second){
        if (hour >= 24 || hour < 0 || min >= 60 || min < 0 || second >= 60 || second < 0) {
            Toast.makeText(getContext(), "时间不合法", Toast.LENGTH_SHORT).show();
            return;
        }
        if (hour >= 12) {//这里我们采用24小时制
            mIsNight = true;//添加一个变量，用于记录是否为下午。
            mHourDegree = (hour + min * 1.0f/60f + second * 1.0f/3600f - 12)*30f;
        } else {
            mIsNight = false;
            mHourDegree = (hour + min * 1.0f/60f + second * 1.0f/3600f )*30f;
        }
        mMinDegree = (min + second * 1.0f/60f) *6f;
        mSecondDegree = second * 6f;
        invalidate();
    }

    public float getTimeTotalSecond() {
        if (mIsNight) {//判断是否为下午，是的话再加12个小时
            mTotalSecond = mHourDegree * 120 + 12 * 3600;
            return mTotalSecond;
        } else {
            mTotalSecond = mHourDegree * 120;
            return mTotalSecond;
        }
    }

    public int getHour() {//获取小时
        return (int) (getTimeTotalSecond() / 3600);
    }
    public int getMin() {//获取分钟
        return (int) ((getTimeTotalSecond() - getHour() * 3600) / 60);
    }
    public int getSecond() {//获取秒钟
        return (int) (getTimeTotalSecond() - getHour() * 3600 - getMin() * 60);
    }
}
