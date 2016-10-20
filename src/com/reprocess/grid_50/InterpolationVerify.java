package com.reprocess.grid_50;

import utils.FileTool;

import java.util.Vector;

/**
 * Created by ZhouXiang on 2016/10/16.
 */
public class InterpolationVerify {
    public static void main(String[] args){

        Vector<String> pois= FileTool.Load("D:\\中期考核\\grid50\\时间插值B\\插值误差.txt","utf-8");
        double total_mse=0;
        double total_rmse=0;
        double total_mae=0;
        for(int i=0;i<pois.size();i++){
            String[] poi=pois.elementAt(i).split(",");
            total_mse+=Double.parseDouble(poi[1]);
            total_rmse+=Double.parseDouble(poi[2]);
            total_mae+=Double.parseDouble(poi[3]);
        }
        System.out.println(total_mse/pois.size());
        System.out.println(total_rmse/pois.size());
        System.out.println(total_mae/pois.size());
    }
}
