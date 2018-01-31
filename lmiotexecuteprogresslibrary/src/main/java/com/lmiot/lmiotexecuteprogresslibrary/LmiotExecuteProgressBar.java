package com.lmiot.lmiotexecuteprogresslibrary;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;


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
    private String mMflag="";
    private int mSpeed=5;
    private int mDelay=3;
    private String mFailText;
    private String mSuccessText;
    private Thread mThread;
    private Handler mHandler;
    private Runnable mRunnable;
    private Handler mHandler01;
    private Runnable mRunnable01;


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


        Log.d("LmiotExecuteProgressBar", "onDraw");

          /*得到view的宽高*/
        int width = getWidth();
        int height = getHeight();
        if(mNum!=0){
            int   i = (int)(width * mNum / 100);

            if(mMflag.equals("success")){
                setVisibility(VISIBLE);

                canvas.drawRect(new Rect(0,0, width, height),mPaintSuccess);
                drawTextMethod(width,height,canvas,mPaintText,mSuccessText+"");
                setGone();

            }
           else if(mMflag.equals("fail")){
                setVisibility(VISIBLE);
                canvas.drawRect(new Rect(0,0, width, height),mPaintFail);
                drawTextMethod(width,height,canvas,mPaintText,mFailText+"");
                setGone();

            }
            else{


                setVisibility(VISIBLE);
                canvas.drawRect(new Rect(0,0, i, height),mPaintProgress);
                drawTextMethod(i,height,canvas,mPaintText,mNum+"%");
            }


        }
        else{
            setVisibility(VISIBLE);
            canvas.drawRect(new Rect(0,0, 0, height),mPaintProgress);
        }

    }

    private void setGone() {
        try {
            //5秒后还没结果,则消失
            if(mHandler01!=null){
                mHandler01.removeCallbacks(mRunnable01);
                mRunnable01=null;
                mHandler01=null;

            }


            mHandler01 = new Handler();
            mRunnable01 = new Runnable() {
                @Override
                public void run() {
                    setVisibility(GONE);
                }
            };
            mHandler01.postDelayed(mRunnable01,mDelay*1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    public void setSuccess(String value){
        setVisibility(VISIBLE);
        mMflag = "success";
        mSuccessText = value;
        invalidate();
    }
    public void setFail(String value){
        setVisibility(VISIBLE);
        mMflag = "fail";
        mFailText = value;
        invalidate();
    }




    public void setProgress(final int progress){
        try {

            setVisibility(VISIBLE);

            if(mThread!=null){
                mThread.interrupt();
                mThread=null;
            }

            //5秒后还没结果,则消失
            if(mHandler!=null){
                mHandler.removeCallbacks(mRunnable);
                mRunnable=null;
                mHandler=null;

            }

            if(mHandler01!=null){
                mHandler01.removeCallbacks(mRunnable01);
                mRunnable01=null;
                mHandler01=null;

            }



            if(progress!=100){
                mMflag = "";
                mNum=0;


                mThread = new Thread() {
                    @Override
                    public void run() {
                        while (true) {

                            try {
                                sleep(mSpeed);
                                mNum++;
                                postInvalidate();
                                if (mNum >= progress) {

                                    break;
                                }


                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                        }
                    }
                };
                mThread.start();





                mHandler = new Handler();
                mRunnable = new Runnable() {
                    @Override
                    public void run() {
                        if (TextUtils.isEmpty(mMflag)) {
                            setFail("等待超时！");
                        }

                    }
                };
                mHandler.postDelayed(mRunnable,(mDelay+1)*1000);


            }
            else{


                mMflag = "success";
                mNum=100;
                mSuccessText="操作成功!";
                postInvalidate();
            }




        } catch (Exception e) {
            e.printStackTrace();
        }


    }





}
