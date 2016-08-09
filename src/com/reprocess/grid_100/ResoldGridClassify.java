package com.reprocess.grid_100;

import com.mongodb.*;
import com.svail.db.db;
import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import utils.FileTool;
import utils.UtilFile;

import java.util.*;


/**
 * Created by ZhouXiang on 2016/8/8.
 */
public class ResoldGridClassify extends GridMerge{
    public static void main(String[] args){
        initial1();

    }
    public static void initial1(){

        for(int i=10;i<=10;i++){
            //1.选定要导出的数据的时间（月份）
            JSONObject condition=new JSONObject();
            condition.put("rowmax",592);
            condition.put("rowmin",590);
            condition.put("colmax",837);
            condition.put("colmin",835);
            condition.put("year","2015");
            condition.put("month",i);
            condition.put("source","woaiwojia");
            condition.put("export_collName","BasicData_Rentout_100");
            condition.put("import_collName","GridData_Rentout_100");

            //2.从数据库中调出满足condition的数据,并且将每个网格的数据存入以key-value形式存入map中
            Map<String,Code_Price_RowCol> map=getCodePrice(condition);

            //3.得到每个网格的均价,并将其存入数据库中
            getAvenragePrice(map,condition);

            System.out.println("ok!");
        }
    }


    /**
     * 2.从数据库中调出满足condition的数据,并存于result_array中
     * @param condition
     */
    public static Map getCodePrice(JSONObject condition){
        String collName=condition.getString("export_collName");
        DBCollection coll = db.getDB().getCollection(collName);

        BasicDBObject document = new BasicDBObject();
        Iterator<String> it=condition.keys();
        while(it.hasNext()){
            String key = it.next();
            String value=condition.getString(key);
            if(key.equals("year")||key.equals("month")||key.equals("source")){
                document.put(key,value);
            }
        }

        DBCursor cursor = coll.find(document);

        String poi="";
        JSONObject obj;
        JSONObject result;
        double unit_price=0;
        String code ;
        int row;
        int col;
        List<Double> pricelist;
        Code_Price_RowCol cpr= new Code_Price_RowCol();
        Map<String,Code_Price_RowCol> map=new HashMap<>();

        if(cursor.hasNext()) {
            while (cursor.hasNext()) {
                poi=cursor.next().toString();
                obj=JSONObject.fromObject(poi);

                code = obj.getString("code");
                row=obj.getInt("row");
                col=obj.getInt("col");
                unit_price=getUnitPrice(obj);

                if (map.containsKey(code)) {

                    cpr = map.get(code);
                    pricelist=cpr.getPricelist();
                    pricelist.add(unit_price);
                    cpr.setPricelist(pricelist);
                    map.put(code,cpr);
                }else{
                    pricelist = new ArrayList<Double>();
                    pricelist.add(unit_price);

                    cpr=new Code_Price_RowCol();
                    cpr.setCode(code);
                    cpr.setCol(col);
                    cpr.setRow(row);
                    cpr.setPricelist(pricelist);
                    map.put(code,cpr);


                }
            }
        }
        System.out.println("共有" + map.size() + "个网格有数据");
        return map;
    }

    /**
     * 3.求每个网格的房价平均值
     * @return
     */
    public static void getAvenragePrice(Map<String,Code_Price_RowCol> map,JSONObject condition) {

        //遍历codeprice中的元素，求每个网格的价格均值
        Iterator codekeys = map.keySet().iterator();
        List<Double> pricelist;
        BasicDBObject code_averagePrice = new BasicDBObject();
        Code_Price_RowCol cpr;
        String code;
        int row;
        int col;
        int importcount=0;

        String collName=condition.getString("import_collName");
        DBCollection coll = db.getDB().getCollection(collName);


        try{
            while (codekeys.hasNext()) {
                code = (String) codekeys.next();

                cpr=map.get(code);
                /*String code1=cpr.getCode();
                System.out.println(code+"  "+code1);*/
                pricelist = cpr.getPricelist();
                double totalprice = 0;
                double average_price = 0;

                if (pricelist.size() != 0) {
                    int count = 0;//统计pricelist中均价不为0的数目
                    for (int i = 0; i < pricelist.size(); i++) {
                        double price = pricelist.get(i);
                        if (price != 0) {
                            totalprice += price;
                            count++;
                        }

                    }
                    if(count!=0){
                        average_price = totalprice / count;
                    }else{
                        average_price=0;
                    }

                }else{
                    average_price=0;
                }
                code_averagePrice.put("code", code);
                code_averagePrice.put("average_price", average_price);
                String color=setColorRegion(average_price);
                code_averagePrice.put("color",color);

                row=cpr.getRow();
                col=cpr.getCol();
                code_averagePrice.put("row",row);
                code_averagePrice.put("col",col);

                String year=condition.getString("year");
                String month=condition.getString("month");
                String source=condition.getString("source");
                code_averagePrice.put("year",year);
                code_averagePrice.put("month",month);
                code_averagePrice.put("source",source);

                DBCursor rls =coll.find(code_averagePrice);
                if(rls == null || rls.size() == 0){
                    coll.insert(code_averagePrice);
                    importcount++;
                }else{
                    System.out.println("该数据已经存在!");
                }
                code_averagePrice.clear();
            }
            System.out.println("共导入"+importcount+"条数据");
        }catch (JSONException e){
            System.out.println(e.getMessage());
            e.printStackTrace();
        }catch (MongoException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }catch (NullPointerException e) {
            // TODO Auto-generated catch block
            System.out.println("发生异常的原因为 :"+e.getMessage());
            e.printStackTrace();
        }
    }


