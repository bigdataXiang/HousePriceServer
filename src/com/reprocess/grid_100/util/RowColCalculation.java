package com.reprocess.grid_100.util;

/**
 * Created by ZhouXiang on 2016/10/12.
 */
public class RowColCalculation {

    public static double width=0.0011785999999997187;//每100m的经度差
    public static double length=9.003999999997348E-4;//每100m的纬度差

    public static int getColMin(double west){
        int colmin=(int) Math.ceil((west-115.417284)/width);
        return colmin;
    }
    public static int getColMax(double east){
        int colmax=(int)Math.ceil((east-115.417284)/width);
        return colmax;
    }
    public static int getRowMin(double south){
        int rowmin=(int)Math.ceil((south-39.438283)/length);
        return rowmin;
    }
    public static int getRowMax(double north){
        int rowmax=(int)Math.ceil((north-39.438283)/length);
        return rowmax;
    }



}
