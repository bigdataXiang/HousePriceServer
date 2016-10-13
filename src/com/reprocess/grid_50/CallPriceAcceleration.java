package com.reprocess.grid_50;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.reprocess.grid_100.CallInterestGrid;
import com.svail.bean.Response;
import com.svail.db.db;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import utils.Tool;
import utils.UtilFile;

import java.util.*;

import static com.reprocess.grid_100.GridMerge.codeMapping100toN00;
import static com.reprocess.grid_100.util.Color.setColorRegion_Acceleration;
import static com.reprocess.grid_100.util.SetCondition.setCallPriceAcceleration;

/**
 * Created by ZhouXiang on 2016/8/24.
 */
public class CallPriceAcceleration extends CallInterestGrid {


    public Response get(String body){

        JSONObject condition=setCallPriceAcceleration(body);
        String resultdata=callMongo(condition);
        System.out.println(resultdata);

        Response r= new Response();
        r.setCode(200);
        r.setContent(resultdata);
        //System.out.println("数据返回到get函数中");
        return r;

    }

    public static String callMongo(JSONObject condition){
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

        //根据大网格调用需要的小网格
        rowmin=(r_min-1)*N+1;
        rowmax=r_max*N;
        colmin=(c_min-1)*N+1;
        colmax=c_max*N;

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
           // printArray_BasicDB(code_array);
            System.out.println("从mongodb中调用的数据个数："+code_array.size());


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

        JSONObject code_index_rowcol=new JSONObject();
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
            String row_col=row+"_"+col;
            code_index_rowcol.put(code,row_col);

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
        //System.out.println("共有"+gridmap.size()+"个网格");
        Iterator it=gridmap.keySet().iterator();
        List<JSONObject> timeseries_price=new ArrayList<>();
        JSONObject date_price;
        JSONObject totalgrid=new JSONObject();//存放的是所有网格的唯一时间价格数据
        if(it.hasNext()){
            while (it.hasNext()){

                code=(int)it.next();
                //System.out.println(code);
                codelist=gridmap.get(code);


                //将一个网格里面的数据处理成一个时间点一个价格的形式
                for(int i=0;i<codelist.size();i++){
                    obj=JSONObject.fromObject(codelist.get(i));
                    date=obj.getString("year")+"-"+obj.getString("month");
                    average_price=obj.getDouble("average_price");

                    if(timeprice_map.containsKey(date)){
                        average_price_list=timeprice_map.get(date);
                        average_price_list.add(average_price);
                        timeprice_map.put(date,average_price_list);
                       // System.out.println(average_price_list);

                    }else{
                        average_price_list=new ArrayList<>();
                        average_price_list.add(average_price);
                        timeprice_map.put(date,average_price_list);
                       // System.out.println(average_price_list);
                    }

                }

                double totalprice=0;
                int counts=0;
                Iterator it_timeprice=timeprice_map.keySet().iterator();//存放的是每一个时间点的价格的集合
                List<Double> averageprice_list=new ArrayList<>();
                date_price=new JSONObject();//里面存放的是该网格价格均值处理之后时间与价格一一对应的数据
                JSONObject timeprice=new JSONObject();

                if(it_timeprice.hasNext()) {
                    while (it_timeprice.hasNext()) {
                        date=(String) it_timeprice.next();
                        averageprice_list=timeprice_map.get(date);
                        //System.out.println(date+":"+averageprice_list);

                        for(int i=0;i<averageprice_list.size();i++){
                            average_price=averageprice_list.get(i);
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
                        //System.out.println(date_price);
                        totalgrid.put(code,date_price);

                    }
                }
                timeprice_map.clear();
            }
        }

        //System.out.println(totalgrid);

        //逐个网格计算网格的价格加速度。加速度的计算方式有两种：
        //第一是计算该时间段内最高的值与最低的值之间产生的加速度；
        //第二种是从从起始时间计算价格加速度
        String computation=condition.getString("computation");

        Iterator totalgrid_it=totalgrid.keys();
        String codeindex;
        JSONObject code_acceleration=new JSONObject();

        JSONArray data=new JSONArray();
        JSONObject data_obj;
        List< JSONObject> jsonArray=new ArrayList<>();
        if(totalgrid_it.hasNext()){
            while(totalgrid_it.hasNext()){

                codeindex=(String)totalgrid_it.next();
                date_price=totalgrid.getJSONObject(codeindex);

                double acceleration=0;
                if(computation.equals("最值计算法")){
                    //第一种：通过计算最大最小值得出加速度值
                    JSONObject result=maxMinValue_Acceleration(date_price);
                    acceleration=result.getDouble("acceleration");
                    String maxprice_date=result.getString("maxprice_date");
                    String minprice_date=result.getString("minprice_date");
                    double maxprice=result.getDouble("maxprice");
                    double minprice=result.getDouble("minprice");

                    code_acceleration.put(codeindex,acceleration);
                    System.out.println("第一种计算方法");
                    System.out.println(codeindex+":"+acceleration);

                    data_obj=new JSONObject();
                    String rowcol=code_index_rowcol.getString(codeindex);
                    int rows=Integer.parseInt(rowcol.substring(0,rowcol.indexOf("_")));
                    int cols=Integer.parseInt(rowcol.substring(rowcol.indexOf("_")+"_".length()));
                    String color=setColorRegion_Acceleration(acceleration);
                    data_obj.put("code",Integer.parseInt(codeindex));
                    data_obj.put("acceleration",acceleration);
                    data_obj.put("row",rows);
                    data_obj.put("col",cols);
                    data_obj.put("color",color);
                    data_obj.put("minprice_date",minprice_date);
                    data_obj.put("maxprice_date",maxprice_date);
                    data_obj.put("maxprice",maxprice);
                    data_obj.put("minprice",minprice);

                    jsonArray.add(data_obj);
                }else if(computation.equals("起止时间计算法")){

                    //第二种：计算开始和结束的时间对应的加速度
                    JSONObject result=startEndTime_Acceleration(date_price,startyear,endyear,startmonth,endmonth);
                    String starttime=result.getString("starttime");
                    String endtime=result.getString("endtime");
                    double startprice=result.getDouble("startprice");
                    double endprice=result.getDouble("endprice");
                    acceleration=result.getDouble("acceleration");

                    code_acceleration.put(codeindex,acceleration);
                    System.out.println("第二种计算方法");
                    System.out.println(codeindex+":"+acceleration);

                    data_obj=new JSONObject();
                    String rowcol=code_index_rowcol.getString(codeindex);
                    int rows=Integer.parseInt(rowcol.substring(0,rowcol.indexOf("_")));
                    int cols=Integer.parseInt(rowcol.substring(rowcol.indexOf("_")+"_".length()));
                    String color=setColorRegion_Acceleration(acceleration);
                    data_obj.put("code",Integer.parseInt(codeindex));
                    data_obj.put("acceleration",acceleration);
                    data_obj.put("row",rows);
                    data_obj.put("col",cols);
                    data_obj.put("color",color);
                    data_obj.put("starttime",starttime);
                    data_obj.put("endtime",endtime);
                    data_obj.put("startprice",startprice);
                    data_obj.put("endprice",endprice);

                    jsonArray.add(data_obj);
                }
            }
        }

        JSONObject nullobj;

        for(int i=r_min;i<=r_max;i++) {
            for (int j =c_min; j<=c_max; j++) {
                String index=""+(j + (2000/N) * (i - 1));
                if(!totalgrid.containsKey(index)){
                    nullobj=new JSONObject();

                    nullobj.put("code",Integer.parseInt(index));
                    nullobj.put("acceleration",0);
                    nullobj.put("row",i);
                    nullobj.put("col",j);
                    nullobj.put("color","");

                    if(computation.equals("最值计算法")){
                        nullobj.put("maxprice_date","");
                        nullobj.put("minprice_date","");
                        nullobj.put("maxprice",0);
                        nullobj.put("minprice",0);
                    }else if(computation.equals("起止时间计算法")){
                        nullobj.put("starttime","");
                        nullobj.put("endtime","");
                        nullobj.put("startprice",0);
                        nullobj.put("endprice",0);
                    }
                    //System.out.println("nullobj:"+nullobj);
                    jsonArray.add(nullobj);
                }
            }
        }


        //根据网格code排序
        Collections.sort(jsonArray, new UtilFile.CodeComparator());

        JSONObject result=new JSONObject();
        result.put("r_min",r_min);
        result.put("r_max",r_max);
        result.put("c_min",c_min);
        result.put("c_max",c_max);
        result.put("N",N);
        System.out.println("一共有"+jsonArray.size()+"个网格");
        result.put("data",jsonArray);
       // System.out.println("最后的数据结果计算出~"+result.toString());

        return result.toString();
    }

    /**用起始时间计算最大最小值*/
    public static JSONObject startEndTime_Acceleration(JSONObject date_price,int startyear,int endyear,int startmonth,int endmonth){
        double acceleration=0;
        Calendar calendar ;
        Iterator keys=date_price.keys();
        String codekey="";
        List<Calendar> datelist=new ArrayList<>();
        if(keys.hasNext()){
            while(keys.hasNext()){
                codekey=(String) keys.next();
                int year=Integer.parseInt(codekey.substring(0,codekey.indexOf("-")));
                int month=Integer.parseInt(codekey.substring(codekey.indexOf("-")+"-".length()));
                calendar=new GregorianCalendar(year,month,1);
                datelist.add(calendar);
            }
        }
        /*System.out.println(datelist);*/
        Calendar early;
        Calendar late;

        //获取最早的月份
        early=datelist.get(0);
        for(int i=1;i<datelist.size();i++){
            int result=early.compareTo(datelist.get(i));
            if(result>0){
                early=datelist.get(i);
            }
        }


        //获取最晚的月份
        late=datelist.get(0);
        for(int i=1;i<datelist.size();i++){
            int result=late.compareTo(datelist.get(i));
            if(result<0){
                late=datelist.get(i);
            }
        }


        String starttime="";
        String endtime="";
        if(early.get(Calendar.MONTH)==0){
            starttime=(early.get(Calendar.YEAR)-1)+"-"+(early.get(Calendar.MONTH)+12);
        }else{
            starttime=early.get(Calendar.YEAR)+"-"+(early.get(Calendar.MONTH));
        }

        if(late.get(Calendar.MONTH)==0){
            endtime=(late.get(Calendar.YEAR)-1)+"-"+(late.get(Calendar.MONTH)+12);
        }else{
            endtime=late.get(Calendar.YEAR)+"-"+late.get(Calendar.MONTH);
        }


        double startprice=date_price.getDouble(starttime);
        double endprice=date_price.getDouble(endtime);
        if(startyear==endyear){
            acceleration=(endprice-startprice)/(endmonth-startmonth)*10000;
        }else {
            acceleration=(endprice-startprice)/((endmonth+12)-startmonth)*10000;
        }

        JSONObject obj=new JSONObject();
        obj.put("acceleration",acceleration);
        obj.put("starttime",starttime);
        obj.put("endtime",endtime);
        obj.put("startprice",startprice);
        obj.put("endprice",endprice);

        return obj;
    }
    /**用最大最小值计算加速度*/
    public static JSONObject maxMinValue_Acceleration(JSONObject date_price){
        double maxprice;
        double minprice;
        String maxprice_date;
        String minprice_date;
        double acceleration=0;
        String key;
        double value;

        Iterator date_price_it=date_price.keys();

                /*System.out.println(codeindex+":"+date_price);*/

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

        //计算最大最小值
        maxprice= Tool.getMaxNum(pricearray);
        int maxindex=Tool.getMaxNum_Index(pricearray);
        maxprice_date=datearray[maxindex];

        minprice= Tool.getMinNum(pricearray);
        int minindex=Tool.getMinNum_Index(pricearray);
        minprice_date=datearray[minindex];

        //System.out.println("max:"+maxprice_date+","+maxprice);
        //System.out.println("min:"+minprice_date+","+minprice);

        int maxyear=Integer.parseInt(maxprice_date.substring(0,maxprice_date.indexOf("-")));
        int minyear=Integer.parseInt(minprice_date.substring(0,minprice_date.indexOf("-")));

        int maxmonth=Integer.parseInt(maxprice_date.substring(maxprice_date.indexOf("-")+"-".length()));
        int minmonth=Integer.parseInt(minprice_date.substring(minprice_date.indexOf("-")+"-".length()));



        if(maxyear==minyear){
            acceleration=(maxprice-minprice)/(maxmonth-minmonth)*10000;
        }else if(maxyear>minyear){
            acceleration=(maxprice-minprice)/((maxmonth+12)-minmonth)*10000;
        }else if(maxyear<minyear){
            acceleration=(maxprice-minprice)/(maxmonth-(minmonth+12))*10000;
        }

        //System.out.println("第一种计算方法");
        //System.out.println("acceleration:"+acceleration);
        JSONObject obj=new JSONObject();
        obj.put("acceleration",acceleration);
        obj.put("maxprice_date",maxprice_date);
        obj.put("minprice_date",minprice_date);
        obj.put("maxprice",maxprice);
        obj.put("minprice",minprice);

        return obj;

    }

    public static void main(String[] args){
        double width=0.0011785999999997187;//每100m的经度差
        double length=9.003999999997348E-4;//每100m的纬度差


        /*//第一种"west":116.24496459960938,"east":116.57455444335936,"south":39.85546510325357,"north":39.97330520737606
        int colmin=(int)Math.ceil((116.24496459960938-115.417284)/width);
        int colmax=(int)Math.ceil((116.57455444335936-115.417284)/width);
        int rowmin=(int)Math.ceil((39.85546510325357-39.438283)/length);
        int rowmax=(int)Math.ceil((39.97330520737606-39.438283)/length);*/


        //第二种"west":116.24050140380858,"east":116.57009124755858,"south":39.85572865909662,"north":39.97356831014807
        int colmin=(int)Math.ceil((116.24050140380858-115.417284)/width);
        int colmax=(int)Math.ceil((116.57009124755858-115.417284)/width);
        int rowmin=(int)Math.ceil((39.85572865909662-39.438283)/length);
        int rowmax=(int)Math.ceil((39.97356831014807-39.438283)/length);
        System.out.println(colmin+","+colmax+","+rowmin+","+rowmax);

        JSONObject condition=new JSONObject();
        condition.put("N",20);
        condition.put("rowmax",rowmax);
        condition.put("rowmin",rowmin);
        condition.put("colmax",colmax);
        condition.put("colmin",colmin);
        condition.put("startyear",2015);
        condition.put("startmonth",10);
        condition.put("endyear",2015);
        condition.put("endmonth",12);
        condition.put("source","woaiwojia");
        condition.put("export_collName","GridData_Resold_100");

        System.out.println(callMongo(condition));
    }
}
