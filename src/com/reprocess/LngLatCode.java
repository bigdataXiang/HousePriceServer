package com.reprocess;

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
    public static int cols=60;
    public static Double LNG;
    public static Double LAT;
    public static ArrayList<Code> codes = new ArrayList<Code>();
    public static String GRIDFOLDER="D:\\房地产可视化\\";

    public static void main(String[] args){

        //checkGridCode();
        setPoiCode(116.41062,39.96986);

    }


    public static void checkGridCode(){
        //网格是从左至右一行一行地填充，故对于某一固定的行，每个网格距离初始网格的经度跨度一直在变，而纬度跨度不变
        for(int  row=1;row<61;row++){
            for(int col=1;col<61;col++){
                double dist_wid=(col-1)*width;
                double dist_len=(row-1)*length;

                double left_down_lng=115.417284+dist_wid;
                double left_down_lat=39.438283+dist_len;
                double right_up_lng=115.45264199999998+dist_wid;
                double right_up_lat=39.46529499999999+dist_len;

                int gridcode=(col+60*(row-1));

                String str=gridcode+" : "+"经度范围 ("+left_down_lng+","+right_up_lng+")"+" 纬度范围 ("+left_down_lat+","+right_up_lat+")";
                FileTool.Dump(str,"E:\\房地产可视化\\gridCheck\\gridScope.txt","utf-8");
            }
        }
    }
    public static void setPoiCode(double longitude,double latitude){

        int row = (int) Math.ceil((latitude - LAT_MIN) / length);
        int col = (int) Math.ceil((longitude - LNG_MIN) / width);
        int code = (col + 60 * (row - 1));

        System.out.println(code);
    }
    public static int setPoiCode(JSONObject jsonObject) {

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


        int row = (int) Math.ceil((latitude - LAT_MIN) / length);
        int col = (int) Math.ceil((longitude - LNG_MIN) / width);
        int code = (col + cols * (row - 1));

        return code;
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
    public static void ProcessFile(){
        Vector<String> pois= FileTool.Load(GRIDFOLDER+"woaiwojia_resold_2016_0428.txt","utf-8");
        for(int i=0;i<pois.size();i++){
            String poi=pois.elementAt(i);
            JSONObject jsonObject = JSONObject.fromObject(poi);
            setPoiCode(jsonObject);
            FileTool.Dump(jsonObject.toString(),GRIDFOLDER+"woaiwojia_resold_2016_0428_code.txt","utf-8");

        }
    }


}
