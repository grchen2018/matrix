package com.rong1.matrix;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;
import java.util.Map;

//用于保存和提取用户的主题设置，在Activity启动时，用户更改主题设置时调用

public class PreferencesService {
    private  Context context;

    public PreferencesService(Context context){

        this.context=context;
    }


    //保存设置中的参数
    //theme为所要保存的值
    public void save(int theme){
        //获得sharedpreferences对象
        SharedPreferences preferences=context.getSharedPreferences("用户设置",context.MODE_PRIVATE);
        SharedPreferences.Editor editor=preferences.edit();
        editor.putInt("主题",theme);
        editor.commit();
    }

    //  获取各项参数，保存到HashMap中，键值对应可以获取到想要的值
    public Map<String,String> getPerferences(){

        Map<String,String> map=new HashMap<String,String>();
        SharedPreferences preferences=context.getSharedPreferences("用户设置",Context.MODE_PRIVATE);
        map.put("主题",String.valueOf(preferences.getInt("主题",0)));
        return map;
    }

}
