package com.reprocess.grid_100;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.svail.db.db;
import net.sf.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static utils.UtilFile.printArray;

/**
 * Created by ZhouXiang on 2016/8/9.
 */
public class CallInterestGrid {
    public static void main(String[] args){

    }
    public static void CallMongo(JSONObject condition){
        String collName=condition.getString("collName");
        DBCollection coll = db.getDB().getCollection(collName);
        BasicDBObject document = new BasicDBObject();

        Iterator<String> it=condition.keys();
        while(it.hasNext()){
            String key = it.next();
            String value=condition.getString(key);
            document.put(key,value);
        }

        DBCursor cursor = coll.find(document);

        List code_array=new ArrayList<>();
        BasicDBObject cond=new BasicDBObject();
        cond.put("$gte",590);
        cond.put("$lte",592);
        document.put("row",cond);
        cond=new BasicDBObject();
        cond.put("$gte",835);
        cond.put("$lte",837);
        document.put("col",cond);
        code_array=coll.find(document).toArray();
        int size=code_array.size();
        System.out.println(size);
        printArray(code_array);

        String poi="";
        int count=0;
        JSONObject obj;
        JSONObject result;
        double unit_price=0;
        int code =0;

        if(cursor.hasNext()){
            while (cursor.hasNext()) {
                count++;
                poi=cursor.next().toString();
                //System.out.println(poi);

                obj=JSONObject.fromObject(poi);
                result=new JSONObject();

                code = obj.getInt("code");
                result.put("code",code);

                unit_price=obj.getDouble("unit_price");
                result.put("unit_price",unit_price);
            }
        }
    }
}
