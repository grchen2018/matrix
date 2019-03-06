package com.rong1.matrix;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.ColorUtils;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Map;

public class MainActivity extends Activity {
    private static final String TAG="MainActivity";

    DecimalFormat decf;//将double型数据格式化为字符串
    String order="";//执行的命令
    String explain="\n1.功能:矩阵相加、相减、相乘、求逆、求行列式." +
            "\n2.应用的功能正常运行，需要授予应用读写存储的权限." +
            "\n3.需要点击添加按钮添加矩阵才可以进行矩阵运算." +
            "\n4.相加、相减、相乘功能需要在矩阵1和矩阵2输入框输入已经添加的矩阵的名称，" +
            "主界面有显示矩阵名称后缀（.txt）不用输入，并且不区分大小写，同时也需要为结果输入一个名称，然后点击相关按钮，结果会保存矩阵1和矩阵2的运算结果." +
            "\n5.求逆和转置功能只需要输入结果的名称和矩阵1的名称然后点击求逆或转置按钮即可求得结果." +
            "\n6.所有运算结果和添加的矩阵都会显示在主界面." +
            "\n7.在结果输入框中输入已存在的矩阵的名称会覆盖掉原有的矩阵，点击刷新按钮会刷掉旧的矩阵数据." +
            "\n8.删除按钮会删除所有已保存的矩阵和结果." +
            "\n9.求行列按钮是单独界面的，需要在里面输入矩阵的行数和矩阵，才可以求行列式.";

    //imageview控件
    ImageView imageView_Add;
    ImageView imageView_plus;
    ImageView imageView_minus;
    ImageView imageView_ride;
    ImageView imageView_inv;
    ImageView imageView_transform;
    ImageView imageView_det;
    ImageView imageView_deleted;
    ImageView imageView_refresh;

    //textview控件
    TextView textView_saved_title;//主界面显示内容textview
    TextView textView_saved;//主界面显示标题textview

    //edittext控件
    EditText editText_newmatrix;//矩阵1和矩阵2运算的到的结果保存在这个矩阵里面
    EditText editText_matrix1;//矩阵1
    EditText editText_matrix2;//矩阵2

    //button控件
    Button button_plus;
    Button button_add;
    Button button_minus;
    Button button_ride;
    Button button_inv;
    Button button_transform;
    Button button_det;
    Button button_deleted;

    //布局
    LinearLayout linearLayout;//更改主题时所需要更改颜色的控件
    LinearLayout linearLayout_a;
    LinearLayout linearLayout_b;
    RelativeLayout relativeLayout;

    //动画
    Animation animation;

    //主题布局
    PreferencesService preferencesService;//用户主题设置数据保存提取类
    PreferencesService2 preferencesService2;//用户精度设置数据保存提取类
    PreferencesService3 preferencesService3;//用户字体大小设置数据保存提取类

    //用户设置参数
    int theme;//选择的主题
    int precision;//选择的精度
    int textsize;//选择的字体大小

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //找到布局
        linearLayout=(LinearLayout)findViewById(R.id.linearLayout_toolbar);
        linearLayout_a=(LinearLayout)findViewById(R.id.linearLayout_a);
        linearLayout_b=(LinearLayout)findViewById(R.id.linearLayout_b);
        relativeLayout=(RelativeLayout) findViewById(R.id.relativeLayout_root);

        //找到textview
        textView_saved=(TextView)findViewById(R.id.textView_saved);
        textView_saved_title=(TextView)findViewById(R.id.textView_saved_title);

        //找到编辑框
        editText_newmatrix=(EditText)findViewById(R.id.newmatrixname_edittext);
        editText_matrix1=(EditText)findViewById(R.id.matrix1_editext);
        editText_matrix2=(EditText)findViewById(R.id.matrix2_edittext);

        //找到按钮
        button_plus=(Button)findViewById(R.id.button_plus);
        button_minus=(Button)findViewById(R.id.button_minus);
        button_ride=(Button)findViewById(R.id.button_ride);
        button_inv=(Button)findViewById(R.id.button_inv);
        button_add=(Button)findViewById(R.id.button_add);
        button_transform=(Button)findViewById(R.id.button_transform);
        button_det=(Button)findViewById(R.id.button_det);
        button_deleted=(Button)findViewById(R.id.button_deleted);


        //找到imageview
        imageView_Add=(ImageView)findViewById(R.id.imageView5);
        imageView_plus=(ImageView)findViewById(R.id.imageView3);
        imageView_minus=(ImageView)findViewById(R.id.imageView4);
        imageView_ride=(ImageView)findViewById(R.id.imageView2);
        imageView_inv=(ImageView)findViewById(R.id.imageView);
        imageView_transform=(ImageView)findViewById(R.id.imageView6);
        imageView_det=(ImageView)findViewById(R.id.imageView7);
        imageView_deleted=(ImageView)findViewById(R.id.imageView8);
        imageView_refresh=(ImageView)findViewById(R.id.imageView_refresh);

        //提取用户主题设置参数并且更改主题
        preferencesService =new PreferencesService(this);
        Map<String,String> params=preferencesService.getPerferences();
        theme=Integer.valueOf(params.get("主题"));
        changeTheme(theme);//改变主题


        //字符串精度,默认精度
        decf=new DecimalFormat("0.000000");

        //提取用户精度设置参数并且更改精度
        preferencesService2 =new PreferencesService2(this);
        Map<String,String> params2=preferencesService2.getPerferences();
        precision=Integer.valueOf(params2.get("精度"));
        changePrecision(precision);//改变精度

        //提取用户字体大小设置参数并且更改字体大小
        preferencesService3 =new PreferencesService3(this);
        Map<String,String> params3=preferencesService3.getPerferences();
        textsize=Integer.valueOf(params3.get("字体大小"));
        changeTextSize(textsize);//改变字体大小

        //显示旋转动画
        animation=AnimationUtils.loadAnimation(this,R.anim.tween_animation);
        animation.setRepeatMode(Animation.RESTART);
        animation.setRepeatCount(1);//显示一次


        //透明状态栏
        initState();

