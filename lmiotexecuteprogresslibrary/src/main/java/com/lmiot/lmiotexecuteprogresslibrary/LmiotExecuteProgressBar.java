package com.lmiot.lmiotexecuteprogresslibrary;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.Timer;
import java.util.TimerTask;


/**
 * 创建日期：2018-01-20 16:53
 * 作者:Mr.Li
 * 描述:
 */
public class LmiotExecuteProgressBar extends View {

    private int mTextColor;
    private int mProgressColor;
    private int mSuccessColor;
    private int mFailColor;


    private Paint mPaintProgress;
    private Paint mPaintText;
    private Paint mPaintSuccess;
    private Paint mPaintFail;
    private int mNum=0;
    private int mFlag=999;
    private int mSpeed=5;
    private int mDelay=3;

    public void setFailText(String failText) {
        mFailText = failText;
    }

    private String mFailText="等待超时！";
    private String mSuccessText="操作成功！";
    private  int TypeSuccess=100;
    private  int TypeFail=-1;
    private Runnable mRunnable;
    private Handler mHandler;
    private TimerTask mTask;
    private Timer mTimer;

    private  Handler mHandlerNew=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what==0x122){
                setGone(mFlag);
            }

        }
    };



    public LmiotExecuteProgressBar(Context context) {
        super(context);
    }

    public LmiotExecuteProgressBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.LmiotExecuteProgressBar, 0, 0);

        mTextColor = typedArray.getColor(R.styleable.LmiotExecuteProgressBar_TextColor, Color.BLACK);
        mProgressColor = typedArray.getColor(R.styleable.LmiotExecuteProgressBar_PrgressColor, Color.GRAY);
        mSuccessColor = typedArray.getColor(R.styleable.LmiotExecuteProgressBar_SuccessColor, Color.GREEN);
        mFailColor = typedArray.getColor(R.styleable.LmiotExecuteProgressBar_FailColor, Color.RED);
        mSpeed = typedArray.getInteger(R.styleable.LmiotExecuteProgressBar_Speed, 10);
        mDelay = typedArray.getInteger(R.styleable.LmiotExecuteProgressBar_GoneDelay, 3);



        mPaintText=new Paint();
        mPaintText.setTextSize(25);
        mPaintText.setColor(mTextColor);
        mPaintText.setAntiAlias(true);

        mPaintProgress=new Paint();
        mPaintProgress.setColor(mProgressColor);

        mPaintSuccess=new Paint();
        mPaintSuccess.setColor(mSuccessColor);


        mPaintFail=new Paint();
        mPaintFail.setColor(mFailColor);


    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

          /*得到view的宽高*/
        int width = getWidth();
        int height = getHeight();

        switch (mFlag){


            case 100:
                canvas.drawRect(new Rect(0,0, width, height),mPaintSuccess);
                drawTextMethod(width,height,canvas,mPaintText,mSuccessText+"");
                setGone(mFlag);
                break;
            case -1:
                canvas.drawRect(new Rect(0,0, width, height),mPaintFail);
                drawTextMethod(width,height,canvas,mPaintText,mFailText+"");
                setGone(mFlag);
                break;
            case 999: //默认不处理
                break;
            default:

                    int   i = (int)(width * mNum / 100);
                    canvas.drawRect(new Rect(0,0, i, height),mPaintProgress);
                    drawTextMethod(i,height,canvas,mPaintText,mNum+"%");



                break;
        }



    }

    private void setGone(final int flag) {



            mHandler = new Handler();
            mRunnable = new Runnable() {
            @Override
            public void run() {

                if(mFlag==-1||mFlag==100){ //超时或成功后延时消失
                    setVisibility(GONE);
                }
                else{ //进度动画完成后
                    setProgress(-1);

                }

            }
        };

         mHandler.postDelayed(mRunnable,mDelay*2000);



    }


    private void drawTextMethod(int width,int height,Canvas canvas,Paint paint,String text) {

        int widthText = (int) paint.measureText(text);
        Rect targetRect = new Rect(width-widthText, 0, width, height);
        Paint.FontMetricsInt fontMetrics = paint.getFontMetricsInt();
        int baseline = targetRect.top + (targetRect.bottom - targetRect.top - fontMetrics.bottom + fontMetrics.top) / 2 - fontMetrics.top;
        // 以下这行是实现水平居中。drawText相应改为传入targetRect.centerX()
        paint.setTextAlign(Paint.Align.RIGHT);
        canvas.drawText(text, targetRect.centerX(), baseline, paint);

    }



    public void setProgress(final int progress){

        if(mTimer!=null){
            mTask.cancel();
            mTimer.cancel();
            mTask=null;
            mTimer=null;

        }

        if(mHandler!=null){
            mHandler.removeCallbacks(mRunnable);
            mHandler=null;
            mRunnable=null;

        }



        if(progress==-1){  //等待超时
            mFlag =TypeFail;
            invalidate();

        }
        else if( progress==100){ //成功
            setVisibility(VISIBLE);
            mFlag =TypeSuccess;
            invalidate();
        }
        else{ //加载进度

            setVisibility(VISIBLE);

            mNum = 0;
            mFlag =progress;



            mTimer = new Timer();
            mTask = new TimerTask() {
                @Override
                public void run() {
                    mNum++;
                    postInvalidate();
                    if(mNum<=progress){
                        postInvalidate();

                        Log.d("LmiotExecuteProgressBar", "定时进行中……");
                    }
                    else{
                        cancel();
                        mHandlerNew.sendEmptyMessage(0x122);



                    }



                }
            };
            mTimer.schedule(mTask,mSpeed,mSpeed);

        }


    }





}
