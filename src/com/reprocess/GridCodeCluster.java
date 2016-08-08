package com.reprocess;

import utils.FileTool;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

/**
 * Created by ZhouXiang on 2016/8/8.
 */
public class GridCodeCluster {
    public static void main(String[] args){

    }
    public static void getCodeInfo(String id,String time,String file){

        Vector<String> pois= FileTool.Load(file,"utf-8");
        Map<Integer,String> indexmap=new HashMap<Integer,String>();
        for(int i=0;i<pois.size();i++){

        }
    }

}
