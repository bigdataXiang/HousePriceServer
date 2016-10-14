package com.reprocess.grid_100.util;

import net.sf.json.JSONObject;

/**
 * Created by ZhouXiang on 2016/10/12.
 */
public class SetCondition {
    public static JSONObject setCallInterestGrid(String body){
        JSONObject obj=JSONObject.fromObject(body);
        System.out.println(obj);

        double west=obj.getDouble("west");
        double east=obj.getDouble("east");
        double south=obj.getDouble("south");
        double north=obj.getDouble("north");

        int zoom=obj.getInt("zoom");
        int N= Resolution.getResolution(zoom);

        //2016年01月
        String time=obj.getString("gridTime");
        int year=Integer.parseInt(time.substring(0,time.indexOf("年")));
        int month=Integer.parseInt(time.substring(time.indexOf("年")+"年".length(),time.indexOf("月")));

        String source=obj.getString("source");
        source= Source.getSource(source);

        int colmin= RowColCalculation.getColMin(west);
        int colmax=RowColCalculation.getColMax(east);
        int rowmin=RowColCalculation.getRowMin(south);
        int rowmax=RowColCalculation.getRowMax(north);

        JSONObject condition=new JSONObject();
        condition.put("N",N);
        condition.put("rowmax",rowmax);
        condition.put("rowmin",rowmin);
        condition.put("colmax",colmax);
        condition.put("colmin",colmin);
        condition.put("year",year);
        condition.put("month",month);
        condition.put("source",source);
        condition.put("export_collName","GridData_Resold_100");

        return condition;
    }
    public static JSONObject setCallInterestGrid_50(String body){
        JSONObject obj=JSONObject.fromObject(body);
        System.out.println(obj);

        double west=obj.getDouble("west");
        double east=obj.getDouble("east");
        double south=obj.getDouble("south");
        double north=obj.getDouble("north");

        int zoom=obj.getInt("zoom");
        int N= Resolution.getResolution(zoom);

        //2016年01月
        String time=obj.getString("gridTime");
        int year=Integer.parseInt(time.substring(0,time.indexOf("年")));
        int month=Integer.parseInt(time.substring(time.indexOf("年")+"年".length(),time.indexOf("月")));

        String source=obj.getString("source");
        source= Source.getSource(source);

        int colmin= RowColCalculation.getColMin_50(west);
        int colmax=RowColCalculation.getColMax_50(east);
        int rowmin=RowColCalculation.getRowMin_50(south);
        int rowmax=RowColCalculation.getRowMax_50(north);

        JSONObject condition=new JSONObject();
        condition.put("N",N);
        condition.put("rowmax",rowmax);
        condition.put("rowmin",rowmin);
        condition.put("colmax",colmax);
        condition.put("colmin",colmin);
        condition.put("year",year);
        condition.put("month",month);
        condition.put("source",source);
        condition.put("export_collName","GridData_Resold_50_Interpolation");

        return condition;
    }

    public static JSONObject setCallPriceAcceleration(String body){
        JSONObject obj=JSONObject.fromObject(body);

        double west=obj.getDouble("west");
        double east=obj.getDouble("east");
        double south=obj.getDouble("south");
        double north=obj.getDouble("north");
        int zoom=obj.getInt("zoom");
        String starttime=obj.getString("starttime");
        String endtime=obj.getString("endtime");

        int startyear=Integer.parseInt(starttime.substring(0,starttime.indexOf("年")));
        int startmonth=Integer.parseInt(starttime.substring(starttime.indexOf("年")+"年".length(),starttime.indexOf("月")));

        int endyear=Integer.parseInt(endtime.substring(0,endtime.indexOf("年")));
        int endmonth=Integer.parseInt(endtime.substring(endtime.indexOf("年")+"年".length(),endtime.indexOf("月")));

        int N= Resolution.getResolution(zoom);
        String source=obj.getString("source");
        source= Source.getSource(source);

        int colmin= RowColCalculation.getColMin(west);
        int colmax=RowColCalculation.getColMax(east);
        int rowmin=RowColCalculation.getRowMin(south);
        int rowmax=RowColCalculation.getRowMax(north);

        //<option>最值计算法</option>
        //<option>起止时间计算法</option>
        String computation=obj.getString("computation");

        JSONObject condition=new JSONObject();
        condition.put("N",N);
        condition.put("computation",computation);
        condition.put("rowmax",rowmax);
        condition.put("rowmin",rowmin);
        condition.put("colmax",colmax);
        condition.put("colmin",colmin);
        condition.put("startyear",startyear);
        condition.put("startmonth",startmonth);
        condition.put("endyear",endyear);
        condition.put("endmonth",endmonth);
        condition.put("source",source);
        condition.put("export_collName","GridData_Resold_100");

        return condition;
    }
    public static JSONObject setCallPriceAcceleration_50(String body){
        JSONObject obj=JSONObject.fromObject(body);

        double west=obj.getDouble("west");
        double east=obj.getDouble("east");
        double south=obj.getDouble("south");
        double north=obj.getDouble("north");
        int zoom=obj.getInt("zoom");
        String starttime=obj.getString("starttime");
        String endtime=obj.getString("endtime");

        int startyear=Integer.parseInt(starttime.substring(0,starttime.indexOf("年")));
        int startmonth=Integer.parseInt(starttime.substring(starttime.indexOf("年")+"年".length(),starttime.indexOf("月")));

        int endyear=Integer.parseInt(endtime.substring(0,endtime.indexOf("年")));
        int endmonth=Integer.parseInt(endtime.substring(endtime.indexOf("年")+"年".length(),endtime.indexOf("月")));

        int N= Resolution.getResolution(zoom);
        String source=obj.getString("source");
        source= Source.getSource(source);

        int colmin= RowColCalculation.getColMin_50(west);
        int colmax=RowColCalculation.getColMax_50(east);
        int rowmin=RowColCalculation.getRowMin_50(south);
        int rowmax=RowColCalculation.getRowMax_50(north);

        //<option>最值计算法</option>
        //<option>起止时间计算法</option>
        String computation=obj.getString("computation");

        JSONObject condition=new JSONObject();
        condition.put("N",N);
        condition.put("computation",computation);
        condition.put("rowmax",rowmax);
        condition.put("rowmin",rowmin);
        condition.put("colmax",colmax);
        condition.put("colmin",colmin);
        condition.put("startyear",startyear);
        condition.put("startmonth",startmonth);
        condition.put("endyear",endyear);
        condition.put("endmonth",endmonth);
        condition.put("source",source);
        condition.put("export_collName","GridData_Resold_50_Interpolation");

        return condition;
    }
}
