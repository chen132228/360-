package com.cxj.animation360.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.cxj.animation360.R;

/**
 * 自定义悬浮球样式
 */
public class FloatCircleView extends View {
    private int width=200;
    private int height=200;
    private Paint circlePaint;//画圆的笔
    private Paint textPaint;//画内容的笔

    private String text="50%";

    private Boolean isDrag=false;//判断是否是拖拉效果
    private Bitmap bitmap;

    public FloatCircleView(Context context) {
        super(context);
        initPaints();//初始化画笔
    }

    public FloatCircleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initPaints();//初始化画笔
    }

    public FloatCircleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPaints();//初始化画笔
    }


    public FloatCircleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initPaints();//初始化画笔
    }

    /**
     * 创建自定义圆形的样式
     */
    private void initPaints() {
        circlePaint = new Paint();
        circlePaint.setColor(Color.BLUE);
        circlePaint.setAntiAlias(true);//启用circlePaint的抗锯齿功能，使绘制的边缘更加平滑。

        textPaint = new Paint();
        textPaint.setTextSize(25);
        textPaint.setColor(Color.WHITE);
        textPaint.setAntiAlias(true);//启用textPaint的抗锯齿功能，使绘制的边缘更加平滑。
        textPaint.setFakeBoldText(true);//设置textPaint使文本显示为假粗体，这会在文本的笔画两端添加一些额外的宽度，让文本看起来更粗

        Bitmap src = BitmapFactory.decodeResource(getResources(), R.mipmap.img);
        //适配大小
        bitmap = Bitmap.createScaledBitmap(src, width, height, true);//创建一个缩放后的位图bitmap，原始位图src被缩放到指定的宽度和高度，
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
       setMeasuredDimension(width,height);//setMeasuredDimension是自定义视图的大小
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (isDrag){
            canvas.drawBitmap(bitmap,0,0,null);//使用小火箭
        }else {
            canvas.drawCircle(width/2,height/2,width/2,circlePaint);//1.前面两个参数表示的是圆的圆心  半径为width/2
            float textWidth = textPaint.measureText(text);//2.measureText 方法来测量字符串 text 的宽度
            float x = width / 2 - textWidth / 2;//3.从画布中心点向左偏移 textWidth / 2 的距离。
            Paint.FontMetrics metrics = textPaint.getFontMetrics();//4.这行代码获取 textPaint 画笔的字体度量信息，存储在 metrics 变量中。字体度量信息包括文本的上升、下降、顶部、底部等尺寸。
            float dy=-(metrics.descent+metrics.ascent)/2;//5.这行代码计算文本绘制的垂直偏移量 dy，以确保文本垂直居中。metrics.descent 是文本下降的最大距离，metrics.ascent 是文本上升的最大距离。将两者相加后除以2，得到文本基线到画布中心点的垂直偏移量。
            float y = height / 2 + dy;//6.从画布中心点向上偏移 dy 的距离。
            canvas.drawText(text,x,y,textPaint);//7.文本的起始点坐标为 (x, y)
        }
    }

    //设置悬浮球的状态
    public void setDragStage(boolean b) {
        isDrag=b;
        invalidate();//刷新
    }
}
