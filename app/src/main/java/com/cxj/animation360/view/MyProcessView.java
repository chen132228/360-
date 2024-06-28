package com.cxj.animation360.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;



public class MyProcessView extends View {

    private int width=200;
    private int height=200;
    private Bitmap bitmap;
    private Canvas bitmapCanvas;
    private Paint circlePaint;
    private Paint progressPaint;
    private Paint textPaint;

    private Path path=new Path();

    private int progress=50;//最高进度
    private int currentProgress=0;//当前的进度
    private int max=100;
    private GestureDetector detector;
    private int count=50;
    private boolean isSingleTag=false;

    private Handler handler=new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
        }
    };

    public MyProcessView(Context context) {
        super(context);
        init();
    }
    public MyProcessView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MyProcessView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public MyProcessView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    /**
     * 设置绘制所需的画笔、位图和触摸监听器
     */
    private void init() {
        //1.绘制圆
        circlePaint = new Paint();
        circlePaint.setAntiAlias(true);
        circlePaint.setColor(Color.argb(0xff,0x3a,0x8b,0x6c));

        //2.绘制进度效果
        progressPaint = new Paint();
        progressPaint.setAntiAlias(true);
        progressPaint.setColor(Color.argb(0xff,0x4e,0x5d,0x6f));
        progressPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));//混合模式   遮挡的效果

        //3.绘制文本
        textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(25);

        bitmap = Bitmap.createBitmap(width,height, Bitmap.Config.ARGB_8888);
        bitmapCanvas = new Canvas(bitmap);

        //手势检测器——目的是为了检测是单击还是双击
        detector = new GestureDetector(new MyGestureDetectorListener());
        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return detector.onTouchEvent(event);
            }
        });
        setClickable(true);//允许点击
    }

    class MyGestureDetectorListener extends GestureDetector.SimpleOnGestureListener{
        @Override
        public boolean onDoubleTap(@NonNull MotionEvent e) {
            Toast.makeText(getContext(),"双击",Toast.LENGTH_SHORT).show();
            startDoubleTapAnimation();//启动双击后的动画效果
            return super.onDoubleTap(e);
        }


        @Override
        public boolean onSingleTapConfirmed(@NonNull MotionEvent e) {
            Toast.makeText(getContext(),"单击",Toast.LENGTH_SHORT).show();
            isSingleTag=true;
            currentProgress=progress;
            startSingleTapAnimation();//启动单击后的动画效果
            return super.onSingleTapConfirmed(e);
        }
    }

    //单击加速球
    private void startSingleTapAnimation() {
        handler.postDelayed(singleTapRunnable,200);//更新UI
    }
    private SingleTapRunnable singleTapRunnable=new SingleTapRunnable();
    class SingleTapRunnable implements Runnable{

        @Override
        public void run() {
            count--;
            if (count>=0){
                invalidate();//重新绘制当前视图
                handler.postDelayed(singleTapRunnable,200);//实现连续动画效果
            }else {
                handler.removeCallbacks(singleTapRunnable);
                count=50;
            }
        }
    }

    private void startDoubleTapAnimation() {
        handler.postDelayed(doubleTapRunnable,50);
    }

    private DoubleTapRunnable doubleTapRunnable=new DoubleTapRunnable();
    class DoubleTapRunnable implements Runnable{

        @Override
        public void run() {
            currentProgress++;
            if (currentProgress<=progress){
                invalidate();
                handler.postDelayed(doubleTapRunnable,50);
            }else {
                handler.removeCallbacks(doubleTapRunnable);
                currentProgress=0;
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(width, height);
    }

    /**
     * 自定义绘制一个圆形进度条和动画效果
     * @param canvas the canvas on which the background will be drawn
     */
    @Override
    protected void onDraw(Canvas canvas) {

        bitmapCanvas.drawCircle(width/2,height/2,width/2,circlePaint);//1.绘制一个圆
        path.reset();//2.绘制新的路径
        float y = (1 - (float) currentProgress / max) * height;
        path.moveTo(width,y);//3.当前点移动到视图的最右侧
        path.lineTo(width,height);//绘制一条线到视图的右下角
        path.lineTo(0,height);//绘制一条线到视图的左下角
        path.lineTo(0,y);//绘制一条线回到起始y坐标

        if (!isSingleTag){
            float d = (1 - ((float) currentProgress / progress)) * 10;//4.计算波浪路径的偏移量
            for (int i = 0; i < 5; i++) {//表示的是波浪
                path.rQuadTo(10,-d,20,0);
                path.rQuadTo(10,d,20,0);
            }
        }else {
            float d=(float) count/50*10;//根据count的值计算偏移量
            if (count%2==0){
                for (int i = 0; i < 5; i++) {
                    path.rQuadTo(20,-d,40,0);//绘制反向的二次贝塞尔曲线
                    path.rQuadTo(20,d,40,0);//绘制正向的二次贝塞尔曲线
                }
            }else {
                for (int i = 0; i < 5; i++) {

                    path.rQuadTo(20,d,40,0);
                    path.rQuadTo(20,-d,40,0);
                }
            }
        }

        path.close();//
        bitmapCanvas.drawPath(path,progressPaint);//画笔在bitmapCanvas画布上绘制路径
        String text=(int)(((float)currentProgress/max)*100)+"%";//计算进度百分比，并转换成字符串
        float textWidth = textPaint.measureText(text);//测量文本宽度
        Paint.FontMetrics metrics = textPaint.getFontMetrics();//获取文本的度量信息
        float baseLine = height / 2 - (metrics.ascent + metrics.descent) / 2;//计算文本基线位置，确保文本
        bitmapCanvas.drawText(text,width/2-textWidth/2,baseLine,textPaint);//绘制文本，位置居中
        canvas.drawBitmap(bitmap,0,0,null);//将bitmapCanvas上的绘制结果绘制到主界面
    }
}
