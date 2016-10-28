package com.reprocess.grid_50;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.svail.bean.Response;
import com.svail.db.db;
import net.sf.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.reprocess.grid_50.GridFeatureStatistics.setAttributeMap;

/**
 * Created by ZhouXiang on 2016/10/27.
 */
public class CallGridFeature {
    public static void main(String[] args){
        JSONObject condition= new JSONObject();
        condition.put("row",65);
        condition.put("col",87);
        condition.put("code",12887);
        condition.put("year","2015");
        condition.put("month","10");
        condition.put("source","woaiwojia");
        condition.put("N",20);
        condition.put("export_collName","GridData_Resold_Investment_50");

        callIntesetGridInfo(condition);
        GridAttributeSummary();
    }
    public Response get(String body){

        houseType_map=new HashMap<>();
        direction_map=new HashMap<>();
        floors_map=new HashMap<>();
        area_map=new HashMap<>();
        price_map=new HashMap<>();
        unitprice_map=new HashMap<>();
        flooron_map=new HashMap<>();

        ///gridinfo	{"row":63,"col":92,"code":12492,"N":20}
        JSONObject condition= JSONObject.fromObject(body);
        condition.put("year","2015");
        condition.put("month","10");
        condition.put("source","woaiwojia");
        condition.put("export_collName","GridData_Resold_Investment_50");

        callIntesetGridInfo(condition);
        String str=GridAttributeSummary();
        System.out.println(str);
        Response r= new Response();
        r.setCode(200);
        r.setContent(str);
        return r;
    }

    //注意，统计不同月份的数据的时候，全局变量要清空！！！
    public static Map<String,Integer> houseType_map=new HashMap<>();
    public static Map<String,Integer> direction_map=new HashMap<>();
    public static Map<String,Integer> floors_map=new HashMap<>();
    public static Map<String,Integer> area_map=new HashMap<>();
    public static Map<String,Integer> price_map=new HashMap<>();
    public static Map<String,Integer> unitprice_map=new HashMap<>();
    public static Map<String,Integer> flooron_map=new HashMap<>();

    //1.将所有位于大网格范围内的小网格搜集起来
    public static void callIntesetGridInfo(JSONObject condition){
        int N=condition.getInt("N");
        String source=condition.getString("source");
        String year=condition.getString("year");
        String month=condition.getString("month");

        String collName_export=condition.getString("export_collName");
        DBCollection coll_export = db.getDB().getCollection(collName_export);

        BasicDBObject document;
        int row=condition.getInt("row");
        int col=condition.getInt("col");
        int code=condition.getInt("code");

        int row_50=(row-1)*N+1;
        int col_50=(col-1)*N+1;
        int code_50;

        String poi="";
        int count=0;

        for(int i=row_50;i<=row_50+N;i++) {
            for (int j = col_50; j <= col_50 + N; j++) {
                code_50=j+4000*(i-1);
                document = new BasicDBObject();
                document.put("code",code_50);
                document.put("row",i);
                document.put("col",j);
                document.put("year",year);
                document.put("month",month);
                document.put("source",source);

                List array=coll_export.find(document).toArray();
                if(array.size()!=0){
                    poi=array.get(0).toString();
                    JSONObject obj=JSONObject.fromObject(poi);
                    obj.remove("_id");

                    if(obj.containsKey("houseType")){
                        String house_type=obj.getString("houseType");
                        setMap(house_type,houseType_map);
                    }

                    if(obj.containsKey("direction")){
                        String direction=obj.getString("direction");
                        setMap(direction,direction_map);
                    }

                    if(obj.containsKey("floors")){
                        String floors=obj.getString("floors");
                        setMap(floors,floors_map);
                    }

                    if(obj.containsKey("flooron")){
                        String flooron=obj.getString("flooron");
                        setMap(flooron,flooron_map);
                    }

                    if(obj.containsKey("area")){
                        String area=obj.getString("area");
                        setMap(area,area_map);
                    }

                    if(obj.containsKey("price")){
                        String price=obj.getString("price");
                        setMap(price,price_map);
                    }

                    if(obj.containsKey("unitprice")){
                        String unitprice=obj.getString("unitprice");
                        setMap(unitprice,unitprice_map);
                    }
                }
            }
        }
    }

    //2.大网格内的属性汇总
    public static String GridAttributeSummary(){
        JSONObject obj=new JSONObject();

        String house_type=ergodicMap(houseType_map);
        obj.put("house_type",house_type);
        String direction=ergodicMap(direction_map);
        obj.put("direction",direction);
        String floors=ergodicMap(floors_map);
        obj.put("floors",floors);
        String flooron=ergodicMap(flooron_map);
        obj.put("flooron",flooron);
        String area=ergodicMap(area_map);
        obj.put("area",area);
        String price=ergodicMap(price_map);
        obj.put("price",price);
        String unitprice=ergodicMap(unitprice_map);
        obj.put("unitprice",unitprice);

        return obj.toString();
    }

    public static void setMap(String attribute,Map<String,Integer> num_map){

        String[] results=attribute.split(";");
        for(int i=0;i<results.length;i++){
            String[] type=results[i].split(",");
            String attr=type[0];
            int num=Integer.parseInt(type[1]);
            if(num_map.containsKey(attr)){
                int temp=num_map.get(attr);
                num=num+temp;
                num_map.put(attr,num);
            }else{
                num_map.put(attr,num);
            }
        }
    }

    public static String ergodicMap(Map<String,Integer> map){

        String str="";
        for(Map.Entry<String,Integer> entry:map.entrySet()){

            String type=entry.getKey();
            int num=entry.getValue();
            str+=type+","+num+";";
        }
        return str;
    }
}
