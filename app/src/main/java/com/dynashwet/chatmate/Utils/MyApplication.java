package com.dynashwet.chatmate.Utils;

import android.app.Application;

import com.danikula.videocache.HttpProxyCacheServer;

public class MyApplication extends Application {

    private static HttpProxyCacheServer proxy;

    public static HttpProxyCacheServer getProxy() {
        return proxy;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        proxy = new HttpProxyCacheServer(this);
    }
}
