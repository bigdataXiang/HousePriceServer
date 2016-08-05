package com.reprocess;

import net.sf.json.JSONObject;

/**
 * Created by ZhouXiang on 2016/8/3.
 */
public class SetPoiCode {
    public static double LAT_MIN = 39.438283;
    public static double LNG_MIN = 115.417284;

    public static int setPoiCode_3000(JSONObject jsonObject) {

        double width=0.03535799999999156;//每三千米的经度差
        double length=0.027011999999992042;//每三千米的纬度差

        Object lat=jsonObject.get("lat");
        String type=lat.getClass().getName();

        double latitude=0;
        double longitude=0;
        if(type.equals("java.lang.Double")){
            latitude = Double.parseDouble(jsonObject.get("lat").toString());
            longitude =Double.parseDouble(jsonObject.get("lng").toString());
        }else if(type.equals("java.lang.String")){
            latitude = Double.parseDouble(jsonObject.get("lat").toString());
            longitude =Double.parseDouble(jsonObject.get("lng").toString());
        }


        int row = (int) Math.ceil((latitude - LAT_MIN) / length);
        int col = (int) Math.ceil((longitude - LNG_MIN) / width);
        int code = (col + 60 * (row - 1));

        return code;
    }

    public static int setPoiCode_2000(JSONObject jsonObject){

        double width=0.023571999999994375;//每二千米的经度差
        double length=0.018007999999994695;//每二千米的纬度差

        Object lat=jsonObject.get("lat");
        String type=lat.getClass().getName();

        double latitude=0;
        double longitude=0;
        if(type.equals("java.lang.Double")){
            latitude = Double.parseDouble(jsonObject.get("lat").toString());
            longitude =Double.parseDouble(jsonObject.get("lng").toString());
        }else if(type.equals("java.lang.String")){
            latitude = Double.parseDouble(jsonObject.get("lat").toString());
            longitude =Double.parseDouble(jsonObject.get("lng").toString());
        }


        int row = (int) Math.ceil((latitude - LAT_MIN) / length);
        int col = (int) Math.ceil((longitude - LNG_MIN) / width);
        int code = (col + 90 * (row - 1));

        return code;

    }

    public static int setPoiCode_1000(JSONObject jsonObject){

        double width=0.011785999999997188;//每一千米的经度差
        double length=0.009003999999997347;//每一千米的纬度差

        Object lat=jsonObject.get("lat");
        String type=lat.getClass().getName();

        double latitude=0;
        double longitude=0;
        if(type.equals("java.lang.Double")){
            latitude = Double.parseDouble(jsonObject.get("lat").toString());
            longitude =Double.parseDouble(jsonObject.get("lng").toString());
        }else if(type.equals("java.lang.String")){
            latitude = Double.parseDouble(jsonObject.get("lat").toString());
            longitude =Double.parseDouble(jsonObject.get("lng").toString());
        }


        int row = (int) Math.ceil((latitude - LAT_MIN) / length);
        int col = (int) Math.ceil((longitude - LNG_MIN) / width);
        int code = (col + 179 * (row - 1));

        return code;

    }

    /**
     * 检查cols的值对否
     * @param jsonObject
     * @return
     */
    public static int setPoiCode_500(JSONObject jsonObject){

        double width=0.005892999999998594;//每五百米的经度差
        double length=0.004501999999998674;//每五百米的纬度差

        Object lat=jsonObject.get("lat");
        String type=lat.getClass().getName();

        double latitude=0;
        double longitude=0;
        if(type.equals("java.lang.Double")){
            latitude = Double.parseDouble(jsonObject.get("lat").toString());
            longitude =Double.parseDouble(jsonObject.get("lng").toString());
        }else if(type.equals("java.lang.String")){
            latitude = Double.parseDouble(jsonObject.get("lat").toString());
            longitude =Double.parseDouble(jsonObject.get("lng").toString());
        }


        int row = (int) Math.ceil((latitude - LAT_MIN) / width);
        int col = (int) Math.ceil((longitude - LNG_MIN) / length);

        int cols = (int) Math.ceil((117.500126 - LNG_MIN) / length);
        int code = (col + cols * (row - 1));

        return code;

    }
}
