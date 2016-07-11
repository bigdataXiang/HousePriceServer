package com.svail.handler;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.svail.db.db;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by ZhouXiang on 2016/7/11.
 */
public class handler_gridcolor {
    public String get(String path){
        String str="";
        return str;
    }
    public void setColor(JSONArray array,double pricemax){
        for (int i=0;i<array.size();i++){
            JSONObject obj= (JSONObject) array.get(i);
            double price=obj.getDouble("price");
            double ratio=price/pricemax;

        }


    }
    static JSONArray result_array = new JSONArray();

    public static double callDataFromMongo(String collName,String source){
        String str="";
        DBCollection coll = db.getDB().getCollection(collName);
        BasicDBObject document = new BasicDBObject();
        document.put("source",source);
        DBCursor cursor = coll.find(document);
        String poi="";

        List<Double> pricelist=new ArrayList<Double>();
        while (cursor.hasNext()) {
            poi=cursor.next().toString();

            JSONObject obj=JSONObject.fromObject(poi);
            JSONObject result=new JSONObject();

            int code = obj.getInt("code");
            result.put("code",code);

            double price = obj.getDouble("price");
            result.put("price",price);
            pricelist.add(price);

            result_array.add(result);
        }

        //求list中的最大价格值
        double price_max= Collections.max(pricelist);
        return  price_max;
    }
}
