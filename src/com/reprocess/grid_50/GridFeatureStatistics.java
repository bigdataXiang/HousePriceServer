package com.reprocess.grid_50;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.reprocess.grid_100.PoiCode;
import com.reprocess.grid_100.util.*;
import com.svail.bean.Response;
import com.svail.db.db;
import net.sf.json.JSONArray;
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

            ergodicStatistics(condition);

            System.out.println("ok!");
        }
    }

    public Response get(String body){

        JSONObject obj= JSONObject.fromObject(body);

        String investment=obj.getString("investment");

        double west=obj.getDouble("west");
        double east=obj.getDouble("east");
        double south=obj.getDouble("south");
        double north=obj.getDouble("north");

        int zoom=obj.getInt("zoom");
        int N= Resolution.getResolution(zoom);

        //2016年01月
        String time=obj.getString("gridTime");
        int year=Integer.parseInt(time.substring(0,time.indexOf("年")));
        int month=Integer.parseInt(time.substring(time.indexOf("年")+"年".length(),time.indexOf("月")));

        String source=obj.getString("source");
        source= Source.getSource(source);

        int colmin= RowColCalculation.getColMin_50(west);
        int colmax=RowColCalculation.getColMax_50(east);
        int rowmin=RowColCalculation.getRowMin_50(south);
        int rowmax=RowColCalculation.getRowMax_50(north);

        if(colmin<0){
            colmin=0;
        }
        if(colmax>4000){
            colmax=4000;
        }
        if(rowmin<0){
            rowmin=0;
        }
        if(rowmax>4000){
            rowmax=4000;
        }

        JSONObject condition=new JSONObject();
        condition.put("investment",investment);
        condition.put("N",N);
        condition.put("rowmax",rowmax);
        condition.put("rowmin",rowmin);
        condition.put("colmax",colmax);
        condition.put("colmin",colmin);
        condition.put("year",year);
        condition.put("month",month);
        condition.put("source",source);
        condition.put("export_collName","BasicData_Resold_50");//这个表里只有十月份的数据，若要其他月份的数据还需要写成BasicData_Resold_100
        condition.put("import_collName","BasicData_Resold_50");

        Response r= new Response();
        r.setCode(200);
        r.setContent("");
        return r;
    }

    public static void getInvestment(JSONObject condition){

        callDataFromMongo(condition);

        statisticCode();

        ergodicStatistics(condition);

        System.out.println("ok!");
    }


    //注意，统计不同月份的数据的时候，全局变量要清空！！！
    public static Map<Integer,Map<String,Integer>> code_houseType_map=new HashMap<>();
    public static Map<Integer,Map<String,Integer>> code_direction_map=new HashMap<>();
    public static Map<Integer,Map<String,Integer>> code_floors_map=new HashMap<>();
    public static Map<Integer,Map<String,Integer>> code_area_map=new HashMap<>();
    public static Map<Integer,Map<String,Integer>> code_price_map=new HashMap<>();
    public static Map<Integer,Map<String,Integer>> code_unitprice_map=new HashMap<>();
    public static TreeSet<Integer> codesSet= new TreeSet<>();

    //1：将每个格网的数据（obj）存储在codelists_map中，其中key是格网code，value是装了所有房源数据的list
    public static void callDataFromMongo(JSONObject condition){
        int N=condition.getInt("N");

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

        int rowmin=condition.getInt("rowmin");
        int rowmax=condition.getInt("rowmax");
        int colmin=condition.getInt("colmin");
        int colmax=condition.getInt("colmax");

        //将小网格合并成大网格
        int r_min=(int) Math.ceil((double)rowmin/N);
        int r_max=(int) Math.ceil((double)rowmax/N);
        int c_min=(int) Math.ceil((double)colmin/N);
        int c_max=(int) Math.ceil((double)colmax/N);

        //根据大网格调用需要的小网格
        rowmin=(r_min-1)*N+1;
        rowmax=r_max*N;
        colmin=(c_min-1)*N+1;
        colmax=c_max*N;

        BasicDBObject cond=new BasicDBObject();
        cond.put("$gte",rowmin);
        cond.put("$lte",rowmax);
        document.put("row",cond);

        cond=new BasicDBObject();
        cond.put("$gte",colmin);
        cond.put("$lte",colmax);
        document.put("col",cond);

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
        String price;
        String unit_price;

        int row_doc;
        int col_doc;
        int[] result_doc;

        int count=0;
        if(cursor.hasNext()) {
            while (cursor.hasNext()){
                BasicDBObject cs = (BasicDBObject)cursor.next();
                poi=cs.toString();
                obj= JSONObject.fromObject(poi);
                obj.remove("_id");

                row_doc=obj.getInt("row");
                col_doc=obj.getInt("col");

                result_doc=RowColCalculation.codeMapping50toN50(row_doc,col_doc,N);
                row=result_doc[0];
                obj.put("row",row);
                col=result_doc[1];
                obj.put("col",col);
                code=result_doc[2];
                obj.put("code",code);
                codesSet.add(code);

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
                    boolean num= NumJudge.isNum(area);
                    if(num){
                        setAttributeMap(code,area,code_area_map);
                    }else {
                        System.out.println(code+":"+area);
                    }

                }

                if(obj.containsKey("price")){
                    price=obj.getString("price");
                    boolean num= NumJudge.isNum(price);
                    if(num){
                        setAttributeMap(code,price,code_price_map);
                    }else {
                        System.out.println(code+":"+price);
                    }
                }

                if(obj.containsKey("unit_price")){
                    unit_price=obj.getString("unit_price");
                    boolean num= NumJudge.isNum(unit_price);
                    if(num){
                        setAttributeMap(code,unit_price,code_unitprice_map);
                    }else {
                        System.out.println(code+":"+unit_price);
                    }
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
        stasticAttributeNum(code_price_map);
        stasticAttributeNum(code_unitprice_map);
    }

    //3、遍历所有网格，汇总每一个网格的统计信息
    //{"r_min":38,"r_max":64,"c_min":63,"c_max":119,"N":20,"data":[{"code":7467,"average_price":4.927035666666665,"color":"#04E738","row":38,"col":67}]}
    public static JSONObject ergodicStatistics(JSONObject condition){
        String investment=condition.getString("investment");
        int N=condition.getInt("N");

        JSONObject total=new JSONObject();
        List<JSONObject> data=new ArrayList<>();
        JSONObject obj;
        double weight_area=0;
        double weight_price=0;
        double weight_unitprice=0;
        String color="";
        double average_price=0;
        int row;
        int col;
        int[] rc=new int[2];
        int code;
        Object[] codeslist=codesSet.toArray();
        for(int i=1;i<=codeslist.length;i++){

            code=(int)codeslist[i];
            obj=new JSONObject();
            if(code_houseType_map.containsKey(code)){
                Map<String,Integer> houseType=code_houseType_map.get(code);
                JSONObject type=getAttributeJson(houseType);
                //obj.put("houseType",type);
            }

            if(code_direction_map.containsKey(code)){
                Map<String,Integer> direction=code_direction_map.get(code);
                JSONObject dir=getAttributeJson(direction);
                //obj.put("direction",dir);
            }

            if(code_floors_map.containsKey(code)){
                Map<String,Integer> floors=code_floors_map.get(code);
                JSONObject floor=getAttributeJson(floors);
                //obj.put("floors",floor);
            }

            if(code_area_map.containsKey(code)){
                Map<String,Integer> area=code_area_map.get(code);
                JSONObject ar=getAttributeJson(area);

                weight_area=getInvestmentThreshold(area);
                //obj.put("area",ar);

            }

            if(code_price_map.containsKey(code)){
                Map<String,Integer> price=code_price_map.get(code);
                JSONObject pr=getAttributeJson(price);

                weight_price=getInvestmentThreshold(price);
                //obj.put("price",pr);
            }

            if(code_unitprice_map.containsKey(code)){
                Map<String,Integer> unitprice=code_unitprice_map.get(code);
                JSONObject up=getAttributeJson(unitprice);

                weight_unitprice=getInvestmentThreshold(unitprice);
                //obj.put("unitprice",up);
            }



            if(obj.size()!=0){
                obj.put("code",code);
                if(investment.equals("总价加权")){
                    average_price=weight_price;
                }else if(investment.equals("均价加权")){
                    average_price=weight_area*weight_unitprice;
                }
                obj.put("average_price",average_price);
                color= Color.setColorRegion_Acceleration(average_price);
                obj.put("color",color);
                rc=RowColCalculation.Code_RowCol(code,N);
                row=rc[0];
                col=rc[1];
                obj.put("row",row);
                obj.put("col",col);
                data.add(obj);
                //FileTool.Dump(obj.toString(),"D:\\test\\栅格特征统计.txt","utf-8");
            }
        }

        return total;
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

    //遍历code下的子map，并将所有的值以json形式返回,这里统计的是每个属性所含有的个数
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

    public static double getInvestmentThreshold(Map<String,Integer> attribute){

        String attr;
        int num;
        int totalnum=0;
        double ratio=0;
        double weightresult=0;
        for(Map.Entry<String,Integer> entry:attribute.entrySet()){
            num=entry.getValue();
            totalnum+=num;
        }

        for(Map.Entry<String,Integer> entry:attribute.entrySet()){
            attr=entry.getKey();
            num=entry.getValue();

            if(totalnum!=0){
                ratio=(double)num/(double)totalnum;
            }

            weightresult+=ratio*Double.parseDouble(attr);
        }
        return weightresult;
    }

    //两种不同的投资门槛值的计算，一是算房源总价的加权值,二是算计算房源的均价，计算房源的均面积，再相乘得总价

}
