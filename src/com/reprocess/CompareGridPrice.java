package com.reprocess;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import utils.FileTool;
import utils.UtilFile;

import java.util.*;

import static utils.UtilFile.JSONArrayToList;

/**
 * Created by ZhouXiang on 2016/8/4.
 */
public class CompareGridPrice {
    public static void main(String[] args){
        sortGridCode("E:\\房地产可视化\\toServer\\resold\\fang\\effective_2015_12.txt");
    }

    /**
     * 将每个网格的数据按照code或者价格进行排序
     * @param file
     */
    public static void sortGridCode(String file){
        Vector<String> poi= FileTool.Load(file,"utf-8");
        String value=poi.elementAt(0);
        JSONObject total=JSONObject.fromObject(value);
        JSONArray data=total.getJSONArray("data");

        List<JSONObject> list= JSONArrayToList(data);
        Collections.sort(list, new UtilFile.Average_PriceComparator());

        Iterator it=list.iterator();
        while(it.hasNext()){
            String str=it.next().toString();
            System.out.println(str);
            FileTool.Dump(str,file.replace(".txt","")+"_sortByPrice.txt","utf-8");
        }

    }




}