        //在xml文件中，edittext的CursorVisible设置了false，光标不可见，所以不可编辑
        //设置EditText可编辑，光标可见
        editText_newmatrix.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editText_newmatrix.setCursorVisible(true);
            }
        });
        editText_matrix1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editText_matrix1.setCursorVisible(true);
            }
        });
        editText_matrix2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editText_matrix2.setCursorVisible(true);
            }
        });

        //toolbar设置
        Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar);
        //toolbar.setTitle("首页");//设置标题
        //toolbar.setTitleTextColor(Color.WHITE);//标题字体颜色
        toolbar.inflateMenu(R.menu.menu_main);//toolbar加载菜单栏
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {//设置菜单监听
                switch(item.getItemId()){
                    case R.id.help:{
                        //do some thing
                        //Toast.makeText(MainActivity.this,"您点击了："+item.getTitle()+"按钮",Toast.LENGTH_SHORT).show();
                        new android.app.AlertDialog.Builder(MainActivity.this).setTitle("帮助").setMessage(explain).show();
                        return  true;
                    }
                    case R.id.search:
                        //do some thing
                        //Toast.makeText(MainActivity.this,"这个功能还没有开发哦！",Toast.LENGTH_SHORT).show();
                        //启动SearchActivity
                        Intent intent2=new Intent(MainActivity.this,SearchActivity.class);
                        startActivityForResult(intent2,1);
                        return  true;
                    case R.id.home:
                        Toast.makeText(MainActivity.this,"您点击了："+item.getTitle()+"按钮",Toast.LENGTH_SHORT).show();
                    case R.id.setting:
                        //do some thing
                        //Toast.makeText(MainActivity.this,"没有什么可设置的^_^",Toast.LENGTH_SHORT).show();
                        Intent intent=new Intent(MainActivity.this,Activity_setting.class);
                        startActivityForResult(intent,2);
                        return  true;
                    case R.id.quit:
                        //关闭当前activity
                        finish();
                        return true;
                    default:
                        return false;
                }

            }
        });
        toolbar.setNavigationIcon(R.drawable.ic_keyboard_arrow_left_white);//设置导航栏图标
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {                    //设置监听
                //Toast.makeText(MainActivity.this,"您点击了：导航栏按钮",Toast.LENGTH_SHORT).show();
                ((Activity)view.getContext()).onBackPressed();//实现返回按钮
            }
        });


        /*
         //显示logo
         getSupportActionBar().setDisplayShowHomeEnabled(true);
         getSupportActionBar().setDisplayUseLogoEnabled(true);
         getSupportActionBar().setLogo(R.mipmap.ic_launcher);
        */

        //显示标题和子标题
         /*
         getSupportActionBar().setDisplayShowTitleEnabled(true);
         toolbar.setTitle("Matrix");
         toolbar.setSubtitle("the detail of toolbar");
         //显示导航按钮
         toolbar.setNavigationIcon(R.drawable.icon_back);
         setStatusBarColor(this,Color.WHITE);//6.0以上如果设置状态栏颜色为亮色则设置字体颜色*/



        //在使用外部存储是要检查外部存储是否可用
        boolean mExternalStorageAvailable=false;
        boolean mExternalStorageWriteable=false;
        String state=Environment.getExternalStorageState();
        if(Environment.MEDIA_MOUNTED.equals(state)){
            //可以写可以读
            //Toast.makeText(this,"可写和读",Toast.LENGTH_SHORT).show();
            mExternalStorageWriteable=mExternalStorageAvailable=true;
        }
        else if(Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)){
            //只可写
            //Toast.makeText(this,"可写不可读",Toast.LENGTH_SHORT).show();
            mExternalStorageAvailable=true;
            mExternalStorageWriteable=false;
        }
        else{
            mExternalStorageWriteable=mExternalStorageAvailable=false;
            //Toast.makeText(this,"不可写和读",Toast.LENGTH_SHORT).show();
        }


        //6.0以上系统请求应用外部存储读写权限
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            requestPower();

           /* //检查应用是否有读写外部存储的权限，如果没有则请求权限，调出权限请求对话框，用户选择后则调用onRequestPermissionsResult方法
            int permissionCheck=ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if(permissionCheck==PackageManager.PERMISSION_DENIED){
                //请求对话框
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            }*/
        }


        //在根目录下创建matrix文件夹
        String rootPath=Environment.getExternalStorageDirectory().getPath();
        File floder=new File(rootPath+File.separator+"matrix");
        if(!floder.exists()){
            if(!floder.mkdirs()){
                Log.d(TAG, "Directory not created");
            };
        }

        //将floder文件夹下的文件写进textView_saved中
        File[] files;
        String result="";
        if(floder.exists()){
           ArrayList<String> s=getFilesAllName(floder);
           if(s!=null){
               for(int i=0;i<s.size();i++){
                   File file1=new File(s.get(i));
                   result=result+file1.getName()+"\n";
                   result=result+readFile(file1)+"\n";
               }

           }
        }

        textView_saved.setText(result);
        Log.d(TAG, "onCreate: done");
    }

    //onCreated方法结束









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
                linearLayout.setBackgroundResource(R.color.pureBlue);
                linearLayout_a.setBackgroundResource(R.color.gray0);
                linearLayout_b.setBackgroundResource(R.color.gray0);
                relativeLayout.setBackgroundResource(R.color.gray1);
                //字体颜色
                textView_saved_title.setTextColor(getResources().getColor(R.color.fontcolor));
                textView_saved.setTextColor(getResources().getColor(R.color.fontcolor));
                editText_matrix1.setTextColor(getResources().getColor(R.color.fontcolor));
                editText_matrix1.setHintTextColor(getResources().getColor(R.color.fontcolor));
                editText_matrix2.setTextColor(getResources().getColor(R.color.fontcolor));
                editText_matrix2.setHintTextColor(getResources().getColor(R.color.fontcolor));
                editText_newmatrix.setTextColor(getResources().getColor(R.color.fontcolor));
                editText_newmatrix.setHintTextColor(getResources().getColor(R.color.fontcolor));
                button_add.setTextColor(getResources().getColor(R.color.fontcolor));
                button_deleted.setTextColor(getResources().getColor(R.color.fontcolor));
                button_det.setTextColor(getResources().getColor(R.color.fontcolor));
                button_transform.setTextColor(getResources().getColor(R.color.fontcolor));
                button_inv.setTextColor(getResources().getColor(R.color.fontcolor));
                button_ride.setTextColor(getResources().getColor(R.color.fontcolor));
                button_minus.setTextColor(getResources().getColor(R.color.fontcolor));
                button_plus.setTextColor(getResources().getColor(R.color.fontcolor));
                //imageview图标
                imageView_Add.setImageResource(R.drawable.ic_add_gray);
                imageView_plus.setImageResource(R.drawable.ic_plus_gray);
                imageView_minus.setImageResource(R.drawable.ic_minus_gray);
                imageView_ride.setImageResource(R.drawable.ic_ride_gray);
                imageView_inv.setImageResource(R.drawable.ic_divide_gray);
                imageView_transform.setImageResource(R.drawable.ic_transform_gray);
                imageView_det.setImageResource(R.drawable.ic_divide_gray);
                imageView_deleted.setImageResource(R.drawable.ic_deleted_gray);
                imageView_refresh.setImageResource(R.drawable.ic_refresh_gray);
                break;
            case 1://夜间模式
                linearLayout.setBackgroundResource(R.color.dark);
                linearLayout_a.setBackgroundResource(R.color.dark);
                linearLayout_b.setBackgroundResource(R.color.dark);
                relativeLayout.setBackgroundResource(R.color.dark);
                //字体颜色
                textView_saved_title.setTextColor(getResources().getColor(R.color.fontcolor));
                textView_saved.setTextColor(getResources().getColor(R.color.fontcolor));
                editText_matrix1.setTextColor(getResources().getColor(R.color.fontcolor));
                editText_matrix1.setHintTextColor(getResources().getColor(R.color.fontcolor));
                editText_matrix2.setTextColor(getResources().getColor(R.color.fontcolor));
                editText_matrix2.setHintTextColor(getResources().getColor(R.color.fontcolor));
                editText_newmatrix.setTextColor(getResources().getColor(R.color.fontcolor));
                editText_newmatrix.setHintTextColor(getResources().getColor(R.color.fontcolor));
                button_add.setTextColor(getResources().getColor(R.color.fontcolor));
                button_deleted.setTextColor(getResources().getColor(R.color.fontcolor));
                button_det.setTextColor(getResources().getColor(R.color.fontcolor));
                button_transform.setTextColor(getResources().getColor(R.color.fontcolor));
                button_inv.setTextColor(getResources().getColor(R.color.fontcolor));
                button_ride.setTextColor(getResources().getColor(R.color.fontcolor));
                button_minus.setTextColor(getResources().getColor(R.color.fontcolor));
                button_plus.setTextColor(getResources().getColor(R.color.fontcolor));
                //imageview图标
                imageView_Add.setImageResource(R.drawable.ic_add_gray);
                imageView_plus.setImageResource(R.drawable.ic_plus_gray);
                imageView_minus.setImageResource(R.drawable.ic_minus_gray);
                imageView_ride.setImageResource(R.drawable.ic_ride_gray);
                imageView_inv.setImageResource(R.drawable.ic_divide_gray);
                imageView_transform.setImageResource(R.drawable.ic_transform_gray);
                imageView_det.setImageResource(R.drawable.ic_divide_gray);
                imageView_deleted.setImageResource(R.drawable.ic_deleted_gray);
                imageView_refresh.setImageResource(R.drawable.ic_refresh_gray);
                break;
            case 2://黑色
                linearLayout.setBackgroundResource(R.color.dark);
                linearLayout_a.setBackgroundResource(R.color.dark);
                linearLayout_b.setBackgroundResource(R.color.dark);
                relativeLayout.setBackgroundResource(R.color.dark);
                //字体颜色
                textView_saved_title.setTextColor(getResources().getColor(R.color.white));
                textView_saved.setTextColor(getResources().getColor(R.color.white));
                editText_matrix1.setTextColor(getResources().getColor(R.color.white));
                editText_matrix1.setHintTextColor(getResources().getColor(R.color.white));
                editText_matrix2.setTextColor(getResources().getColor(R.color.white));
                editText_matrix2.setHintTextColor(getResources().getColor(R.color.white));
                editText_newmatrix.setTextColor(getResources().getColor(R.color.white));
                editText_newmatrix.setHintTextColor(getResources().getColor(R.color.white));
                button_add.setTextColor(getResources().getColor(R.color.white));
                button_deleted.setTextColor(getResources().getColor(R.color.white));
                button_det.setTextColor(getResources().getColor(R.color.white));
                button_transform.setTextColor(getResources().getColor(R.color.white));
                button_inv.setTextColor(getResources().getColor(R.color.white));
                button_ride.setTextColor(getResources().getColor(R.color.white));
                button_minus.setTextColor(getResources().getColor(R.color.white));
                button_plus.setTextColor(getResources().getColor(R.color.white));
                //imageview图标
                imageView_Add.setImageResource(R.drawable.ic_add_a_white);
                imageView_plus.setImageResource(R.drawable.ic_plus_white);
                imageView_minus.setImageResource(R.drawable.ic_minus_white);
                imageView_ride.setImageResource(R.drawable.ic_ride_white);
                imageView_inv.setImageResource(R.drawable.ic_divide_white);
                imageView_transform.setImageResource(R.drawable.ic_transform_white);
                imageView_det.setImageResource(R.drawable.ic_divide_white);
                imageView_deleted.setImageResource(R.drawable.ic_deleted_white);
                imageView_refresh.setImageResource(R.drawable.ic_refresh_white);
                break;

            case 3://绿色
                linearLayout.setBackgroundResource(R.color.green);
                linearLayout_a.setBackgroundResource(R.color.gray0);
                linearLayout_b.setBackgroundResource(R.color.gray0);
                relativeLayout.setBackgroundResource(R.color.gray1);
                //字体颜色
                textView_saved_title.setTextColor(getResources().getColor(R.color.fontcolor));
                textView_saved.setTextColor(getResources().getColor(R.color.fontcolor));
                editText_matrix1.setTextColor(getResources().getColor(R.color.fontcolor));
                editText_matrix1.setHintTextColor(getResources().getColor(R.color.fontcolor));
                editText_matrix2.setTextColor(getResources().getColor(R.color.fontcolor));
                editText_matrix2.setHintTextColor(getResources().getColor(R.color.fontcolor));
                editText_newmatrix.setTextColor(getResources().getColor(R.color.fontcolor));
                editText_newmatrix.setHintTextColor(getResources().getColor(R.color.fontcolor));
                button_add.setTextColor(getResources().getColor(R.color.fontcolor));
                button_deleted.setTextColor(getResources().getColor(R.color.fontcolor));
                button_det.setTextColor(getResources().getColor(R.color.fontcolor));
                button_transform.setTextColor(getResources().getColor(R.color.fontcolor));
                button_inv.setTextColor(getResources().getColor(R.color.fontcolor));
                button_ride.setTextColor(getResources().getColor(R.color.fontcolor));
                button_minus.setTextColor(getResources().getColor(R.color.fontcolor));
                button_plus.setTextColor(getResources().getColor(R.color.fontcolor));
                //imageview图标
                imageView_Add.setImageResource(R.drawable.ic_add_gray);
                imageView_plus.setImageResource(R.drawable.ic_plus_gray);
                imageView_minus.setImageResource(R.drawable.ic_minus_gray);
                imageView_ride.setImageResource(R.drawable.ic_ride_gray);
                imageView_inv.setImageResource(R.drawable.ic_divide_gray);
                imageView_transform.setImageResource(R.drawable.ic_transform_gray);
                imageView_det.setImageResource(R.drawable.ic_divide_gray);
                imageView_deleted.setImageResource(R.drawable.ic_deleted_gray);
                imageView_refresh.setImageResource(R.drawable.ic_refresh_gray);
                break;

            case 4://红色
                linearLayout.setBackgroundResource(R.color.red);
                linearLayout_a.setBackgroundResource(R.color.gray0);
                linearLayout_b.setBackgroundResource(R.color.gray0);
                relativeLayout.setBackgroundResource(R.color.gray1);
                //字体颜色
                textView_saved_title.setTextColor(getResources().getColor(R.color.fontcolor));
                textView_saved.setTextColor(getResources().getColor(R.color.fontcolor));
                editText_matrix1.setTextColor(getResources().getColor(R.color.fontcolor));
                editText_matrix1.setHintTextColor(getResources().getColor(R.color.fontcolor));
                editText_matrix2.setTextColor(getResources().getColor(R.color.fontcolor));
                editText_matrix2.setHintTextColor(getResources().getColor(R.color.fontcolor));
                editText_newmatrix.setTextColor(getResources().getColor(R.color.fontcolor));
                editText_newmatrix.setHintTextColor(getResources().getColor(R.color.fontcolor));
                button_add.setTextColor(getResources().getColor(R.color.fontcolor));
                button_deleted.setTextColor(getResources().getColor(R.color.fontcolor));
                button_det.setTextColor(getResources().getColor(R.color.fontcolor));
                button_transform.setTextColor(getResources().getColor(R.color.fontcolor));
                button_inv.setTextColor(getResources().getColor(R.color.fontcolor));
                button_ride.setTextColor(getResources().getColor(R.color.fontcolor));
                button_minus.setTextColor(getResources().getColor(R.color.fontcolor));
                button_plus.setTextColor(getResources().getColor(R.color.fontcolor));
                //imageview图标
                imageView_Add.setImageResource(R.drawable.ic_add_gray);
                imageView_plus.setImageResource(R.drawable.ic_plus_gray);
                imageView_minus.setImageResource(R.drawable.ic_minus_gray);
                imageView_ride.setImageResource(R.drawable.ic_ride_gray);
                imageView_inv.setImageResource(R.drawable.ic_divide_gray);
                imageView_transform.setImageResource(R.drawable.ic_transform_gray);
                imageView_det.setImageResource(R.drawable.ic_divide_gray);
                imageView_deleted.setImageResource(R.drawable.ic_deleted_gray);
                imageView_refresh.setImageResource(R.drawable.ic_refresh_gray);
                break;

            case 5://紫色
                linearLayout.setBackgroundResource(R.color.purple);
                linearLayout_a.setBackgroundResource(R.color.gray0);
                linearLayout_b.setBackgroundResource(R.color.gray0);
                relativeLayout.setBackgroundResource(R.color.gray1);
                //字体颜色
                textView_saved_title.setTextColor(getResources().getColor(R.color.fontcolor));
                textView_saved.setTextColor(getResources().getColor(R.color.fontcolor));
                editText_matrix1.setTextColor(getResources().getColor(R.color.fontcolor));
                editText_matrix1.setHintTextColor(getResources().getColor(R.color.fontcolor));
                editText_matrix2.setTextColor(getResources().getColor(R.color.fontcolor));
                editText_matrix2.setHintTextColor(getResources().getColor(R.color.fontcolor));
                editText_newmatrix.setTextColor(getResources().getColor(R.color.fontcolor));
                editText_newmatrix.setHintTextColor(getResources().getColor(R.color.fontcolor));
                button_add.setTextColor(getResources().getColor(R.color.fontcolor));
                button_deleted.setTextColor(getResources().getColor(R.color.fontcolor));
                button_det.setTextColor(getResources().getColor(R.color.fontcolor));
                button_transform.setTextColor(getResources().getColor(R.color.fontcolor));
                button_inv.setTextColor(getResources().getColor(R.color.fontcolor));
                button_ride.setTextColor(getResources().getColor(R.color.fontcolor));
                button_minus.setTextColor(getResources().getColor(R.color.fontcolor));
                button_plus.setTextColor(getResources().getColor(R.color.fontcolor));
                //imageview图标
                imageView_Add.setImageResource(R.drawable.ic_add_gray);
                imageView_plus.setImageResource(R.drawable.ic_plus_gray);
                imageView_minus.setImageResource(R.drawable.ic_minus_gray);
                imageView_ride.setImageResource(R.drawable.ic_ride_gray);
                imageView_inv.setImageResource(R.drawable.ic_divide_gray);
                imageView_transform.setImageResource(R.drawable.ic_transform_gray);
                imageView_det.setImageResource(R.drawable.ic_divide_gray);
                imageView_deleted.setImageResource(R.drawable.ic_deleted_gray);
                imageView_refresh.setImageResource(R.drawable.ic_refresh_gray);
                break;
        }
    }



    //改变精度
    public void changePrecision(int precision){

        switch(precision){
            case 0:
                decf=new DecimalFormat("0.000000");
                break;
            case 1:
                decf=new DecimalFormat("0.00");
                break;
            case 2:
                decf=new DecimalFormat("0.0000");
                break;
            case 3:
                decf=new DecimalFormat("0.00000000");
                break;
            case 4:
                decf=new DecimalFormat("0.0000000000");
                break;
        }

    }


    //改变字体大小
    public void changeTextSize(int textsize){
        switch(textsize){
            case 0:
                textView_saved.setTextSize(TypedValue.COMPLEX_UNIT_SP,15);
                break;
            case 1:
                textView_saved.setTextSize(TypedValue.COMPLEX_UNIT_SP,13);
                break;
            case 2:
                textView_saved.setTextSize(TypedValue.COMPLEX_UNIT_SP,16);
                break;
            case 3:
                textView_saved.setTextSize(TypedValue.COMPLEX_UNIT_SP,19);
                break;



        }

    }


    //更改对话框颜色
    public void changeThemeDialog(LinearLayout linearLayout,int theme){
        switch (theme){
            case 0://默认
                linearLayout.setBackgroundResource(R.color.white);
                break;
            case 1://夜间
                linearLayout.setBackgroundResource(R.color.dark);
                break;
            case 2://黑色
                linearLayout.setBackgroundResource(R.color.dark);
                break;
            case 3://绿色
                linearLayout.setBackgroundResource(R.color.green);
                break;
            case 4://红色
                linearLayout.setBackgroundResource(R.color.red);
                break;
            case 5://紫色
                linearLayout.setBackgroundResource(R.color.purple);
                break;
        }
    }



    //读取文件
    public String readFile(File file){
        String result="";
        BufferedReader br = null;
        try {
            br=new BufferedReader(new FileReader(file));
            String tmp;
            while((tmp=br.readLine())!=null){
                result=result+tmp+"\n";
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(br!=null){
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return  result;
    }


    //写文件操作
    public void writeFile(File file,String str){

        FileOutputStream fileOutputStream;
        try{
            fileOutputStream=new FileOutputStream(file);
        }catch (FileNotFoundException e){
            return;
        }
        try {
            fileOutputStream.write(str.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                fileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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


    //读取文件中的矩阵数据
    public double[][] readMatrix(File file){
        String result="";
        BufferedReader br = null;
        try {
            br=new BufferedReader(new FileReader(file));
            String tmp;
            while((tmp=br.readLine())!=null){
                result=result+tmp+"\n";
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(br!=null){
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        String A[],B[];
        A=result.trim().split("[\\n]");
        Log.d(TAG, "readMatrix: A[0]="+A[0]+"  A的长度："+A.length);

        int column=A[0].split("[\\s]+").length;//[\s]+表示1个或多个连续的空白字符
        double matrix[][]=new double[A.length][column];

        for(int i=0;i<A.length;i++){
            A[i]=A[i].trim();
            B=A[i].split("[\\s]+");
            for(int j=0;j<column;j++){
                Log.d(TAG, "readMatrix: B["+j+"]="+B[j]+"   B的长度："+B.length);
                B[j]=B[j].trim();
                try{
                    matrix[i][j]=Double.valueOf(B[j].trim());
                }catch (Exception e){
                    Toast.makeText(MainActivity.this,"数据格式有误",Toast.LENGTH_SHORT).show();
                    return null;
                }

            }
        }
        return  matrix;
    }



    //点击添加按钮添加矩阵
    public void onButtonAddClick(View view) {

        //为添加按钮添加动画
        imageView_Add.startAnimation(animation);

        showSelfDialog2();
    }

    //点击求行列式按钮
    public void onButtonDetClick(View view) {

        //为求行列式按钮添加动画
        imageView_det.startAnimation(animation);

        Log.d(TAG,"onButtonDetClick  in");
        showSelfDialog();//调用生成自定义对话框方法
        Log.d(TAG,"onButtonDetClick  done");

    }


    //点击相加按钮，计算矩阵1与矩阵2相加的结果
    public void onButtonPlusClick(View view) {

        //为相加按钮添加动画
        imageView_plus.startAnimation(animation);


        double matrix1[][];
        double matrix2[][];
        String matrix1name=editText_matrix1.getText().toString();
        String matrix2name=editText_matrix2.getText().toString();
        String newmatrixname=editText_newmatrix.getText().toString();
        if(matrix1name.equals("")||matrix2name.equals("")||newmatrixname.equals("")){
            Toast.makeText(MainActivity.this,"请输入正确的矩阵名称",Toast.LENGTH_SHORT).show();
            return;
        }
        File file1=Environment.getExternalStoragePublicDirectory("matrix"+File.separator+matrix1name+".txt");
        File file2=Environment.getExternalStoragePublicDirectory("matrix"+File.separator+matrix2name+".txt");
        if(file1.exists()){
            matrix1=readMatrix(file1);
        }
        else {
            Toast.makeText(MainActivity.this,"矩阵1不存在，请重新输入",Toast.LENGTH_SHORT).show();
            return;
        }

        if(file2.exists()){
            matrix2=readMatrix(file2);
        }
        else {
            Toast.makeText(MainActivity.this,"矩阵2不存在，请重新输入",Toast.LENGTH_SHORT).show();
            return;
        }

        int row1=matrix1.length;//行数
        int column1=matrix1[0].length;//列数

        int row2=matrix2.length;
        int column2=matrix2[0].length;

        if(row1==row2 && column1==column2){
            double matrix3[][]=matrixPlus(matrix1,matrix2);
            //输出到文件
            String str="";//新的矩阵字符串
            for(int i=0;i<row1;i++){
                for(int j=0;j<column1;j++){
                    str=str+decf.format(matrix3[i][j])+"\t\t\t\t\t\t";
                }
                str=str+"\n";
            }

            File file=Environment.getExternalStoragePublicDirectory("matrix"+File.separator+newmatrixname+".txt");
            if(!file.exists()){
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            //写入文件
            writeFile(file,str);
            //这个命令字符串
            String thisorder=newmatrixname+"="+matrix1name+"+"+matrix2name+"\n";
            //全部命令字符串
            order=order+thisorder;
            //首页矩阵字符串
            str=textView_saved.getText()+thisorder+"\n"+newmatrixname+".txt"+"\n"+str+"\n";
            textView_saved.setText(str);
            Toast.makeText(MainActivity.this,"相加成功",Toast.LENGTH_SHORT).show();
        }else{

            Toast.makeText(MainActivity.this,"矩阵的行数和列数不相等，请从新输入",Toast.LENGTH_SHORT).show();
        }


    }

    //点击删除全部按钮，会删除掉matrix文件夹下的所有文件
    public void onButtonDeletedClick(View view) {

        //为删除全部按钮添加动画
        imageView_deleted.startAnimation(animation);

        String rootPath=Environment.getExternalStorageDirectory().getPath();
        File floder=new File(rootPath+File.separator+"matrix");
        ArrayList<String> s=getFilesAllName(floder);

        if(s==null){
            Toast.makeText(MainActivity.this,"文件夹为空",Toast.LENGTH_SHORT).show();
            return;
        }
        else{
            for(int i=0;i<s.size();i++){
                deletedFile(s.get(i));
            }
            Toast.makeText(MainActivity.this,"删除完成",Toast.LENGTH_SHORT).show();
        }

        textView_saved.setText("");

    }


    //点击转置按钮，求矩阵1的转置矩阵
    public void onButtonTransformClick(View view) {

        //为转置按钮添加动画
        imageView_transform.startAnimation(animation);

        double matrix1[][];
        String matrix1name=editText_matrix1.getText().toString();
        String newmatrixname=editText_newmatrix.getText().toString();
        if(matrix1name.equals("")||newmatrixname.equals("")){
            Toast.makeText(MainActivity.this,"请输入正确的矩阵名称",Toast.LENGTH_SHORT).show();
            return;
        }
        File file1=Environment.getExternalStoragePublicDirectory("matrix"+File.separator+matrix1name+".txt");
        if(file1.exists()){
            matrix1=readMatrix(file1);
        }
        else {
            Toast.makeText(MainActivity.this,"矩阵1不存在，请重新输入",Toast.LENGTH_SHORT).show();
            return;
        }

        int row=matrix1.length;
        int column=matrix1[0].length;
        double matrix3[][]=matrixTransform(matrix1);
        //输出到文件
        String str="";
        for(int i=0;i<column;i++){
            for(int j=0;j<row;j++){
                str=str+decf.format(matrix3[i][j])+"\t\t\t\t\t\t";
            }
            str=str+"\n";
        }

        File file=Environment.getExternalStoragePublicDirectory("matrix"+File.separator+newmatrixname+".txt");
        if(!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //写入文件
        writeFile(file,str);
        //这个命令字符串
        String thisorder=newmatrixname+"="+matrix1name+".'\n";
        //全部命令字符串
        order=order+thisorder;
        //首页矩阵字符串
        str=textView_saved.getText()+thisorder+"\n"+newmatrixname+".txt"+"\n"+str+"\n";
        textView_saved.setText(str);
        Toast.makeText(MainActivity.this,"转置成功",Toast.LENGTH_SHORT).show();

    }



    //点击刷新按钮,刷新已保存的矩阵，并显示在textview_saved中
    public void onButtonRefreshClick(View view) {

        //为刷新按钮添加动画
        imageView_refresh.startAnimation(animation);

        //找到matrix文件夹
        String rootPath=Environment.getExternalStorageDirectory().getPath();
        String result="";
        File floder=new File(rootPath+File.separator+"matrix");

        //将floder文件夹下的文件写进textView_saved中
        File[] files;
        if(floder.exists()){
            ArrayList<String> s=getFilesAllName(floder);
            if(s!=null){
                for(int i=0;i<s.size();i++){
                    File file1=new File(s.get(i));
                    result=result+file1.getName()+"\n";
                    result=result+readFile(file1)+"\n";
                }

            }
        }
        result=result+order;
        textView_saved.setText(result);
    }


    //点击矩阵相减按钮，计算矩阵1与矩阵2相减
    public void onButtonminusClick(View view) {


        //为矩阵相减按钮添加动画
        imageView_minus.startAnimation(animation);

        double matrix1[][];
        double matrix2[][];
        String matrix1name=editText_matrix1.getText().toString();
        String matrix2name=editText_matrix2.getText().toString();
        String newmatrixname=editText_newmatrix.getText().toString();
        if(matrix1name.equals("")||matrix2name.equals("")||newmatrixname.equals("")){
            Toast.makeText(MainActivity.this,"请输入正确的矩阵名称",Toast.LENGTH_SHORT).show();
            return;
        }
        File file1=Environment.getExternalStoragePublicDirectory("matrix"+File.separator+matrix1name+".txt");
        File file2=Environment.getExternalStoragePublicDirectory("matrix"+File.separator+matrix2name+".txt");
        if(file1.exists()){
            matrix1=readMatrix(file1);
        }
        else {
            Toast.makeText(MainActivity.this,"矩阵1不存在，请重新输入",Toast.LENGTH_SHORT).show();
            return;
        }

        if(file2.exists()){
            matrix2=readMatrix(file2);
        }
        else {
            Toast.makeText(MainActivity.this,"矩阵2不存在，请重新输入",Toast.LENGTH_SHORT).show();
            return;
        }

        int row1=matrix1.length;//行数
        int column1=matrix1[0].length;//列数

        int row2=matrix2.length;
        int column2=matrix2[0].length;

        if(row1==row2 && column1==column2){
            double matrix3[][]=matrixMinus(matrix1,matrix2);
            //输出到文件
            String str="";
            for(int i=0;i<row1;i++){
                for(int j=0;j<column1;j++){
                    str=str+decf.format(matrix3[i][j])+"\t\t\t\t\t\t";
                }
                str=str+"\n";
            }

            File file=Environment.getExternalStoragePublicDirectory("matrix"+File.separator+newmatrixname+".txt");
            if(!file.exists()){
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            //写入文件
            writeFile(file,str);
            //这个命令字符串
            String thisorder=newmatrixname+"="+matrix1name+"-"+matrix2name+"\n";
            //全部命令字符串
            order=order+thisorder;
            //首页矩阵字符串
            str=textView_saved.getText()+thisorder+"\n"+newmatrixname+".txt"+"\n"+str+"\n";
            textView_saved.setText(str);
            Toast.makeText(MainActivity.this,"相减成功",Toast.LENGTH_SHORT).show();
        }else{

            Toast.makeText(MainActivity.this,"矩阵的行数和列数不相等，请从新输入",Toast.LENGTH_SHORT).show();
        }


    }



    //点击矩阵相乘按钮，计算矩阵1乘以矩阵2
    public void onButtonRideClick(View view) {


        //为矩阵相乘按钮添加动画
        imageView_ride.startAnimation(animation);

        double matrix1[][];
        double matrix2[][];
        String matrix1name=editText_matrix1.getText().toString();
        String matrix2name=editText_matrix2.getText().toString();
        String newmatrixname=editText_newmatrix.getText().toString();
        if(matrix1name.equals("")||matrix2name.equals("")||newmatrixname.equals("")){
            Toast.makeText(MainActivity.this,"请输入正确的矩阵名称",Toast.LENGTH_SHORT).show();
            return;
        }
        File file1=Environment.getExternalStoragePublicDirectory("matrix"+File.separator+matrix1name+".txt");
        File file2=Environment.getExternalStoragePublicDirectory("matrix"+File.separator+matrix2name+".txt");
        if(file1.exists()){
            matrix1=readMatrix(file1);
        }
        else {
            Toast.makeText(MainActivity.this,"矩阵1不存在，请重新输入",Toast.LENGTH_SHORT).show();
            return;
        }

        if(file2.exists()){
            matrix2=readMatrix(file2);
        }
        else {
            Toast.makeText(MainActivity.this,"矩阵2不存在，请重新输入",Toast.LENGTH_SHORT).show();
            return;
        }

        int row1=matrix1.length;//行数
        int column1=matrix1[0].length;//列数

        int row2=matrix2.length;
        int column2=matrix2[0].length;

        if(column1==row2){
            double matrix3[][]=matrixRides(matrix1,matrix2);
            //输出到文件
            String str="";
            for(int i=0;i<row1;i++){
                for(int j=0;j<column2;j++){
                    str=str+decf.format(matrix3[i][j])+"\t\t\t\t\t\t";
                }
                str=str+"\n";
            }

            File file=Environment.getExternalStoragePublicDirectory("matrix"+File.separator+newmatrixname+".txt");
            if(!file.exists()){
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            //写入文件
            writeFile(file,str);
            //这个命令字符串
            String thisorder=newmatrixname+"="+matrix1name+"*"+matrix2name+"\n";
            //全部命令字符串
            order=order+thisorder;
            //首页矩阵字符串
            str=textView_saved.getText()+thisorder+"\n"+newmatrixname+".txt"+"\n"+str+"\n";
            textView_saved.setText(str);
            Toast.makeText(MainActivity.this,"相乘成功",Toast.LENGTH_SHORT).show();
        }else{

            Toast.makeText(MainActivity.this,"矩阵1的列数和矩阵2的行数不相等，请从新输入",Toast.LENGTH_SHORT).show();
        }


    }


    //点击矩阵求逆按钮，求矩阵1的逆矩阵
    public void onButtonInvClick(View view) {

        //为矩阵求逆按钮添加动画
        imageView_inv.startAnimation(animation);

        double matrix1[][];
        String matrix1name=editText_matrix1.getText().toString();
        String newmatrixname=editText_newmatrix.getText().toString();
        if(matrix1name.equals("")||newmatrixname.equals("")){
            Toast.makeText(MainActivity.this,"请输入正确的矩阵名称",Toast.LENGTH_SHORT).show();
            return;
        }
        File file1=Environment.getExternalStoragePublicDirectory("matrix"+File.separator+matrix1name+".txt");
        if(file1.exists()){
            matrix1=readMatrix(file1);
        }
        else {
            Toast.makeText(MainActivity.this,"矩阵1不存在，请重新输入",Toast.LENGTH_SHORT).show();
            return;
        }

        int row=matrix1.length;//行数
        int column=matrix1[0].length;//列数

        if(row==column){
            double matrix3[][]=new double[row][row];
            int sucess=matrixInv(matrix1,matrix3);
            if(sucess!=1){//成功求逆则sucess的值为1
                Toast.makeText(MainActivity.this,"矩阵1的逆不存在",Toast.LENGTH_SHORT).show();
                return;
            }
            //输出到文件
            String str="";
            for(int i=0;i<row;i++){
                for(int j=0;j<column;j++){
                    str=str+decf.format(matrix3[i][j])+"\t\t\t\t\t\t";
                }
                str=str+"\n";
            }

            File file=Environment.getExternalStoragePublicDirectory("matrix"+File.separator+newmatrixname+".txt");
            if(!file.exists()){
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            //写入文件
            writeFile(file,str);
            //这个命令字符串
            String thisorder=newmatrixname+"="+"inv("+matrix1name+")\n";
            //全部命令字符串
            order=order+thisorder;
            //首页矩阵字符串
            str=textView_saved.getText()+thisorder+"\n"+newmatrixname+".txt"+"\n"+str+"\n";
            textView_saved.setText(str);
            Toast.makeText(MainActivity.this,"求逆成功",Toast.LENGTH_SHORT).show();
        }else{

            Toast.makeText(MainActivity.this,"矩阵1不是方阵，请从新输入",Toast.LENGTH_SHORT).show();
        }


    }






    // 删除文件夹下的所有文件filepath文文件夹路径
    public void deletedFile(String filepath){

        File file=new File(filepath);
        file.delete();

    }


    //矩阵相加函数
    public double[][] matrixPlus(double A[][],double B[][]){
        double C[][];
        int row=A.length;
        int column=A[0].length;
        C=new double[row][column];
        for(int i=0;i<row;i++){
            for(int j=0;j<column;j++){
                C[i][j]=A[i][j]+B[i][j];
            }
        }

        return C;
    }


    //矩阵相减函数
    public double[][] matrixMinus(double A[][],double B[][]){
        double C[][];
        int row=A.length;
        int column=A[0].length;
        C=new double[row][column];
        for(int i=0;i<row;i++){
            for(int j=0;j<column;j++){
                C[i][j]=A[i][j]-B[i][j];
            }
        }

        return C;
    }



    //矩阵相乘函数
    public double[][] matrixRides(double A[][],double B[][]){
        double C[][];
        int row1=A.length;//矩阵1行数
        int column1=A[0].length;//矩阵1列数
        int column2=B[0].length;//矩阵2列数
        double temp=0.0;
        C=new double[row1][column2];
        for(int i=0;i<row1;i++){
            for(int j=0;j<column2;j++){
                for(int k=0;k<column1;k++){
                    temp=temp+A[i][k]*B[k][j];
                }
                C[i][j]=temp;
                temp=0.0;
            }
        }

        return C;
    }


    //矩阵求逆函数
    public int matrixInv(double Target[][],double C[][]){
        //

        int n=Target.length;//n为行数
        int i,j,k,m,p,q,sucess=0;//sucess等于1则矩阵的逆存在
        double l=0.0,t=0.0,t1=0.0;
        int a[]=new int[n];
        double A[][]=new double[n][n];//A矩阵为目标矩阵，要求逆的矩阵，值来自target矩阵
        double B[][]=new double[n][n];//B矩阵为改变了行顺序后的逆矩阵，C为逆矩阵，由将B矩阵改变行顺序获得


        for(i=0;i<n;i++){
            a[i]=i;
            for(j=0;j<n;j++){

                A[i][j]=Target[i][j];//赋值给A
                if(i==j){
                    B[i][j]=1.0;
                }else{
                    B[i][j]=0.0;
                }

            }
        }

        //开始计算
        for(i=0,j=0;i<n && j<n;i++,j++){

            t=A[a[i]][j];               /*对角线元素*/
            p=i;                        /*p记录第j列元素最大值行号的下标*/
            if (t<0){
                t=-t;
            }

            /*找第j列的最大元素,k表示在第几行*/
            for(k=i+1;k<n;k++){
                t1=A[a[k]][j];
                if(t1<0){
                    t1=-t1;
                }
                if(t1>t){               /*如果t1大于t，则记录行号,使t=t1,继续循环直到找出第j列的最大值所在的行号*/
                    p=k;                //p记录再到最大元素所在的行号
                    t=t1;
                }
            }

            /*交换行号，q为中间变量*/
            q=a[i];
            a[i]=a[p];
            a[p]=q;

            //t为所在列最大的元素，也是对角线元素
            t=A[a[i]][j];
            /*如果对角线元素等于0，则行列式为0，矩阵的逆不存在*/
            if(t==0){
                sucess=0;
                return sucess;
            }

            /*消去第i行第j列元素以下的元素*/
            for(k=i+1;k<n;k++){/*k控制行*/
                l=A[a[k]][j]/t;
                for(m=0;m<n;m++){/*m控制列*/
                    A[a[k]][m]=A[a[k]][m]-l*A[a[i]][m];
                    B[a[k]][m]=B[a[k]][m]-l*B[a[i]][m];
                }
            }
        }


        for (i=0,j=0;i<n&&j<n;i++,j++){
            /*消去第0到n-1行，第j+1列的元素，最后一行右边没有元素不用消去，所以i<n-1*/
            if(i<n-1) {
                for(k=0;k<=i;k++){  /*a[k]控制行*/
                    l = A[a[k]][j + 1] / A[a[i + 1]][j + 1];
                    Log.d(TAG, "count l="+String.valueOf(l));
                    for (m = 0; m < n; m++) {
                        //消去i+1行对角元素的上面的元素
                        A[a[k]][m] = A[a[k]][m] - l * A[a[i + 1]][m];
                        B[a[k]][m] = B[a[k]][m] - l * B[a[i + 1]][m];
                    }
                }
            }
        }

        /*将对角线元素化为1,并将B的顺序改变后赋值给C*/
        for(i=0;i<n;i++){
            t=A[a[i]][i];
            for(j=0;j<n;j++){
                A[a[i]][j]=A[a[i]][j]/t;
                B[a[i]][j]=B[a[i]][j]/t;
                C[i][j]=B[a[i]][j];
            }
        }

        sucess=1;
        return sucess;
    }



    //矩阵转置函数
    public double[][] matrixTransform(double A[][]){
        int row=A.length;
        int column=A[0].length;

        double B[][]=new double[column][row];

        for(int i=0;i<column;i++){
            for(int j=0;j<row;j++){
                B[i][j]=A[j][i];
            }
        }

        return B;

    }






    //  6.0以上系统需要重写次方法以创建文件夹
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1:

                if(grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    //取得权限，所执行的操作
                    String rootPath=Environment.getExternalStorageDirectory().getPath();
                    //创建一个文件夹
                    File floder=new File(rootPath+File.separator+"matrix");
                    if(!floder.exists()){
                        if(!floder.mkdirs()){
                            Log.d(TAG, "Directory not created");
                        };
                    }
                    /*//在floder文件夹下创建一个matrix.txt文件
                    File file=new File(floder,"matrix.txt");
                    try {
                        if(!file.createNewFile()){
                            Log.d(TAG, "file not created");
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }*/

                    String result="";
                    //将floder文件夹下的文件写进textView_saved中
                    File[] files;
                    if(floder.exists()){
                        ArrayList<String> s=getFilesAllName(floder);
                        if(s!=null){
                            for(int i=0;i<s.size();i++){
                                File file1=new File(s.get(i));
                                result=result+file1.getName()+"\n";
                                result=result+readFile(file1)+"\n";
                            }

                        }
                    }

                    textView_saved.setText(result);

                }
                else{
                    //未取得权限
                    //用户在过去拒绝了权限请求，并在权限请求系统对话框中选择了 Don’t ask again 选项，
                    // 则可以再次跟用户说明情况，并且引导用户去打开权限
                    Toast.makeText(this,"请在设置中打开此应用的存储权限！否则功能无法正常运行！",Toast.LENGTH_SHORT).show();
                }
                break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    //如果运行在Android 6.0及其以上的设备，请在读写非应用内部路径的存储前，
    // 写下动态申请内存读写权限的代码，以请求外部存储的读写权限
    public void requestPower() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                /*为了帮助查找用户可能需要解释的情形，Android 提供了一个实用程序方法，
                 *即 shouldShowRequestPermissionRationale()。
                 * 第一次应用程序安装后，返回的为false。
                 *用户拒绝了请求，此方法将返回 true。
                 *如果用户在过去拒绝了权限请求，并在权限请求系统对话框中选择了 Don’t ask again 选项，此方法将返回 false。
                 * 如果设备规范禁止应用具有该权限，此方法也会返回 false。
                 */
                //Toast.makeText(this,"true",Toast.LENGTH_SHORT).show();
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

            } else {

                //Toast.makeText(this,"false",Toast.LENGTH_SHORT).show();
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            }
        }
    }



    //透明状态栏
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
            //设置让应用主题内容占据状态栏和导航栏    隐藏导航栏View.SYSTEM_UI_FLAG_HIDE_NAVIGATION|   添加这个会导致toolbar高度太高和导航栏遮挡应用界面
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            //设置状态栏颜色为透明，导航栏颜色为半透明
            window.setStatusBarColor(Color.TRANSPARENT);
            //半透明导航栏
            window.setNavigationBarColor(getResources().getColor(R.color.translucent));
            //隐藏标题栏
            //ActionBar actionBar = getSupportActionBar();
            //actionBar.hide();
        }
    }


    /*沉浸式状态栏，导航栏和状态栏要点击才显示*/
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        //判断是否有焦点
        /*if(hasFocus && Build.VERSION.SDK_INT >= 19){
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            );
        }*/
    }

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
    // 创建菜单方法
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        return super.onCreateOptionsMenu(menu);
    }
    //响应菜单方法
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }



    //弹出自定义输入行数对话框方法
    private  void showSelfDialog(){
        Log.d(TAG,"进入showSelfDialog");
        final int[] row = {0};
        //生成一个对话框builder对象
        final AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this);
        //用来自MainActivity的布局填充器加载R.layout.input_dialog布局，生成view对象,view就代表自定义对话框布局文件
        //按照所选择的主题加载对话框
        View view=null;
        switch(theme){
            case 0://默认
                view= LayoutInflater.from(MainActivity.this).inflate(R.layout.input_dialog,null);
                break;
            case 1://夜间
                view= LayoutInflater.from(MainActivity.this).inflate(R.layout.input_dialog,null);
                break;
            case 2://黑
                view= LayoutInflater.from(MainActivity.this).inflate(R.layout.input_dialog_darktheme,null);
                break;
            case 3://绿
                view= LayoutInflater.from(MainActivity.this).inflate(R.layout.input_dialog_darktheme,null);
                break;
            case 4://红
                view= LayoutInflater.from(MainActivity.this).inflate(R.layout.input_dialog_darktheme,null);
                break;
            case 5://紫色
                view= LayoutInflater.from(MainActivity.this).inflate(R.layout.input_dialog_darktheme,null);
                break;
                default:
                    view= LayoutInflater.from(MainActivity.this).inflate(R.layout.input_dialog,null);
                break;
        }

        Button define_button=(Button)view.findViewById(R.id.define_button);
        Button cancal_button=(Button)view.findViewById(R.id.cancel_button);

        //更改对话框颜色
        LinearLayout linearLayout_dialog=(LinearLayout)view.findViewById(R.id.linearLayout_dialog1);
        changeThemeDialog(linearLayout_dialog,theme);


        final EditText input_edittext=(EditText)view.findViewById(R.id.inputnumber_edittext);
        builder.setView(view);//设置对话框布局
        Log.d(TAG,"设置对话框布局完成");
        final AlertDialog dialog=builder.show();//显示对话框,得到一个AlertDialog对象
        Log.d(TAG,"显示对话框完成");

        define_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //点击确定按钮发生的事件
                Log.d(TAG,"进入对话框确定按钮的响应事件");
                String check=input_edittext.getText().toString();
                if(check.equals("")){
                    Toast.makeText(MainActivity.this,"请输入正确的数字！",Toast.LENGTH_SHORT).show();
                    return;
                }
                int input=Integer.valueOf(check);
                //Toast.makeText(MainActivity.this,input_edittext.getText().toString(),Toast.LENGTH_SHORT).show();
                if(input==0){
                    Toast.makeText(MainActivity.this,"请输入正确的数字！",Toast.LENGTH_SHORT).show();
                    return;
                }
                row[0] =input;
                Intent intent=new Intent(MainActivity.this,qiuni.class);
                intent.putExtra("key",row[0]);
                //带返回值启动activity，需要重写本activity中的onActivityResult方法
                startActivityForResult(intent,1);
                Log.d(TAG,"对话框确定按钮的响应事件完成");
                //关闭对话框
                dialog.dismiss();
            }
        });
        //点击取消按钮发生的事情
        cancal_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //关闭对话框
                dialog.dismiss();
            }
        });

    }


    //弹出自定义输入行数和列数对话框方法
    private  void showSelfDialog2(){
        Log.d(TAG,"进入showSelfDialog2");
        final int[] row = new int[1];
        final int[] column = new int[1];
        //生成一个对话框builder对象
        final AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this);
        //用来自MainActivity的布局填充器加载R.layout.input_dialog布局，生成view对象,view就代表自定义对话框布局文件
        //按照所选择的主题加载对话框
        View view=null;
        switch(theme){
            case 0://默认
                view= LayoutInflater.from(MainActivity.this).inflate(R.layout.add_input_dialog,null);
                break;
            case 1://夜间
                view= LayoutInflater.from(MainActivity.this).inflate(R.layout.add_input_dialog,null);
                break;
            case 2://黑
                view= LayoutInflater.from(MainActivity.this).inflate(R.layout.add_input_dialog_darktheme,null);
                break;
            case 3://绿
                view= LayoutInflater.from(MainActivity.this).inflate(R.layout.add_input_dialog_darktheme,null);
                break;
            case 4://红
                view= LayoutInflater.from(MainActivity.this).inflate(R.layout.add_input_dialog_darktheme,null);
                break;
            case 5://紫色
                view= LayoutInflater.from(MainActivity.this).inflate(R.layout.add_input_dialog_darktheme,null);
                break;
                default:
                    view= LayoutInflater.from(MainActivity.this).inflate(R.layout.add_input_dialog,null);
                    break;
        }
        Button define_button=(Button)view.findViewById(R.id.define_button);
        Button cancal_button=(Button)view.findViewById(R.id.cancel_button);

        //更改对话框颜色
        LinearLayout linearLayout_dialog2=(LinearLayout)view.findViewById(R.id.linearLayout_dialog2);
        changeThemeDialog(linearLayout_dialog2,theme);

        final EditText input1_edittext=(EditText)view.findViewById(R.id.inputnumber1_edittext);
        final EditText input2_edittext=(EditText)view.findViewById(R.id.inputnumber2_edittext);

        builder.setView(view);//设置对话框布局
        Log.d(TAG,"设置对话框布局完成");
        final AlertDialog dialog=builder.show();//显示对话框,得到一个AlertDialog对象
        Log.d(TAG,"显示对话框完成");

        define_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //点击确定按钮发生的事件
                Log.d(TAG,"进入对话框确定按钮的响应事件");
                String check1=input1_edittext.getText().toString();
                String check2=input2_edittext.getText().toString();
                if(check1.equals("")||check2.equals("")){
                    Toast.makeText(MainActivity.this,"请输入正确的数字！",Toast.LENGTH_SHORT).show();
                    return;
                }
                int input1=Integer.valueOf(check1);
                int input2=Integer.valueOf(check2);
                //Toast.makeText(MainActivity.this,input_edittext.getText().toString(),Toast.LENGTH_SHORT).show();
                if(input1==0||input2==0){
                    Toast.makeText(MainActivity.this,"请输入正确的数字！",Toast.LENGTH_SHORT).show();
                    return;
                }
                row[0] =input1;
                column[0] =input2;
                Intent intent=new Intent(MainActivity.this,AddMatrix.class);
                intent.putExtra("row",row[0]);
                intent.putExtra("column",column[0]);

                //带返回值启动activity，需要重写本activity中的onActivityResult方法
                startActivityForResult(intent,2);
                Log.d(TAG,"对话框确定按钮的响应事件完成");
                //关闭对话框
                dialog.dismiss();
            }
        });
        //点击取消按钮发生的事情
        cancal_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //关闭对话框
                dialog.dismiss();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //判断结果码是否与回传码相同
        if(resultCode==1){//从添加界面和求行列式界面返回
            //获取返回数据
            String ok=data.getStringExtra("ok");
            //进行操作
            String rootPath=Environment.getExternalStorageDirectory().getPath();
            File floder=new File(rootPath+File.separator+"matrix");

            String result="";
            //将floder文件夹下的文件写进textView_saved中
            File[] files;
            if(floder.exists()){
                ArrayList<String> s=getFilesAllName(floder);
                if(s!=null){
                    for(int i=0;i<s.size();i++){
                        File file1=new File(s.get(i));
                        result=result+file1.getName()+"\n";
                        result=result+readFile(file1)+"\n";
                    }

                }
            }
            result=result+order;
            textView_saved.setText(result);
        }
        //从设置界面返回来
        if(requestCode==2){
            //提取用户主题设置参数并改变主题
            Map<String,String> params=preferencesService.getPerferences();
            theme=Integer.valueOf(params.get("主题"));
            changeTheme(theme);//改变主题

            //提取用户精度设置参数并且更改精度
            Map<String,String> params2=preferencesService2.getPerferences();
            precision=Integer.valueOf(params2.get("精度"));
            changePrecision(precision);//改变精度

            Map<String,String> params3=preferencesService3.getPerferences();
            textsize=Integer.valueOf(params3.get("字体大小"));
            changeTextSize(textsize);//改变字体大小

        }

    }



}
