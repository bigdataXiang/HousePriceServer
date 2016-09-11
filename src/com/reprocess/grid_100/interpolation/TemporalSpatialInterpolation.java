package com.reprocess.grid_100.interpolation;

import net.sf.json.JSONObject;

/**
 * Created by ZhouXiang on 2016/9/11.
 */
public class TemporalSpatialInterpolation {
    public  static void main(String[] args){

        temporalSpatialInterpolation("44563","2015-12");
    }
    public static void temporalSpatialInterpolation(String code,String date){

        JSONObject spatial_Interpolation=SpatialInterpolation.getInterpolationResult();
        JSONObject time_Interpolation=TimeInterpolation.getInterpolationResult();

        //System.out.println(spatial_Interpolation);
        //System.out.println(time_Interpolation);

        JSONObject spatial_obj=spatial_Interpolation.getJSONObject(code);
        JSONObject time_obj=time_Interpolation.getJSONObject(date);

        //System.out.println(spatial_obj);
        //System.out.println(time_obj);

        double price_spatial=spatial_obj.getDouble(date);
        double price_time=time_obj.getDouble(code);

        JSONObject A_obj=SpatialInterpolation.getA();
        JSONObject B_obj=TimeInterpolation.getB();

        double A=A_obj.getDouble(code);
        double B=B_obj.getDouble(date);
        double k=(A/B);

        B=1/(k+1);
        A=k*B;
        //System.out.println(A);
        //System.out.println(B);

        double price=A*price_spatial+B*price_time;
        System.out.println(price_spatial);
        System.out.println(price_time);
        System.out.println(price);

    }
}
