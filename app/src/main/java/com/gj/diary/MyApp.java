package com.gj.diary;

import android.app.Application;
import android.content.Context;

/**
 * Created by zhufeng on 2017/1/10.
 */

public class MyApp extends Application {

    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
    }

    public static Context getAppContext(){
        return mContext;
    }
}
