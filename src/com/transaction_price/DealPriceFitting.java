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

    //将所有小区信息都融合在一起，找出2015至2016年间成交的数据
    public static void communityResearch(String community){
        DBCollection coll_BasicData = db.getDB().getCollection("BasicData_Resold_100");
        DBCollection coll_fang = db.getDB().getCollection("Deals_fang");
        DBCollection coll_aiwujiwu = db.getDB().getCollection("Deals_aiwujiwu");
        DBCollection coll_lianjia = db.getDB().getCollection("Deals_lianjia");
        DBCollection coll_woaiwojia = db.getDB().getCollection("Deals_woaiwojia");

        DBCollection coll_community = db.getDB().getCollection("Deals_community");

        BasicDBObject document=new BasicDBObject();
        document.put("community",community);
        BasicDBObject cond=new BasicDBObject();
        cond.put("$gte",2015);
        cond.put("$lte",2016);
        document.put("year",cond);


        /*DBCursor cursor = coll_BasicData.find(document);
        while (cursor.hasNext()){
            //FileTool.Dump(cursor.next().toString(),"D:\\小论文\\dealdata\\小区名\\阳光新干线\\抓取数据.txt","utf-8");
            BasicDBObject doc=(BasicDBObject)cursor.next();
        }*/

        DBCursor cursor = coll_fang.find(document);
        while (cursor.hasNext()){
            //FileTool.Dump(cursor.next().toString(),"D:\\小论文\\dealdata\\小区名\\阳光新干线\\fang.txt","utf-8");
            BasicDBObject doc=(BasicDBObject)cursor.next();
            doc=getDocument(doc);
            importToDB(coll_community,doc,"fang");
        }

        cursor = coll_aiwujiwu.find(document);
        while (cursor.hasNext()){
            //FileTool.Dump(cursor.next().toString(),"D:\\小论文\\dealdata\\小区名\\阳光新干线\\aiwujiwu.txt","utf-8");

            BasicDBObject doc=(BasicDBObject)cursor.next();
            doc=getDocument(doc);
            importToDB(coll_community,doc,"aiwujiwu");
        }

        cursor = coll_lianjia.find(document);
        while (cursor.hasNext()){
            //FileTool.Dump(cursor.next().toString(),"D:\\小论文\\dealdata\\小区名\\阳光新干线\\lianjia.txt","utf-8");

            BasicDBObject doc=(BasicDBObject)cursor.next();
            doc=getDocument(doc);
            importToDB(coll_community,doc,"lianjia");
        }

        cursor = coll_woaiwojia.find(document);
        while (cursor.hasNext()){
            //FileTool.Dump(cursor.next().toString(),"D:\\小论文\\dealdata\\小区名\\阳光新干线\\woaiwojia.txt","utf-8");

            BasicDBObject doc=(BasicDBObject)cursor.next();
            doc=getDocument(doc);
            importToDB(coll_community,doc,"woaiwojia");
        }

    }
    //获取document中某些特定的想要的字段
    public static BasicDBObject getDocument(BasicDBObject document){
        BasicDBObject doc=new BasicDBObject();
        if(document.containsField("community")){
            String community=document.getString("community");
            doc.put("community",community);
        }
        if(document.containsField("bedroomSum")){
            int bedroomSum=document.getInt("bedroomSum");
            doc.put("bedroomSum",bedroomSum);
        }
        if(document.containsField("livingRoomSum")){
            int livingRoomSum=document.getInt("livingRoomSum");
            doc.put("livingRoomSum",livingRoomSum);
        }
        if(document.containsField("year")){
            int year=document.getInt("year");
            doc.put("year",year);
        }
        if(document.containsField("month")){
            int month=document.getInt("month");
            doc.put("month",month);
        }
        if(document.containsField("day")){
            int day=document.getInt("day");
            doc.put("day",day);
        }
        if(document.containsField("price")){
            double price=document.getDouble("price");
            doc.put("price",price);
        }
        if(document.containsField("spaceArea")){
            double spaceArea=document.getDouble("spaceArea");
            doc.put("spaceArea",spaceArea);
        }
        /*if(document.containsField("floor")){
            String floor=document.getString("floor");
            doc.put("floor",floor);
        }
        if(document.containsField("layers")){
            int layers=document.getInt("layers");
            doc.put("layers",layers);
        }*/
        return doc;
    }

    //将成交混合数据（各个网站的数据放在一起）导入数据库中
    public static void importToDB(DBCollection coll,BasicDBObject document,String source){
        DBCursor rls =coll.find(document);
        if(rls == null || rls.size() == 0){
            document.put("source",source);
            coll.insert(document);
        }else{
            System.out.println("该数据已经存在!");
        }
    }

    //查询该小区的所有成交数据，并且打印出来看看
    public static void queryCommunityTransaction(String community){
        DBCollection coll= db.getDB().getCollection("Deals_community");
        BasicDBObject document=new BasicDBObject();
        document.put("community",community);
        DBCursor cursor = coll.find(document);
        while (cursor.hasNext()){
            BasicDBObject doc=(BasicDBObject)cursor.next();
            doc.remove("_id");
            FileTool.Dump(doc.toString(),"D:\\小论文\\dealdata\\小区名\\阳光新干线\\"+community+".txt","utf-8");
        }
    }

    //根据成交记录找出挂牌信息
    public static void queryListingInformation(JSONObject obj,int i){
        String community=obj.getString("community");
        int bedroomSum=obj.getInt("bedroomSum");
        int livingRoomSum=obj.getInt("livingRoomSum");
        int year=obj.getInt("year");
        int month=obj.getInt("month");
        int day=obj.getInt("day");
        double price=obj.getDouble("price");
        int spaceArea=obj.getInt("spaceArea");

        BasicDBObject document=new BasicDBObject();
        document.put("community",community);
        document.put("rooms",bedroomSum);
        document.put("halls",livingRoomSum);
        document.put("area",spaceArea);

        DBCollection coll_BasicData = db.getDB().getCollection("BasicData_Resold_100");
        DBCursor cursor = coll_BasicData.find(document);
        while (cursor.hasNext()){
            BasicDBObject doc=(BasicDBObject)cursor.next();
            FileTool.Dump(doc.toString(),"D:\\小论文\\dealdata\\小区名\\阳光新干线\\抓取数据_"+i+".txt","utf-8");
        }



    }

}
