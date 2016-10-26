package com.reprocess.grid_50;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.reprocess.grid_100.PoiCode;
import com.svail.db.db;
import net.sf.json.JSONObject;
import utils.FileTool;

import java.util.*;

/**
 * Created by ZhouXiang on 2016/10/24.
 */
public class GridFeatureStatistics {
    public static void main(String[] args){
        for(int i=10;i<=10;i++){
            //1.选定要导出的数据的时间（月份）
            JSONObject condition=new JSONObject();
            condition.put("year","2015");
            condition.put("month",i);
            condition.put("source","woaiwojia");
            condition.put("export_collName","BasicData_Resold_100");
            condition.put("import_collName","BasicData_Resold_50");

            //2.从数据库中调出满足condition的数据,并且将每个网格的数据存入以key-value形式存入map中
            callDataFromMongo(condition);

            statisticCode();

            ergodicStatistics();

            System.out.println("ok!");
        }
    }


    //注意，统计不同月份的数据的时候，全局变量要清空！！！
    public static Map<Integer,Map<String,Integer>> code_houseType_map=new HashMap<>();
    public static Map<Integer,Map<String,Integer>> code_direction_map=new HashMap<>();
    public static Map<Integer,Map<String,Integer>> code_floors_map=new HashMap<>();
    public static Map<Integer,Map<String,Integer>> code_area_map=new HashMap<>();

    //1：将每个格网的数据（obj）存储在codelists_map中，其中key是格网code，value是装了所有房源数据的list
    public static void callDataFromMongo(JSONObject condition){
        String collName_export=condition.getString("export_collName");
        DBCollection coll_export = db.getDB().getCollection(collName_export);

        String collName_import=condition.getString("import_collName");
        DBCollection coll_import = db.getDB().getCollection(collName_import);

        BasicDBObject document = new BasicDBObject();
        Iterator<String> it=condition.keys();
        while(it.hasNext()){
            String key = it.next();
            String value=condition.getString(key);
            if(key.equals("year")||key.equals("month")||key.equals("source")){
                document.put(key,value);
            }
        }

        DBCursor cursor = coll_export.find(document);

        String poi;
        JSONObject obj;
        double lng;
        double lat;
        int code;
        int row;
        int col;

        String house_type;
        String area;
        String floors;
        String direction;
        String flooron;

        int count=0;
        if(cursor.hasNext()) {
            while (cursor.hasNext()){
                BasicDBObject cs = (BasicDBObject)cursor.next();
                poi=cs.toString();
                obj= JSONObject.fromObject(poi);
                obj.remove("_id");
                //System.out.println(obj);

                lng=obj.getDouble("lng");
                lat=obj.getDouble("lat");
                String[] result= PoiCode.setPoiCode_50(lat,lng).split(",");
                code = Integer.parseInt(result[0]);
                row=Integer.parseInt(result[1]);
                col=Integer.parseInt(result[2]);

                obj.put("code",code);
                obj.put("row",row);
                obj.put("col",col);

                //将数据存入50*50的源数据表BasicData_Resold_50中
                DBCursor rls =coll_import.find(cs);
                if(rls == null || rls.size() == 0){
                    coll_import.insert(cs);
                }else{
                    //System.out.println("该数据已经存在!");
                }

                if(obj.containsKey("house_type")){
                    house_type=obj.getString("house_type");
                    setAttributeMap(code,house_type,code_houseType_map);
                }

                if(obj.containsKey("direction")){
                    direction=obj.getString("direction");
                    setAttributeMap(code,direction,code_direction_map);
                }

                if(obj.containsKey("floors")){
                    floors=obj.getString("floors");
                    setAttributeMap(code,floors,code_floors_map);
                }

                if(obj.containsKey("area")){
                    area=obj.getString("area");
                    setAttributeMap(code,area,code_area_map);
                }

                ++count;
            }
        }
        System.out.println("共有" +count+ "条数据");
    }

    //2:遍历整个code_houseType_map，计算每个网格里边的户型的个数，并生成json格式数据
    public static void statisticCode(){
        stasticAttributeNum(code_houseType_map);
        stasticAttributeNum(code_direction_map);
        stasticAttributeNum(code_floors_map);
        stasticAttributeNum(code_area_map);
    }

    //3、遍历所有网格，汇总每一个网格的统计信息
    public static void ergodicStatistics(){
        JSONObject obj;
        for(int code=1;code<=4000*4000;code++){

            obj=new JSONObject();
            if(code_houseType_map.containsKey(code)){
                Map<String,Integer> houseType=code_houseType_map.get(code);
                JSONObject type=getAttributeJson(houseType);
                obj.put("houseType",type);
            }

            if(code_direction_map.containsKey(code)){
                Map<String,Integer> direction=code_direction_map.get(code);
                JSONObject dir=getAttributeJson(direction);
                obj.put("direction",dir);
            }

            if(code_floors_map.containsKey(code)){
                Map<String,Integer> floors=code_floors_map.get(code);
                JSONObject floor=getAttributeJson(floors);
                obj.put("floors",floor);
            }

            if(code_area_map.containsKey(code)){
                Map<String,Integer> area=code_area_map.get(code);
                JSONObject ar=getAttributeJson(area);
                obj.put("area",ar);
            }

            if(obj.size()!=0){
                obj.put("code",code);
                FileTool.Dump(obj.toString(),"D:\\test\\栅格特征统计.txt","utf-8");
            }
        }
    }

    //建立一个map，其中key为code，value是一个属性值——个数的一个子map
    public static void setAttributeMap(int code,String attribute,Map<Integer,Map<String,Integer>> map){
        if(map.containsKey(code)){

            Map<String,Integer> num_map=map.get(code);
            if(num_map.containsKey(attribute)){
                int num=num_map.get(attribute);
                num_map.put(attribute,++num);
                map.put(code,num_map);

            }else {
                num_map.put(attribute,1);
                map.put(code,num_map);
            }

        }else {
            Map<String,Integer> num_map=new HashMap<>();
            num_map.put(attribute,1);
            map.put(code,num_map);
        }
    }

    //验证所有的统计结果是否与总的数据的相符
    public static void stasticAttributeNum(Map<Integer,Map<String,Integer>> map){
        int code;
        String attribute="";
        int num;
        Map<String,Integer> attribute_num;

        int count=0;
        for(Map.Entry<Integer,Map<String,Integer>> entry:map.entrySet()){
            code=entry.getKey();
            attribute_num=entry.getValue();

            JSONObject obj=new JSONObject();
            obj.put("code",code);
            for(Map.Entry<String,Integer> entry1:attribute_num.entrySet()){
                attribute=entry1.getKey();
                num=entry1.getValue();
                obj.put(attribute,num);
                count+=num;
            }
            // System.out.println(obj);
        }
        System.out.println("共有"+count+"条"+attribute+"信息");
    }

    //遍历code下的子map，并将所有的值以json形式返回
    public static JSONObject getAttributeJson(Map<String,Integer> attribute){

        String attr;
        int num;
        JSONObject object=new JSONObject();
        for(Map.Entry<String,Integer> entry:attribute.entrySet()){
            attr=entry.getKey();
            num=entry.getValue();

            object.put(attr,num);
        }
        return object;
    }

    //两种不同的投资门槛值的计算，一是算房源总价的加权值

    //二是算计算房源的均价，计算房源的均面积，再相乘得总价

}
