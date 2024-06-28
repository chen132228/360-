package com.cxj.animation360.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.cxj.animation360.engine.FloatViewManager;

public class MyFloatService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        FloatViewManager floatViewManager=FloatViewManager.getInstance(this);
        floatViewManager.showFloatCircleView();//展示悬浮球
    }
}
