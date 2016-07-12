package com.svail.handler;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.svail.db.db;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import utils.ColorUtils;
import utils.FileTool;

import java.util.*;

/**
 * Created by ZhouXiang on 2016/7/11.
 */
public class handler_gridcolor {

    public String get(String path){

        JSONObject date=new JSONObject();
        date.put("year","2016");
        date.put("month","06");
        date.put("day","03");
        double max = getMaxPrice("rentout_code","fang",date);
        JSONArray result = getAvenragePrice(result_array );
        String data=setColor(result,max);

        return data;
    }
 /*  public static void main(String[] args){

       JSONObject date=new JSONObject();
       date.put("year","2016");
       date.put("month","06");
       date.put("day","03");
       double max = getMaxPrice("rentout_code","fang",date);
       JSONArray result = getAvenragePrice(result_array );
       String data=setColor(result,max);
       FileTool.Dump(data,"D:/gridcolor.txt","utf-8");
   }*/

    /**
     * 给每个网格的均价赋予一个颜色值
     * @param array
     * @param pricemax
     */
    public static String  setColor(JSONArray array,double pricemax){
        int V=0;
        JSONObject backdata=new JSONObject();
        JSONArray finalresult=new JSONArray();

        if(pricemax!=0){
            for (int i=0;i<array.size();i++){
                JSONObject obj= (JSONObject) array.get(i);
                //System.out.print(obj);
                double price=obj.getDouble("average_price");
                float ratio= (float) ((price/pricemax)*100);
                V = Math.round(ratio);

                String color="";
                int [] rgb=new int[3];
                color=ColorUtils.HSV2StrRGB(120,100,V,rgb);
                obj.put("color",color);

                finalresult.add(obj);
            }
            backdata.put("data",finalresult);
        }
       return backdata.toString();
    }
    static JSONArray result_array = new JSONArray();

    public static double getMaxPrice(String collName,String source,JSONObject date){
        String str="";

        DBCollection coll = db.getDB().getCollection(collName);
        BasicDBObject document = new BasicDBObject();
        document.put("source",source);
        document.put("date",date);
        DBCursor cursor = coll.find(document);
        String poi="";

        List<Double> pricelist=new ArrayList<Double>();
        double price_max=0;
        if(cursor.hasNext()){
            while (cursor.hasNext()) {
                poi=cursor.next().toString();

                JSONObject obj=JSONObject.fromObject(poi);
                //System.out.println(obj);
                JSONObject result=new JSONObject();

                int code = obj.getInt("gridcode");
                result.put("code",code);

                double price = obj.getDouble("price");
                result.put("price",price);
                pricelist.add(price);

                result_array.add(result);
            }
            System.out.println("该天一共有数据"+result_array.size()+"条");
            //求list中的最大价格值
            price_max= Collections.max(pricelist);
        }

     return  price_max;
    }

    public static JSONArray getAvenragePrice(JSONArray array){

        JSONObject codeprice=new JSONObject();

        for (int i=0;i<array.size();i++){
            JSONObject poi= (JSONObject) array.get(i);
            String code=poi.getString("code");

            if(codeprice.containsKey(code)){
                List<Double> pricelist= (List<Double>) codeprice.get(code);
                double price=poi.getDouble("price");
                pricelist.add(price);
                codeprice.put(code,pricelist);

            }else{
                List<Double> pricelist=new ArrayList<Double>();
                double price=poi.getDouble("price");
                pricelist.add(price);
                codeprice.put(code,pricelist);
            }
        }
        //计算实际有数据的网格的个数
        System.out.println("该天一共有"+codeprice.size()+"个网格有数据");

        //遍历codeprice中的元素，求每个时间节点的价格均值
        List<JSONObject> list = new ArrayList<JSONObject>(); //对时间进行排序的list
        Iterator codekeys = codeprice.keys();
        while(codekeys.hasNext()){

            JSONObject price=new JSONObject();
            String code=(String)codekeys.next();
            price.put("code",code);

            List<Double> pricelist=(List<Double>) codeprice.get(code);
            double totalprice=0;
            double average_price=0;
            if(pricelist.size()!=0){
                for(int i=0;i<pricelist.size();i++){
                    totalprice+=pricelist.get(i);
                }
                average_price=totalprice/pricelist.size();
            }

            price.put("average_price",average_price);
            list.add(price);
        }

        //对时间进行排序
//      System.out.println("开始排序：");
        Collections.sort(list, new CodeComparator()); // 根据网格code排序

        JSONArray finalresult=new JSONArray();
        Iterator it = list.iterator();
        while (it.hasNext()) {
            finalresult.add(it.next());
        }

        return finalresult;
    }
    //根据code进行排序
    static class CodeComparator implements Comparator{
        public int compare(Object object1, Object object2) {
            JSONObject p1 = (JSONObject) object1;
            JSONObject p2 = (JSONObject) object2;

            int code1=p1.getInt("code");
            int code2=p2.getInt("code");

            int flag=0;
            if(code1>code2){
                flag=1;
            }
           return flag;
        }
    }
}
