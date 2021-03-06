package com.reprocess.grid_100.ContourGeneration;

import com.mongodb.*;
import com.reprocess.grid_100.interpolation.SpatialInterpolation;
import com.svail.bean.Response;
import com.svail.db.db;
import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import utils.FileTool;
import utils.UtilFile;

import java.util.*;

import static com.reprocess.grid_100.CallInterestGrid.getLngLat;
import static com.reprocess.grid_100.PoiCode.setPoiCode_100;

/**
 * Created by ZhouXiang on 2016/9/13.
 */
public class ContourGeneration {
    public static JSONArray lackvalue_grids=new JSONArray();
    public static Map<Integer,JSONObject> seriuseLack_map=new HashMap<>();//时间连续的数量小于4个月，则不参与空间插值，因为这样插值出来的结果可能很不好
    public static Map<String, Map<String, Double>> interpolation_result= new HashMap<>();//dataset的key是网格的code，value是网格对应的时间价格序列值

    public static List<JSONObject> fullData=new ArrayList<>();//用来装那些每个月份都有值的网格json
    public static List<JSONObject> interpolationData=new ArrayList<>();//用来装那些某些月份的值缺乏但是已经插值好了的json
    public static Map<String,String> failedData=new HashMap<>();//用来装那些插值不成功的mse特别大的值

    public Response get(String body){
        JSONObject object=JSONObject.fromObject(body);
        String time=object.getString("gridTime");
        int year=Integer.parseInt(time.substring(0,time.indexOf("年")));
        int month=Integer.parseInt(time.substring(time.indexOf("年")+"年".length(),time.indexOf("月")));
        time=year+"-"+month;
        int N=5;

        Map<Integer,JSONObject> mergedata=new HashMap<>();
        DBCollection coll = db.getDB().getCollection("resold_woaiwojia_interpolation");
        List code_array=new ArrayList<>();
        code_array=coll.find().toArray();
        JSONObject obj;
        String temp;
        double average_price;
        JSONObject timeseries;
        List<JSONObject> data=new ArrayList<>();

        for(int i=0;i<code_array.size();i++){
            temp=code_array.get(i).toString();
            obj=JSONObject.fromObject(temp);
            //System.out.println(obj);
            obj.remove("_id");
            int code=obj.getInt("code");

            timeseries=obj.getJSONObject("timeseries");

            average_price=0;
            if(timeseries.size()!=0){
                if(timeseries.containsKey(time)){
                    average_price=timeseries.getDouble(time);
                }else{
                    System.out.println(code+"在"+time+"的插值怎么没有？！");
                }
            }else{
                average_price=0;
            }

            String rowcol_str=codeToRowCol(code,N);
            String[] rowcol=rowcol_str.split(",");
            int row=Integer.parseInt(rowcol[0]);
            int col=Integer.parseInt(rowcol[1]);
            String color=setColorRegion(average_price);

            obj=new JSONObject();
            obj.put("code",code);
            obj.put("average_price",average_price);
            obj.put("row",row);
            obj.put("col",col);
            obj.put("color",color);
            //System.out.println(obj);
            data.add(obj);
        }
        System.out.println("开始排序：");
        Collections.sort(data, new UtilFile.CodeComparator());

        JSONObject result=new JSONObject();
        int r_min=1;
        int r_max=2000/N;
        int c_min=1;
        int c_max=2000/N;
        result.put("r_min",r_min);
        result.put("r_max",r_max);
        result.put("c_min",c_min);
        result.put("c_max",c_max);
        result.put("N",N);
        result.put("data",data);

        System.out.println("ok");
        //System.out.println(result);

        Response r= new Response();
        r.setCode(200);
        r.setContent(result.toString());
        return r;

    }


    public static void main(String[] args){

        callMongo();

    }

