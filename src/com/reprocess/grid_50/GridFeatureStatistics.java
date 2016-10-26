package com.reprocess.grid_50;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.reprocess.grid_100.Code_Price_RowCol;
import com.reprocess.grid_100.PoiCode;
import com.reprocess.grid_100.SchoolPoi;
import com.svail.db.db;
import net.sf.json.JSONObject;

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

            statisticGridInfor();

            statisticCode();

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
        double area;
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

                house_type=obj.getString("house_type");
                setAttributeMap(code,house_type,code_houseType_map);


                direction=obj.getString("direction");
                setAttributeMap(code,direction,code_direction_map);


                floors=obj.getString("floors");
                setAttributeMap(code,floors,code_floors_map);


                area=obj.getDouble("area");
                setAttributeMap(code,""+area,code_area_map);

                ++count;
            }
        }
        System.out.println("共有" +count+ "条数据");
    }


    //2:遍历整个houseType_codelists_map，逐个统计格网内的信息
    public static void statisticGridInfor(){
        int code;
        List<Integer> codes_list;
        JSONObject obj;

        String house_type;
        int area;
        int floors;
        String direction;
        String flooron;

        for(Map.Entry<String,List<Integer>> entry:houseType_codelists_map.entrySet()){

            house_type=entry.getKey();
            codes_list=entry.getValue();
            Map<Integer,Integer> codes_statistics=new HashMap<>();

            for(int i=0;i<codes_list.size();i++){
                code=codes_list.get(i);

                if(codes_statistics.containsKey(code)){
                    int num=codes_statistics.get(code);
                    codes_statistics.put(code,++num);

                }else {
                    codes_statistics.put(code,1);
                }

                if(code_houseType_map.containsKey(code)){
                    Map<String,Integer> houseType_num_map=code_houseType_map.get(code);
                    if(houseType_num_map.containsKey(house_type)){
                        int num=houseType_num_map.get(house_type);
                        houseType_num_map.put(house_type,++num);
                        code_houseType_map.put(code,houseType_num_map);

                    }else {
                        houseType_num_map.put(house_type,1);
                        code_houseType_map.put(code,houseType_num_map);
                    }


                }else {
                    Map<String,Integer> houseType_num_map=new HashMap<>();
                    houseType_num_map.put(house_type,1);
                    code_houseType_map.put(code,houseType_num_map);
                }
            }
        }
    }

    //3:遍历整个code_houseType_map，计算每个网格里边的户型的个数，并生成json格式数据
    public static void statisticCode(){

        int code;
        String houseType;
        int num;
        Map<String,Integer> houseType_num;

        int count=0;
        for(Map.Entry<Integer,Map<String,Integer>> entry:code_houseType_map.entrySet()){
            code=entry.getKey();
            houseType_num=entry.getValue();

            JSONObject obj=new JSONObject();
            obj.put("code",code);
            for(Map.Entry<String,Integer> entry1:houseType_num.entrySet()){
                houseType=entry1.getKey();
                num=entry1.getValue();
                obj.put(houseType,num);
                count+=num;
            }
           // System.out.println(obj);
        }
        System.out.println("共有"+count+"条户型信息");
    }

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


}
