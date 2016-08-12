package com.reprocess.grid_100;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.sf.json.JSONObject;
import utils.FileTool;
import utils.geotext.GeoQuery;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Vector;
import java.util.zip.GZIPInputStream;

/**
 * Created by ZhouXiang on 2016/8/12.
 */
public class SchoolPoi {
    public static void main(String[] args) {

        StringToJson("E:\\房地产可视化\\toSchool\\小学\\普通.txt");

    }

    public static void StringToJson(String file) {
        Vector<String> pois = FileTool.Load(file, "utf-8");
        String poi;
        String school;
        String type;
        String address;
        JSONObject obj;
        for (int i = 0; i < pois.size(); i++) {
            poi = pois.elementAt(i);

            school = poi.substring(0, poi.indexOf("[普通]"));
            type = poi.substring(poi.indexOf("[普通]"), poi.indexOf("学校地址："));
            address = poi.substring(poi.indexOf("学校地址：") + "学校地址：".length());

            obj = new JSONObject();
            obj.put("school", school);
            obj.put("type", type);
            obj.put("address", address);

            FileTool.Dump(obj.toString(), file.replace(".txt", "") + "_json.txt", "utf-8");
        }

    }
}
