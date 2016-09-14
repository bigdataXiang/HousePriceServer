package com.reprocess.grid_100.interpolation;

import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import utils.FileTool;
import utils.UtilFile;

import java.util.*;

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
    public static void main(String[] args){
        creatAllGridsData();
        findLackDataGrid(4);
        interpolationResult();
        JSONArray failed=interpolation_MSE();
        String code;
        /*for(int i=0;i<failed.size();i++){
            code=failed.get(i).toString();
            compareFailedCode(code);
        }*/

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

    /**8、将不用插值的数据和已经插值的数据进行合并,并且存于fullData.txt文件中*/
    public static Map MergeData(){
        Map<Integer,JSONObject> mergedata=new HashMap<>();
        fullData.addAll(interpolationData);
        JSONObject obj;
        Map<String,String> codekey=new HashMap<>();
        String code="";
        for(int i=0;i<fullData.size();i++){
            obj=fullData.get(i);
            //System.out.println(obj);
            code=obj.getString("code");
            codekey.put(code,"");
        }


        JSONObject nullobj;
        JSONObject timeseries=new JSONObject();
        for(int i=1;i<=400;i++) {
            for (int j =1; j<=400; j++) {
                String codeindex=""+(j + (2000/5) * (i - 1));
                if(!codekey.containsKey(codeindex)){
                    nullobj=new JSONObject();
                    nullobj.put("code",Integer.parseInt(codeindex));
                    nullobj.put("timeseries",timeseries);
                    fullData.add(nullobj);
                }

            }
        }

        Collections.sort(fullData, new UtilFile.CodeComparator()); // 根据网格code排序
        for(int i=0;i<fullData.size();i++){

            //FileTool.Dump(fullData.get(i).toString(),"D:\\fullData1.txt","utf-8");//fullData1.txt中，code是整型的表示是不需要插值的数据，code是Stirng型的表示是插值的数据
            JSONObject o=fullData.get(i);
            int c=o.getInt("code");
            mergedata.put(c,o);
        }
        return mergedata;
    }

    /**9、将插值后的结果全面表现在地图上*/
    public static void girdDatas(String time,Map mergedata,int N){

        int r_min=1;
        int r_max=2000/N;
        int c_min=1;
        int c_max=2000/N;
        JSONArray data=new JSONArray();
        for(int code=1;code<=mergedata.size();code++){
            JSONObject obj=(JSONObject) mergedata.get(code);
            JSONObject timeseries=obj.getJSONObject("timeseries");
            double average_price=0;

            if(timeseries.size()!=0){
                if(timeseries.containsKey(time)){
                    average_price=timeseries.getDouble(time);
                }else{
                    System.out.println(code+"在"+time+"的插值怎么没有？！");
                }
            }else{
                average_price=0;
            }
            int[] rowcol=codeToRowCol(code,N);
            int row=rowcol[0];
            int col=rowcol[1];
            String color="";

            obj=new JSONObject();
            obj.put("code",code);
            obj.put("average_price",average_price);
            obj.put("row",row);
            obj.put("col",col);


        }
    }

    /**10、建立N00*N00的行列号与code的映射关系*/
    public static int[] codeToRowCol(int code,int N){

        Map<Integer,int[]> map=new HashMap<>();
        int rows=2000/N;
        int cols=2000/N;
        int gridcode;
        int[] rowcol=new int[2];
        for(int r=1;r<rows;r++){
            for(int c=1;c<cols;c++){
                gridcode=c+(r-1)*cols;
                rowcol[0]=r;
                rowcol[1]=c;
                map.put(gridcode,rowcol);
            }
        }

        if(map.containsKey(code)){
            rowcol=map.get(code);
        }else {
            System.out.println("无对应的行列号");
        }

        return rowcol;

    }

    /**11、建立更密集的配色方案*/
    public static String setColorRegion(double price){
        String color="";
        boolean result;
        for(double i=0.5;i<=15;){
            result=section(price,i,i+0.5);
            if(result){

            }
            i=i+0.5;
        }

        return color;
    }
    /** 判断x是否在区间（min，max]内*/
    public static  boolean section(double x,double min,double max){
        boolean result;
        if(x>min&&x<=max){
            result=true;
        }else {
            result=false;
        }
        return result;
    }

}
