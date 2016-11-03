package com.transaction_price;

import net.sf.json.JSONObject;
import utils.FileTool;

import java.util.Vector;

/**
 * Created by ZhouXiang on 2016/11/2.
 */
public class WoaiWojia {
    public static void main(String[] args){
        getUrls("D:\\test\\woaiwojia\\urls.txt");
    }
    public static void getUrls(String file){
        Vector<String> pois= FileTool.Load(file,"utf-8");

        for(int i=0;i<pois.size();i++){
            String poi=pois.elementAt(i);
            JSONObject obj=JSONObject.fromObject(poi);
            String url=obj.getString("url");

            JSONObject result=new JSONObject();
            result.put("url",url);

            FileTool.Dump(result.toString(),"D:\\test\\woaiwojia\\处理后的urls.txt","utf-8");
        }
    }
}
