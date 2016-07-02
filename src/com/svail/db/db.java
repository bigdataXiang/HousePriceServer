package com.svail.db;

import com.mongodb.DB;
import com.mongodb.Mongo;

/**
 * Created by ZhouXiang on 2016/7/2.
 */
public class db {
    public static DB getDB(){
        Mongo m;
        DB db;
        try {
            System.out.println("运行开始:");
            m = new Mongo("192.168.6.9", 27017);
            //m = new Mongo("127.0.0.1", 27017);
            //m.dropDatabase("test");
            db= m.getDB("houseprice");
            return db;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
}
