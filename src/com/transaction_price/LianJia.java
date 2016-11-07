package com.transaction_price;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.svail.db.db;
import net.sf.json.JSONObject;
import utils.FileTool;

import java.util.Vector;
import java.util.jar.Pack200;

/**
 * Created by ZhouXiang on 2016/11/1.
 */
public class LianJia {
    public static void main(String[] args){
        //processData("D:\\test\\lianjia\\有问题的数据.txt");
        tidyData("D:\\小论文\\dealdata\\LianJia\\处理后的成交数据.txt");
    }
    public static void processData(String file){
        Vector<String> pois= FileTool.Load(file,"utf-8");
        for(int i=0;i<pois.size();i++){
            String p=pois.elementAt(i);


                String[] poi=p.split(";");

                /*for(int ii=0;ii<poi.length;ii++){
                    System.out.println(poi[ii]);
                }*/
                JSONObject obj=new JSONObject();
                int j;
                //System.out.println(pois.elementAt(i));
            try{
                for(j=0;j<poi.length;j++){
                    if(j==0){
                        String[] str=poi[j].split(" ");
                        obj.put("community",str[0]);
                        obj.put("house_type",str[1]);
                        obj.put("area",str[2].replace("平米",""));
                        continue;
                    }else if(j==1){
                        //System.out.println(poi[j]);
                        String[] str=poi[j].replace(" ","").split("\\|");
                        /*for(int ss=0;ss<str.length;ss++){
                            System.out.println(str[ss]);
                        }*/

                        obj.put("direction",str[0]);
                        obj.put("fitment",str[1]);
                        obj.put("elevator",str[2]);
                        continue;
                    }else if(j==2){
                        obj.put("time",poi[j]);
                        continue;
                    }else if(j==3){
                        obj.put("price",poi[j]);
                        continue;
                    }else if(j==5){
                        String[] str=poi[j].split(" ");
                        if(str.length>1){
                            obj.put("floor",str[0]);
                            obj.put("built_year",str[1]);
                        }else if(str.length==1){
                            obj.put("floor",str[0]);
                        }

                        continue;
                    }else if(j==6){
                        obj.put("source",poi[j]);
                        continue;
                    }else if(j==7){
                        obj.put("unit_prcie",poi[j]);
                        continue;
                    }else if(j==9){
                        obj.put("traffic",poi[j]);
                        continue;
                    }
                }
                //System.out.println(i+":"+obj);
                FileTool.Dump(obj.toString(),"D:\\test\\lianjia\\处理后的成交数据_2.txt","utf-8");

            }catch (ArrayIndexOutOfBoundsException e){
                //FileTool.Dump(p,"D:\\test\\lianjia\\有问题的数据.txt","utf-8");
                System.out.println(e.getMessage());
            }

        }
    }

    public static void tidyData(String file){
        DBCollection coll = db.getDB().getCollection("Deals_lianjia");
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
            for(i=25239;i<pois.size();i++){
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

                if(obj.containsKey("fitment")){
                    String fitment=obj.getString("fitment");
                    document.put("fitment",fitment);
                }

                if(obj.containsKey("elevator")){
                    String elevator=obj.getString("elevator");
                    document.put("elevator",elevator);
                }

                if(obj.containsKey("time")){
                    String str=obj.getString("time");
                    String[] time=str.split("\\.");
                    String year=time[0];
                    String month=time[1];
                    String day=time[2];

                    if(month.startsWith("0")){
                        month=month.substring(1);
                    }
                    if(day.startsWith("0")){
                        day=day.substring(1);
                    }

                    document.put("contract",str.replace(".","-"));
                    document.put("year",Integer.parseInt(year));
                    document.put("month",Integer.parseInt(month));
                    document.put("day",Integer.parseInt(day));
                }


                if(obj.containsKey("price")){
                    String str=obj.getString("price").replace("万","");
                    if(str.indexOf("暂无")==-1){
                        price=Double.parseDouble(str);
                        document.put("price",price);
                    }
                }
                //document.put("dealweb","市场信息");
                if(obj.containsKey("source")){
                    String source=obj.getString("source");
                    document.put("dealweb",source);

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

                if(obj.containsKey("built_year")){
                    String str=obj.getString("built_year");
                    if(str.indexOf("年")!=-1){
                        String built_year=str.substring(0,str.indexOf("年"));
                        document.put("built_year",built_year);
                        String type=str.substring(str.indexOf("年")+"".length()).replace("建","");
                        document.put("type",type);
                    }else{
                        document.put("type",str.replace("建",""));
                    }

                }


                if(obj.containsKey("unit_prcie")){
                    String str=obj.getString("unit_prcie");
                    if(str.indexOf("暂无")==-1&&str.indexOf("房本")==-1){
                        unitPrice=Integer.parseInt(str)*0.0001;
                        document.put("unitPrice",unitPrice);
                    }
                }

                if(obj.containsKey("traffic")){
                    String info=obj.getString("traffic").replace("查看同户型成交记录","");
                    document.put("info",info);
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
            FileTool.Dump(""+i,"D:\\小论文\\dealdata\\LianJia\\NumberFormatException.txt","utf-8");
        }catch (ArrayIndexOutOfBoundsException e){
            System.out.println(e.getMessage());
            FileTool.Dump(""+i,"D:\\小论文\\dealdata\\LianJia\\ArrayIndexOutOfBoundsException.txt","utf-8");
        }


        System.out.println("共导入"+documentcount+"条数据");
    }
}
