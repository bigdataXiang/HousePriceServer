package com.reprocess.grid_100.interpolation;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.reprocess.grid_100.Code_Price_RowCol;
import com.svail.bean.Response;
import com.svail.db.db;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import utils.FileTool;
import utils.UtilFile;

import java.math.BigDecimal;
import java.util.*;

import static com.reprocess.grid_100.GridMerge.codeMapping100toN00;
import static com.reprocess.grid_100.ResoldGridClassify.setColorRegion;

/**
 * Created by ZhouXiang on 2016/9/5.
 */
public class InterestedRegion {
    public static Map<Integer, JSONObject> jsonArray_map=new HashMap<>();//用于存放北京区域内N00*N00分辨率时的每个网格的时序数据
    public static Map<String, Map<String, Double>> dataset = new HashMap<>();//dataset的key是网格的code，value是网格对应的时间价格序列值
    public Map<String, Map<String, Double>> getDataSet() {
        return dataset;
    }

    public static void main(String[] args){

 /*       *//**1、先生成整个北京区域内的每个网格的时序数据，存放刚到jsonArray_map中*//*
        JSONObject condition=new JSONObject();
        condition.put("N",5);
        condition.put("source","woaiwojia");
        condition.put("export_collName","GridData_Resold_100");
        getAllGridSeriesValue(condition);

        *//**2、设置好调用数据库的参数*//*
        condition=condition();

        *//**3、返回有缺失值的网格的编码*//*
        JSONArray lack_value_grid=findNullGrid(condition);//["44553","44556","44563","44564","44566","45364","45365","46157","46159","46161","46166","44157","44161","44163","44165","44954","45754","45765","46560","46561","44565","44953","44955","45356"]
*/

        String path="D:\\github.com\\bigdataXiang\\HousePriceServer\\src\\com\\reprocess\\grid_100\\interpolation\\";
        /**4、初始化数据集*/
        Vector<String> gridmap=FileTool.Load(path+"gridmap.txt","utf-8");
        JSONObject code_timevalue;
        for(int i=0;i<gridmap.size();i++){
            code_timevalue=JSONObject.fromObject(gridmap.elementAt(i));
            initDataSet(code_timevalue);
        }

        /*//打印数据集
        for (Map.Entry<String, Map<String, Double>> p1 : dataset.entrySet()) {
            System.out.println(p1.getKey());
        }
*/


        /**5、6、计算有缺失数据的网格与全部网格的相关系数 r ,并且返回相关性最强的20个*/
        Vector<String> grids= FileTool.Load(path+"lackvalue_grid.txt","utf-8");
        JSONArray lackvalue_grids=JSONArray.fromObject(grids.elementAt(0));
        JSONObject code_relatedCode=findRelatedCode(lackvalue_grids);

        /**7、计算单个缺失数据的网格与其他网格的相关性系数*/
        codesCovariance(code_relatedCode);


    }
    public static int getResolution(int zoom){
        int N;
        switch(zoom){
            case 18:N=1;
                break;
            case 17:N=2;
                break;
            case 16:N=3;
                break;
            case 15:N=5;
                break;
            case 14:N=10;
                break;
            case 13:N=20;
                break;
            case 12:N=30;
                break;
            case 11:N=40;
                break;
            default:N=50;
                break;
        }
        return N;
    }
    /**1.生成北京区域内N00*N00分辨率时的每个网格的时序数据,并且存放在jsonArray_map中*/
    public static void getAllGridSeriesValue(JSONObject condition){
        int N=condition.getInt("N");

        String collName=condition.getString("export_collName");
        DBCollection coll = db.getDB().getCollection(collName);
        BasicDBObject document = new BasicDBObject();

        String source=condition.getString("source");
        document.put("source",source);
        List code_array=coll.find(document).toArray();
        //System.out.println(code_array.size());

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

        Map<String,String> codekey= new HashMap<>();
        if(it.hasNext()){
            while (it.hasNext()){


                code=(int)it.next();
                codekey.put(""+code,"");
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

        //全部区域内网格的行列号加上去
        JSONArray nullgrid=new JSONArray();//用来装那些有缺失数据的点
        String key="";
        JSONObject value;
        JSONObject comparedata=new JSONObject();
        Iterator it_totalgrid=totalgrid.keys();

        if(it_totalgrid.hasNext()){
            while (it_totalgrid.hasNext()){
                key=(String) it_totalgrid.next();
                value=totalgrid.getJSONObject(key);
                jsonArray_map.put(Integer.parseInt(key),value);
                String rowcol=code_index_rowcol.getString(key);
                int rows=Integer.parseInt(rowcol.substring(0,rowcol.indexOf("_")));
                int cols=Integer.parseInt(rowcol.substring(rowcol.indexOf("_")+"_".length()));

                comparedata.put("code",Integer.parseInt(key));
                comparedata.put("timeseries",value);
                comparedata.put("row",rows);
                comparedata.put("col",cols);
            }
        }
    }

    /**2、设置好调用数据库的参数*/
    public static JSONObject condition(){
        double width=0.0011785999999997187;//每100m的经度差
        double length=9.003999999997348E-4;//每100m的纬度差

        int colmin=(int)Math.ceil((116.31834983825684-115.417284)/width);
        int colmax=(int)Math.ceil((116.40074729919434-115.417284)/width);
        int rowmin=(int)Math.ceil((39.934223203947056-39.438283)/length);
        int rowmax=(int)Math.ceil((39.96366837052331-39.438283)/length);

        JSONObject condition=new JSONObject();
        condition.put("N",5);
        condition.put("rowmax",rowmax);
        condition.put("rowmin",rowmin);
        condition.put("colmax",colmax);
        condition.put("colmin",colmin);
        condition.put("startyear",2015);
        condition.put("startmonth",10);
        condition.put("endyear",2016);
        condition.put("endmonth",5);
        condition.put("source","woaiwojia");
        condition.put("export_collName","GridData_Resold_100");

        return condition;

    }

    /**3、找出屏幕范围内有空值的网格*/
    public static JSONArray findNullGrid(JSONObject condition){

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

           // System.out.println(document);
            code_array=coll.find(document).toArray();
           // System.out.println("第一年数据："+code_array.size());

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

            //System.out.println(document);
            code_array_temp=coll.find(document).toArray();
            //System.out.println("第二年数据："+code_array_temp.size());

            //将code_array_temp中的数据添加到code_array中
            code_array.addAll(code_array_temp);

           // System.out.println("合并后的list的大小："+code_array.size());
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

        Map<String,String> codekey= new HashMap<>();
        if(it.hasNext()){
            while (it.hasNext()){


                code=(int)it.next();
                codekey.put(""+code,"");
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

        //将屏幕范围内数据有缺失的网格整理一下
        JSONArray nullgrid=new JSONArray();//用来装那些有缺失数据的点
        String key="";
        JSONObject value;
        JSONObject comparedata=new JSONObject();
        Iterator it_totalgrid=totalgrid.keys();
        if(it_totalgrid.hasNext()){

            while (it_totalgrid.hasNext()){
                key=(String) it_totalgrid.next();
                value=totalgrid.getJSONObject(key);

                //将数据不足八个月的都放到nullgrid里面去，要进行插值的
                if(value.size()!=8){
                    nullgrid.add(key);
                    //System.out.println(value);
                }
            }
        }

        JSONObject nullobj;
        int nullcount=0;
        for(int i=r_min;i<=r_max;i++) {
            for (int j =c_min; j<=c_max; j++) {
                String codeindex=""+(j + (2000/N) * (i - 1));
                if(!codekey.containsKey(codeindex)){
                    nullgrid.add(codeindex);
                    //System.out.println(nullobj);
                }

            }
        }
        System.out.println("有缺失数据的网格数目："+nullgrid.size());
        System.out.println("有缺失数据的网格数目："+nullgrid);
        return nullgrid;
    }

    /**4、初始化DataSet数据集，其实第四步和第一步可以融合一下，去所里再更改一下*/
    public static void initDataSet(JSONObject code_timevalue) {

        String code=code_timevalue.getString("code");
        JSONObject timeseries=code_timevalue.getJSONObject("timeseries");

        Map<String, Double> timevalue_map = new HashMap<String, Double>();
        Iterator it = timeseries.keys();
        String key_date="";
        double value_price=0;
        if(it.hasNext()){
            while (it.hasNext()){
                key_date=(String) it.next();
                value_price=timeseries.getDouble(key_date);
                timevalue_map.put(key_date, value_price);
            }
        }

        //System.out.println(code+"  "+timevalue_map.size());
        dataset.put(code, timevalue_map);
    }

    /**5、计算两两code之间的皮尔逊相关度值 皮尔逊相关系数=协方差(x,y)/[标准差(x)*标准差(y)]*/
    public static double pearson(String self_code, String related_code) {
        // 找出双方都有的数据,（皮尔逊算法要求）
        List<String> list = new ArrayList<String>();
        if(dataset.containsKey(self_code)&&dataset.containsKey(related_code)){

            for (Map.Entry<String, Double> p1 : dataset.get(self_code).entrySet()) {
                if (dataset.get(related_code).containsKey(p1.getKey())) {
                    list.add(p1.getKey());
                }
            }
        }


        int N = list.size();//如果是对总体的计算一般使用n，如果是对样本的计算一般使用n-1，这样是对总体的无偏估计
        double r;

        //有可能存在两组数就没有完全相同的时间点，故这里是需要判断的
        if(N!=0){
            double sumX = 0.0;
            double sumY = 0.0;
            double sumX_Sq = 0.0;
            double sumY_Sq = 0.0;
            double sumXY = 0.0;

            for (String name : list) {
                Map<String, Double> p1Map = dataset.get(self_code);
                Map<String, Double> p2Map = dataset.get(related_code);

                sumX += p1Map.get(name);
                sumY += p2Map.get(name);
                sumX_Sq += Math.pow(p1Map.get(name), 2);
                sumY_Sq += Math.pow(p2Map.get(name), 2);
                sumXY += p1Map.get(name) * p2Map.get(name);
            }

            //System.out.println("sumXY:"+sumXY);
           // System.out.println("sumX * sumY/N:"+sumX * sumY/N);
            double numerator = sumXY - sumX * sumY / N;
            double denominator = Math.sqrt((sumX_Sq - sumX * sumX / N)
                    * (sumY_Sq - sumY * sumY / N));

            // 分母不能为0
            if (denominator == 0) {
                return 0;
            }

            r= numerator / denominator;

        }else{
            r=0;
        }
        return  r;
    }

    /**6、计算lackdata_code与全区域的其他网格的皮尔逊系数 r ，并且返回20个相关系数最高的值，有些code之间的相关系数高是因为本身数据量少，故要做二次筛选 */
    public static JSONObject findRelatedCode(JSONArray lackvalue_grids){

        double r=0;
        String lackdata_code="";
        JSONObject r_adjacentcode;
        JSONObject interpolation_singlecode=new JSONObject();

        for(int i=0;i<lackvalue_grids.size();i++){

            lackdata_code=lackvalue_grids.getString(i);
            //System.out.println("lackdata_code:"+lackdata_code);

            List rlist=new ArrayList<>();
            for (Map.Entry<String, Map<String, Double>> ds : dataset.entrySet()) {

                String related_code=ds.getKey();
                int size=dataset.get(related_code).size();

                if(lackdata_code.equals(related_code)){

                }else if(size>7){//选取本身时间连续性比较好的网格进行相关性计算
                    r=pearson(lackdata_code,related_code);

                    //选取相关性大于0.5的网格
                    if(r>0.9&&r<1){
                        r_adjacentcode = new JSONObject();
                        r_adjacentcode.put("code", related_code);
                        r_adjacentcode.put("r",r);
                        rlist.add(r_adjacentcode);
                    }
                }
            }
            //加入list之前先根据相关性进行排序
            Collections.sort(rlist, new UtilFile.RComparator());

            //取相关性最高的十个code
            List list_10=new ArrayList<>();
            if(rlist.size()>10){
                for(int rl=rlist.size()-1;rl>rlist.size()-11;rl--){
                    list_10.add(rlist.get(rl));
                }
            }else {
                list_10.addAll(rlist);//将rlist全部复制到list_10中
            }

            if(list_10.size()==0){
                //System.out.println("要搞清楚"+lackdata_code+"为什么与所有的网格相关性都是0");
            }else {
                System.out.println(lackdata_code+" : "+list_10.size()+" "+list_10);
                interpolation_singlecode.put(lackdata_code,list_10);
            }
        }
        return interpolation_singlecode;
    }

    /**7、计算两个网格之间的协方差*/
    public static double covariance(String code1, String code2){

        // 找出双方都有的数据
        List<String> list = new ArrayList<String>();
        for (Map.Entry<String, Double> p1 : dataset.get(code1).entrySet()) {
            if (dataset.get(code2).containsKey(p1.getKey())) {
                list.add(p1.getKey());
            }
        }

        //System.out.println(list);
        int N = list.size();
        double cov;
        double cov_temp;

        if(N!=0){
            double sumX = 0.0;
            double sumY = 0.0;
            double sumXY = 0.0;

            for (String name : list) {
                Map<String, Double> p1Map = dataset.get(code1);
                Map<String, Double> p2Map = dataset.get(code2);

                sumX += p1Map.get(name);
                sumY += p2Map.get(name);
                sumXY += p1Map.get(name) * p2Map.get(name);
            }
            /*System.out.println(sumXY);
            System.out.println(sumX * sumY / N);*/

            cov_temp=(1/(double)N);
            BigDecimal b = new BigDecimal(cov_temp*(sumXY - sumX * sumY / N));
            cov = b.setScale(6, BigDecimal.ROUND_HALF_UP).doubleValue();
            //System.out.println(cov);
            //cov =cov_temp*(sumXY - sumX * sumY / N);

        }else {
            cov=0;
        }

        return cov;
    }

    /**8、计算单个缺失数据的网格与其他网格的相关性系数*/
    public static void codesCovariance(JSONObject code_relatedCode){

        String lackdata_code;
        List related_list;
        JSONObject related_code_json;
        String related_code;
        Iterator it=code_relatedCode.keys();
        double lackdata_related_cov;

        if(it.hasNext()){
            while(it.hasNext()){

                lackdata_code=(String) it.next();
                related_list=code_relatedCode.getJSONArray(lackdata_code);

                System.out.println("lackdata_code:"+lackdata_code);
                printDataSetMap(lackdata_code);

                for(int i=0;i<related_list.size();i++){
                    related_code_json=JSONObject.fromObject(related_list.get(i));
                    related_code=related_code_json.getString("code");

                    System.out.println("related_code:"+related_code);
                    printDataSetMap(related_code);

                    lackdata_related_cov=covariance(lackdata_code,related_code);
                    System.out.println(lackdata_code+"和"+related_code+":"+lackdata_related_cov);
                }

            }
        }
    }

    /**打印dataset里面的map值*/
    public static void printDataSetMap(String dataset_key){
        Map<String, Double> map=dataset.get(dataset_key);
        for (Map.Entry<String, Double> p : map.entrySet()) {
            System.out.print(p.getKey()+":"+p.getValue()+" ; ");
        }
        System.out.println("\n");
    }





}
