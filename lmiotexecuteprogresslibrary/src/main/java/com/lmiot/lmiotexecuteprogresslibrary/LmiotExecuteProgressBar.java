package com.lmiot.lmiotexecuteprogresslibrary;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.TranslateAnimation;

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
    private int mSpeed;
    private String mFailText;
    private String mSuccessText;


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
        if(mNum!=0){
            int   i = (int)(width * mNum / 100);

            if(mMflag.equals("success")){

                canvas.drawRect(new Rect(0,0, width, height),mPaintSuccess);
                drawTextMethod(width,height,canvas,mPaintText,mSuccessText+"");

            }
           else if(mMflag.equals("fail")){
                canvas.drawRect(new Rect(0,0, width, height),mPaintFail);
                drawTextMethod(width,height,canvas,mPaintText,mFailText+"");

            }
            else{
                canvas.drawRect(new Rect(0,0, i, height),mPaintProgress);
                drawTextMethod(i,height,canvas,mPaintText,mNum+"%");
            }


        }
        else{
            canvas.drawRect(new Rect(0,0, 0, height),mPaintProgress);
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
        mMflag = "success";
        mSuccessText = value;
        postInvalidate();
    }
    public void setFail(String value){
        mMflag = "fail";
        mFailText = value;
        postInvalidate();
    }
    public void setProgress(final int progress){

        mMflag = "";
        mNum=0;
        new Thread(){
            @Override
            public void run() {
                while (true){

                    try {
                        sleep(mSpeed);
                        mNum++;
                        postInvalidate();
                        if(mNum>=progress){
                            break;
                        }


                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }
        }.start();



    }


    /**
     * 获取文字宽度
     * @param content
     * @param paint
     * @return
     */
    public int getTextWidth(String content,Paint paint){
        int width = 0;
        if(content!=null&&content.length()>0){
            int length = content.length();
            float[] widths = new float[length];
            paint.getTextWidths(content,widths);
            for (int i = 0; i < length; i++) {
                width += (int)Math.ceil(widths[i]);
            }
        }
        return width;
    }

}
