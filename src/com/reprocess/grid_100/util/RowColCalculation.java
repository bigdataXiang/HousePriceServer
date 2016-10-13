package com.reprocess.grid_100.util;

import net.sf.json.JSONObject;

/**
 * Created by ZhouXiang on 2016/10/12.
 */
public class RowColCalculation {

    public static double width=0.0011785999999997187;//每100m的经度差
    public static double length=9.003999999997348E-4;//每100m的纬度差

    /**通过最东的坐标获取当前范围内的最小列号*/
    public static int getColMin(double west){
        int colmin=(int) Math.ceil((west-115.417284)/width);
        return colmin;
    }

    /**通过最东的坐标获取当前范围内的最大列号*/
    public static int getColMax(double east){
        int colmax=(int)Math.ceil((east-115.417284)/width);
        return colmax;
    }

     /**通过最南的坐标获取当前范围内的最小行号*/
    public static int getRowMin(double south){
        int rowmin=(int)Math.ceil((south-39.438283)/length);
        return rowmin;
    }

    /**通过最北的坐标获取当前范围内的最大行号*/
    public static int getRowMax(double north){
        int rowmax=(int)Math.ceil((north-39.438283)/length);
        return rowmax;
    }

    /**建立(N*50)*(N*50)的网格编码与50*50的网格编码的映射关系*/
    public static int[] codeMapping50toN50(int row_50,int col_50,int N){
        int cols=4000;
        int row;
        int col;
        int code;
        int[] result =new int[3];
        double rowtemp=(double)row_50/N;
        row= (int) Math.ceil(rowtemp);
        result[0]=row;
        double coltemp=(double)col_50/N;
        col= (int) Math.ceil(coltemp);
        result[1]=col;
        code=(col + (cols/N)* (row - 1));
        result[2]=code;
        return result;
    }

    /**通过网格值code计算行列号值*/
    public static int[] Code_RowCol(int code,int N){
        int[] rowcol=new int[2];
        int cols=4000/N;
        int row=code/cols+1;
        int col=code%cols;

        rowcol[0]=row;
        rowcol[1]=col;

        return rowcol;
    }

    /**通过行列号值计算网格值*/
    public static int RowCol_Code(int row,int col,int N){
        int code=0;
        int cols=4000/N;
        code=col+cols*(row-1);
        return code;
    }

    /**已知网格的行列号和分辨率，求网格的四个角的坐标*/
    public static JSONObject getLngLat(int row, int col, int N){
        double width=0.0011785999999997187*N;//每N00m的经度差
        double length=9.003999999997348E-4*N;//每N00m的纬度差

       /* double width=5.892999999998593E-4;//每50m的经度差
        double length=4.501999999998674E-4;//每50m的纬度差*/
        JSONObject corners=new JSONObject();

        double base_lng=115.417284;
        double base_lat=39.438283;

        double[] southwest=new double[2];
        double[] southeast=new double[2];
        double[] northeast=new double[2];
        double[] northwest=new double[2];

        double lat=0;
        double lng=0;

        //西南角经纬度
        lat=(row-1)*length;
        lng=(col-1)*width;

        southwest[0]=base_lng+lng;
        southwest[1]=base_lat+lat;
        corners.put("southwest",southwest);

        southeast[0]=base_lng+lng+width;
        southeast[1]=base_lat+lat;
        corners.put("southeast",southeast);

        northeast[0]=base_lng+lng+width;
        northeast[1]=base_lat+lat+length;
        corners.put("northeast",northeast);

        northwest[0]=base_lng+lng;
        northwest[1]=base_lat+lat+length;
        corners.put("northwest",northwest);

        //System.out.println(corners.toString());

        return corners;
    }



}
