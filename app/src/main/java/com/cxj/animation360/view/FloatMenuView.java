package com.cxj.animation360.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.cxj.animation360.R;
import com.cxj.animation360.engine.FloatViewManager;

/**
 * 自定义加速球
 */
public class FloatMenuView extends LinearLayout {

    private LinearLayout ll;
    private TranslateAnimation animation;

    public FloatMenuView(Context context) {
        super(context);
        View root = View.inflate(getContext(), R.layout.float_menu_view, null);
        ll = root.findViewById(R.id.ll);
        animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF,0,
                Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF, 0);
        animation.setDuration(500);
        animation.setFillAfter(true);
        ll.setAnimation(animation);
        root.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                FloatViewManager manager=FloatViewManager.getInstance(context);//1.获取浮窗管理者实例
                manager.hideFloatMenuView();//2.点击空白处，隐藏加速球，且显示浮窗小球
                manager.showFloatCircleView();
                return false;//触摸事件没有被消耗，允许事件继续传递。(还可以继续点击浮窗小球）
            }
        });
        addView(root);
    }

    //执行动画
    public void startAnimation(){
        animation.start();
    }

    public FloatMenuView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        View root = View.inflate(getContext(), R.layout.float_menu_view, null);
        addView(root);
    }

    public FloatMenuView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        View root = View.inflate(getContext(), R.layout.float_menu_view, null);
        addView(root);
    }

    public FloatMenuView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }
}