    public static String setColorRegion(double price){
        String color="";

        if(price>10){
            color="#FF0000";
        }else if(price>9&&price<=10){
            color="#FF0D0D";
        }else if(price>8&&price<=9){
            color="#FF1919";
        }else if(price>7&&price<=8){
            color="#FF2626";
        }else if(price>6&&price<=7){
            color="#FF3333";
        }else if(price>5&&price<=6){
            color="#FF4040";
        }else if(price>4&&price<=5){
            color="#FF4D4D";
        }else if(price>3&&price<=4){
            color="#FF6666";
        }else if(price>2&&price<=3){
            color="#FF8080";
        }else if(price>1&&price<=2){
            color="#FF9999";
        }else{
            color="#FFB3B3";
        }
        return color;
    }
    /**
     * 4.给有房价值的每个网格的均价赋予一个颜色值
     * @param array
     */









    public static String  setColor(JSONArray array){
        JSONObject backdata=new JSONObject();
        JSONArray finalresult=new JSONArray();

        System.out.println("开始计算每个网格的颜色：");
        String color="";
        for (int i=0;i<array.size();i++){
            JSONObject obj= (JSONObject) array.get(i);
            double price=obj.getDouble("average_price");

            color=setColorRegion(price);
            obj.put("color",color);

            finalresult.add(obj);
        }
        backdata.put("data",finalresult);
        return backdata.toString();
    }
    public static List<JSONObject> result_array=new ArrayList<JSONObject>();
    public static ArrayList<Code_Price_RowCol> codes = new ArrayList<Code_Price_RowCol>();
    public static void addCode(Code_Price_RowCol c) {
        codes.add(c);
    }
    public static void importToMongo(JSONObject condition,List<BasicDBObject> dbList){
        String collName=condition.getString("import_collName");
        DBCollection coll = db.getDB().getCollection(collName);

        try {
            if(dbList.size()!=0){
                int count=0;
                for(int i=0;i<dbList.size();i++){
                    BasicDBObject obj=dbList.get(i);

                    DBCursor rls =coll.find(obj);

                    if(rls == null || rls.size() == 0){
                        coll.insert(obj);
                        count++;
                    }else{
                        System.out.println("该数据已经存在!");
                    }
                }
                System.out.println("共导入"+count+"条数据");
            }

        }catch (MongoException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }catch (NullPointerException e) {
            // TODO Auto-generated catch block
            System.out.println("发生异常的原因为 :"+e.getMessage());
            e.printStackTrace();
        }

    }

    /**
     * 5.填充房价值为空的网格的颜色，并对所有网格进行排序
     * @param data
     * @return
     */
    public static void FilledGridData(String data,JSONObject condition){
        System.out.println("开始填充值为空的网格的颜色：");
        String str="";
        JSONObject data_obj=JSONObject.fromObject(data);
        JSONArray data_array=data_obj.getJSONArray("data");

        List<BasicDBObject> list = new ArrayList<BasicDBObject>(); //对时间进行排序的list
        BasicDBObject obj=new BasicDBObject();
        JSONObject codekey=new JSONObject();
        for(int i=0;i<data_array.size();i++){
            obj= (BasicDBObject) data_array.get(i);
            list.add(obj);

            String code=obj.get("code").toString();
            codekey.put(code,"");
        }


        String year=condition.getString("year");
        String month=condition.getString("month");
        String source=condition.getString("source");
        for(int row=1;row<=2000;row++){
            for(int col=1;col<=2000;col++){
                String codeindex=""+(col + 2000 * (row - 1));
                if(!codekey.containsKey(codeindex)){
                    obj.clear();
                    obj.put("code",codeindex);
                    obj.put("average_price",0);
                    obj.put("color","#BFBFBF");
                    obj.put("row",row);
                    obj.put("col",col);
                    obj.put("time","");
                    obj.put("year",year);
                    obj.put("month",month);
                    obj.put("source",source);
                    list.add(obj);
                    obj.clear();
                }
            }
        }

        Collections.sort(list, new UtilFile.CodeComparator()); // 根据网格code排序
        importToMongo(condition,list);
    }
    /**
     * 获取obj中的unitprice
     * @param obj
     * @return
     */
    public static double getUnitPrice(JSONObject obj){
        double unit_price=0;
        if(obj.containsKey("unit_price")){
            String temp=obj.getString("unit_price").replace("元","");
            if(temp.length()!=0){
                unit_price=Double.parseDouble(temp);
            }else {
                unit_price=0;
            }

        }else if(obj.containsKey("price")&&obj.containsKey("area")){
            double area = obj.getDouble("area");
            double price= obj.getDouble("price");

            if(area!=0){
                unit_price=price/area;
            }else{
                unit_price=0;
            }

        }
        return unit_price;
    }
     /**
     * 比较两个poi中对应的两个code的大小，并对这两个poi进行排序
     */
    static class CodeComparator implements Comparator {
        public int compare(Object object1, Object object2) {

            JSONObject obj1=JSONObject.fromObject(object1);
            JSONObject obj2=JSONObject.fromObject(object2);

            int code1=obj1.getInt("code");
            int code2=obj2.getInt("code");

            int flag = new Integer(code1).compareTo(new Integer(code2));
            return flag;
        }
    }
}