    /**调用mongodb生成网格值*/
    /**13、找出区域中不同价格区间内的网格，并进行分类*/
    public static void callMongo(){
        String time="2015-10";
        int N=5;

        DBCollection coll = db.getDB().getCollection("resold_woaiwojia_interpolation");
        List code_array=new ArrayList<>();
        code_array=coll.find().toArray();
        JSONObject obj;
        String temp;
        double average_price;
        JSONObject timeseries;
        List<JSONObject> data=new ArrayList<>();
        Map<Integer,List<JSONObject>> contour=new HashMap<>();

        int row;
        int col;
        for(int i=0;i<code_array.size();i++){
            //System.out.println(i);

            temp=code_array.get(i).toString();
            obj=JSONObject.fromObject(temp);
            //System.out.println(obj);
            obj.remove("_id");
            int code=obj.getInt("code");

            timeseries=obj.getJSONObject("timeseries");

            average_price=0;
            if(timeseries.size()!=0){
                if(timeseries.containsKey(time)){
                    average_price=timeseries.getDouble(time);
                }else{
                    System.out.println(code+"在"+time+"的插值怎么没有？！");
                }
            }else{
                average_price=0;
            }

            String rowcol_str=codeToRowCol(code,N);
            String[] rowcol=rowcol_str.split(",");
            row=Integer.parseInt(rowcol[0]);
            col=Integer.parseInt(rowcol[1]);

            obj=new JSONObject();
            obj.put("code",code);
            obj.put("average_price",average_price);
            obj.put("row",row);
            obj.put("col",col);
            obj.put("corners",getLngLat(row,col,N));
            //System.out.println(obj);

            int arrangement;
            if(average_price>8){
                arrangement=8;
            }else if(average_price>=7&&average_price<8){
                arrangement=7;
            }else if(average_price>=6&&average_price<7){
                arrangement=6;
            }else if(average_price>=5&&average_price<6){
                arrangement=5;
            }else if(average_price>=4&&average_price<5){
                arrangement=4;
            }else if(average_price>=3&&average_price<4){
                arrangement=3;
            }else {
                arrangement=2;
            }

            if(contour.containsKey(arrangement)){
                data=contour.get(arrangement);
                data.add(obj);
                //System.out.println(data);
                contour.put(arrangement,data);
            }else{
                data=new ArrayList<>();
                data.add(obj);
                contour.put(arrangement,data);
            }
        }

        System.out.println("选取网格：");
        data=contour.get(8);
        for(int i=0;i<data.size();i++){
            obj=data.get(i);
            //System.out.println(obj);
            FileTool.Dump(obj.toString(),"D:\\contour_8.txt","utf-8");
        }

        data=contour.get(7);
        for(int i=0;i<data.size();i++){
            obj=data.get(i);
            //System.out.println(obj);
            FileTool.Dump(obj.toString(),"D:\\contour_7.txt","utf-8");
        }

        data=contour.get(6);
        for(int i=0;i<data.size();i++){
            obj=data.get(i);
            //System.out.println(obj);
            FileTool.Dump(obj.toString(),"D:\\contour_6.txt","utf-8");
        }

        data=contour.get(5);
        for(int i=0;i<data.size();i++){
            obj=data.get(i);
            //System.out.println(obj);
            FileTool.Dump(obj.toString(),"D:\\contour_5.txt","utf-8");
        }

        data=contour.get(4);
        for(int i=0;i<data.size();i++){
            obj=data.get(i);
            //System.out.println(obj);
            FileTool.Dump(obj.toString(),"D:\\contour_4.txt","utf-8");
        }

        data=contour.get(3);
        for(int i=0;i<data.size();i++){
            obj=data.get(i);
            //System.out.println(obj);
            FileTool.Dump(obj.toString(),"D:\\contour_3.txt","utf-8");
        }

        data=contour.get(2);
        for(int i=0;i<data.size();i++){
            obj=data.get(i);
            //System.out.println(obj);
            FileTool.Dump(obj.toString(),"D:\\contour_2.txt","utf-8");
        }
    }
    /**生成最后的插值*/
    public static void run(){
        creatAllGridsData();
        findLackDataGrid(4);
        interpolationResult();
        JSONArray failed=interpolation_MSE();
        String code;

        for(int i=0;i<failed.size();i++){
            code=failed.get(i).toString();
            failedData.put(code,"");
        }

        for(int i=0;i<lackvalue_grids.size();i++){
            code=lackvalue_grids.get(i).toString();
            if(failedData.containsKey(code)){
                //如果code属于failedData里面的，说明都是插值失败了的
            }else{
                addInterpolation(code);
            }

        }
        Map<Integer,JSONObject> mergedata=MergeData();

        System.out.println("开始合并数据：");
        JSONObject result=girdDatas("2015-10",mergedata,5);
        System.out.println("result:"+result);
        FileTool.Dump(result.toString(),"D:\\result.txt","utf-8");
    }

/**一、先对zoom=11，分辨率=500*500的网格进行插值*/
//{"west":115.74508666992188,"east":117.06344604492189,"south":39.6834113242346,"north":40.15106273116229,"zoom":11,"gridTime":"2015年10月","source":"我爱我家"}

