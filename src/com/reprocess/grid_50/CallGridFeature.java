package com.reprocess.grid_50;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.reprocess.grid_100.util.Source;
import com.svail.bean.Response;
import com.svail.db.db;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import utils.UtilFile;

import java.util.*;

import static com.reprocess.grid_50.GridFeatureStatistics.setAttributeMap;

/**
 * Created by ZhouXiang on 2016/10/27.
 */
public class CallGridFeature {

    public static void main(String[] args){
        JSONObject condition= new JSONObject();

        //{"row":1062,"col":1720,"code":4245720,"N":1,"gridTime":"2015年10月","source":"我爱我家"}
        condition.put("row",160);
        condition.put("col",1516);
        condition.put("code",637516);
        condition.put("year","2015");
        condition.put("month","10");
        condition.put("source","woaiwojia");
        condition.put("N",1);
        condition.put("export_collName","GridData_Resold_Investment_50");//GridData_Resold_FeatureStatistics_50
        //GridData_Resold_Investment_50：不含"type"值的单元格网数据
        //GridData_Resold_FeatureStatistics_50:含有"type"值的单元格网数据，该数据统计了基本格网内每个户型的加权面积、价格和所占比例
        condition.put("BasicPoi_50","BasicData_Resold_50");

        JSONObject types=callIntesetGridInfo(condition);
        types=timeSort_type(types);//按照日期进行排序
        JSONObject result=GridAttributeSummary();
        result.put("type",types);

        Map<String,Map<Double,Double>> price_featureStatistics=dataFusion(time_price);
        JSONObject price=ergodicDataFusionMap(price_featureStatistics);

        Map<String,Map<Double,Double>> area_featureStatistics=dataFusion(time_area);
        JSONObject unitprice=ergodicDataFusionMap(area_featureStatistics);

        Map<String,Map<Double,Double>> unitprice_featureStatistics=dataFusion(time_unitprice);
        JSONObject area=ergodicDataFusionMap(unitprice_featureStatistics);

        JSONObject anverW=averageWeighted(unitprice,area);

        JSONObject obj=new JSONObject();

        JSONObject object=new JSONObject();
        object.put("total",price);
        String type="first";
        JSONObject first=downPayments(area,price,type);
        object.put("first",first);
        type="second";
        JSONObject second=downPayments(area,price,type);
        object.put("second",second);
        obj.put("price_weight",object);


        object=new JSONObject();
        object.put("total",anverW);
        type="first";
        first=downPayments(area,anverW,type);
        object.put("first",first);
        type="second";
        second=downPayments(area,anverW,type);
        object.put("second",second);
        obj.put("unitprice_weight",object);

        //将曲线的数据加到result中去
        result.put("curve",obj);

        //尝试用基本单元格的信息进行户型统计
        JSONObject dt=date_houseTypeListStatistic(date_type);
        result.put("types",dt);


        System.out.println(result);
    }
    public Response get(String body){

        houseType_map=new HashMap<>();
        direction_map=new HashMap<>();
        floors_map=new HashMap<>();
        area_map=new HashMap<>();
        price_map=new HashMap<>();
        unitprice_map=new HashMap<>();
        flooron_map=new HashMap<>();

        time_price=new HashMap<>();//用于存放每月对应的价格统计信息
        time_area=new HashMap<>();//用于存放每月对应的面积统计信息
        time_unitprice=new HashMap<>();//用于存放每月对应的均价统计信息

        houseType_list=new HashMap<>();


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
        JSONObject result=GridAttributeSummary();

        Map<String,Map<Double,Double>> price_featureStatistics=dataFusion(time_price);
        JSONObject price=ergodicDataFusionMap(price_featureStatistics);
        //System.out.println(price);

        Map<String,Map<Double,Double>> area_featureStatistics=dataFusion(time_area);
        JSONObject unitprice=ergodicDataFusionMap(area_featureStatistics);
        //System.out.println(unitprice);

        Map<String,Map<Double,Double>> unitprice_featureStatistics=dataFusion(time_unitprice);
        JSONObject area=ergodicDataFusionMap(unitprice_featureStatistics);
        //System.out.println(area);

        JSONObject anverW=averageWeighted(unitprice,area);
       // System.out.println(anverW);

        JSONObject obj=new JSONObject();

        JSONObject object=new JSONObject();
        object.put("total",price);
        String type="first";
        JSONObject first=downPayments(area,price,type);
        object.put("first",first);
        type="second";
        JSONObject second=downPayments(area,price,type);
        object.put("second",second);
        obj.put("price_weight",object);


        object=new JSONObject();
        object.put("total",anverW);
        type="first";
        first=downPayments(area,anverW,type);
        object.put("first",first);
        type="second";
        second=downPayments(area,anverW,type);
        object.put("second",second);
        obj.put("unitprice_weight",object);

        //将曲线的数据加到result中去
        result.put("curve",obj);

        System.out.println(result);
        Response r= new Response();
        r.setCode(200);
        r.setContent(result.toString());
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

    public static Map<String,List<String>> time_price=new HashMap<>();//用于存放每月对应的价格统计信息
    public static Map<String,List<String>> time_area=new HashMap<>();//用于存放每月对应的面积统计信息
    public static Map<String,List<String>> time_unitprice=new HashMap<>();//用于存放每月对应的均价统计信息

    public static Map<String,List<JSONObject>> houseType_list=new HashMap<>();//用于统计大格网中的户型特征
    public static Map<String,List<JSONObject>> date_type=new HashMap<>();//用于统计大格网中的不同月份的户型加权统计信息

    //1.将所有位于大网格范围内的小网格搜集起来
    public static JSONObject callIntesetGridInfo(JSONObject condition){
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

        String poi="";

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

        List<String> vaule_time_price;
        List<String> vaule_time_area;
        List<String> vaule_time_unitprice;

        JSONObject types=dataFromBasicPoi_50(row_50,row_50+N,col_50,col_50+N,source);


        List array=coll_export.find(document).toArray();
        for(int i=0;i<array.size();i++){

            poi=array.get(i).toString();
            JSONObject obj=JSONObject.fromObject(poi);
            obj.remove("_id");

            //System.out.println(obj);

            String y=obj.getString("year");
            String m=obj.getString("month");
            String date=y+"-"+m;


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
                String area=obj.getString("area");

                if(y.equals(year)&&m.equals(month)){
                    setMap(area,area_map);
                }

                if(time_area.containsKey(date)){
                    vaule_time_area=time_area.get(date);
                    vaule_time_area.add(area);
                    time_area.put(date,vaule_time_area);

                }else {
                    vaule_time_area=new ArrayList<>();
                    vaule_time_area.add(area);
                    time_area.put(date,vaule_time_area);
                }
            }

            if(obj.containsKey("price")){
                String price=obj.getString("price");
                if(y.equals(year)&&m.equals(month)){
                    setMap(price,price_map);
                }

                if(time_price.containsKey(date)){
                    vaule_time_price=time_price.get(date);
                    vaule_time_price.add(price);
                    time_price.put(date,vaule_time_price);

                }else {
                    vaule_time_price=new ArrayList<>();
                    vaule_time_price.add(price);
                    time_price.put(date,vaule_time_price);
                }
            }

            if(obj.containsKey("unitprice")){
                String unitprice=obj.getString("unitprice");
                if(y.equals(year)&&m.equals(month)){
                    setMap(unitprice,unitprice_map);
                }

                if(time_unitprice.containsKey(date)){
                    vaule_time_unitprice=time_unitprice.get(date);
                    vaule_time_unitprice.add(unitprice);
                    time_unitprice.put(date,vaule_time_unitprice);

                }else {
                    vaule_time_unitprice=new ArrayList<>();
                    vaule_time_unitprice.add(unitprice);
                    time_unitprice.put(date,vaule_time_unitprice);
                }
            }

            //新增了"type"数据栏，该key对应的value是基本格网的户型统计信息

            //date_type的key是日期，value是每个月该大网格下所有小网格的户型数据的集合
            if(obj.containsKey("type")){
                JSONObject type=obj.getJSONObject("type");
                if(date_type.containsKey(date)) {
                    List<JSONObject> type_list=date_type.get(date);
                    type_list.add(type);
                    date_type.put(date,type_list);
                }else {
                    List<JSONObject> type_list=new ArrayList<>();
                    type_list.add(type);
                    date_type.put(date,type_list);
                }
            }
        }

        return types;
    }

