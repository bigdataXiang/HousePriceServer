package com.transaction_price;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.svail.db.db;
import net.sf.json.JSONObject;
import utils.FileTool;

import java.util.Vector;

/**
 * Created by ZhouXiang on 2016/11/4.
 */
public class Fang {
    public static void main(String[] args){
        tidyData("D:\\小论文\\dealdata\\Fang\\房天下二手房成交数据1102.txt");
    }
    public static void tidyData(String file){
        DBCollection coll = db.getDB().getCollection("Deals_fang");
        BasicDBObject document;
        int bedroomSum;
        String contract;
        String floorStr;
        String floor;
        int houseType;
        int layers;
        int livingRoomSum;
        double price;
        double unitPrice=0;
        double spaceArea;
        String community;

        Vector<String> pois= FileTool.Load(file,"utf-8");
        for(int i=0;i<pois.size();i++){
            String poi=pois.elementAt(i);
            JSONObject obj=JSONObject.fromObject(poi);
            document=new BasicDBObject();

            if(obj.containsKey("title")){
                String[] infos=obj.getString("title").split(" ");
                if(infos.length==3){
                    document.put("community",infos[0]);

                    String ht=infos[1];
                    if(ht.indexOf("室")!=-1&&ht.indexOf("厅")!=-1){
                        String shi=ht.substring(0,ht.indexOf("室"));
                        String ting=ht.substring(ht.indexOf("室")+"".length(),ht.indexOf("厅"));

                        document.put("bedroomSum",Integer.parseInt(shi));
                        document.put("livingRoomSum",Integer.parseInt(ting));
                    }

                    String area=infos[2].replace("平米","");
                    document.put("spaceArea",Double.parseDouble(area));

                }else{
                    System.out.println("title数据不足三组"+i);
                }

            }

            if(obj.containsKey("time")){
                String[] time=obj.getString("time").split("-");
                String year=time[0];
                String month=time[1];
                String day=time[2];

                if(month.startsWith("0")){
                    month=month.substring(1);
                }
                if(day.startsWith("0")){
                    day=day.substring(1);
                }
                document.put("contract",obj.getString("time"));
                document.put(year,Integer.parseInt(year));
                document.put(month,Integer.parseInt(month));
                document.put(day,Integer.parseInt(day));


            }
        }

    }
}
