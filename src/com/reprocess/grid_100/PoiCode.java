package com.reprocess.grid_100;

import com.mongodb.BasicDBObject;
import net.sf.json.JSONObject;

import java.io.IOException;

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

    public static String setPoiCode_50(double latitude,double longitude) {

        double width=5.892999999998593E-4;//每50m的经度差
        double length=4.501999999998674E-4;//每50m的纬度差


        int row = (int) Math.ceil((latitude - LAT_MIN) / length);
        int col = (int) Math.ceil((longitude - LNG_MIN) / width);
        int code = (col + 4000 * (row - 1));

        String result=code+","+row+","+col;

        return result;
    }

    public static void main(String[] args) throws IOException
    {

        //System.out.println(39.442785-39.438283);
        double k=0.5*((0.011791000000002327+0.011580999999992514+0.011891000000005647+0.011880999999988262)/40);
        System.out.println("经度差："+k);
        System.out.println(115.417284+k);
        double l=0.5*(0.009003999999997347/10);
        System.out.println("纬度差："+l);
        System.out.println(39.438283+l);

        double test[]=BLToGauss(115.417284,41.059244);
        double test1[]=BLToGauss(115.417284,(41.059244-l));
        double dist=Math.sqrt(Math.pow((test[0]-test1[0]),2)+Math.pow((test[1]-test1[1]),2));//计算两点之间的平面距离
        System.out.println("只改变纬度的情况下:"+dist);

        double test2[]=BLToGauss(115.417284,41.059244);
        double test3[]=BLToGauss(115.417284-k,41.059244);
        dist=Math.sqrt(Math.pow((test2[0]-test3[0]),2)+Math.pow((test2[1]-test3[1]),2));//计算两点之间的平面距离
        System.out.println("只改变经度的情况下:"+dist);

    }
    /**
     * 由经纬度反算成高斯投影坐标
     *
     * @param longitude
     * @param latitude
     */
    public static double[] BLToGauss(double longitude, double latitude) {

        int ProjNo = 0;

        // 带宽
        int ZoneWide = 6;

        double longitude1, latitude1, longitude0, X0, Y0, xval, yval;
        double a, f, e2, ee, NN, T, C, A, M, iPI;

        // 3.1415926535898/180.0;
        iPI = 0.0174532925199433;

        // 54年北京坐标系参数
        a = 6378245.0;
        f = 1.0 / 298.3;

        // 80年西安坐标系参数
        // a=6378140.0;
        // f=1/298.257;

        ProjNo = (int) (longitude / ZoneWide);
        longitude0 = ProjNo * ZoneWide + ZoneWide / 2;
        longitude0 = longitude0 * iPI;

        // 经度转换为弧度
        longitude1 = longitude * iPI;

        // 纬度转换为弧度
        latitude1 = latitude * iPI;

        e2 = 2 * f - f * f;
        ee = e2 * (1.0 - e2);
        NN = a / Math.sqrt(1.0 - e2 * Math.sin(latitude1) * Math.sin(latitude1));
        T = Math.tan(latitude1) * Math.tan(latitude1);
        C = ee * Math.cos(latitude1) * Math.cos(latitude1);
        A = (longitude1 - longitude0) * Math.cos(latitude1);
        M = a * ((1 - e2 / 4 - 3 * e2 * e2 / 64 - 5 * e2 * e2 * e2 / 256) * latitude1 - (3 * e2 / 8 + 3 * e2 * e2 / 32 + 45 * e2 * e2 * e2 / 1024) * Math.sin(2 * latitude1) + (15 * e2 * e2 / 256 + 45 * e2 * e2 * e2 / 1024) * Math.sin(4 * latitude1) - (35 * e2 * e2 * e2 / 3072) * Math.sin(6 * latitude1));
        xval = NN * (A + (1 - T + C) * A * A * A / 6 + (5 - 18 * T + T * T + 72 * C - 58 * ee) * A * A * A * A * A / 120);
        yval = M + NN * Math.tan(latitude1) * (A * A / 2 + (5 - T + 9 * C + 4 * C * C) * A * A * A * A / 24 + (61 - 58 * T + T * T + 600 * C - 330 * ee) * A * A * A * A * A * A / 720);
        X0 = 1000000L * (ProjNo + 1) + 500000L;
        Y0 = 0;
        xval = xval + X0;
        yval = yval + Y0;
        return new double[] { xval, yval };
    }

}