    //2.大网格内的属性汇总，返回结果如下：
    //{"house_type":"4室2厅3卫,3;","direction":"南北,3;","floors":"3,3;","flooron":"下部,3;","area":"373.0,3;","price":"1800.0,3;","unitprice":"4.8257375,3;"}
    public static JSONObject GridAttributeSummary(){
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

        return obj;
    }

    /**1、统计每个num_map中各个特征的总个数*/
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

    /**2、遍历map,统计map中的各个特征的值*/
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


    /**3、处理长时序的价格、均价和面积数据*/
    public static Map<String,Map<Double,Double>> dataFusion(Map<String,List<String>> map){
        Map<String,Map<Double,Double>> date_featureStatistics=new HashMap<>();
        for(Map.Entry<String,List<String>>entry:map.entrySet()){
            String date=entry.getKey();
            List<String> value=entry.getValue();

            Map<Double,Double> type_num=new HashMap<>();//每个月都有一个特征数量统计
            for(int i=0;i<value.size();i++){
                String[] poi=value.get(i).split(";");

                for(int j=0;j<poi.length;j++){
                    String[] typenum=poi[j].split(",");
                    double type=Double.parseDouble(typenum[0]);
                    double num=Double.parseDouble(typenum[1]);

                    if(type_num.containsKey(type)){

                        double temp=type_num.get(type);
                        temp+=num;
                        type_num.put(type,temp);

                    }else {
                        type_num.put(type,num);
                    }
                }
            }

            date_featureStatistics.put(date,type_num);
        }
        return date_featureStatistics;
    }

