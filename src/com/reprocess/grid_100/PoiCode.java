package com.reprocess.grid_100;

import com.mongodb.BasicDBObject;
import net.sf.json.JSONObject;

/**
 * Created by ZhouXiang on 2016/8/8.
 */
public class PoiCode {
    public static double LAT_MIN = 39.438283;
    public static double LNG_MIN = 115.417284;


    /**
     * 将所有的poi的code设置成100*100的网格编码的值
     * @param document 将要导入mongodb的文件
     * @param jsonObject 本地文件中的poi
     * @param cols 整个网格的列数
     * @return
     */
    public static BasicDBObject setPoiCode_100(BasicDBObject document, JSONObject jsonObject,int cols) {

        double width=0.0011785999999997187;//每100m的经度差
        double length=9.003999999997348E-4;//每100m的纬度差

        Object lat=jsonObject.get("latitude");
        String type=lat.getClass().getName();

        double latitude=0;
        double longitude=0;
        if(type.equals("java.lang.Double")){
            latitude = Double.parseDouble(jsonObject.get("latitude").toString());
            longitude =Double.parseDouble(jsonObject.get("longitude").toString());
        }else if(type.equals("java.lang.String")){
            latitude = Double.parseDouble(jsonObject.get("latitude").toString());
            longitude =Double.parseDouble(jsonObject.get("longitude").toString());
        }


        int row = (int) Math.ceil((latitude - LAT_MIN) / length);
        int col = (int) Math.ceil((longitude - LNG_MIN) / width);
        int code = (col + cols * (row - 1));

        document.put("row",row);
        document.put("col",col);
        document.put("code",code);

        return document;
    }
}
