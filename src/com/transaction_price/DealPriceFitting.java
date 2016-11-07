package com.transaction_price;

import com.mongodb.*;
import com.svail.db.db;
import net.sf.json.JSONObject;
import utils.FileTool;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

/**
 * Created by ZhouXiang on 2016/11/7.
 */
public class DealPriceFitting {
    public static void main(String[] args){
        getCommunitySet();
        matchCommunity();
    }
    public static Set<String> communitySet=new HashSet<>();

    //从每一个网站的成交数据库中找出所有的小区名字
    public static void getCommunitySet(){

        DBCollection coll = db.getDB().getCollection("Deals_fang");
        DBCursor cursor = coll.find();
        BasicDBObject document;
        while (cursor.hasNext()) {
            document=(BasicDBObject)cursor.next();
            //System.out.println(document);
            String community=document.getString("community");
            communitySet.add(community);
        }

        coll = db.getDB().getCollection("Deals_aiwujiwu");
        cursor = coll.find();
        while (cursor.hasNext()) {
            document=(BasicDBObject)cursor.next();
            String community=document.getString("community");
            communitySet.add(community);
        }

        coll = db.getDB().getCollection("Deals_lianjia");
        cursor = coll.find();
        while (cursor.hasNext()) {
            document=(BasicDBObject)cursor.next();
            String community=document.getString("community");
            communitySet.add(community);
        }

        coll = db.getDB().getCollection("Deals_woaiwojia");
        cursor = coll.find();
        while (cursor.hasNext()) {
            document=(BasicDBObject)cursor.next();
            String community=document.getString("community");
            communitySet.add(community);
        }

        System.out.println(communitySet.size());



    }

    //从BasicData_Resold_100中找到与成交房源相同的名字小区数据
    public static void matchCommunity(){
        DBCollection coll = db.getDB().getCollection("BasicData_Resold_100");
        BasicDBObject document;

        //遍历set
        Iterator<String> it=communitySet.iterator();
        while (it.hasNext()){
            String str=it.next();
            document=new BasicDBObject();
            document.put("community",str);
            DBCursor cursor = coll.find(document);

            if (cursor.size()!=0){
                FileTool.Dump(str,"D:\\小论文\\dealdata\\小区名\\实际有数据的小区.txt","utf-8");
            }
        }
    }
}
