package com.rong1.matrix;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;
import java.util.Map;


//用于保存和提取用户的字体大小设置，在Activity启动时，用户更改字体大小设置时调用
public class PreferencesService3 {

    private Context context;

    public PreferencesService3(Context context){

        this.context=context;
    }


    //保存设置中的参数
    //textsize为所要保存的值
    public void save(int textsize){
        //获得sharedpreferences对象
        SharedPreferences preferences=context.getSharedPreferences("用户设置",context.MODE_PRIVATE);
        SharedPreferences.Editor editor=preferences.edit();
        editor.putInt("字体大小", textsize);
        editor.commit();
    }

    //  获取各项参数，保存到HashMap中，键值对应可以获取到想要的值
    public Map<String,String> getPerferences(){

        Map<String,String> map=new HashMap<String,String>();
        SharedPreferences preferences=context.getSharedPreferences("用户设置",Context.MODE_PRIVATE);
        map.put("字体大小",String.valueOf(preferences.getInt("字体大小",0)));
        return map;
    }
}
