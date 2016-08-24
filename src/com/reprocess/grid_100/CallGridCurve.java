package com.reprocess.grid_100;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.svail.bean.Response;
import com.svail.db.db;
import com.svail.handler.handler_gridprice;
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
        condition.put("N",5);
        condition.put("source","woaiwojia");
        condition.put("export_collName","GridData_Resold_100");

        System.out.println(callGridData_Resold_100(condition));
    }

    public Response get(String body){

        JSONObject condition= JSONObject.fromObject(body);
        condition.put("source","woaiwojia");
        condition.put("export_collName","GridData_Resold_100");

        Response r= new Response();
        r.setCode(200);
        r.setContent(callGridData_Resold_100(condition));
        return r;

    }
    public static String callGridData_Resold_100(JSONObject condition){

        String collName=condition.getString("export_collName");
        DBCollection coll = db.getDB().getCollection(collName);

        int row=condition.getInt("row");
        int col=condition.getInt("col");
        int N=condition.getInt("N");

        int row_100=(row-1)*N+1;
        int col_100=(col-1)*N+1;
        int code_100;

        BasicDBObject document;
        String source;
        String poi="";
        int count=0;
        String date;
        List<JSONObject> list;
        Map<String,List> map= new HashMap<>();
        JSONObject obj;

        for(int i=row_100;i<=row_100+N;i++){
            for(int j=col_100;j<=col_100+N;j++){

                document = new BasicDBObject();

                source=condition.getString("source");
                document.put("source",source);

                code_100=j+2000*(i-1);
                document.put("code",code_100);

                document.put("row",i);
                document.put("col",j);

                DBCursor cursor = coll.find(document);
                if(cursor.hasNext()){
                    while (cursor.hasNext()){
                        poi=cursor.next().toString();
                        obj=JSONObject.fromObject(poi);
                        obj.remove("_id");
                        date=obj.getString("year")+"-"+obj.getString("month")+"-"+"01";

                        if(map.containsKey(date)){
                            list=map.get(date);
                            list.add(obj);
                            map.put(date,list);

                        }else{
                            list=new ArrayList<>();
                            list.add(obj);
                            map.put(date,list);

                        }
                        count++;
                    }
                }
            }

        }

        System.out.println("总共有"+count+"个小网格");

        Iterator it=map.keySet().iterator();
        double average_price;
        List<JSONObject> time_price_list=new ArrayList<>();
        JSONObject time_price;
        if(it.hasNext()){
            while(it.hasNext()){
                date=(String) it.next();
                list=map.get(date);
                //System.out.println(map.get(date));

                double totalprice=0;
                int counts=0;
                for(int i=0;i<list.size();i++){
                    obj=list.get(i);
                    if(obj.containsKey("average_price")){
                        average_price=obj.getDouble("average_price");
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
                time_price.put("average_price",average_price);
                time_price_list.add(time_price);
            }
        }

        //将list数组按照价格排序
        Collections.sort(time_price_list, new UtilFile.Average_PriceComparator());
        //System.out.println(time_price_list);
        int suggestedMin=time_price_list.get(0).getInt("average_price");
        int suggestedMax=time_price_list.get(time_price_list.size()-1).getInt("average_price")+1;
        //System.out.println(suggestedMin);
        //System.out.println(suggestedMax);

        //将list数组按照时间排序
        Collections.sort(time_price_list, new UtilFile.TimeComparator());

        JSONObject result=new JSONObject();
        result.put("suggestedMin",suggestedMin);
        result.put("suggestedMax",suggestedMax);
        result.put("data",time_price_list);
        System.out.println(result.toString());

        return result.toString();

    }
}
