package com.rong1.matrix;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Map;
import java.util.Objects;


/**
 * Created by rong1 on 2018/5/7.
 */

public class qiuni extends Activity {
    private static final String TAG ="onClick";
    int row_qiuni;
    MyAdapter myAdapter;//gridview适配器
    String arraryS[];//矩阵的一维字符串数组
    DecimalFormat decf;//将double型数据格式化为字符串

    //主题布局
    PreferencesService preferencesService;//用户设置数据保存提取类
    PreferencesService2 preferencesService2;//用户精度设置数据保存提取类
    PreferencesService3 preferencesService3;//用户字体大小设置数据保存提取类

    LinearLayout linearLayout;//更改主题时所需要更改颜色的控件
    TextView textView1;
    TextView textView2;
    TextView textview_result;//结果textview

    int theme;//选择的主题
    int precision;//选择的精度
    int textsize;//选择的字体大小

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_count);

        //找到布局
        linearLayout=(LinearLayout)findViewById(R.id.linearLayout_inv);
        textView1=(TextView)findViewById(R.id.textView_det1);
        textView2=(TextView)findViewById(R.id.textView_det2);
        textview_result =(TextView)findViewById(R.id.result_tv);

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

        //提取用户字体大小设置参数并且更改字体大小
        preferencesService3 =new PreferencesService3(this);
        Map<String,String> params3=preferencesService3.getPerferences();
        textsize=Integer.valueOf(params3.get("字体大小"));
        changeTextSize(textsize);//改变字体大小

        //状态栏透明
        initState();

        //获取intent
        Intent intent=getIntent();
        Log.d(TAG,"获取Intent成功");
        row_qiuni=intent.getIntExtra("key",1);

        //设置返回数据
        intent.putExtra("OK","收到矩阵的行数了");
        setResult(1,intent);//销毁此activity是自动返回到上一activity
        Log.d(TAG,"获取数据完成");

        Button count_button=(Button)findViewById(R.id.count_button);
        final GridView gridView=(GridView)findViewById(R.id.gridview1);
        //gridView参数设置
        horizontal_layout(gridView,row_qiuni);
        //初始化数据
        String s[]=new String[row_qiuni*row_qiuni];
        //为gridview建立适配器
        myAdapter=new MyAdapter(this,row_qiuni,row_qiuni);
        gridView.setAdapter(myAdapter);
        arraryS=myAdapter.getS();
        Log.d("矩阵数组长度","arraryS.length="+arraryS.length);
        //要求逆的矩阵arrayA
        final double arraryA[][]=new double[row_qiuni][row_qiuni];
        //点击计算按钮，开始计算
        count_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //将arrarylist集合中的数据取出保存到arraryA数组中
                int k=0;
                String check="";
                String str="";
                for(int i=0;i<row_qiuni;i++){
                    for(int j=0;j<row_qiuni;j++){
                        check=arraryS[k];
                        if(check.equals("")){
                            textview_result.setText("请输入正确的矩阵");
                            Toast.makeText(qiuni.this,"请输入正确的矩阵",Toast.LENGTH_SHORT).show();
                            str="";
                            return;
                        }
                        arraryA[i][j]=Double.valueOf(check);
                        str=str+decf.format(arraryA[i][j])+"\t\t\t\t\t";
                        k++;
                    }
                    str=str+"\n";
                }

                //写数据到文件中
                File file=new File(Environment.getExternalStorageDirectory()+File.separator+"matrix"+File.separator+"matrix.txt");
                if(!file.exists()){
                    try {
                        file.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                writeFile(file,str);

                //Toast.makeText(qiuni.this,check+":"+arraryS.length,Toast.LENGTH_SHORT).show();

                //声明求逆对象计算逆矩阵
                int sign;
                String result_string="";
                QiuniCount qiuni=new QiuniCount(row_qiuni,arraryA);
                Log.d(TAG, "onClick: 声明QiuniCount对象完成");

                sign=qiuni.count();
                Log.d(TAG, "onClick: 调用count()方法完成");

                if(sign==0){
                    textview_result.setText("逆矩阵不存在！\n\ndet:0");
                    Toast.makeText(qiuni.this,"逆矩阵不存在",Toast.LENGTH_SHORT).show();
                }
                else if(sign==1){
                    Log.d(TAG, "onClick: 输出到文本框");
                    for(int i=0;i<row_qiuni;i++){
                        for(int j=0;j<row_qiuni;j++){
                            result_string=result_string+decf.format(qiuni.C[i][j])+"\t\t\t";
                            Log.d(TAG,result_string);
                        }
                        result_string=result_string+"\n";
                    }
                    result_string=result_string+"\n"+"det："+decf.format(qiuni.det);
                    textview_result.setText(result_string);
                }

                Log.d(TAG,"onclick done. arraryA[n][n]="+String.valueOf(arraryA[row_qiuni-1][row_qiuni-1]));

            }
        });



        //文件操作方法
        Log.i("codecraeer","getFilesDir = "+ getFilesDir());
        Log.i("codecraeer","getExternalFilesDir = "+ Objects.requireNonNull(getExternalFilesDir("exter_test")).getAbsolutePath());
        Log.i("codecraeer","getDownloadCacheDirectory = "+ Environment.getDownloadCacheDirectory().getAbsolutePath());
        Log.i("codecraeer","getDataDirectory = "+ Environment.getDataDirectory().getAbsolutePath());
        Log.i("codecraeer","getExternalStorageDirectory = "+ Environment.getExternalStorageDirectory().getAbsolutePath());
        Log.i("codecraeer","getExternalStoragePublicDirectory = "+ Environment.getExternalStoragePublicDirectory("pub_test"));


        /*for(int i=0;i<row_qiuni*row_qiuni;i++){
            HashMap<String,Object> map=new HashMap<String, Object>();
            map.put("edtext","0");
            arrayList.add(map);
        }
        //为gridView设置适配器
        SimpleAdapter simpleAdapter=new SimpleAdapter(this,arrayList,R.layout
                .gridview_item,new String[]{"edtext"},new int[]{R.id.edtext});
        gridView.setAdapter(simpleAdapter);*/




        /*Button button=(Button)findViewById(R.id.buttonCount);//计算按钮
        final EditText text1=(EditText)findViewById(R.id.editextJieshu);//阶数文本框
        final EditText text2=(EditText)findViewById((R.id.editTextJuzhen)); //要求逆矩阵文本框
        final EditText text3=(EditText)findViewById(R.id.editTextJieguo);//结果文本框
        text2.setMovementMethod(new ScrollingMovementMethod());//设置滚动条,在xml中配置android:scrollbars
        text3.setMovementMethod(new ScrollingMovementMethod());
        text1.setText(row_qiuni+"");//要显示数字row_qiuni一定要加""否则报错
        Log.d(TAG,"setText(row_qiuni)完成");
        //点击计算按钮事件
       button.setOnClickListener(new View.OnClickListener(){
           @Override
           public void onClick(View view) {
               int n;
               int i=0,j=0,sign;
               n=Integer.valueOf(text1.getText().toString());
               String a,b="";
               String[] row=new String[n];
               String[] str=new String[n];

               for(i=0;i<n;i++){
                   row[i]="";
               }
               DecimalFormat decf=new DecimalFormat("0.0000");
               //a=String.valueOf(n);
               a=text2.getText().toString();
               if(Objects.equals(a, "")){
                   text3.setText("请输入正确的矩阵！");
                   return;
               }
               row=a.split("\n");//按"\n"分行，row表示每一行的数据
               for(i=0;i<n;i++){
                   if(row[i].equals("")||row[i].equals(" ")||row[i].equals("\n")){
                       text3.setText("请输入正确的矩阵！");
                       return;
                   }
                   Log.d(TAG,row[i]);
               }
               double[][] A=new double[n][n];
               //double[][] B=new double[n][n];
               for(i=0;i<n;i++){
                    str=row[i].split(" ");
                    for(j=0;j<n;j++){
                        Log.d(TAG,str[j]);
                        A[i][j]=Double.valueOf(str[j]);
                        //b=b+String.valueOf(A[i][j]);
                        //text3.setText(b);
                        //b=b+" ";
                        //text3.setText(b);
                    }
                    //b=b+"\n";
                   //text3.setText(b);
               }
               Log.d(TAG, "onClick: 读取数据完成");
               
               QiuniCount qiuni=new QiuniCount(n,A);
               
               Log.d(TAG, "onClick: 声明QiuniCount对象完成");
               
               sign=qiuni.count();
               
               Log.d(TAG, "onClick: 调用count()方法完成");
               
               if(sign==0){
                   text3.setText("逆矩阵不存在！");
               }
               else if(sign==1){
                   for(i=0;i<n;i++){
                       for(j=0;j<n;j++){
                           Log.d(TAG, "onClick: 输出到文本框");

                           b=b+decf.format(qiuni.C[i][j])+"   ";
                           Log.d(TAG,b);
                           //text3.setText(b);
                           //text3.setText(b);
                       }
                       b=b+";\n";
                       //text3.setText(b);
                   }
                   b=b+"\n"+"矩阵的行列式为："+decf.format(qiuni.det);
                   text3.setText(b);
               }

               a=String.valueOf(b.length());
               Toast.makeText(getApplicationContext(),a, Toast.LENGTH_SHORT).show();
           }
       });

       */


        Log.d(TAG,"onCreat结束");
    }








    //更改主题颜色
    public void changeTheme(int theme){
        switch (theme){
            case 0:
                linearLayout.setBackgroundResource(R.color.gray1);
                textView1.setTextColor(getResources().getColor(R.color.fontcolor));
                textView2.setTextColor(getResources().getColor(R.color.fontcolor));
                textview_result.setTextColor(getResources().getColor(R.color.fontcolor));
                break;
            case 1://夜间
                linearLayout.setBackgroundResource(R.color.dark);
                textView1.setTextColor(getResources().getColor(R.color.fontcolor));
                textView2.setTextColor(getResources().getColor(R.color.fontcolor));
                textview_result.setTextColor(getResources().getColor(R.color.fontcolor));
                break;
            case 2://黑色
                linearLayout.setBackgroundResource(R.color.dark);
                textView1.setTextColor(getResources().getColor(R.color.white));
                textView2.setTextColor(getResources().getColor(R.color.white));
                textview_result.setTextColor(getResources().getColor(R.color.white));
                break;
            case 3://绿色
                linearLayout.setBackgroundResource(R.color.green);
                textView1.setTextColor(getResources().getColor(R.color.white));
                textView2.setTextColor(getResources().getColor(R.color.white));
                textview_result.setTextColor(getResources().getColor(R.color.white));
                break;
            case 4://红色
                linearLayout.setBackgroundResource(R.color.red);
                textView1.setTextColor(getResources().getColor(R.color.white));
                textView2.setTextColor(getResources().getColor(R.color.white));
                textview_result.setTextColor(getResources().getColor(R.color.white));
                break;
            case 5://紫色
                linearLayout.setBackgroundResource(R.color.purple);
                textView1.setTextColor(getResources().getColor(R.color.white));
                textView2.setTextColor(getResources().getColor(R.color.white));
                textview_result.setTextColor(getResources().getColor(R.color.white));
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
                textview_result.setTextSize(TypedValue.COMPLEX_UNIT_SP,15);
                break;
            case 1:
                textview_result.setTextSize(TypedValue.COMPLEX_UNIT_SP,13);
                break;
            case 2:
                textview_result.setTextSize(TypedValue.COMPLEX_UNIT_SP,16);
                break;
            case 3:
                textview_result.setTextSize(TypedValue.COMPLEX_UNIT_SP,19);
                break;



        }

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

    //读文件操作
    public String readFile(File file){
        String result="";
        BufferedReader br = null;
        try {
            br=new BufferedReader(new FileReader(file));
            String tmp;
            while((tmp=br.readLine())!=null){
                result+=tmp;
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



    /*gridView内嵌HorizontScrollView的参数设置方法*/
    public void  horizontal_layout(GridView gridView ,int size) {

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




    public void onButtonClean(View view) {
        final EditText text1=(EditText)findViewById(R.id.editextJieshu);
        //要求逆矩阵文本框
        final EditText text2=(EditText)findViewById((R.id.editTextJuzhen));
        //结果文本框
        final EditText text3=(EditText)findViewById(R.id.editTextJieguo);
        text1.setText("");
        text2.setText("");
        text3.setText("");
    }
}



/*外部类*/
class QiuniCount{
    private static final String TAG="QiuniCount";
    int sucess;
    double[][] A;/*A为要求逆的矩阵*/
    double[][] B;/*B为改变了行顺序的逆矩阵，因为在求逆矩阵过程中利用了列主元高斯消去法求逆矩阵，矩阵A的列主元顺序改变*/
    double[][] C;/*C为逆矩阵*/
    double det;
    double Ann;
    int[]a;/*a记录行号*/
    int n;/*行数*/
    QiuniCount(int n,double[][] A){/*构造函数*/
        B=new double[n][n];
        C=new double[n][n];
        a=new int[n];
        det=0.0;
        this.n=n;
        this.A=A;
        Ann=A[n-1][n-1];
        sucess=0;
        for(int i=0;i<n;i++){
            a[i]=i;//数组的值为0,1,2,3,4,.....,n-1
            for(int j=0;j<n;j++){
                if(i==j) {
                    B[i][j] = 1.0;
                    C[i][j] = 0.0;
                }
                else{
                    B[i][j] = 0.0;
                    C[i][j] = 0.0;
                }
            }
        }
        Log.d(TAG, "QiuniCount: 构造方法运行完毕");
    }

    int count(){
        Log.d(TAG, "count:count()方法开始执行 ");
        int i,j,k,m,p,q;
        double l=0.0,t=0.0,t1=0.0;

        Log.d(TAG, "count: 变量声明完毕");

        for(i=0,j=0;i<n && j<n;i++,j++){

            Log.d(TAG, "count: 进入循环");

            t=A[a[i]][j];               /*对角线元素*/
            p=i;                        /*p记录第j列元素最大值行号的下标*/
            if (t<0){
                t=-t;
            }

            /*找第j列的最大元素,k表示在第几行*/
            for(k=i+1;k<n;k++){
                Log.d(TAG, "count: 进入第一个循环中的第一个循环");
                t1=A[a[k]][j];
                if(t1<0){
                    t1=-t1;
                }
                if(t1>t){               /*如果t1大于t，则记录行号,使t=t1,继续循环直到找出第j列的最大值所在的行号*/
                    p=k;                //p记录再到最大元素所在的行号
                    t=t1;
                }
            }

            Log.d(TAG, "count: 找第j列的最大元素,k控制行");

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
                Log.d(TAG, "count: 进入第一个循环中的第二个循环");
                l=A[a[k]][j]/t;
                for(m=0;m<n;m++){/*m控制列*/
                    A[a[k]][m]=A[a[k]][m]-l*A[a[i]][m];
                    B[a[k]][m]=B[a[k]][m]-l*B[a[i]][m];
                }
            }
        }


        Log.d(TAG, "count: 消去第i行第j列元素以下的元素");
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
            Log.d(TAG, "count: 消去第0到n-1行，第j+1列的元素");
        }



        /*计算行列式*/
        det=1.0;
        for(i=0;i<n;i++){
            for(j=0;j<n;j++){
                if(i==j)
                    det=det*A[a[i]][j];
            }
        }

        Log.d(TAG, "count:计算行列式 ");

        /*将对角线元素化为1*/
        for(i=0;i<n;i++){
            t=A[a[i]][i];
            for(j=0;j<n;j++){
                A[a[i]][j]=A[a[i]][j]/t;
                B[a[i]][j]=B[a[i]][j]/t;
                C[i][j]=B[a[i]][j];
            }
        }

        Log.d(TAG, "det="+det);

        int signal=0;


        //判断换行的次数，没换一次行行列式符号改变
        for(i=0;i<n;i++){
            Log.d(TAG, "a["+i+"]="+a[i]);
            if(a[i]!=i){
                signal=signal+1;
            }
        }

        if(signal>0){
            signal=signal-1;
            det=Math.pow(-1.0,(double)signal)*det;
        }

        Log.d(TAG, "count:将对角线元素化为1 ");
        sucess=1;
        return sucess;
    }
}