    /**1、先生成整个北京区域内的每个网格的时序数据，存放刚到jsonArray_map中*/
    public static void creatAllGridsData(){
        JSONObject condition=new JSONObject();
        condition.put("N",5);
        condition.put("source","woaiwojia");
        condition.put("export_collName","GridData_Resold_100");
        SpatialInterpolation.getAllGridSeriesValue(condition);
    }

    /**2、找出jsonArray_map中时序数据有缺失的网格存于lackvalue_grids，这些网格都是需要进行插值处理的,n为最小所需时间数*/
    public static void findLackDataGrid(int n){
        Map<Integer, JSONObject> jsonArray_map=SpatialInterpolation.jsonArray_map;

        int code;
        JSONObject timeseries;
        for (Map.Entry<Integer, JSONObject> entry : jsonArray_map.entrySet()) {

            code=entry.getKey();
            timeseries=entry.getValue();
            SpatialInterpolation.initDataSet(""+code,timeseries,SpatialInterpolation.dataset);//将所有的缺失或者不确实的数据全部放到dataset中

            int size=timeseries.size();
            if(size<n){
                seriuseLack_map.put(code,timeseries);//时间连续的数量小于4个月，则不参与空间插值，因为这样插值出来的结果可能很不好
            }else if(size>=n&&size<8){
                //System.out.println("Key = " + code + ", Value = " +timeseries );
                lackvalue_grids.add(code);
            }else if(size==8){
                JSONObject obj=new JSONObject();
                obj.put("code",code);
                obj.put("timeseries",timeseries);
                fullData.add(obj);
            }
        }
    }

    /**3、计算缺失数据的网格的插值结果,并且存放于interpolation_result中*/
    public static void interpolationResult(){

        //System.out.println(lackvalue_grids);

        JSONObject code_relatedCode=SpatialInterpolation.findRelatedCode(lackvalue_grids);

        JSONObject spatial=SpatialInterpolation.codesCovariance(code_relatedCode);

        System.out.println(spatial);
        //interpolation_result
        Iterator iterator=spatial.keys();
        String code;
        JSONObject timeseries;
        if(iterator.hasNext()){
            while (iterator.hasNext()){
                code=iterator.next().toString();
                timeseries=spatial.getJSONObject(code);
                SpatialInterpolation.initDataSet(""+code,timeseries,interpolation_result);
            }
        }
    }

    /**4、计算该网格的真实值与插值的均方误差：mse*/
    public static double compareInterpolationResult(String code){

        Map<String, Double> real_value_map=new HashMap<>();
        Map<String, Double> interpolation_value_map=new HashMap<>();
        if(SpatialInterpolation.dataset.containsKey(code)){
            real_value_map=SpatialInterpolation.dataset.get(code);
        }
        if(interpolation_result.containsKey(code)){
            interpolation_value_map=interpolation_result.get(code);
        }

        String date="";
        double real_price;
        double interpolation_price;
        double difference;
        double difference_2;
        double sum=0;
        int count=0;
        if(interpolation_value_map.size()!=0){
            for (Map.Entry<String, Double> p : real_value_map.entrySet()) {
                date=p.getKey();
                if (interpolation_value_map.containsKey(date)) {
                    real_price=real_value_map.get(date);
                    interpolation_price=interpolation_value_map.get(date);
                    difference=Math.abs(real_price-interpolation_price);
                    difference_2=Math.pow(difference,2);
                    sum+=difference_2;
                    count++;
                }
            }
        }

        double mse;
        if(count!=0){
            mse=sum/count;
        }else{
            mse=0;
        }

        return mse;
    }

