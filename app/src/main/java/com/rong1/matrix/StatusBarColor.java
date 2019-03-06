package com.rong1.matrix;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.graphics.ColorUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

/**
 * Created by rong1 on 2018/9/12.
 * 设置状态栏颜色和透明的类
 * 需要在对应的xml文件中的第一个只控件加入：android:fitsSystemWindows="true"   android:clipToPadding="true"
 */

public class StatusBarColor {

    /**
     * 设置状态栏颜色
     *
     * @param activity       需要设置的activity
     * @param color          状态栏颜色值  Color.parseColor("#ff000000")
     */
    public static void setStatusBarColor(Activity activity, int color) {
        //操作系统的api版本大于21，才能改变状态栏的颜色
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            //设置状态栏的颜色
            activity.getWindow().setStatusBarColor(color);

            //如果大于6.0  当状态栏颜色为亮色，就改变状态栏的字体颜色
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if(ColorUtils.calculateLuminance(color) >= 0.5){
                    activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                } else {
                    activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
                }
            }

        }
    }
    /*
    * 设置状态栏透明
    *
    * @param   window   需要从调用的此方法的类中获取Window对象：window
    *                   获取方式：Window window = getWindow();
    * */
    public static void initState(Window window) {
        //安卓4.0才可以设置透明状态栏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //透明状态栏
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //透明导航栏
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        //安卓5.0以上状态栏透明
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //让应用主题内容占用系统状态栏的空间,注意:下面两个参数必须一起使用 stable 牢固的
            //设置让应用主题内容占据状态栏和导航栏
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION|View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            //设置状态栏和导航栏颜色为透明
            window.setStatusBarColor(Color.TRANSPARENT);
            window.setNavigationBarColor(Color.TRANSPARENT);
            //隐藏标题栏
            //ActionBar actionBar = getSupportActionBar();
            //actionBar.hide();
        }
    }
}
