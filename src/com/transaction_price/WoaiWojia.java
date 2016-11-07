package com.transaction_price;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.svail.db.db;
import net.sf.json.JSONObject;
import utils.FileTool;

import java.util.Vector;

/**
 * Created by ZhouXiang on 2016/11/2.
 */
public class WoaiWojia {
    public static void main(String[] args){
        //getUrls("D:\\test\\woaiwojia\\urls.txt");
        tidyData("D:\\小论文\\dealdata\\WoaiWojia\\交易记录.txt");
    }
    public static void getUrls(String file){
        Vector<String> pois= FileTool.Load(file,"utf-8");

        for(int i=0;i<pois.size();i++){
            String poi=pois.elementAt(i);
            JSONObject obj=JSONObject.fromObject(poi);
            String url=obj.getString("url");

            JSONObject result=new JSONObject();
            result.put("url",url);

            FileTool.Dump(result.toString(),"D:\\test\\woaiwojia\\处理后的urls.txt","utf-8");
        }
    }

    public static void tidyData(String file){

        DBCollection coll = db.getDB().getCollection("Deals_woaiwojia");
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
            for(i=0;i<pois.size();i++){
                String poi=pois.elementAt(i);
                JSONObject obj=JSONObject.fromObject(poi);
                document=new BasicDBObject();

                if(obj.containsKey("所在小区")){
                    community=obj.getString("所在小区");
                    document.put("community",community);
                }
                if(obj.containsKey("面积")){
                    String str=obj.getString("面积").replace("㎡","");
                    spaceArea=Double.parseDouble(str);
                    document.put("spaceArea",spaceArea);
                }
                if(obj.containsKey("成交单价")){
                    String str=obj.getString("成交单价").replace("元/平米","");
                    unitPrice=Double.parseDouble(str)*0.0001;
                    document.put("unitPrice",unitPrice);
                }
                //2室1厅11/16  西南
                if(obj.containsKey("房源信息")){
                    String str=obj.getString("房源信息");
                    if(str.indexOf("厅")!=-1){
                        houseType=str.substring(0,str.indexOf("厅")+"厅".length());
                        document.put("houseType",houseType);

                        String substr=str.substring(str.indexOf("厅")+"厅".length());
                        if(substr.indexOf(" ")!=-1){
                            String[] info=substr.split(" ");

                            if(info[0].indexOf("/")!=-1){
                                floor=info[0].substring(0,info[0].indexOf("/"));
                                layers=Integer.parseInt(info[0].substring(info[0].indexOf("/")+"/".length()));

                                document.put("floor",floor);
                                document.put("layers",layers);
                            }

                        }
                    }
                }
                if(obj.containsKey("签约日期")){
                    String str=obj.getString("签约日期");
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
                    document.put("dealweb","我爱我家");
                    document.put("contract",str);
                    document.put("year",Integer.parseInt(year));
                    document.put("month",Integer.parseInt(month));
                    document.put("day",Integer.parseInt(day));

                }
                if(obj.containsKey("成交价")){
                    String str=obj.getString("成交价").replace("万元","");
                    price=Double.parseDouble(str);
                    document.put("price",price);
                }
                if(obj.containsKey("经纪人")){
                    String agent=obj.getString("经纪人");
                    document.put("agent",agent);
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
            FileTool.Dump(""+i,"D:\\小论文\\dealdata\\WoaiWojia\\NumberFormatException.txt","utf-8");
        }catch (ArrayIndexOutOfBoundsException e){
            System.out.println(e.getMessage());
            FileTool.Dump(""+i,"D:\\小论文\\dealdata\\WoaiWojia\\ArrayIndexOutOfBoundsException.txt","utf-8");
        }

    }
}