    /**5、计算lackvalue_grids数组中每个网格插值前后的mse的值，并且将mse的值较大的挑选出来*/
    public static JSONArray interpolation_MSE(){
        String code="";
        double mse;
        JSONArray failed_interpolation_codes=new JSONArray();
        try{
            Map<String, String> pearson_is_0=SpatialInterpolation.pearson_is_0;

            for(int i=0;i<lackvalue_grids.size();i++){

                code=lackvalue_grids.get(i).toString();

                if(pearson_is_0.containsKey(code)){
                    //System.out.println();
                    failed_interpolation_codes.add(code);

                }else {
                    mse=compareInterpolationResult(code);
                    if(mse>0.1){
                        failed_interpolation_codes.add(code);
                       // System.out.println(code+":"+mse);
                    }
                }
            }
            //System.out.println(lackvalue_grids.size());

        }catch (NullPointerException e){
            System.out.println(code);
        }
        return failed_interpolation_codes;
    }

    /**6、比较mse的值较大的code的真实值和插值，并且将其打印出来*/
    public static void compareFailedCode(String code){
        Map<String, Double> real_value_map=new HashMap<>();
        Map<String, Double> interpolation_value_map=new HashMap<>();
        if(SpatialInterpolation.dataset.containsKey(code)&&interpolation_result.containsKey(code)){
            real_value_map=SpatialInterpolation.dataset.get(code);
            interpolation_value_map=interpolation_result.get(code);

            String date="";
            double real_price;
            double interpolation_price;

            System.out.println(code+":");
            if(interpolation_value_map.size()!=0){
                for (Map.Entry<String, Double> p : real_value_map.entrySet()) {
                    date=p.getKey();
                    if (interpolation_value_map.containsKey(date)) {
                        real_price=real_value_map.get(date);
                        interpolation_price=interpolation_value_map.get(date);
                        System.out.println(date+":"+real_price+" , "+interpolation_price);
                    }
                }
            }
            System.out.print("\n");
            SpatialInterpolation.printSeparator(40);//打印分隔符
        }
    }

    /**7、将插值结果符合(即mse小于0.1)的网格进行插值操作*/
    public static void addInterpolation(String code){

        Map<String, Double> real_value_map=new HashMap<>();
        Map<String, Double> interpolation_value_map=new HashMap<>();
        if(SpatialInterpolation.dataset.containsKey(code)&&interpolation_result.containsKey(code)){
            real_value_map=SpatialInterpolation.dataset.get(code);
            interpolation_value_map=interpolation_result.get(code);

            String date="";
            double real_price;
            double interpolation_price;

            //System.out.println(code+":");
            JSONObject timeseries=new JSONObject();
            if(interpolation_value_map.size()!=0){
                for (Map.Entry<String, Double> p : interpolation_value_map.entrySet()) {

                    date=p.getKey();
                    if (real_value_map.containsKey(date)) {
                        real_price=real_value_map.get(date);
                        timeseries.put(date,real_price);

                        interpolation_price=interpolation_value_map.get(date);
                        //System.out.println(date+":"+real_price+" , "+interpolation_price);
                    }else{
                        interpolation_price=interpolation_value_map.get(date);
                        real_price=interpolation_price;//将真实值中没有的日期用插值中对应的日期的值来替代
                        timeseries.put(date,real_price);

                        //System.out.println(date+":"+real_price+" , "+interpolation_price);
                    }
                }
            }
            JSONObject obj=new JSONObject();
            obj.put("code",code);
            obj.put("timeseries",timeseries);
            interpolationData.add(obj);
            //System.out.print("\n");
            //SpatialInterpolation.printSeparator(40);//打印分隔符
        }
    }

    /**8、将不用插值的数据fullData和已经插值的数据interpolationData)进行合并,并且存于mongodb中*/
    public static Map MergeData(){
        Map<Integer,JSONObject> mergedata=new HashMap<>();
        fullData.addAll(interpolationData);
        JSONObject obj;
        int code;
        for(int i=0;i<fullData.size();i++){
            obj=fullData.get(i);
            code=obj.getInt("code");
            //System.out.println(code+":"+obj);
            FileTool.Dump(obj.toString(),"D:\\objs.txt","utf-8");
            mergedata.put(code,obj);
        }
        return mergedata;
    }

