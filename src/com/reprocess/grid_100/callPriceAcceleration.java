package com.reprocess.grid_100;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.svail.bean.Response;
import com.svail.db.db;
import net.sf.json.JSONObject;
import utils.Tool;

import java.util.*;

import static com.reprocess.grid_100.GridMerge.codeMapping100toN00;
import static utils.UtilFile.printArray_BasicDB;

/**
 * Created by ZhouXiang on 2016/8/24.
 */
public class callPriceAcceleration {
    public Response get(String body){
        JSONObject condition= JSONObject.fromObject(body);
        condition.put("source","woaiwojia");
        condition.put("export_collName","GridData_Resold_100");

        Response r= new Response();
        r.setCode(200);
        r.setContent("");
        return r;

    }
    public static void main(String[] args){
        double width=0.0011785999999997187;//每100m的经度差
        double length=9.003999999997348E-4;//每100m的纬度差

        /*System.out.println(0.0011785999999997187*5);
        System.out.println(9.003999999997348E-4*5);
        System.out.println(115.417284+0.0011785999999997187*5);
        System.out.println(39.438283+9.003999999997348E-4*5);*/


        int colmin=(int)Math.ceil((116.21629714965819-115.417284)/width);
        int colmax=(int)Math.ceil((116.5458869934082-115.417284)/width);
        int rowmin=(int)Math.ceil((39.91473966049243-39.438283)/length);
        int rowmax=(int)Math.ceil((40.03274067972939-39.438283)/length);

        JSONObject condition=new JSONObject();
        condition.put("N",5);
        condition.put("rowmax",rowmax);
        condition.put("rowmin",rowmin);
        condition.put("colmax",colmax);
        condition.put("colmin",colmin);
        condition.put("startyear",2015);
        condition.put("startmonth",10);
        condition.put("endyear",2016);
        condition.put("endmonth",05);
        condition.put("source","woaiwojia");
        condition.put("export_collName","GridData_Resold_100");

        test(condition);
        System.out.println();
    }

