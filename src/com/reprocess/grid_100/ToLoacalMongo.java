package com.reprocess.grid_100;

import com.mongodb.*;
import net.sf.json.JSONObject;
import utils.FileTool;

import java.util.Vector;

import static com.reprocess.grid_100.PoiCode.setPoiCode_100;

/**
 * Created by ZhouXiang on 2016/9/9.
 */
public class ToLoacalMongo {

    public static void main(String[] args){
        toLocalMongo("helloword");
        System.out.println("ok!");
    }

    public static void toLocalMongo(String collName){
        Mongo m;
        try {
            System.out.println("运行开始:");
            m = new MongoClient("127.0.0.1", 27017);   //127.0.0.1

            DB db = m.getDB("houseprice");
            DBCollection coll = db.getCollection(collName);//coll.drop();
            BasicDBObject document=new BasicDBObject() ;
            document.put("hello","word");

            coll.insert(document);

        } catch (MongoException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NullPointerException e) {
            // TODO Auto-generated catch block
            System.out.println("发生异常的原因为 :"+e.getMessage());
            e.printStackTrace();
        }
    }
}
