package com.reprocess.grid_50;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.reprocess.grid_100.util.Source;
import com.svail.bean.Response;
import com.svail.db.db;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.*;

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
        System.out.println(GridAttributeSummary());
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
        String source=condition.getString("source");
        source= Source.getSource(source);
        String time=condition.getString("gridTime");
        int year=Integer.parseInt(time.substring(0,time.indexOf("年")));
        int month=Integer.parseInt(time.substring(time.indexOf("年")+"年".length(),time.indexOf("月")));

        condition.put("year",""+year);
        condition.put("month",""+month);
        condition.put("source",source);
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

        document = new BasicDBObject();

        BasicDBObject cond=new BasicDBObject();
        cond.put("$gte",row_50);
        cond.put("$lte",row_50+N);
        document.put("row",cond);

        cond=new BasicDBObject();
        cond.put("$gte",col_50);
        cond.put("$lte",col_50 + N);
        document.put("col",cond);

        //document.put("year",year);
        //document.put("month",month);
        document.put("source",source);

        List array=coll_export.find(document).toArray();
        for(int i=0;i<array.size();i++){

            poi=array.get(i).toString();//array.size()的值为1
            JSONObject obj=JSONObject.fromObject(poi);
            obj.remove("_id");
            String y=obj.getString("year");
            String m=obj.getString("month");


            if(obj.containsKey("houseType")){
                if(y.equals(year)&&m.equals(month)){
                    String house_type=obj.getString("houseType");
                    setMap(house_type,houseType_map);
                }
            }

            if(obj.containsKey("direction")){
                if(y.equals(year)&&m.equals(month)){
                    String direction=obj.getString("direction");
                    setMap(direction,direction_map);
                }
            }

            if(obj.containsKey("floors")){
                if(y.equals(year)&&m.equals(month)){
                    String floors=obj.getString("floors");
                    setMap(floors,floors_map);
                }
            }

            if(obj.containsKey("flooron")){
                if(y.equals(year)&&m.equals(month)){
                    String flooron=obj.getString("flooron");
                    setMap(flooron,flooron_map);
                }
            }

            if(obj.containsKey("area")){
                if(y.equals(year)&&m.equals(month)){
                    String area=obj.getString("area");
                    setMap(area,area_map);
                }
            }

            if(obj.containsKey("price")){
                if(y.equals(year)&&m.equals(month)){
                    String price=obj.getString("price");
                    setMap(price,price_map);
                }
            }

            if(obj.containsKey("unitprice")){
                if(y.equals(year)&&m.equals(month)){
                    String unitprice=obj.getString("unitprice");
                    setMap(unitprice,unitprice_map);
                }
            }

        }
    }

    //2.大网格内的属性汇总，返回结果如下：
    //{"house_type":"4室2厅3卫,3;","direction":"南北,3;","floors":"3,3;","flooron":"下部,3;","area":"373.0,3;","price":"1800.0,3;","unitprice":"4.8257375,3;"}
    public static String GridAttributeSummary(){
        JSONObject obj=new JSONObject();

        String house_type=ergodicMap(houseType_map);
        //List<JSONObject> house_type=ergodicMap(houseType_map);
        obj.put("house_type",house_type);
        String direction=ergodicMap(direction_map);
        //List<JSONObject> direction=ergodicMap(direction_map);
        obj.put("direction",direction);
        String floors=ergodicMap(floors_map);
        //List<JSONObject> floors=ergodicMap(floors_map);
        obj.put("floors",floors);
        String flooron=ergodicMap(flooron_map);
        //List<JSONObject> flooron=ergodicMap(flooron_map);
        obj.put("flooron",flooron);
        String area=ergodicMap(area_map);
        //List<JSONObject> area=ergodicMap(area_map);
        obj.put("area",area);
        String price=ergodicMap(price_map);
        //List<JSONObject> price=ergodicMap(price_map);
        obj.put("price",price);
        String unitprice=ergodicMap(unitprice_map);
        //List<JSONObject> unitprice=ergodicMap(unitprice_map);
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

    /**遍历map,统计map中的各个特征的值*/
    public static String ergodicMap(Map<String,Integer> map){
        List<JSONObject> list=new ArrayList<>();
        String str="";
        for(Map.Entry<String,Integer> entry:map.entrySet()){

            String type=entry.getKey();
            int num=entry.getValue();
            str+=type+","+num+";";
        }
        return str;
    }

    /**计算网格内房子的首付情况:只考虑普通住宅情况*/
    //二套房首付比率50%(去年四成的执行率低)
    //首套首付比率30%（2016年9月30日之前），二套房首付比率35%（2016年9月30日之后）
    //契税：首套1%，二套3%
    //评估价和网签价相当，大概在成交价的80%到90%之间，这里按照80%来计算
    public static double calculationDownPayment(double area,String type,int year,int month,double price){
        double deedTax=deedTaxCalculation(area,type,year, month);
        double serviceCharge=0.027;
        double netSigned=0.8;
        double loan=loanCalculation(type,year,month);

        double totalPrice=price*(1+deedTax+serviceCharge)-netSigned*price*loan;
        return totalPrice;
    }

    /**分情况讨论2016，930新政之前和之后的契税问题*/
    public static double deedTaxCalculation(double area,String type,int year,int month){
        double deedTax=0;
        if(type.equals("first")){
            if(year==2016&&month>=10){
                if(area<=90){
                    deedTax=0.01;
                }else {
                    deedTax=0.015;
                }
            }else{
                if(area<=90){
                    deedTax=0.01;
                }else if(area>90&&area<=144){
                    deedTax=0.015;
                }else {
                    deedTax=0.03;
                }
            }

        }else if(type.equals("second")){
            deedTax=0.03;
        }
        return deedTax;
    }

    /**分情况讨论2016，930新政之后的首付比率问题*/
    public static double loanCalculation(String type,int year,int month){
        double loan=0;
        if(type.equals("first")){
            if(year==2016&&month>=10){
                loan=1-0.35;
            }else {
                loan=1-0.3;
            }
        }else if(type.equals("second")){
            loan=1-0.5;
        }
        return loan;
    }

}