    /**9、将插值后的结果全面表现在地图上*/
    public static JSONObject girdDatas(String time,Map mergedata,int N){

        int r_min=1;
        int r_max=2000/N;
        int c_min=1;
        int c_max=2000/N;
        List<JSONObject> data=new ArrayList<>();
        Map<Integer,String> codekey=new HashMap<>();

        int code;
        JSONObject obj;
        JSONObject timeseries;
        double average_price=0;
        Iterator it=mergedata.keySet().iterator();
        if(it.hasNext()){
            while(it.hasNext()){
                code=(int)it.next();
                obj=(JSONObject) mergedata.get(code);
                timeseries=obj.getJSONObject("timeseries");

                average_price=0;
                if(timeseries.size()!=0){
                    if(timeseries.containsKey(time)){
                        average_price=timeseries.getDouble(time);
                    }else{
                        System.out.println(code+"在"+time+"的插值怎么没有？！");
                    }
                }else{
                    average_price=0;
                }

                String rowcol_str=codeToRowCol(code,N);
                String[] rowcol=rowcol_str.split(",");
                int row=Integer.parseInt(rowcol[0]);
                int col=Integer.parseInt(rowcol[1]);
                String color=setColorRegion(average_price);

                obj=new JSONObject();
                obj.put("code",code);
                obj.put("average_price",average_price);
                obj.put("row",row);
                obj.put("col",col);
                obj.put("color",color);
                //System.out.println(obj);
                data.add(obj);
                codekey.put(code,"");
            }
        }

        Collections.sort(data, new UtilFile.CodeComparator());

        JSONObject result=new JSONObject();
        result.put("r_min",r_min);
        result.put("r_max",r_max);
        result.put("c_min",c_min);
        result.put("c_max",c_max);
        result.put("N",N);
        result.put("data",data);
        return result;
    }

    /**10、建立N00*N00的行列号与code的映射关系*/
    public static String codeToRowCol(int code,int N){

        Map<Integer,String> map=new HashMap<>();
        int rows=2000/N;
        int cols=2000/N;
        int gridcode;
        String rowcol="";
        for(int r=1;r<=rows;r++){
            for(int c=1;c<=cols;c++){
                gridcode=c+(r-1)*cols;
                rowcol=r+","+c;
                map.put(gridcode,rowcol);
            }
        }

        if(map.containsKey(code)){
            rowcol=map.get(code);
        }else {
            System.out.println("无对应的行列号:"+code);
        }
        return rowcol;
    }

    /**11、建立更密集的配色方案*/
    public static String setColorRegion(double price){
        String color="";

        if(price>9){
            color="#C70305";
        }else if(price>8&&price<=9){
            color="#EA4706";
        }else if(price>7.5&&price<=8){
            color="#E97A04";
        }else if(price>7&&price<=7.5){
            color="#E9A708";
        }else if(price>6.5&&price<=7){
            color="#E6CC05";
        }else if(price>6&&price<=6.5){
            color="#E9E507";
        }else if(price>5.5&&price<=6){
            color="#D8EB00";
        }else if(price>5&&price<=5.5){
            color="#B8E705";
        }else if(price>4.5&&price<=5){
            color="#04E738";
        }else if(price>4&&price<=4.5){
            color="#06E884";
        }else if(price>3.5&&price<=4){
            color="#08E9C7";
        }else if(price>3&&price<=3.5){
            color="#03EAE4";
        }else if(price>2.5&&price<=3){
            color="#09BAEC";
        }else if(price>2&&price<=2.5){
            color="#077BEA";
        }else{
            color="#1411D2";
        }

        return color;
    }

    /**12、将数据导入mongodb中*/
    public static void toMongoDB(String collName,String file){

        Mongo m;
        try {
            System.out.println("运行开始:");
            m = new MongoClient("127.0.0.1", 27017);   //127.0.0.1
            DB db = m.getDB("houseprice");

            DBCollection coll = db.getCollection(collName);//coll.drop();
            BasicDBObject document;

            Vector<String> objs=FileTool.Load(file,"utf-8");
            String poi="";
            JSONObject obj;
            for(int i=0;i<objs.size();i++){
                poi=objs.elementAt(i);
                //document=BasicDBObject.parse(poi);
                //System.out.println(document);
                //coll.insert(document);
            }
        } catch (MongoException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NullPointerException e) {
            // TODO Auto-generated catch block
            System.out.println("发生异常的原因为 :"+e.getMessage());
            e.printStackTrace();
        }
    }

}
