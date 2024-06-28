package com.cxj.animation360.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.cxj.animation360.engine.FloatViewManager;

public class MyFloatService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {  //该方法返回的 IBinder 对象定义了客户端用来与服务进行交互的编程接口。
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        FloatViewManager floatViewManager=FloatViewManager.getInstance(this);
        floatViewManager.showFloatCircleView();//展示悬浮球
    }
}
