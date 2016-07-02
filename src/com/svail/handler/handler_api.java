package com.svail.handler;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.svail.db.db;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by ZhouXiang on 2016/7/2.
 */
public class handler_api implements handler{
    public static Double X_MAX = 2.0542041271351546E7;// 2.0542041271351546E7
    public static Double X_MIN = 2.036373920422157E7;
    public static Double Y_MAX = 4547353.496401368;
    public static Double Y_MIN = 4368434.982578722;



    @Override
    public String get(String path){
        return setCode(1000,"2015","12","20").toString();
    }
    /**
     * 给网格创建编码
     * @param index  网格的分辨率
     */
    public  JSONArray setCode(int index,String year,String  month,String  day) {
		/*
		 * 将北京的东北角和西南角的坐标转换成平面坐标 BLToGauss(117.500126,41.059244)
		 * BLToGauss(115.417284,39.438283) BLToGauss: 2.0542041271351546E7
		 * 4547353.496401368 BLToGauss: 2.036373920422157E7 4368434.982578722
		 * 两点之间的距离是:252593.47127613405 178302.06712997705 178918.51382264588
		 *
		 */

        List< DBObject> pois=new ArrayList<>();
        pois=getPois("fang","2015","12","20");

        int rows = (int) Math.ceil((X_MAX - X_MIN) / index);
        int cols = (int) Math.ceil((Y_MAX - Y_MIN) / index);
        JSONArray array=new JSONArray();
        // 创建栅格编码
        long mm = 1;
        for (int rr = 1; rr <= rows; rr++) {
            for (int cc = 1; cc <= cols; cc++) {
                JSONObject obj= new JSONObject();
                obj.put("row",rr);
                obj.put("col",cc);
                obj.put("code",mm);
                double price=getPrice(mm,pois);
                obj.put("price",price);
                array.add(obj);
                mm++;
            }
        }
        return array;
    }
    public  List<DBObject> getPois( String source, String year, String month, String day){
        DBCollection coll = db.getDB().getCollection("rentout_code");
        List<DBObject> poilist=new ArrayList<>();

        BasicDBObject document_queue = new BasicDBObject();
        document_queue.put("source",source);
        JSONObject obj_date=new JSONObject();
        obj_date.put("year",year);
        obj_date.put("month",month);
        obj_date.put("day",day);
        document_queue.put("date",obj_date);

        DBCursor cursor = coll.find(document_queue);
        int count=0;
        JSONArray array = new JSONArray();

        while (cursor.hasNext()) {
            DBObject poi=cursor.next();
            poilist.add(poi);
        }
        return poilist;
    }
    public  double getPrice(long querycode, List<DBObject> poilist){
        double totalprice=0;
        int count=0;

        for(int i=0;i<poilist.size();i++){
            DBObject obj=poilist.get(i);
            long code=(long)obj.get("gridcode");
            if(querycode==code){
                if(obj.containsField("price")){
                    double price=(double)obj.get("price");
                    totalprice+=price;
                    count++;
                }
            }

        }
        double average_price;
        if(count==0){
           average_price=0;
        }else{
            average_price=totalprice/count;
        }
        return average_price;

    }

}