    /**4、遍历3中得出的统计结果，并且将其以json的格式打印出来*/
    public static JSONObject ergodicDataFusionMap(Map<String,Map<Double,Double>> date_featureStatistics){
        JSONObject obj=new JSONObject();
        for(Map.Entry<String,Map<Double,Double>>entry:date_featureStatistics.entrySet()){
            String date=entry.getKey();
            Map<Double,Double> typenum=entry.getValue();

            //统计所有的value的和
            double totalValue=0;
            for(Map.Entry<Double,Double>entry1:typenum.entrySet()){
                totalValue+=entry1.getValue();
            }

            //计算每一个key的权重
            double result=0;
            for(Map.Entry<Double,Double>entry1:typenum.entrySet()){
                double type=entry1.getKey();
                double value=entry1.getValue();

                result+=type*(value/totalValue);
            }

            obj.put(date,result);
        }
        obj=timeSort(obj);
        return obj;
    }

    /**5、均价加权的计算方法*/
    public static JSONObject averageWeighted(JSONObject unitprice,JSONObject area){
        JSONObject obj=new JSONObject();
        Iterator<String> dates=unitprice.keys();
        while(dates.hasNext()){
            String date=dates.next();
            double price=unitprice.getDouble(date)*area.getDouble(date);

            obj.put(date,price);
        }

        return obj;
    }

    /**6、计算每个月对应的房贷首付*/
    public static JSONObject downPayments(JSONObject area,JSONObject price,String type){
        JSONObject obj=new JSONObject();

       // System.out.println(price);//{"2015-10":1800,"2015-11":3600}
        Iterator<String> dates=price.keys();
        while(dates.hasNext()) {
            String date = dates.next();
            double pr = price.getDouble(date);
            double ar=area.getDouble(date);
            String[] d=date.split("-");
            int year=Integer.parseInt(d[0]);
            int month=Integer.parseInt(d[1]);

            double downpay=calculationDownPayment(ar,type,year,month,pr);
            obj.put(date,downpay);
        }

        return obj;
    }

    /**计算网格内房子的首付情况:由于有部分住宅面积超过144平米，属于非普通住宅，故要把非普通住宅计算进来*/
    //二套房首付比率50%(去年四成的执行率低)
    //首套首付比率30%（2016年9月30日之前），二套房首付比率35%（2016年9月30日之后）
    //契税：首套1%，二套3%
    //评估价和网签价相当，大概在成交价的80%到90%之间，这里按照80%来计算
    public static double calculationDownPayment(double area,String type,int year,int month,double price){
        double deedTax=deedTaxCalculation(area,type,year, month);
        double serviceCharge=0.027;
        double netSigned=0.8;
        double loan=loanCalculation(type,year,month,area);

        double totalPrice=price*(1+deedTax+serviceCharge)-netSigned*price*loan;
        return totalPrice;
    }

