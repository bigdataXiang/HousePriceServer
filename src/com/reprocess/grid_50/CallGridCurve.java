package com.reprocess.grid_50;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.svail.bean.Response;
import com.svail.db.db;
import net.sf.json.JSONObject;
import utils.UtilFile;

import java.util.*;

/**
 * Created by ZhouXiang on 2016/8/11.
 */
public class CallGridCurve {
    public static void main(String[] args){

        JSONObject condition=new JSONObject();
        condition.put("row",107);
        condition.put("col",176);
        condition.put("gridcode",42158);
        condition.put("N",10);
        condition.put("source","anjuke");
        condition.put("export_collName","GridData_Resold_100");

        System.out.println(callGridData_Resold_50_Interpolation(condition));
    }

    public Response get(String body){

        JSONObject condition= JSONObject.fromObject(body);
        condition.put("source","anjuke");
        condition.put("export_collName","GridData_Resold_50_Interpolation");

        Response r= new Response();
        r.setCode(200);
        r.setContent(callGridData_Resold_50_Interpolation(condition));
        //System.out.println(callGridData_Resold_100(condition));
        return r;


    }
    public static String callGridData_Resold_50_Interpolation(JSONObject condition){

        String collName=condition.getString("export_collName");
        DBCollection coll = db.getDB().getCollection(collName);

        int row=condition.getInt("row");
        int col=condition.getInt("col");
        int N=condition.getInt("N");

        int row_50=(row-1)*N+1;
        int col_50=(col-1)*N+1;
        int code_50;

        BasicDBObject document;
        String source;
        String poi="";
        int count=0;
        String date;
        List<JSONObject> list_fang;
        List<JSONObject> list_woaiwojia;
        Map<String,List> map_fang= new HashMap<>();
        Map<String,List> map_woaiwojia= new HashMap<>();
        JSONObject obj;
        int count_fang=0;
        int count_woaiwojia=0;
        for(int i=row_50;i<=row_50+N;i++){
            for(int j=col_50;j<=col_50+N;j++){

                document = new BasicDBObject();

                source=condition.getString("source");
                //document.put("source",source);

                code_50=j+4000*(i-1);
                document.put("code",code_50);

                document.put("row",i);
                document.put("col",j);

                DBCursor cursor = coll.find(document);
                if(cursor.hasNext()){
                    while (cursor.hasNext()){
                        poi=cursor.next().toString();
                        obj=JSONObject.fromObject(poi);
                        source=obj.getString("source");
                        obj.remove("_id");
                        date=obj.getString("year")+"-"+obj.getString("month")+"-"+"01";

                        if(source.equals("woaiwojia")){
                            count_woaiwojia++;
                            if(map_woaiwojia.containsKey(date)){
                                list_woaiwojia=map_woaiwojia.get(date);
                                list_woaiwojia.add(obj);
                                map_woaiwojia.put(date,list_woaiwojia);

                            }else{
                                list_woaiwojia=new ArrayList<>();
                                list_woaiwojia.add(obj);
                                map_woaiwojia.put(date,list_woaiwojia);
                            }
                        }else if(source.equals("fang")){
                            count_fang++;
                            if(map_fang.containsKey(date)){
                                list_fang=map_fang.get(date);
                                list_fang.add(obj);
                                map_fang.put(date,list_fang);

                            }else{
                                list_fang=new ArrayList<>();
                                list_fang.add(obj);
                                map_fang.put(date,list_fang);
                            }
                        }
                        count++;
                    }
                }
            }

        }

        System.out.println("总共有"+count+"个小网格");
        System.out.println("fang有"+count_fang+"个小网格");
        System.out.println("woaiwojia有"+count_woaiwojia+"个小网格");



        double average_price;
        List<JSONObject> woaiwojia_time_price_list=new ArrayList<>();
        List<JSONObject> fang_time_price_list=new ArrayList<>();
        List<JSONObject> blend_time_price_list=new ArrayList<>();
        JSONObject time_price;
        TreeSet dates=new TreeSet();//存放日期

        Map<String,Double> average_woaiwojia=new HashMap<>();
        Iterator it_woaiwojia=map_woaiwojia.keySet().iterator();
        if(it_woaiwojia.hasNext()){
            while(it_woaiwojia.hasNext()){
                date=(String) it_woaiwojia.next();
                dates.add(date);
                list_woaiwojia=map_woaiwojia.get(date);
                //System.out.println(map.get(date));

                double totalprice=0;
                int counts=0;
                for(int i=0;i<list_woaiwojia.size();i++){
                    obj=list_woaiwojia.get(i);
                    if(obj.containsKey("price")){
                        average_price=obj.getDouble("price");
                        totalprice+=average_price;
                        counts++;
                    }
                }

                if(counts!=0){
                    average_price=totalprice/counts;
                }else {
                    average_price=0;
                }

                time_price=new JSONObject();
                time_price.put("date",date);
                time_price.put("source","woaiwojia");
                time_price.put("average_price",average_price);
                average_woaiwojia.put(date,average_price);
                woaiwojia_time_price_list.add(time_price);
            }
        }
        Map<String,Double> average_fang=new HashMap<>();
        Iterator it_fang=map_fang.keySet().iterator();
        if(it_fang.hasNext()){
            while(it_fang.hasNext()){
                date=(String) it_fang.next();
                dates.add(date);
                list_fang=map_fang.get(date);
                //System.out.println(map.get(date));

                double totalprice=0;
                int counts=0;
                for(int i=0;i<list_fang.size();i++){
                    obj=list_fang.get(i);
                    if(obj.containsKey("price")){
                        average_price=obj.getDouble("price");
                        totalprice+=average_price;
                        counts++;
                    }
                }

                if(counts!=0){
                    average_price=totalprice/counts;
                }else {
                    average_price=0;
                }

                time_price=new JSONObject();
                time_price.put("date",date);
                time_price.put("source","fang");
                time_price.put("average_price",average_price);
                average_fang.put(date,average_price);
                fang_time_price_list.add(time_price);
            }
        }
        Iterator<String> it_blend=dates.iterator();
        double fang=0;
        double woaiwojia=0;
        double average=0;
        while(it_blend.hasNext()){
            date=it_blend.next();
            if(average_fang.containsKey(date)&&average_woaiwojia.containsKey(date)){
                fang=average_fang.get(date);
                woaiwojia=average_woaiwojia.get(date);
                average=(fang+woaiwojia)/2;

                time_price=new JSONObject();
                time_price.put("date",date);
                time_price.put("source","blend");
                time_price.put("average_price",average);
                blend_time_price_list.add(time_price);
            }
        }
        System.out.println(dates);

        //将fang_time_price_list数组按照价格排序
        Collections.sort(fang_time_price_list, new UtilFile.Average_PriceComparator());
        //将woaiwojia_time_price_list数组按照价格排序
        Collections.sort(woaiwojia_time_price_list, new UtilFile.Average_PriceComparator());
        //将blend_time_price_list数组按照价格排序
        Collections.sort(blend_time_price_list, new UtilFile.Average_PriceComparator());


        JSONObject totalresult=new JSONObject();
        List<Integer> min=new ArrayList<>();
        List<Integer> max=new ArrayList<>();
        int suggestedMin_1=0;
        int suggestedMax_1=0;
        int suggestedMin_2=0;
        int suggestedMax_2=0;
        int suggestedMin_3=0;
        int suggestedMax_3=0;
        int suggestedMin=0;
        int suggestedMax=0;
        JSONObject result;


        if(fang_time_price_list.size()!=0){
            suggestedMin_1=fang_time_price_list.get(0).getInt("average_price");
            suggestedMax_1=fang_time_price_list.get(fang_time_price_list.size()-1).getInt("average_price")+1;
            min.add(suggestedMin_1);
            max.add(suggestedMax_1);
            //将list数组按照时间排序
            Collections.sort(fang_time_price_list, new UtilFile.TimeComparator());
        }
        result=new JSONObject();
        result.put("data",fang_time_price_list);
        totalresult.put("fang",result);
        //System.out.println(result.toString());

        if(woaiwojia_time_price_list.size()!=0){
            suggestedMin_2=woaiwojia_time_price_list.get(0).getInt("average_price");
            suggestedMax_2=woaiwojia_time_price_list.get(woaiwojia_time_price_list.size()-1).getInt("average_price")+1;
            min.add(suggestedMin_2);
            max.add(suggestedMax_2);
            //将list数组按照时间排序
            Collections.sort(woaiwojia_time_price_list, new UtilFile.TimeComparator());
        }
        result=new JSONObject();
        result.put("data",woaiwojia_time_price_list);
        totalresult.put("woaiwojia",result);
        //System.out.println(result.toString());

        if(blend_time_price_list.size()!=0){
            suggestedMin_3=blend_time_price_list.get(0).getInt("average_price");
            suggestedMax_3=blend_time_price_list.get(blend_time_price_list.size()-1).getInt("average_price")+1;
            min.add(suggestedMin_3);
            max.add(suggestedMax_3);
            //将list数组按照时间排序
            Collections.sort(blend_time_price_list, new UtilFile.TimeComparator());
        }
        result=new JSONObject();
        result.put("data",blend_time_price_list);
        totalresult.put("blend",result);
        //System.out.println(result.toString());

        Collections.sort(min);
        Collections.sort(max);
        if(min.size()!=0){
            suggestedMin=min.get(0);
        }
        if(max.size()!=0){
            suggestedMax=max.get(0);
        }
        totalresult.put("suggestedMin",suggestedMin);
        totalresult.put("suggestedMax",suggestedMax);
        System.out.println(totalresult.toString());

        return totalresult.toString();
    }
}