    public static void test(JSONObject condition){
        int rowmin=condition.getInt("rowmin");
        int rowmax=condition.getInt("rowmax");
        int colmin=condition.getInt("colmin");
        int colmax=condition.getInt("colmax");
        int N=condition.getInt("N");

        //将小网格合并成大网格
        int r_min=(int) Math.ceil((double)rowmin/N);
        int r_max=(int) Math.ceil((double)rowmax/N);
        int c_min=(int) Math.ceil((double)colmin/N);
        int c_max=(int) Math.ceil((double)colmax/N);

        String collName=condition.getString("export_collName");
        DBCollection coll = db.getDB().getCollection(collName);
        BasicDBObject document = new BasicDBObject();

        String source=condition.getString("source");
        document.put("source",source);

        int startyear=condition.getInt("startyear");
        int startmonth=condition.getInt("startmonth");
        int endyear=condition.getInt("endyear");
        int endmonth=condition.getInt("endmonth");

        BasicDBObject cond=new BasicDBObject();
        List code_array=new ArrayList<>();
        List code_array_temp=new ArrayList<>();

        if(startyear==endyear){
            //先调用前一年的数据
            cond.put("$gte",startyear);
            cond.put("$lte",startyear);
            document.put("year",cond);

            cond=new BasicDBObject();
            cond.put("$gte",startmonth);
            cond.put("$lte",endmonth);
            document.put("month",cond);

            cond=new BasicDBObject();
            cond.put("$gte",rowmin);
            cond.put("$lte",rowmax);
            document.put("row",cond);

            cond=new BasicDBObject();
            cond.put("$gte",colmin);
            cond.put("$lte",colmax);
            document.put("col",cond);

            //System.out.println(document);
            code_array=coll.find(document).toArray();
            System.out.println(code_array.size());

        }else if(endyear>startyear){
            //先调用前一年的数据
            cond.put("$gte",startyear);
            cond.put("$lte",startyear);
            document.put("year",cond);

            cond=new BasicDBObject();
            cond.put("$gte",startmonth);
            cond.put("$lte",12);
            document.put("month",cond);

            cond=new BasicDBObject();
            cond.put("$gte",rowmin);
            cond.put("$lte",rowmax);
            document.put("row",cond);

            cond=new BasicDBObject();
            cond.put("$gte",colmin);
            cond.put("$lte",colmax);
            document.put("col",cond);

            System.out.println(document);
            code_array=coll.find(document).toArray();
            System.out.println("第一年数据："+code_array.size());

            //再调用第二年的数据
            document = new BasicDBObject();
            document.put("source",source);
            cond=new BasicDBObject();
            cond.put("$gte",endyear);
            cond.put("$lte",endyear);
            document.put("year",cond);

            cond=new BasicDBObject();
            cond.put("$gte",1);
            cond.put("$lte",endmonth);
            document.put("month",cond);

            cond=new BasicDBObject();
            cond.put("$gte",rowmin);
            cond.put("$lte",rowmax);
            document.put("row",cond);

            cond=new BasicDBObject();
            cond.put("$gte",colmin);
            cond.put("$lte",colmax);
            document.put("col",cond);

            System.out.println(document);
            code_array_temp=coll.find(document).toArray();
            System.out.println("第二年数据："+code_array_temp.size());

            //将code_array_temp中的数据添加到code_array中
            code_array.addAll(code_array_temp);

            System.out.println("合并后的list的大小："+code_array.size());

        }


        BasicDBObject doc;
        int row_doc;
        int col_doc;
        int[] result_doc;
        List code_array_after=new ArrayList<>();
        Map<Integer,List> gridmap= new HashMap<>();
        List<BasicDBObject> codelist;
        int code;
        int row;
        int col;

        for(int i=0;i<code_array.size();i++){
            doc= (BasicDBObject) code_array.get(i);
            doc.remove("_id");
            row_doc=doc.getInt("row");
            col_doc=doc.getInt("col");

            //System.out.println("转换前的100*100的网格数据"+doc);

            //将doc中的row、col、code从100分辨率的转换成N00分辨率的
            result_doc=codeMapping100toN00(row_doc,col_doc,N);
            row=result_doc[0];
            doc.put("row",row);
            col=result_doc[1];
            doc.put("col",col);
            code=result_doc[2];
            doc.put("code",code);

            //System.out.println("转换后的N00*N00的网格数据"+doc);
            code_array_after.add(doc);

            if (gridmap.containsKey(code)) {
                codelist=gridmap.get(code);
                codelist.add(doc);
                gridmap.put(code,codelist);
            }else{
                codelist=new ArrayList<>();
                codelist.add(doc);
                gridmap.put(code,codelist);
            }
        }

        //统计gridmap中一共有多少个网格，并且对每个网格的doc作操作
        String date;
        JSONObject obj;
        Map<String,List> timeprice_map= new HashMap<>();
        List<Double> average_price_list=new ArrayList<>();
        double average_price;
        System.out.println("共有"+gridmap.size()+"个网格");
        Iterator it=gridmap.keySet().iterator();
        List<JSONObject> timeseries_price=new ArrayList<>();
        JSONObject date_price;
        JSONObject totalgrid=new JSONObject();//存放的是所有网格的唯一时间价格数据
        if(it.hasNext()){
            while (it.hasNext()){
                code=(int)it.next();
                codelist=gridmap.get(code);
                //printArray_BasicDB(codelist);

                //将一个网格里面的数据处理成一个时间点一个价格的形式
                for(int i=0;i<codelist.size();i++){
                    obj=JSONObject.fromObject(codelist.get(i));
                    date=obj.getString("year")+"-"+obj.getString("month");
                    average_price=obj.getDouble("average_price");

                    if(timeprice_map.containsKey(date)){
                        average_price_list=timeprice_map.get(date);
                        average_price_list.add(average_price);
                        timeprice_map.put(date,average_price_list);

                    }else{
                        average_price_list=new ArrayList<>();
                        average_price_list.add(average_price);
                        timeprice_map.put(date,average_price_list);

                    }

                }

                double totalprice=0;
                int counts=0;
                Iterator it_timeprice=timeprice_map.keySet().iterator();//存放的是每一个时间点的价格的集合
                date_price=new JSONObject();//里面存放的是该网格价格均值处理之后时间与价格一一对应的数据
                if(it_timeprice.hasNext()) {
                    while (it_timeprice.hasNext()) {
                        date=(String) it_timeprice.next();
                        average_price_list=timeprice_map.get(date);

                        for(int i=0;i<average_price_list.size();i++){
                            average_price=average_price_list.get(i);
                            if(average_price!=0){
                                totalprice+=average_price;
                                counts++;
                            }
                        }
                        if(counts!=0){
                            average_price=totalprice/counts;
                        }else {
                            average_price=0;
                        }
                        date_price.put(date,average_price);
                        totalgrid.put(code,date_price);
                    }
                }
            }
        }

        System.out.println(totalgrid);

        //逐个网格计算网格的价格加速度。加速度的计算方式有两种：
        //第一是计算该时间段内最高的值与最低的值之间产生的加速度；
        //第二种是从从起始时间计算价格加速度

        Iterator totalgrid_it=totalgrid.keys();
        String key;
        double value;
        double maxprice;
        double minprice;
        String maxprice_date;
        String minprice_date;
        String codeindex;
        JSONObject code_acceleration=new JSONObject();
        if(totalgrid_it.hasNext()){
            while(totalgrid_it.hasNext()){

                codeindex=(String)totalgrid_it.next();
                date_price=totalgrid.getJSONObject(codeindex);
                Iterator date_price_it=date_price.keys();

                System.out.println(codeindex+":"+date_price);

                String[] datearray=new String[date_price.size()];
                double[] pricearray=new double[date_price.size()];
                int count=0;
                if(date_price_it.hasNext()) {
                    while (date_price_it.hasNext()) {
                        key=(String)date_price_it.next();
                        value=date_price.getDouble(key);
                        datearray[count]=key;
                        pricearray[count]=value;
                        count++;
                    }
                }

                //第一种：计算最大最小值
                maxprice= Tool.getMaxNum(pricearray);
                int maxindex=Tool.getMaxNum_Index(pricearray);
                maxprice_date=datearray[maxindex];

                minprice= Tool.getMinNum(pricearray);
                int minindex=Tool.getMinNum_Index(pricearray);
                minprice_date=datearray[minindex];

                System.out.println("max:"+maxprice_date+","+maxprice);
                System.out.println("min:"+minprice_date+","+minprice);

                int maxyear=Integer.parseInt(maxprice_date.substring(0,maxprice_date.indexOf("-")));
                int minyear=Integer.parseInt(minprice_date.substring(0,minprice_date.indexOf("-")));

                int maxmonth=Integer.parseInt(maxprice_date.substring(maxprice_date.indexOf("-")+"-".length()));
                int minmonth=Integer.parseInt(minprice_date.substring(minprice_date.indexOf("-")+"-".length()));

                double acceleration=0;

                if(maxyear==minyear){
                    acceleration=(maxprice-minprice)/(maxmonth-minmonth);
                }else if(maxyear>minyear){
                    acceleration=(maxprice-minprice)/((maxmonth+12)-minmonth);
                }else if(maxyear<minyear){
                    acceleration=(maxprice-minprice)/(maxmonth-(minmonth+12));
                }

                code_acceleration.put(codeindex,acceleration);
                System.out.println("第一种计算方法");
                System.out.println("acceleration:"+acceleration);


                //第二种：计算开始和结束的时间对应的加速度
                GregorianCalendar calendar;
                Iterator keys=date_price.keys();
                String codekey="";
                List<GregorianCalendar> datelist=new ArrayList<>();
                if(keys.hasNext()){
                    while(keys.hasNext()){
                        codekey=(String) keys.next();
                        int year=Integer.parseInt(codekey.substring(0,codekey.indexOf("-")));
                        int month=Integer.parseInt(codekey.substring(codekey.indexOf("-")+"-".length()));
                        calendar=new GregorianCalendar(year,month,1);
                        datelist.add(calendar);
                    }
                }

                System.out.println(datelist);

                /*double startprice=date_price.getDouble(starttime);
                double endprice=date_price.getDouble(endtime);
                if(startyear==endyear){
                    acceleration=(endprice-startprice)/(endmonth-startmonth);
                }else {
                    acceleration=(endprice-startprice)/((endmonth+12)-startmonth);
                }

                code_acceleration.put(codeindex,acceleration);
                System.out.println("第二种计算方法");
                System.out.println("acceleration:"+acceleration);*/
            }
        }
        //System.out.println(code_acceleration);







    }
}
