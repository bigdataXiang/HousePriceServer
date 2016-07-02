package com.svail.handler;

import com.mongodb.*;
import com.svail.db.db;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;


/**
 * Created by ZhouXiang on 2016/7/2.
 */
public class handler_info {
    public String get(){
        return callDataFromMongo("rentout_code","fang",15275);
    }
    public String callDataFromMongo(String collName,String source,int code){
        DBCollection coll = db.getDB().getCollection(collName);
        BasicDBObject document = new BasicDBObject();
        document.put("gridcode",code);
        DBCursor cursor = coll.find(document);
        String poi="";
        int count=0;
        JSONArray array = new JSONArray();
        while (cursor.hasNext()) {
            poi=cursor.next().toString();

            JSONObject obj=JSONObject.fromObject(poi);
            Object date=obj.get("date");
            JSONObject obj_date=JSONObject.fromObject(date);
            array.add(obj_date);

//            String year=obj_date.getString("year").replace(" ","");
//            String month=obj_date.getString("month").replace(" ","");
//            String day=obj_date.getString("day").replace(" ","");
        }
        return array.toString();
    }
}
