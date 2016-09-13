package com.reprocess.grid_100.interpolation;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by ZhouXiang on 2016/9/13.
 */
public class ContourGeneration {
    public static JSONArray lackvalue_grids=new JSONArray();
    public static Map<Integer,JSONObject> seriuseLack_map=new HashMap<>();
    public static Map<String, Map<String, Double>> interpolation_result= new HashMap<>();//dataset的key是网格的code，value是网格对应的时间价格序列值

    public static void main(String[] args){
        creatAllGridsData();
        findLackDataGrid(4);
        interpolationResult();
        interpolation_MSE();
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

    /**2、找出jsonArray_map中时序数据有缺失的网格存于，这些网格都是需要进行插值处理的,n为最小所需时间数*/
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
            }else if(size>=n&&size<9){
                //System.out.println("Key = " + code + ", Value = " +timeseries );
                lackvalue_grids.add(code);
            }
        }
    }

    /**3、计算缺失数据的网格的插值结果,并且存放于interpolation_result中*/
    public static void interpolationResult(){

        JSONObject code_relatedCode=SpatialInterpolation.findRelatedCode(lackvalue_grids);

        JSONObject spatial=SpatialInterpolation.codesCovariance(code_relatedCode);

        //System.out.println(spatial);
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

    /**4、对比该网格的真实值与插值的结果*/
    public static double compareInterpolationResult(String code){

        Map<String, Double> real_value_map=SpatialInterpolation.dataset.get(code);
        Map<String, Double> interpolation_value_map=interpolation_result.get(code);

        String date="";
        double real_price;
        double interpolation_price;
        double difference;
        double difference_2;
        double sum=0;
        int count=0;
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
        double mse;
        if(count!=0){
            mse=sum/count;
        }else{
            mse=0;
        }

        return mse;
    }

    /**5、计算lackvalue_grids数组中每个网格插值前后的mse的值，并且将mse的值较大的挑选出来*/
    public static void interpolation_MSE(){
        String code="";
        double mse;
        try{

            for(int i=0;i<lackvalue_grids.size();i++){

                code=lackvalue_grids.get(i).toString();
                mse=compareInterpolationResult(code);
                System.out.println(code+":"+mse);
            }
            System.out.println(lackvalue_grids.size());

        }catch (NullPointerException e){
            System.out.println(code);
        }

    }

}
