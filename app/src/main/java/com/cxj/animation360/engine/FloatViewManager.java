package com.cxj.animation360.engine;

import static androidx.core.app.ActivityCompat.startActivityForResult;

import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.cxj.animation360.view.FloatCircleView;
import com.cxj.animation360.view.FloatMenuView;

import java.lang.reflect.Field;

/**
 * 用于管理浮动在屏幕上的悬浮球
 */
public class FloatViewManager {
    private final WindowManager wm;
    public Context context;
    private final FloatMenuView floatMenuView;
    public static FloatViewManager instance;

    private FloatCircleView circleView;
    WindowManager.LayoutParams params;
    private float startX;

    /**
     * 触摸悬浮球监听——为了判断悬浮球的触摸类型
     */
    private View.OnTouchListener circleViewTouchListener=new View.OnTouchListener() {

        private float y0;
        private float x0;
        private float startY;

        /**
         * @param v     The view the touch event has been dispatched to.
         * @param event The MotionEvent object containing full information about
         *              the event.
         * @return
         */
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()){
                case MotionEvent.ACTION_DOWN:
                    //1.移动前记录一下起始位置
                    startX = event.getRawX();
                    startY = event.getRawY();
                    //TODO 为了消费点击事件
                    x0 = event.getRawX();
                    y0 = event.getRawY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    //1.记录移动之后所在的位置
                    float x = event.getRawX();
                    float y = event.getRawY();
                    //2.记录移动的偏移量
                    float dx = x - startX;
                    float dy = y - startY;
                    //3.设置在窗口
                    params.x+=dx;
                    params.y+=dy;
                    //3.1移动的时候，悬浮球形状改变
                    circleView.setDragStage(true);
                    //4.刷新当前位置
                    wm.updateViewLayout(circleView,params);
                    //5.设置当前位置
                    startX=x;
                    startY=y;
                    Log.d("TAG", "偏移了: "+event);
                    break;
                case MotionEvent.ACTION_UP:
                    //1.记录当前的位置
                    float endX = event.getRawX();
                    //2.悬浮球只能靠左或者靠右
                    if (endX>getScreenWidth()/2){
                        params.x=getScreenWidth()-circleView.getWidth();
                    }else {
                        params.x=0;//表示悬浮球所处在屏幕的位置
                    }
                    circleView.setDragStage(false);
                    wm.updateViewLayout(circleView,params);

                    //TODO 判断是否消费当前点击事件
                    if (Math.abs(endX-x0)>6){
                        return false;
                    }else {
                        return false;
                    }

                default:
                    break;
            }
            return false;
        }
    };

    //获取屏幕的宽度
    private int getScreenWidth(){
        return wm.getDefaultDisplay().getWidth();
    }

    //获取屏幕的高度
    private int getScreenHeight(){
        return wm.getDefaultDisplay().getHeight();
    }

    //获取
    public int getStateHeight(){
        try {
            Class<?> c = Class.forName("com.android.internal.R$dimen");
            Object o = c.newInstance();
            Field field = c.getField("status_bar_height");
            int x = (int) field.get(o);
            return context.getResources().getDimensionPixelSize(x);
        } catch (Exception e) {
           return 0;
        }
    }

    /**
     * （第一步）悬浮球管理者
     * @param context
     */
    public FloatViewManager(Context context) {
        this.context = context;
        wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);//1.获取WindowManager实例

        circleView= new FloatCircleView(context);

        circleView.setOnTouchListener(circleViewTouchListener);

        circleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context,"点击悬浮球",Toast.LENGTH_SHORT).show();
                //隐藏circleView 显示菜单栏 开启动画
                wm.removeView(circleView);
                showFloatMenuView();
                floatMenuView.startAnimation();
            }
        });

        floatMenuView = new FloatMenuView(context);
    }

    /**
     * 加速球的显示
     */
    private void showFloatMenuView() {
        WindowManager.LayoutParams params=new WindowManager.LayoutParams();
        params.width=getScreenWidth();
        params.height=getScreenHeight()-getStateHeight();
        params.gravity=Gravity.BOTTOM | Gravity.LEFT;
        params.x=0;
        params.y=0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            params.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        }else if (Build.VERSION.SDK_INT > 24) {
            params.type = WindowManager.LayoutParams.TYPE_PHONE;
        } else {
            params.type = WindowManager.LayoutParams.TYPE_TOAST;
        }
        params.flags= WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
        params.format= PixelFormat.RGBA_8888;
        wm.addView(floatMenuView,params);
    }

    /**
     * 第一步：获取悬浮球的单例对象
     * @param context
     * @return
     */
    public static FloatViewManager getInstance(Context context){
        if (instance==null){
            synchronized (FloatViewManager.class){
                if (instance==null){
                    instance=new FloatViewManager(context);
                }
            }
        }
        return instance;
    }


    /**
     * 显示悬浮球
     */
    public void showFloatCircleView(){
        if (params==null){
            params=new WindowManager.LayoutParams();//1.新的 WindowManager.LayoutParams 对象，用于定义如何将视图添加到窗口管理器。
            params.width=circleView.getWidth();//2.设置了浮动视图的宽度和高度，它们分别等于 circleView 的宽度和高度
            params.height=circleView.getHeight();
            params.gravity= Gravity.TOP | Gravity.LEFT;//3.设置浮动的位置
            params.x=0;//4.浮动视图的初始x和y坐标，这里都设置为0，意味着视图将从屏幕左上角开始。
            params.y=20;

            //5.设置浮动视图的类型
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                params.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
            }else if (Build.VERSION.SDK_INT > 24) {
                params.type = WindowManager.LayoutParams.TYPE_PHONE;
            } else {
                params.type = WindowManager.LayoutParams.TYPE_TOAST;
            }
            //6.FLAG_NOT_FOCUSABLE 表示视图不会获取焦点，FLAG_NOT_TOUCH_MODAL 表示当触摸事件发生在该视图上时，不会阻塞其他视图的触摸事件。
            params.flags= WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
            params.format= PixelFormat.RGBA_8888;
        }

        wm.addView(circleView,params);

    }

    //隐藏悬浮球
    public void hideFloatMenuView() {
        wm.removeView(floatMenuView);
    }
}
