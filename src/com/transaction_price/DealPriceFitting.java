package com.transaction_price;

import com.mongodb.*;
import com.svail.db.db;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import utils.FileTool;

import java.util.*;

/**
 * Created by ZhouXiang on 2016/11/7.
 */
public class DealPriceFitting {
    public static void main(String[] args){
        //getCommunitySet();
        //matchCommunity();
        //dataDuplicateRemoval();
        System.out.println("begin:");
        findHangoutDeals("D:\\小论文\\dealdata\\小区名\\成交数据_挂牌数据\\成交数据_挂牌数据.txt");
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
        if(document.containsField("floor")){
            String floor=document.getString("floor");
            doc.put("floor",floor);
        }
        if(document.containsField("layers")){
            int layers=document.getInt("layers");
            if(layers!=0){
                doc.put("layers",layers);
            }
        }
        if(document.containsField("direction")){
            String direction=document.getString("direction");
            doc.put("direction",direction);
        }
        /*if(document.containsField("url")){
            String url=document.getString("url");
            doc.put("url",url);
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
            queryListingInformation(doc);
        }
    }

    /**用来装那些满足成交条件的从BasicData中挑出来的数据*/
    public static DBCollection coll_BasicDataMeetDeal= db.getDB().getCollection("Deals_BasicDataMeetDeal");
    //根据成交记录找出挂牌信息
    public static void queryListingInformation(BasicDBObject obj){
        String community="";
        if(obj.containsField("community")){
            community=obj.getString("community");
        }
        int bedroomSum=0;
        if(obj.containsField("bedroomSum")){
            bedroomSum=Integer.parseInt(obj.getString("bedroomSum"));
        }
        int livingRoomSum=0;
        if(obj.containsField("livingRoomSum")){
            livingRoomSum=Integer.parseInt(obj.getString("livingRoomSum"));
        }
        int year=0;
        if(obj.containsField("year")){
            year=Integer.parseInt(obj.getString("year"));
        }
        int month=0;
        if(obj.containsField("month")){
            month=Integer.parseInt(obj.getString("month"));
        }
        int day=0;
        if(obj.containsField("day")){
            day=Integer.parseInt(obj.getString("day"));
        }
        double price;
        if(obj.containsField("price")){
            price=obj.getDouble("price");
        }
        int spaceArea=0;
        if(obj.containsField("spaceArea")){
            spaceArea=obj.getInt("spaceArea");
        }
        String direction="";
        if(obj.containsField("direction")){
            direction=obj.getString("direction");
        }
        int layers=0;
        if(obj.containsField("layers")){
            layers=Integer.parseInt(obj.getString("layers"));
        }


        BasicDBObject document=new BasicDBObject();
        if(community.length()!=0){
            document.put("community",community);
        }
        if(bedroomSum!=0){
            document.put("rooms",bedroomSum);
        }
        if(livingRoomSum!=0){
            document.put("halls",livingRoomSum);
        }
        if(spaceArea!=0){
            document.put("area",spaceArea);
        }
        if(direction.length()!=0){
            document.put("direction",direction);
        }
        if(layers!=0){
            document.put("floors",""+layers);
        }

        DBCollection coll_BasicData = db.getDB().getCollection("BasicData_Resold_100");
        DBCursor cursor = coll_BasicData.find(document);
        int year_doc;
        int month_doc;
        int day_doc;
        List<String> basicDatas=new ArrayList<>();
        while (cursor.hasNext()){
            BasicDBObject doc=(BasicDBObject)cursor.next();
            if(doc.containsField("year")&&doc.containsField("month")&&doc.containsField("day")){
                year_doc=Integer.parseInt(doc.getString("year"));
                month_doc=Integer.parseInt(doc.getString("month"));
                day_doc=Integer.parseInt(doc.getString("day"));

                if(year_doc<=year&&month_doc<=month&&day_doc<=day){
                    doc=getDocument_BasicData(doc);
                    DBCursor rls =coll_BasicDataMeetDeal.find(doc);
                    if(rls == null || rls.size() == 0){
                        //System.out.println(doc);
                        coll_BasicDataMeetDeal.insert(doc);
                        doc.remove("_id");
                        basicDatas.add(doc.toString());
                        //_id
                        //System.out.println(doc);

                    }else{
                        System.out.println("exist!");
                    }

                    //FileTool.Dump(doc.toString(),"D:\\小论文\\dealdata\\小区名\\阳光新干线\\抓取数据_"+i+".txt","utf-8");
                }
            }

        }

        JSONObject result=new JSONObject();
        if(basicDatas.size()!=0){
            result.put("dealdata",obj);
            result.put("basicdata",basicDatas);
            FileTool.Dump(result.toString(),"D:\\小论文\\dealdata\\小区名\\成交数据_挂牌数据\\成交数据_挂牌数据.txt","utf-8");
        }

    }

    //获取BasicData_Resold_100数据库中某些数据的特定字段
    public static  BasicDBObject getDocument_BasicData(BasicDBObject document){
        BasicDBObject doc=new BasicDBObject();
        if(document.containsField("community")){
            String community=document.getString("community");
            doc.put("community",community);
        }
        if(document.containsField("rooms")){
            int bedroomSum=document.getInt("rooms");
            doc.put("bedroomSum",bedroomSum);
        }
        if(document.containsField("halls")){
            int livingRoomSum=document.getInt("halls");
            doc.put("livingRoomSum",livingRoomSum);
        }
        if(document.containsField("year")){
            String year=document.getString("year");
            doc.put("year",Integer.parseInt(year));
        }
        if(document.containsField("month")){
            String month=document.getString("month");
            if(month.startsWith("0")){
                month=month.substring(1);
            }
            doc.put("month",Integer.parseInt(month));
        }
        if(document.containsField("day")){
            String day=document.getString("day");
            if(day.startsWith("0")){
                day=day.substring(1);
            }
            doc.put("day",Integer.parseInt(day));
        }
        if(document.containsField("price")){
            double price=document.getDouble("price");
            doc.put("price",price);
        }
        if(document.containsField("area")){
            double spaceArea=document.getDouble("area");
            doc.put("spaceArea",spaceArea);
        }
        if(document.containsField("source")){
            String source=document.getString("source");
            doc.put("source",source);
        }
        if(document.containsField("direction")){
            String direction=document.getString("direction");
            doc.put("direction",direction);
        }
        if(document.containsField("flooron")){
            String floor=document.getString("flooron");
            doc.put("floor",floor);
        }
        if(document.containsField("floors")){
            String floors=document.getString("floors");
            doc.put("layers",Integer.parseInt(floors));
        }
        if(document.containsField("url")){
            String url=document.getString("url");
            doc.put("url",url);
        }

        return doc;
    }

    //第二次对比房源数据。这一次对比加上了朝向、楼层等信息，确保房源能够一一对应
    public static void compareListDeal(JSONObject obj,DBCollection coll){
        BasicDBObject document=new BasicDBObject();
        Iterator<String> it=obj.keySet().iterator();
        while(it.hasNext()){
            String key=it.next();
            document.put(key,obj.get(key));
        }
        Cursor cs=coll.find(document);
        while (cs.hasNext()){
            System.out.println(cs.next());
        }
    }

    //针对BasicData_Resold_100数据进行去重
    public static void dataDuplicateRemoval(){
        DBCollection coll_BasicData = db.getDB().getCollection("BasicData_Resold_100");
        DBCollection coll_BasicData_DuplicateRemoval = db.getDB().getCollection("BasicData_Resold_100_DuplicateRemoval");
        BasicDBObject document;
        Cursor cursor=coll_BasicData.find();
        while (cursor.hasNext()){
            document=(BasicDBObject)cursor.next();
            document.remove("_id");

            DBCursor rls =coll_BasicData_DuplicateRemoval.find(document);
            if(rls == null || rls.size() == 0){
                coll_BasicData_DuplicateRemoval.insert(document);
            }else{
                System.out.println("exist!");
            }
        }
    }

    public static void findHangoutDeals(String file){
        Vector<String> pois=FileTool.Load(file,"utf-8");
        for(int i=0;i<pois.size();i++){

            String poi=pois.elementAt(i);
            JSONObject obj=JSONObject.fromObject(poi);
            JSONObject dealdata=obj.getJSONObject("dealdata");
            JSONArray basicdata=obj.getJSONArray("basicdata");
            String result="";
            int total=0;

            if(dealdata.containsKey("price")){
                int dealprice=dealdata.getInt("price");
                result+=dealprice+";";
                for(int j=0;j<basicdata.size();j++){
                    JSONObject bd=(JSONObject)basicdata.get(j);
                    int basicprice=bd.getInt("price");
                    total+=basicprice;
                }

                double avenrage=(double)total/basicdata.size();

                double ratio=0;
                ratio=Math.abs(dealprice-avenrage)/dealprice;
                if(ratio<0.3&&dealprice<avenrage){
                    result+=avenrage;
                    FileTool.Dump(result,"D:\\小论文\\dealdata\\小区名\\成交数据_挂牌数据\\成交_挂牌.txt","utf-8");
                    System.out.println(i);
                }
            }
        }
    }

}
