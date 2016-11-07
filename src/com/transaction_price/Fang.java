package com.transaction_price;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.svail.db.db;
import net.sf.json.JSONObject;
import utils.FileTool;

import java.util.Vector;

/**
 * Created by ZhouXiang on 2016/11/4.
 */
public class Fang {
    public static void main(String[] args){
        tidyData_shichang("D:\\小论文\\dealdata\\Fang\\房天下二手房成交数据1102_市场信息.txt");
    }
    public static void tidyData_fang(String file){
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

        int documentcount=0;
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
                        String ting=ht.substring(ht.indexOf("室")+"室".length(),ht.indexOf("厅"));

                        document.put("bedroomSum",Integer.parseInt(shi));
                        document.put("livingRoomSum",Integer.parseInt(ting));
                        document.put("houseType",ht);
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
                document.put("year",Integer.parseInt(year));
                document.put("month",Integer.parseInt(month));
                document.put("day",Integer.parseInt(day));
            }

            if(obj.containsKey("dealweb")){
                String dealweb=obj.getString("dealweb");
                document.put("dealweb",dealweb);
            }

            if(obj.containsKey("url")){
                String url=obj.getString("url");
                document.put("url",url);
            }

            if(obj.containsKey("price")){
                String str=obj.getString("price").replace("万","");
                price=Double.parseDouble(str);
                document.put("price",price);
            }

            if(obj.containsKey("unitprice")){
                String str=obj.getString("unitprice").replace("元","");
                unitPrice=Integer.parseInt(str)*0.0001;
                document.put("unitPrice",unitPrice);
            }

            if(obj.containsKey("direction")){
                String direction=obj.getString("direction");
                document.put("direction",direction);

            }

            if(obj.containsKey("area")){
                spaceArea=obj.getDouble("area");
                document.put("spaceArea",spaceArea);
            }

            if(obj.containsKey("floor")){
                floorStr=obj.getString("floor");
                if(floorStr.indexOf("（")!=-1){
                    String[] str=floorStr.split("（");
                    floor=str[0];
                    layers=Integer.parseInt(str[1].replace("共","").replace("层","").replace("）",""));

                    document.put("floor",floor);
                    document.put("layers",layers);
                }
            }

            if(obj.containsKey("community")){
                String str=obj.getString("community");
                if(str.indexOf("(")!=-1&&str.indexOf(")")!=-1){
                    community=str.substring(0,str.indexOf("("));
                    String location=str.substring(str.indexOf("(")+"(".length(),str.indexOf(")"));

                    //document.put("community",community);
                    document.put("location",location);
                }

            }

            DBCursor rls =coll.find(document);
            if(rls == null || rls.size() == 0){
                documentcount++;
                coll.insert(document);
            }else{
                System.out.println("该数据已经存在!");
            }
        }

        System.out.println("共导入"+documentcount+"条数据");
    }
    public static void tidyData_shichang(String file){
        DBCollection coll = db.getDB().getCollection("Deals_fang");
        BasicDBObject document;
        int bedroomSum;
        String contract;
        String floorStr;
        String floor;
        String houseType;
        int layers;
        int livingRoomSum;
        double price;
        double unitPrice=0;
        double spaceArea;
        String community;

        int documentcount=0;
        Vector<String> pois= FileTool.Load(file,"utf-8");
        int i=0;
        try{
            for(i=122090;i<pois.size();i++){
                String poi=pois.elementAt(i);
                JSONObject obj=JSONObject.fromObject(poi);
                document=new BasicDBObject();

                if(obj.containsKey("community")){
                    community=obj.getString("community");
                    document.put("community",community);
                }

                if(obj.containsKey("house_type")){
                    houseType=obj.getString("house_type");
                    document.put("houseType",houseType);

                    if(houseType.indexOf("室")!=-1&&houseType.indexOf("厅")!=-1){
                        String shi=houseType.substring(0,houseType.indexOf("室"));
                        String ting=houseType.substring(houseType.indexOf("室")+"室".length(),houseType.indexOf("厅"));

                        document.put("bedroomSum",Integer.parseInt(shi));
                        document.put("livingRoomSum",Integer.parseInt(ting));
                    }
                }

                if(obj.containsKey("area")){
                    spaceArea=obj.getDouble("area");
                    document.put("spaceArea",spaceArea);
                }

                if(obj.containsKey("direction")){
                    String direction=obj.getString("direction").replace("向","");
                    document.put("direction",direction);

                }

                if(obj.containsKey("floor")){
                    floorStr=obj.getString("floor");
                    if(floorStr.indexOf("（")!=-1){
                        String[] str=floorStr.split("（");
                        floor=str[0];
                        layers=Integer.parseInt(str[1].replace("共","").replace("层","").replace("）",""));

                        document.put("floor",floor);
                        document.put("layers",layers);
                    }
                }

                if(obj.containsKey("location")){
                    String location=obj.getString("location");
                    document.put("location",location);
                }

                if(obj.containsKey("time")){
                    String str=obj.getString("time").replace("市场信息","");
                    String[] time=str.split("-");
                    String year=time[0];
                    String month=time[1];
                    String day=time[2];

                    if(month.startsWith("0")){
                        month=month.substring(1);
                    }
                    if(day.startsWith("0")){
                        day=day.substring(1);
                    }
                    document.put("dealweb","市场信息");
                    document.put("contract",str);
                    document.put("year",Integer.parseInt(year));
                    document.put("month",Integer.parseInt(month));
                    document.put("day",Integer.parseInt(day));
                }

                if(obj.containsKey("price")){
                    String str=obj.getString("price").replace("万","");
                    price=Double.parseDouble(str);
                    document.put("price",price);
                }

                if(obj.containsKey("unit_price")){
                    String str=obj.getString("unit_price").replace("/�O","");
                    unitPrice=Integer.parseInt(str)*0.0001;
                    document.put("unitPrice",unitPrice);
                }


                DBCursor rls =coll.find(document);
                if(rls == null || rls.size() == 0){
                    documentcount++;
                    coll.insert(document);
                }else{
                    System.out.println("该数据已经存在!");
                }
            }

        }catch (NumberFormatException e){
            System.out.println(e.getMessage());
            FileTool.Dump(""+i,"D:\\小论文\\dealdata\\Fang\\异常数据的序号.txt","utf-8");
        }


        System.out.println("共导入"+documentcount+"条数据");
    }


}
