package com.rong1.matrix;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Map;

public class AddMatrix extends Activity {

    private  static String TAG="addMatrix";
    MyAdapter myAdapter;//gridview适配器
    String arraryS[];//矩阵的一维字符串数组
    String matrixname;//矩阵名字的
    String matrixStr;//格式化后的矩阵字符串数组
    EditText matrixname_edittext;//保存文件名称的Edittext

    DecimalFormat decf;//将double型数据格式化为字符串

    //主题布局
    PreferencesService preferencesService;//用户主题设置数据保存提取类
    PreferencesService2 preferencesService2;//用户精度设置数据保存提取类

    LinearLayout linearLayout;//更改主题时所需要更改颜色的控件
    TextView textView1;//标题1
    TextView textView2;//标题2

    int theme;//选择的主题
    int precision;//选择的精度



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addmatrix);

        //找到布局和控件
        linearLayout=(LinearLayout)findViewById(R.id.linearlayout_root);
        textView1=(TextView)findViewById(R.id.textView_add1) ;
        textView2=(TextView)findViewById(R.id.textView_add2) ;

        //提取用户设置参数并且更改主题
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

        //状态栏透明
        initState();


        final int row;//矩阵行数
        final int column;//矩阵列数

        //获取intent，并且获取数据
        Intent intent=getIntent();
        row=intent.getIntExtra("row",1);
        column=intent.getIntExtra("column",1);
        Log.d(TAG, "onCreate: "+String.valueOf(column));

        //设置返回数据
        intent.putExtra("OK","收到矩阵的行数和列数了");
        setResult(1,intent);//销毁此activity是自动返回到上一activity

        //保存按钮
        Button save_button=(Button)findViewById(R.id.saved_button);
        //保存文件名称的Edittext
        matrixname_edittext=(EditText)findViewById(R.id.matrixname_edittext);
        //gridview
        GridView gridView=(GridView)findViewById(R.id.gridview1);
        //gridView参数设置
        horizontal_layout(gridView,column);
        //初始化数据
        String s[]=new String[row*column];
        //为gridview建立适配器
        myAdapter=new MyAdapter(this,row,column);
        gridView.setAdapter(myAdapter);

        //java的方法调用都是传值的，这里myAdapter中的s对象与arraryS对象不同，
        // 但对象所指向的地址相同，所以要在保存点击按钮事件中获取myAdapter中
        // 的s数组的值。如果在点击按钮事件之外获取myAdapter中的s数组的值，则这些值不是我们想要的
        arraryS=myAdapter.getS();


        //点击保存按钮，将矩阵以.txt形式保存到手机中
        save_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //格式化数组字符串(一定要在点击按钮时件内)
                matrixStr="";
                int k=0;
                for(int i=0;i<row;i++){
                    for(int j=0;j<column;j++){
                        if(arraryS[k].equals("")){
                            Toast.makeText(AddMatrix.this,"请输入正确的矩阵",Toast.LENGTH_SHORT).show();
                            return;
                        }
                        double a=Double.valueOf(arraryS[k]);
                        matrixStr=matrixStr+decf.format(a)+"\t\t\t\t\t";
                        Log.d(TAG, "onCreate: arraryS:"+arraryS[k]);
                        k++;
                    }
                    matrixStr=matrixStr+"\n";
                }

                //将数组写到文件中，文件名为 输入的名字
                matrixname=matrixname_edittext.getText().toString();
                if(matrixname.equals("")){
                    Toast.makeText(AddMatrix.this,"请输入矩阵名称",Toast.LENGTH_SHORT).show();
                    return;
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
                //检查根目录下matrix文件夹是否已创建，没有创建则创建
                String rootPath=Environment.getExternalStorageDirectory().getPath();
                File floder=new File(rootPath+File.separator+"matrix");
                if(!floder.exists()){
                    if(!floder.mkdirs()){
                        Log.d(TAG, "Directory not created");
                    };
                }

                //在matrix文件夹下创建一个矩阵文件，文件名为输入的名字
                matrixname=matrixname_edittext.getText().toString();
                File file=new File(floder,matrixname+".txt");
                if(!file.exists()){//文件不存在则创建
                    try {
                        if(!file.createNewFile()){
                            Log.d(TAG, "file not created");
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }

                //写数据到文件中
                if(file.exists()){
                    writeFile(file,matrixStr);
                    Toast.makeText(AddMatrix.this,"保存成功",Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(AddMatrix.this,"保存失败",Toast.LENGTH_SHORT).show();
                }

            }
        });


    }



    //更改主题颜色
    public void changeTheme(int theme){
        switch (theme){
            case 0://默认
                linearLayout.setBackgroundResource(R.color.gray1);
                textView1.setTextColor(getResources().getColor(R.color.fontcolor));
                textView2.setTextColor(getResources().getColor(R.color.fontcolor));
                break;
            case 1://夜间
                linearLayout.setBackgroundResource(R.color.dark);
                textView1.setTextColor(getResources().getColor(R.color.fontcolor));
                textView2.setTextColor(getResources().getColor(R.color.fontcolor));
                break;
            case 2://黑色
                linearLayout.setBackgroundResource(R.color.dark);
                textView1.setTextColor(getResources().getColor(R.color.white));
                textView2.setTextColor(getResources().getColor(R.color.white));
                break;
            case 3://绿色
                linearLayout.setBackgroundResource(R.color.green);
                textView1.setTextColor(getResources().getColor(R.color.white));
                textView2.setTextColor(getResources().getColor(R.color.white));
                break;
            case 4://红色
                linearLayout.setBackgroundResource(R.color.red);
                textView1.setTextColor(getResources().getColor(R.color.white));
                textView2.setTextColor(getResources().getColor(R.color.white));
                break;
            case 5:
                linearLayout.setBackgroundResource(R.color.purple);
                textView1.setTextColor(getResources().getColor(R.color.white));
                textView2.setTextColor(getResources().getColor(R.color.white));
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



    /*gridView内嵌HorizontScrollView的参数设置方法*/
    public void  horizontal_layout(GridView gridView , int size) {

        //DisplayMetrics 类提供了一种关于显示的通用信息，如显示大小，分辨率和字体
        DisplayMetrics dm = new DisplayMetrics();
        //Log.d("yujian<<", display.toString());//直接在这里输出，信息都是0.
        //将当前窗口的一些信息放在DisplayMetrics类中，
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        float density = dm.density; //密度dpi为160的density为1.0   480为3
        int allWidth = (int) (110 * size * density); //gridView总宽度100*size
        int itemWidth = (int) (100 * density);//一个控件的宽度
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(allWidth,
                LinearLayout.LayoutParams.MATCH_PARENT);
        gridView.setLayoutParams(params);// 设置GirdView布局参数
        gridView.setColumnWidth(itemWidth);// 列表项宽100
        gridView.setHorizontalSpacing(10);// 列表项水平间距
        gridView.setStretchMode(GridView.NO_STRETCH);//缩放模式
        gridView.setNumColumns(size);//总长度
    }



    //  6.0以上系统需要重写次方法以创建文件夹
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1:

                if(grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    //取得权限，所执行的操作
                    String rootPath= Environment.getExternalStorageDirectory().getPath();
                    //创建一个文件夹
                    File floder=new File(rootPath+File.separator+"matrix");
                    if(!floder.exists()){
                        if(!floder.mkdirs()){
                            Log.d(TAG, "Directory not created");
                        };
                    }
                    //在matrix文件夹下创建一个矩阵文件，文件名为输入的名字
                    matrixname=matrixname_edittext.getText().toString();
                    File file=new File(floder,matrixname+".txt");
                    if(!file.exists()){//文件不存在则创建
                        try {
                            if(!file.createNewFile()){
                                Log.d(TAG, "file not created");
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    //写数据到文件中
                    if(file.exists()){
                        writeFile(file,matrixStr);
                        Toast.makeText(AddMatrix.this,"保存成功",Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(AddMatrix.this,"保存失败",Toast.LENGTH_SHORT).show();
                    }


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

    /*透明状态栏*/
    private void initState() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //透明导航栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
    }

    /*沉浸式状态栏，导航栏和状态栏要点击才显示*/
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        //判断是否有焦点
        if(hasFocus && Build.VERSION.SDK_INT >= 19){
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            );
        }
    }

}
