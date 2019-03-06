package com.rong1.matrix;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

public class SearchActivity extends Activity {

    ArrayList<String> files;//所有文件的绝对路径
    File file[];//文件
    String filename[];//文件名
    final static String TAG="SearchActivity";

    PreferencesService preferencesService;//用户主题设置数据保存提取类
    LinearLayout linearLayout_search;
    int theme;//选择的主题



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        //透明状态栏
        initState();

        //找到布局
        linearLayout_search=(LinearLayout)findViewById(R.id.linearlayout_search);

        //提取用户主题设置参数并且更改主题
        preferencesService =new PreferencesService(this);
        Map<String,String> params=preferencesService.getPerferences();
        theme=Integer.valueOf(params.get("主题"));
        changeTheme(theme);//改变主题


        //设置返回
        Intent intent=getIntent();
        setResult(1,intent);

        ListView listView=(ListView)findViewById(R.id.listview);
        Log.d(TAG, "onCreate: 1");

        String rootPath=Environment.getExternalStorageDirectory().getPath();
        File floder=new File(rootPath+File.separator+"matrix");
        Log.d(TAG, "onCreate: 2");
        if(floder.exists()){
            files=getFilesAllName(floder);
            filename=new String[files.size()];
            file=new File[files.size()];
            Log.d(TAG, "onCreate: 3");
            //获取文件
            for(int i=0;i<files.size();i++){
                file[i]=new File(files.get(i));
                filename[i]=file[i].getName();//获取文件名字
            }
            Log.d(TAG, "onCreate: 4");
            //适配器
            ArrayAdapter<String> arrayAdapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,filename);
            Log.d(TAG, "onCreate: 5");
            listView.setAdapter(arrayAdapter);
            Log.d(TAG, "onCreate: 6");
            //设置监听
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    //Toast.makeText(SearchActivity.this,"您点击了："+filename[i],Toast.LENGTH_SHORT).show();
                    showNormalDialog(i);//显示删除对话框
                }
            });

        }




    }


    //显示删除对话框
    public void showNormalDialog(final int select){


        AlertDialog.Builder builder=new AlertDialog.Builder(SearchActivity.this);
        builder.setTitle("提示");
        builder.setMessage("是否删除“"+filename[select]+"”文件？");
        builder.setCancelable(true);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                if(file[select].exists()){
                    if(file[select].delete()){
                        Toast.makeText(SearchActivity.this,"删除成功",Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(SearchActivity.this,"删除失败",Toast.LENGTH_SHORT).show();
                    }
                    dialogInterface.dismiss();
                }
                else {
                    Toast.makeText(SearchActivity.this,"已经删除",Toast.LENGTH_SHORT).show();
                }
            }
        });

        //显示对话框
        builder.show();

    }



    //获取文件夹下的所有文件名称
    public ArrayList<String> getFilesAllName(File floder){
        File[] files=floder.listFiles();
        if(files==null){
            //空目录
            return null;
        }
        ArrayList<String> s=new ArrayList<String>();
        for(int i=0;i<files.length;i++){
            s.add(files[i].getAbsolutePath());
        }

        return s;
    }



    //更改主题颜色
    public void changeTheme(int theme){
        switch (theme){
            case 0://默认
                linearLayout_search.setBackgroundResource(R.color.pureBlue);
                break;
            case 1://夜间
                linearLayout_search.setBackgroundResource(R.color.dark);
                break;
            case 2://黑色
                linearLayout_search.setBackgroundResource(R.color.dark);
                break;
            case 3://绿色
                linearLayout_search.setBackgroundResource(R.color.green);
                break;
            case 4://红色
                linearLayout_search.setBackgroundResource(R.color.red);
                break;
            case 5://紫色
                linearLayout_search.setBackgroundResource(R.color.purple);
                break;

        }
    }


    private void initState() {
        //安卓4.0才可以设置透明状态栏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //透明导航栏
            //getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        //安卓5.0以上状态栏透明
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //让应用主题内容占用系统状态栏的空间,注意:下面两个参数必须一起使用 stable 牢固的
            //设置让应用主题内容占据状态栏和导航栏    导航栏View.SYSTEM_UI_FLAG_HIDE_NAVIGATION|   添加这个会导致toolbar高度太高和导航栏遮挡应用界面
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
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
