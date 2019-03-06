package com.rong1.matrix;


//矩阵运算工具类
public class MatrixOperate {


    //矩阵相加函数
    public static double[][] matrixPlus(double A[][],double B[][]){
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
    public static double[][] matrixMinus(double A[][],double B[][]){
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
    public static double[][] matrixRides(double A[][],double B[][]){
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
    public static int matrixInv(double Target[][],double C[][]){

        //Target为要求逆的矩阵
        //C为求到的结果，Target的逆矩阵

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
                    B[i][j]=1.0;//对角线化为1
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
                    //Log.d(TAG, "count l="+String.valueOf(l));
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
    public static double[][] matrixTransform(double A[][]){
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

}
