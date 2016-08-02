package com.svail.com.reprocess;

import com.svail.bean.Code;
import net.sf.json.JSONObject;
import utils.FileTool;

import java.util.ArrayList;
import java.util.Vector;

/**
 * Created by ZhouXiang on 2016/8/2.
 */
public class LngLatCode {

    public static double LAT_MIN = 39.438283;
    public static double LNG_MIN = 115.417284;
    public static double width=0.03535799999999156;//每三千米的经度差
    public static double length=0.027011999999992042;//每三千米的纬度差
    public static int rows;
    public static int cols;
    public static Double LNG;
    public static Double LAT;
    public static ArrayList<Code> codes = new ArrayList<Code>();
    public static String GRIDFOLDER="D:\\房地产可视化\\";

    public static void main(String[] args){
        setGridCode();
        Vector<String> pois= FileTool.Load("","utf-8");
    }
    public static void setGridCode(){
        int mm = 1;
        for (int rr = 1; rr <= 60; rr++) {
            for (int cc = 1; cc <= 60; cc++) {
                Code c = new Code();
                c.setRow(rr);
                c.setCol(cc);
                c.setCode(mm);
                mm++;
                addCode(c);
            }
        }
    }
    public static void addCode(Code c) {
        codes.add(c);
    }

    public static int setPoiCode(String poi) {

        int code;
        JSONObject jsonObject = JSONObject.fromObject(poi);
        Object lat=jsonObject.get("latitude");
        String type=lat.getClass().getName();

        double latitude=0;
        double longitude=0;
        if(type.equals("java.lang.Double")){
            latitude = Double.parseDouble(jsonObject.get("latitude").toString());
            longitude =Double.parseDouble(jsonObject.get("longitude").toString());
        }else if(type.equals("java.lang.String")){
            latitude = Double.parseDouble(jsonObject.get("latitude").toString());
            longitude =Double.parseDouble(jsonObject.get("longitude").toString());
        }


        int row = (int) Math.ceil((latitude - LAT_MIN) / width);
        int col = (int) Math.ceil((longitude - LNG_MIN) / width);
        int index = (col + cols * (row - 1));

        // 依据行列数算出某行某列对应的编码
        code = codes.get(index + 1).code; // 由于codes中的第0个数的编码为1，故所有的index需要加1

        return code;
    }


}
