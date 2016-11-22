package com.reprocess.grid_50;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.reprocess.grid_100.PoiCode;
import com.svail.db.db;
import net.sf.json.JSONObject;

/**
 * Created by ZhouXiang on 2016/11/21.
 */
public class Resold100ToResold50 {
    public static void main(String[] args){
        DBCollection coll_export = db.getDB().getCollection("BasicData_Resold_100");
        DBCollection coll_Basic50 = db.getDB().getCollection("BasicData_Resold_50");

        String poi;
        JSONObject obj;
        double lng;
        double lat;
        int code;
        int row;
        int col;

        DBCursor cursor = coll_export.find();
        while (cursor.hasNext()){
            BasicDBObject cs = (BasicDBObject)cursor.next();
            poi=cs.toString();
            obj= JSONObject.fromObject(poi);
            obj.remove("_id");
            lat=obj.getDouble("lat");
            lng=obj.getDouble("lng");

            String[] result= PoiCode.setPoiCode_50(lat,lng).split(",");
            code=Integer.parseInt(result[0]);
            row=Integer.parseInt(result[1]);
            col=Integer.parseInt(result[2]);

            cs.put("code",code);
            cs.put("row",row);
            cs.put("col",col);

            DBCursor rls =coll_Basic50.find(cs);
            if(rls == null || rls.size() == 0){
                coll_Basic50.insert(cs);
            }else{
                System.out.println("exsit!");
            }
        }
    }
}
