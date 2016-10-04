package com.reprocess.grid_100.ContourGeneration;

import net.sf.json.JSONObject;
import utils.FileTool;

import java.util.Vector;

/**
 * Created by ZhouXiang on 2016/10/4.
 */
public class DrawContour {
    public static void main(String[] args){

        drawContour("D:\\中期考核\\等值线\\contour_8.txt");
    }

    /**画等高线*/
    public static void drawContour(String file){

        Vector<String> pois= FileTool.Load(file,"utf-8");

        String poi="";
        JSONObject obj;
        int row;
        int col;

        for(int i=0;i<pois.size();i++){

            poi=pois.elementAt(i);
            obj=JSONObject.fromObject(poi);
            row=obj.getInt("row");
            col=obj.getInt("col");

        }
    }
}