    /**分情况讨论2016，930新政之前和之后的契税问题*/
    public static double deedTaxCalculation(double area,String type,int year,int month){
        double deedTax=0;
        if(type.equals("first")){
            /*首套住房：
            2016.9.30之前：
            90（含）平方米以下：普通1%，非普通3%
                 90-144平方米：普通1.5%，非普通3%
                 144平方米以上：3%
            2016.9.30之后：
            90（含）平方米以下：1%
            90-144平方米：1.5%
            144平方米以上：1.5%
             */
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
            /*
            新政前后都是一样的收费标准
            第二套改善性住房：
            90（含）平方米以下：3%
            90（含）平方米以上：3%
             */
            deedTax=0.03;
        }
        return deedTax;
    }

    /**分情况讨论2016，930新政之后的首付比率问题*/
    public static double loanCalculation(String type,int year,int month,double area){
        double loan=0;
        if(type.equals("first")){
            if(year==2016&&month>=10){
                if(area<144){
                    loan=1-0.35;
                }else{
                    loan=1-0.4;
                }
            }else {
                loan=1-0.3;
            }
        }else if(type.equals("second")){
            if(area<144){
                loan=1-0.5;
            }else{
                loan=1-0.7;
            }
        }
        return loan;
    }

    /**对房源数据{"2015-11":3600,"2015-10":1800}按照日期进行排序*/
    public static JSONObject timeSort(JSONObject obj){
        List<JSONObject> time_price=new ArrayList<>();
        Iterator<String> it=obj.keySet().iterator();

        while(it.hasNext()){

            String date=it.next();
            double price=obj.getDouble(date);
            JSONObject r=new JSONObject();
            r.put("date",date);
            r.put("price",price);
            time_price.add(r);
        }
        Collections.sort(time_price, new UtilFile.TimeComparator());

        JSONObject result=new JSONObject();
        for(int i=0;i<time_price.size();i++){
            JSONObject r=time_price.get(i);
            String date=r.getString("date");
            double price=r.getDouble("price");
            result.put(date,price);
        }
        return result;
    }
    /**对统计数据type":{"2016-01":{"3室1厅1卫":{"price":112,"area":93,"unit_price":1.2043011,"ratio":1}},,,进行排序*/
    public static JSONObject timeSort_type(JSONObject obj){
        List<JSONObject> time_price=new ArrayList<>();
        Iterator<String> it=obj.keySet().iterator();

        while(it.hasNext()){

            String date=it.next();
            JSONObject hys=obj.getJSONObject(date);
            JSONObject r=new JSONObject();
            r.put("date",date);
            r.put("hys",hys);
            time_price.add(r);
        }
        Collections.sort(time_price, new UtilFile.TimeComparator());

        JSONObject result=new JSONObject();
        for(int i=0;i<time_price.size();i++){
            JSONObject r=time_price.get(i);
            String date=r.getString("date");
            JSONObject hys=r.getJSONObject("hys");
            result.put(date,hys);
        }
        return result;
    }


    //统计houseType_list中的每个户型的特征
    public static JSONObject houseTypeListStatistic(Map<String,List<JSONObject>> map){
        String hy;
        List<JSONObject> list;
        JSONObject obj;

        JSONObject result=new JSONObject();
        //统计所有户型中每个户型所占的比率
        double totalratios=0;
        for(Map.Entry<String,List<JSONObject>>entry:map.entrySet()){
            list=entry.getValue();
            for(int i=0;i<list.size();i++) {
                obj = list.get(i);
                totalratios+=obj.getDouble("ratio");
            }
        }


        for(Map.Entry<String,List<JSONObject>>entry:map.entrySet()){
            hy=entry.getKey();//户型类型
            list=entry.getValue();

            double total_price=0;
            double total_unitprice=0;
            double total_area=0;

            for(int i=0;i<list.size();i++){
                obj=list.get(i);
                total_price+=obj.getDouble("price");
                total_unitprice+=obj.getDouble("unitprice");
                total_area+=obj.getDouble("area");
            }

            double aver_price=0;
            double aver_unitprice=0;
            double aver_area=0;
            double aver_ratio=0;

            //统计某个特定的户型的均价，即所有格网该户型的值的加权
            for(int i=0;i<list.size();i++){
                obj=list.get(i);
                double price=obj.getDouble("price");
                aver_price+=price*(price/total_price);
                double unitprice=obj.getDouble("unitprice");
                aver_unitprice+=unitprice*(unitprice/total_unitprice);
                double area=obj.getDouble("area");
                aver_area+=area*(area/total_area);
                double ratio=obj.getDouble("ratio");
                aver_ratio+=ratio/totalratios;
            }

            JSONObject temp=new JSONObject();
            temp.put("price",aver_price);
            temp.put("unitprice",aver_unitprice);
            temp.put("area",aver_area);
            temp.put("ratio",aver_ratio);

            result.put(hy,temp);
        }
        return  result;
    }

