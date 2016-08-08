package com.reprocess.grid_100;

import utils.FileTool;

import static utils.UtilFile.printArray;

/**
 * Created by ZhouXiang on 2016/8/8.
 */
public class GridMerge {
    public static void main(String args[]){
        printArray(codeMapping100toN00(11,2,5));
    }
    //建立500*500的网格编码与100*100的网格编码的映射关系
    public static int[] codeMapping100toN00(int row_100,int col_100,int N){
        int cols=2000;
        int row;
        int col;
        int code;
        int[] result =new int[3];
        double rowtemp=(double)row_100/N;
        row= (int) Math.ceil(rowtemp);
        result[0]=row;
        double coltemp=(double)col_100/N;
        col= (int) Math.ceil(coltemp);
        result[1]=col;
        code=(col + (cols/N)* (row - 1));
        result[2]=code;
        return result;
    }
}
