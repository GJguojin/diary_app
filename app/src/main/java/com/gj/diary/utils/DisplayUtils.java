package com.gj.diary.utils;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.Window;
import android.view.WindowManager;

import com.gj.diary.MyApp;

import java.lang.reflect.Method;

public class DisplayUtils {

    public static int getStatusBarHeight() {
        int height = 0;
        int resourceId = MyApp.getAppContext().getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            height = MyApp.getAppContext().getResources().getDimensionPixelSize(resourceId);
        }
        return height;
    }

    public static int getActionBarHeight() {
        // Calculate ActionBar height
        int actionBarHeight = 0;
        TypedValue tv = new TypedValue();
        if (MyApp.getAppContext().getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, MyApp.getAppContext().getResources()
                    .getDisplayMetrics());
        }
        if (actionBarHeight == 0) {
            actionBarHeight = 45;
        }
        return actionBarHeight;
    }

    public static int dp2Px(int dp) {
        DisplayMetrics dm = MyApp.getAppContext().getResources().getDisplayMetrics();
        int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, dm);
        return px;
    }

    public static float dp2PxInFloat(float dp) {
        DisplayMetrics dm = MyApp.getAppContext().getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, dm);
    }

    public static int sp2Px(int sp) {
        DisplayMetrics dm = MyApp.getAppContext().getResources().getDisplayMetrics();
        int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, dm);
        return px;
    }

    //获取屏幕原始尺寸高度，包括虚拟功能键高度
    public static int getDpi() {
        int dpi = 0;
        WindowManager windowManager = (WindowManager) MyApp.getAppContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        @SuppressWarnings("rawtypes")
        Class c;
        try {
            c = Class.forName("android.view.Display");
            @SuppressWarnings("unchecked")
            Method method = c.getMethod("getRealMetrics", DisplayMetrics.class);
            method.invoke(display, displayMetrics);
            dpi = displayMetrics.heightPixels;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dpi;
    }

    /**
     * 获取虚拟按键的高度
     *
     * @return
     */
    public static int getBottomStatusHeight() {
        int totalHeight = getDpi();
        int contentHeight = getScreenHeight();
        return totalHeight - contentHeight;
    }

    /**
     * 标题栏高度
     *
     * @return
     */
    public static int getTitleHeight(Activity activity) {
        return activity.getWindow().findViewById(Window.ID_ANDROID_CONTENT).getTop();
    }

    /**
     * 获得状态栏的高度
     *
     * @return
     */
    public static int getStatusHeight() {
        int statusHeight = -1;
        try {
            Class<?> clazz = Class.forName("com.android.internal.R$dimen");
            Object object = clazz.newInstance();
            int height = Integer.parseInt(clazz.getField("status_bar_height")
                    .get(object).toString());
            statusHeight = MyApp.getAppContext().getResources().getDimensionPixelSize(height);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return statusHeight;
    }

    /**
     * 获得屏幕宽度
     *
     * @return
     */
    public static int getScreenWidth() {
        DisplayMetrics metric = MyApp.getAppContext().getResources().getDisplayMetrics();
        int screenWidth = metric.widthPixels;
        return screenWidth;
    }

    /**
     * 获得屏幕高度
     *
     * @return
     */
    public static int getScreenHeight() {
        WindowManager wm = (WindowManager) MyApp.getAppContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.heightPixels;
    }
}