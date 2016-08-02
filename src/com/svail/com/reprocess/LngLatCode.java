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
    public static int rows=60;
    public static int cols=60;
    public static Double LNG;
    public static Double LAT;
    public static ArrayList<Code> codes = new ArrayList<Code>();
    public static String GRIDFOLDER="D:\\房地产可视化\\";

    public static void main(String[] args){
        setGridCode();
        Vector<String> pois= FileTool.Load(GRIDFOLDER+"woaiwojia_resold_2016_0428.txt","utf-8");
        for(int i=0;i<pois.size();i++){
            String poi=pois.elementAt(i);
            JSONObject jsonObject = JSONObject.fromObject(poi);
            setPoiCode(jsonObject);
            FileTool.Dump(jsonObject.toString(),GRIDFOLDER+"woaiwojia_resold_2016_0428_code.txt","utf-8");

        }
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
        /*for(int k=0;k<codes.size();k++){
            String str="codes的第"+k+"个数是:"+codes.get(k).row+"行,"+codes.get(k).col+"列,"+codes.get(k).code+"\r\n";
            FileTool.Dump(str,GRIDFOLDER+"woaiwojia_gridecode_"+3000+".txt","utf-8");

        }*/
    }
    public static void addCode(Code c) {
        codes.add(c);
    }

    public static JSONObject setPoiCode(JSONObject jsonObject) {

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
        int code = (col + cols * (row - 1));

        jsonObject.put("gridcode",code);

        return jsonObject;
    }


}
