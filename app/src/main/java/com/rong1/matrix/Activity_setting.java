package com.rong1.matrix;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import java.util.Map;

public class Activity_setting extends Activity {
    private static String TAG="setting";

    PreferencesService preferencesService;//用户主题设置数据保存提取类
    PreferencesService2 preferencesService2;//用户精度设置数据保存提取类
    PreferencesService3 preferencesService3;//用户字体大小设置数据保存提取类

    LinearLayout linearLayout;//更改主题时所需要更改颜色的控件
    LinearLayout linearLayout_setting_root;
    TextView textView_title;
    int theme;//选择的主题


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        Log.d(TAG, "onCreate: 1");
        //将状态栏给为透明
        initState();

        //找到空间和布局
        linearLayout=(LinearLayout)findViewById(R.id.linearLayout_toolbar);
        linearLayout_setting_root=(LinearLayout)findViewById(R.id.linearLayout_setting_root) ;
        textView_title=(TextView)findViewById(R.id.textView_title);

        //设置导航栏点击事件
        Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar2);
        Log.d(TAG, "onCreate: 2");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(MainActivity.this,"您点击了：导航栏按钮",Toast.LENGTH_SHORT).show();
                ((Activity)view.getContext()).onBackPressed();//实现返回按钮
            }
        });
        Log.d(TAG, "onCreate: 3 ");
        //设置导航栏图标
        toolbar.setNavigationIcon(R.drawable.ic_keyboard_arrow_left_white);
        Log.d(TAG, "onCreate: 4");


        //提取用户主题设置参数并且更改主题
        preferencesService =new PreferencesService(this);
        Map<String,String> params=preferencesService.getPerferences();
        theme=Integer.valueOf(params.get("主题"));
        changeTheme(theme);//改变主题

        //提取用户精度设置参数
        preferencesService2 =new PreferencesService2(this);

        //提取用户字体大小设置参数
        preferencesService3 =new PreferencesService3(this);

        //返回到到上一个activity，则通过reaultCode设置主题颜色
        Intent intent=getIntent();
        setResult(2,intent);


    }

    //点击设置里面的主题选项产生的事件
    public void onThemeClick(View view) {
        //Toast.makeText(Activity_setting.this,"你点击了主题按钮",Toast.LENGTH_LONG).show();
        showSingleChoiceDialog();

    }

    //点击设置里面的字体产生的事件
    public void onFontClick(View view) {

        //Toast.makeText(Activity_setting.this,"这个功能还没有开发哦！",Toast.LENGTH_SHORT).show();
        showSingleChoiceDialog3();

    }

    //点击设置里面的精度选项产生的事件
    public void onPrecisionClick(View view) {

        //Toast.makeText(Activity_setting.this,"这个功能还没有开发哦！",Toast.LENGTH_SHORT).show();
        showSingleChoiceDialog2();

    }





    //点击设置里面的关于选项产生的事件
    public void onAboutClick(View view) {

        String versionname="";
        try {
            versionname=getVersionName();
        } catch (Exception e) {
            e.printStackTrace();
        }
        new android.app.AlertDialog.Builder(Activity_setting.this).setTitle("关于").setMessage("\n感谢您的使用，" +
                "遇到问题或者有什么建议请联系944987699@qq.com，谢谢！"+"\n\n版本："+versionname+"\n作者：grchen\n").show();

    }


    //获取版本名称方法
    private String getVersionName() throws Exception{
        // 获取packagemanager的实例  
        PackageManager packageManager = getPackageManager();
        // getPackageName()是你当前类的包名，0代表是获取版本信息  
        PackageInfo packInfo = packageManager.getPackageInfo(getPackageName(),0);
        String version = packInfo.versionName;
        return version;
    }



    //更改主题颜色
    public void changeTheme(int theme){
        switch (theme){
            case 0://默认
                textView_title.setTextColor(getResources().getColor(R.color.white));
                linearLayout.setBackgroundResource(R.color.pureBlue);
                linearLayout_setting_root.setBackgroundResource(R.color.gray1);
                break;
            case 1://夜间
                textView_title.setTextColor(getResources().getColor(R.color.white));
                linearLayout.setBackgroundResource(R.color.dark);
                linearLayout_setting_root.setBackgroundResource(R.color.dark);
                break;
            case 2://黑色
                textView_title.setTextColor(getResources().getColor(R.color.white));
                linearLayout.setBackgroundResource(R.color.dark);
                linearLayout_setting_root.setBackgroundResource(R.color.dark);
                break;
            case 3://绿色
                linearLayout.setBackgroundResource(R.color.green);
                textView_title.setTextColor(getResources().getColor(R.color.white));
                linearLayout_setting_root.setBackgroundResource(R.color.gray1);
                break;
            case 4://红色
                linearLayout.setBackgroundResource(R.color.red);
                textView_title.setTextColor(getResources().getColor(R.color.white));
                linearLayout_setting_root.setBackgroundResource(R.color.gray1);
                break;
            case 5://紫色
                linearLayout.setBackgroundResource(R.color.purple);
                textView_title.setTextColor(getResources().getColor(R.color.white));
                linearLayout_setting_root.setBackgroundResource(R.color.gray1);
                break;

        }
    }


    //显示主题选择单选对话框
    public void showSingleChoiceDialog(){
        final String[] items=new String[]{"默认","夜间模式","黑色","绿色","红色","紫色"};

        //当用户点击选择了新主题后，退出(activity还在)重新点击了主题按钮，默认主题改变了所以再要获取新的主题值
        int theme1;
        Map<String,String> params=preferencesService.getPerferences();
        theme1=Integer.valueOf(params.get("主题"));

        AlertDialog.Builder builder=new AlertDialog.Builder(Activity_setting.this);
        builder.setTitle("主题");

        //第二个参数为默认点击了哪个选项
        builder.setSingleChoiceItems(items, theme1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //改变主题
                changeTheme(i);
                //保存用户选择的主题参数
                preferencesService.save(i);
                //String select_item=items[i].toString();
                //Toast.makeText(Activity_setting.this,"你选择了<"+select_item+">",Toast.LENGTH_SHORT).show();
            }
        });

        //确定按钮
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                dialogInterface.dismiss();
            }
        });

        //取消按钮
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        builder.show();

    }



    //显示精度选择单选对话框
    public void showSingleChoiceDialog2(){
        final String[] items=new String[]{"默认（6位）","2位","4位","8位","10位"};

        //当用户点击选择了新主题后，退出(activity还在)重新点击了精度按钮，默认精度改变了所以再要获取新的精度值
        int precision;
        Map<String,String> params=preferencesService2.getPerferences();
        precision=Integer.valueOf(params.get("精度"));

        final int[] select = {precision};

        AlertDialog.Builder builder=new AlertDialog.Builder(Activity_setting.this);
        builder.setTitle("精度");

        //第二个参数为默认点击了哪个选项
        builder.setSingleChoiceItems(items, precision, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                select[0] =i;
                //String select_item=items[i].toString();
                //Toast.makeText(Activity_setting.this,"你选择了<"+select_item+">",Toast.LENGTH_SHORT).show();
            }
        });

        //确定按钮
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //保存用户选择的主题参数
                preferencesService2.save(select[0]);
                Toast.makeText(Activity_setting.this,"您选择了："+items[select[0]]+",精度仅对新的矩阵有效",Toast.LENGTH_SHORT).show();
                dialogInterface.dismiss();
            }
        });

        //取消按钮
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        builder.show();

    }



    //显示字体大小选择单选对话框
    public void showSingleChoiceDialog3(){
        final String[] items=new String[]{"默认","小","中","大"};

        //当用户点击选择了新的字体大小后，退出(activity还在)重新点击了精度按钮，默认字体大小改变了所以再要获取新的字体大小值
        int textsize;
        Map<String,String> params=preferencesService3.getPerferences();
        textsize =Integer.valueOf(params.get("字体大小"));

        final int[] select = {textsize};

        AlertDialog.Builder builder=new AlertDialog.Builder(Activity_setting.this);
        builder.setTitle("字体大小");

        //第二个参数为默认点击了哪个选项
        builder.setSingleChoiceItems(items, textsize, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                select[0] =i;
                //String select_item=items[i].toString();
                //Toast.makeText(Activity_setting.this,"你选择了<"+select_item+">",Toast.LENGTH_SHORT).show();
            }
        });

        //确定按钮
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //保存用户选择的主题参数
                preferencesService3.save(select[0]);
                //Toast.makeText(Activity_setting.this,"您选择了："+items[select[0]]+",精度仅对新的矩阵有效",Toast.LENGTH_SHORT).show();
                dialogInterface.dismiss();
            }
        });

        //取消按钮
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        builder.show();

    }





    //将状态栏改为透明
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
