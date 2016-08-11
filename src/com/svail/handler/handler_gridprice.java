package com.svail.handler;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.svail.bean.Response;
import com.svail.db.db;
import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import utils.FileTool;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by ZhouXiang on 2016/7/10.
 */
public class handler_gridprice{
    public static int count=0;
    public static void main(String[] args){
        for(int i=1;i<3601;i++){
            String str=callDataFromMongo("resold_code_3000","woaiwojia",i);
            FileTool.Dump(str,"D:\\gridcurve.txt","utf-8");

        }

    }

    public Response get(String body){
        JSONObject content=JSONObject.fromObject(body);
        String collname=content.getString("collname");
        String type=content.getString("type");
        int code=content.getInt("code");
        Response r= new Response();
        r.setCode(200);
        r.setContent(callDataFromMongo(collname,type,code));
        return r;
    }
    public static String callDataFromMongo(String collName,String source,int code){
        DBCollection coll = db.getDB().getCollection(collName);
        BasicDBObject document = new BasicDBObject();
        document.put("gridcode",code);
        document.put("source",source);
        DBCursor cursor = coll.find(document);
        String poi="";

        JSONArray array = new JSONArray();
        if(cursor.hasNext()){
            while (cursor.hasNext()) {
                poi=cursor.next().toString();
                //System.out.println(poi);
                JSONObject obj=JSONObject.fromObject(poi);
                JSONObject result=new JSONObject();
                try{
                    if(obj.containsKey("date")){
                        Object date=obj.get("date");

                        JSONObject obj_date= JSONObject.fromObject(date);
                        String year=obj_date.getString("year").replace(" ","");
                        String month=obj_date.getString("month").replace(" ","");
                        String day=obj_date.getString("day").replace(" ","");

                        String dateID=year+"-"+month+"-"+day;
                        result.put("date",dateID);

                        Object price=obj.get("price");
                        result.put("price",price);

                        if(collName.equals("resold_code_3000")){
                            price=obj.get("unit_price");
                            result.put("price",price);
                        }

                        array.add(result);

                    }
                }catch(JSONException e){
                    e.printStackTrace();
                    System.out.println(poi);
                }

            }
        }

        return getTimeSeriesPrice(array.toString(),code);
    }

    public static String getTimeSeriesPrice(String arrayresult,int code){
        String result="";
        JSONArray array=JSONArray.fromObject(arrayresult);
        JSONObject timeprice=new JSONObject();
        for(int i=0;i<array.size();i++){

            JSONObject poi= (JSONObject) array.get(i);
            String date=poi.getString("date");

            if(timeprice.containsKey(date)){
                List<Double> pricelist= (List<Double>) timeprice.get(date);
                double price=poi.getDouble("price");
                pricelist.add(price);
                timeprice.put(date,pricelist);

            }else{
                List<Double> pricelist=new ArrayList<Double>();
                double price=poi.getDouble("price");
                pricelist.add(price);
                timeprice.put(date,pricelist);
            }
        }

        //遍历timeprice中的元素，求每个时间节点的价格均值
        List<JSONObject> list = new ArrayList<JSONObject>(); //对时间进行排序的list
        Iterator timekeys = timeprice.keys();
        while(timekeys.hasNext()){
            JSONObject dateprice=new JSONObject();

            String date=(String)timekeys.next();
            dateprice.put("date",date);

            List<Double> pricelist=(List<Double>) timeprice.get(date);
            double totalprice=0;
            double average_price=0;
            if(pricelist.size()!=0){
                for(int i=0;i<pricelist.size();i++){
                    totalprice+=pricelist.get(i);
                }
                average_price=totalprice/pricelist.size();
            }
            dateprice.put("average_price",average_price);
            list.add(dateprice);
        }
       /* for(int i=0;i<list.size();i++){
            System.out.println(list.get(i));
        }*/

        //对时间进行排序
//        System.out.println("开始排序：");
        Collections.sort(list, new TimeComparator()); // 根据时间排序

        JSONArray finalresult=new JSONArray();
        Iterator it = list.iterator();
        while (it.hasNext()) {
            finalresult.add(it.next());
        }

        JSONObject backdata=new JSONObject();
        backdata.put("code",code);
        backdata.put("data",finalresult);


        result=backdata.toString();
        return result;
    }
    // 自定义方法：分行打印输出list中的元素
    public static void myprint(List<JSONObject> list) {
        Iterator it = list.iterator(); // 得到迭代器，用于遍历list中的所有元素
        while (it.hasNext()) {// 如果迭代器中有元素，则返回true
            System.out.println("\t" + it.next());// 显示该元素
        }
    }
    // 按时间来排序
    static class TimeComparator implements Comparator {
        public int compare(Object object1, Object object2) {// 实现接口中的方法
            JSONObject p1 = (JSONObject) object1; // 强制转换
            //System.out.println(p1);
            JSONObject p2 = (JSONObject) object2;

            String date1=(String)p1.get("date");
            String date2=(String)p2.get("date");

            String[] dates=date1.split("-");
            int year1=Integer.parseInt(dates[0]);
            int month1=Integer.parseInt(dates[1]);
            int day1=Integer.parseInt(dates[2]);
            GregorianCalendar calendar1=new GregorianCalendar(year1,month1,day1);
           // System.out.println(calendar1);

            dates=date2.split("-");
            int year2=Integer.parseInt(dates[0]);
            int month2=Integer.parseInt(dates[1]);
            int day2=Integer.parseInt(dates[2]);
            GregorianCalendar calendar2=new GregorianCalendar(year2,month2,day2);
           // System.out.println(calendar2);

            return calendar1.compareTo(calendar2);

        }
    }
}