    //按月份统计每一个houseType_list中的特征
    public static JSONObject date_houseTypeListStatistic(Map<String,List<JSONObject>> map){

        String date;
        List<JSONObject> types;
        JSONObject results=new JSONObject();
        for(Map.Entry<String,List<JSONObject>>entry:map.entrySet()){
            date=entry.getKey();
            types=entry.getValue();

            Map<String,List<JSONObject>> houseType_list=new HashMap<>();
            for(int i=0;i<types.size();i++){
                JSONObject type=types.get(i);

                Iterator<String> hy_it=type.keys();
                while (hy_it.hasNext()){
                    String hy=hy_it.next();
                    JSONObject hy_obj=type.getJSONObject(hy);

                    if(houseType_list.containsKey(hy)){
                        List<JSONObject> objs=houseType_list.get(hy);
                        objs.add(hy_obj);
                        houseType_list.put(hy,objs);
                    }else {
                        List<JSONObject> objs=new ArrayList<>();
                        objs.add(hy_obj);
                        houseType_list.put(hy,objs);
                    }
                }
            }
            JSONObject result=houseTypeListStatistic(houseType_list);
            results.put(date,result);
        }

        return results;
    }

    //从MongoDB中调用大格网下的所有子格网的poi数据
    public static JSONObject dataFromBasicPoi_50(int rowmin,int rowmax,int colmin,int colmax,String source){
        DBCollection coll_Basic50 = db.getDB().getCollection("BasicData_Resold_50");

        BasicDBObject document = new BasicDBObject();

        BasicDBObject cond=new BasicDBObject();
        cond.put("$gte",rowmin);
        cond.put("$lte",rowmax);
        document.put("row",cond);

        cond=new BasicDBObject();
        cond.put("$gte",colmin);
        cond.put("$lte",colmax);
        document.put("col",cond);

        document.put("source",source);

        Map<String,List<BasicDBObject>> date_poilist=new HashMap<>();
        List<DBObject> array=coll_Basic50.find(document).toArray();

        String year;
        String month;
        String date="";
        List<BasicDBObject> pois;
        BasicDBObject poi;
        String house_type;

        String area;
        String price;
        String unit_price;

        //按照日期对poi进行分类，一个月为一类
        for(int i=0;i<array.size();i++){
            BasicDBObject doc=(BasicDBObject)array.get(i);
            doc.remove("_id");


            if(doc.containsField("year")&&doc.containsField("month")){
                year=doc.getString("year");
                month=doc.getString("month");
                date=year+"-"+month;
            }

            if(date.length()!=0){

                if(date_poilist.containsKey(date)){
                    pois=date_poilist.get(date);
                    pois.add(doc);
                    date_poilist.put(date,pois);
                }else{
                    pois=new ArrayList<>();
                    pois.add(doc);
                    date_poilist.put(date,pois);
                }
            }
        }

        double area_aver=0;
        double price_aver=0;
        double unit_price_aver=0;

        double area_total=0;
        double price_total=0;
        double unit_price_total=0;

        int area_num=0;
        int price_num=0;
        int unit_price_num=0;

        //遍历map，逐个月对poi的特征进行统计
        Map<String,Map<String,List<BasicDBObject>>> date_houseType_poilist=new HashMap<>();

        JSONObject date_ht_data=new JSONObject();
        for(Map.Entry<String,List<BasicDBObject>>entry:date_poilist.entrySet()){
            date=entry.getKey();//date=year+"-"+month;
            String[] d=date.split("-");
            year=d[0];
            month=d[1];
            if(month.startsWith("0")){
                month=month.substring(1);
            }
            pois=entry.getValue();

            Map<String,List<BasicDBObject>> houseType_poilist=new HashMap<>();

            //对每个月份的数据，按照户型进行分类
            //我知道了，导致这个原因的是同一个变量名字被不同的变量用了！！
            for(int i=0;i<pois.size();i++){
                poi=pois.get(i);
                if(poi.containsField("house_type")){
                    house_type=poi.getString("house_type");

                    if(houseType_poilist.containsKey(house_type)){
                        List<BasicDBObject> hy_pois=houseType_poilist.get(house_type);
                        hy_pois.add(poi);
                        houseType_poilist.put(house_type,hy_pois);
                    }else{
                        List<BasicDBObject> hy_pois=new ArrayList<>();
                        hy_pois.add(poi);
                        houseType_poilist.put(house_type,hy_pois);
                    }
                }
            }

            //遍历该月份的每个户型的pois,计算每个户型的比率
            JSONObject ratios=new JSONObject();
            int totals=0;
            for(Map.Entry<String,List<BasicDBObject>>entry1:houseType_poilist.entrySet()){
                pois=entry1.getValue();
                totals+=pois.size();
            }
            double ratio;
            for(Map.Entry<String,List<BasicDBObject>>entry1:houseType_poilist.entrySet()){
                house_type=entry1.getKey();
                pois=entry1.getValue();
                //System.out.println(date+":"+house_type+":"+pois.size());
                ratio=(double)(pois.size())/(double)totals;
                ratios.put(house_type,ratio);
            }

            //遍历该月份的每个户型的pois，计算每个户型的均价
            JSONObject ht_data=new JSONObject();
            for(Map.Entry<String,List<BasicDBObject>>entry1:houseType_poilist.entrySet()){
                house_type=entry1.getKey();
                pois=entry1.getValue();

                area_aver=0;
                price_aver=0;
                unit_price_aver=0;

                area_total=0;
                price_total=0;
                unit_price_total=0;

                area_num=0;
                price_num=0;
                unit_price_num=0;

                JSONObject data=new JSONObject();
                for(int i=0;i<pois.size();i++){
                    poi=pois.get(i);

                    if(poi.containsField("area")){
                        area_total+=poi.getDouble("area");
                        area_num++;
                    }
                    if(poi.containsField("price")){
                        price_total+=poi.getDouble("price");
                        price_num++;
                    }
                    if(poi.containsField("unit_price")){
                        unit_price_total+=poi.getDouble("unit_price");
                        unit_price_num++;
                    }
                }

                area_aver=area_total/(double)area_num;
                price_aver=price_total/(double)price_num;
                unit_price_aver=unit_price_total/(double)unit_price_num;

                data.put("price",price_aver);
                data.put("area",area_aver);
                data.put("unit_price",unit_price_aver);
                if(ratios.containsKey(house_type)){
                    double r=ratios.getDouble(house_type);
                    data.put("ratio",r);
                }

                //在这里还要讨论一下不同首付比率和契税情况下的首付问题
                double downPayment;

                JSONObject first=new JSONObject();
                double deedTax=deedTaxCalculation(area_aver,"first",Integer.parseInt(year), Integer.parseInt(month));
                downPayment=differentDownpayment(price_aver,0.3,deedTax);
                first.put("in30",downPayment);
                downPayment=differentDownpayment(price_aver,0.35,deedTax);
                first.put("in35",downPayment);
                downPayment=differentDownpayment(price_aver,0.4,deedTax);
                first.put("in40",downPayment);
                downPayment=differentDownpayment(price_aver,0.45,deedTax);
                first.put("in45",downPayment);
                downPayment=differentDownpayment(price_aver,0.5,deedTax);
                first.put("in50",downPayment);
                data.put("first",first);


                JSONObject second=new JSONObject();
                deedTax=deedTaxCalculation(area_aver,"second",Integer.parseInt(year), Integer.parseInt(month));
                downPayment=differentDownpayment(price_aver,0.4,deedTax);
                second.put("in40",downPayment);
                downPayment=differentDownpayment(price_aver,0.5,deedTax);
                second.put("in50",downPayment);
                downPayment=differentDownpayment(price_aver,0.55,deedTax);
                second.put("in55",downPayment);
                downPayment=differentDownpayment(price_aver,0.6,deedTax);
                second.put("in60",downPayment);
                downPayment=differentDownpayment(price_aver,0.65,deedTax);
                second.put("in65",downPayment);
                downPayment=differentDownpayment(price_aver,0.70,deedTax);
                second.put("in70",downPayment);
                data.put("second",second);

                ht_data.put(house_type,data);
            }
            date_ht_data.put(date,ht_data);
        }

        return date_ht_data;
    }

    //计算不同首付比率下每个户型的首付款额度
    public static double differentDownpayment(double price,double loan,double deedTax){
        double downPayment=0;

        double serviceCharge=0.027;
        double netSigned=0.8;

        downPayment=price*(1+deedTax+serviceCharge)-netSigned*price*(1-loan);
        return downPayment;
    }
}
