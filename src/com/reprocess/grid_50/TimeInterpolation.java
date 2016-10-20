package com.reprocess.grid_50;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.reprocess.grid_100.interpolation.InformationFusion;
import com.reprocess.grid_100.interpolation.NiMatrix;
import com.reprocess.grid_100.interpolation.SpatialInterpolation;
import com.reprocess.grid_100.util.RowColCalculation;
import com.svail.db.db;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import utils.FileTool;
import utils.UtilFile;

import java.math.BigDecimal;
import java.util.*;

import static com.reprocess.grid_100.GridMerge.codeMapping100toN00;
import static com.reprocess.grid_100.util.RowColCalculation.codeMapping50toN50;

/**
 * Created by ZhouXiang on 2016/9/8.
 */
public class TimeInterpolation {
    public static Map<String, Map<Integer, Double>> dataset_time_codeprice = new HashMap<>();
    public static Map<String, Map<String, Double>> dataset = new HashMap<>();
    public static Map<Integer, JSONObject> jsonArray_map=new HashMap<>();//用于存放北京区域内N00*N00分辨率时的每个网格的时序数据
    public static Map<String, JSONObject> interpolation_grids=new HashMap<>();
    public static Map<String, Map<String, Double>> interpolation_result= new HashMap<>();
    public static JSONArray failed_interpolation_codes=new JSONArray();
    public static JSONArray qualified_interpolation_codes=new JSONArray();
    public static Map<Integer, JSONObject> interpolation_value_grids=new HashMap<>();

    public static void main(String[] args){

        getInterpolationResult();
    }
    /**整个插值的过程汇总，最后求得每一个月份的每个网格的插值结果*/
    public static void getInterpolationResult(){
        step_1();
        step_2();
        step_3();
        step_4();
        step_5();
        step_6();
    }

    /**step_1:先生成整个北京区域内的每个网格的时序数据，存放刚到jsonArray_map中,使得全局变量jsonArray_map有值*/
    public static void step_1(){

        JSONObject condition=new JSONObject();
        condition.put("N",1);
        condition.put("source","woaiwojia");
        condition.put("export_collName","GridData_Resold_50");
        getAllGridSeriesValue(condition);
    }
    /**step_2:初始化数据集dataset*/
    public static void step_2(){
        int code;
        JSONObject date_price;
        for (Map.Entry<Integer, JSONObject> entry : jsonArray_map.entrySet()) {
            code=entry.getKey();
            date_price=entry.getValue();
            initDataSet(""+code,date_price,dataset);
        }
    }
    /**step_3:通过逐月访问数据库，初始化dataset_time_codeprice*/
    public static void step_3(){
        String[] dates={"2015-11","2015-10","2016-3","2016-2","2016-5","2015-12","2016-4","2016-1"};
        int year;
        int month;
        JSONObject obj;
        for(int i=0;i<dates.length;i++){
            String d=dates[i];
            year=Integer.parseInt(d.substring(0,d.indexOf("-")));
            month=Integer.parseInt(d.substring(d.indexOf("-")+"-".length()));
            obj=CallMongo(year,month,"GridData_Resold_50","woaiwojia",1);
            initDataset_time_codeprice(obj);
        }
    }
    /**step_4:求出每个月的相关月份的r值*/
    public static JSONObject step_4(){
        String[] dates={"2015-11","2015-10","2016-3","2016-2","2016-5","2015-12","2016-4","2016-1"};
        JSONObject month_relatedMonth=findRelatedMonth(dates);
        return month_relatedMonth;
    }
    /**计算所有有缺失数据的网格的插值结果，并且将最终的结果存一份存到interpolation_result中*/
    public static JSONObject step_5(){
        JSONObject month_relatedMonth=step_4();
        JSONObject time=monthsCovariance(month_relatedMonth);

        Iterator iterator=time.keys();
        String date;
        JSONObject codeseries;
        if(iterator.hasNext()){
            while (iterator.hasNext()){
                date=iterator.next().toString();
                codeseries=time.getJSONObject(date);
                Iterator it=codeseries.keys();
                while(it.hasNext()){
                    int code=Integer.parseInt(it.next().toString());
                    double price=codeseries.getDouble(""+code);
                    if(interpolation_result.containsKey(""+code)){
                        Map<String, Double> date_price=interpolation_result.get(""+code);
                        date_price.put(date,price);
                        interpolation_result.put(""+code,date_price);

                    }else {
                        Map<String, Double> date_price=new HashMap<>();
                        date_price.put(date,price);
                        interpolation_result.put(""+code,date_price);
                    }
                }
            }
        }
        return time;
    }
    /**step_6:计算interpolation_result中每个网格插值前后的mae的值，并且将mae的值较大的挑选出来,存在failed_interpolation_codes中*/
    public static void step_6(){
        String code;
        double mse;
        double rmse;
        double mae;

        for (Map.Entry<String, Map<String, Double>> entry : interpolation_result.entrySet()) {
            code=entry.getKey();
            mse=errorCalculation(code)[0];
            rmse=errorCalculation(code)[1];
            mae=errorCalculation(code)[2];

            if(mae>1){
                failed_interpolation_codes.add(code);
            }else {
                qualified_interpolation_codes.add(code);
                FileTool.Dump(code,"D:\\中期考核\\grid50\\时间插值B\\mae大于1.txt","utf-8");

                String str=code+","+mse+","+rmse+","+mae;
                FileTool.Dump(str,"D:\\中期考核\\grid50\\时间插值B\\插值误差.txt","utf-8");
            }
        }
        System.out.println("插值失败的网格有："+qualified_interpolation_codes.size());
    }
    /**step_7:将插值结果符合(即mse小于0.25、或者mae小于1)的网格进行插值操作*/
    public static void step_7(){
        int size=qualified_interpolation_codes.size();
        String code;
        for(int i=0;i<size;i++){
            code=qualified_interpolation_codes.getString(i);
            addInterpolation(code);
        }
    }



    /**1.生成北京区域内N50*N50分辨率时的每个网格的时序数据,并且存放在jsonArray_map中*/
    public static void getAllGridSeriesValue(JSONObject condition){
        int N=condition.getInt("N");

        String collName=condition.getString("export_collName");
        DBCollection coll = db.getDB().getCollection(collName);
        BasicDBObject document = new BasicDBObject();

        String source=condition.getString("source");
        document.put("source",source);
        List code_array=coll.find(document).toArray();
        //System.out.println(code_array.size());

        BasicDBObject doc;//doc里面存放的是网格编码经过融合的而成的poi数据
        int row_doc;
        int col_doc;
        int[] result_doc;
        List code_array_after=new ArrayList<>();//存放属于同一编码的doc数据
        Map<Integer,List> gridmap= new HashMap<>();//通过code与codelist的键值关系，建立索引
        List<BasicDBObject> codelist;//存放属于同一编码的doc数据
        int code;
        int row;
        int col;

        JSONObject code_index_rowcol=new JSONObject();
        for(int i=0;i<code_array.size();i++){
            doc= (BasicDBObject) code_array.get(i);
            doc.remove("_id");
            row_doc=doc.getInt("row");
            col_doc=doc.getInt("col");


            //将doc中的row、col、code从50分辨率的转换成N50分辨率的
            result_doc= codeMapping50toN50(row_doc,col_doc,N);
            row=result_doc[0];
            doc.put("row",row);
            col=result_doc[1];
            doc.put("col",col);
            code=result_doc[2];
            doc.put("code",code);
            String row_col=row+"_"+col;
            code_index_rowcol.put(code,row_col);

            //System.out.println("转换后的N50*N50的网格数据"+doc);
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
    /**2、初始化DataSet数据集:dataset的key为code值，value为一个子map(key:date,value:price)*/
    public static void initDataSet(String code,JSONObject timeseries,Map dataset) {


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
        dataset.put(code, timevalue_map);
    }
    /**3、按月份调用每个月都数值的网格code*/
    public static JSONObject CallMongo(int year,int month,String collName,String source, int N){


        DBCollection coll = db.getDB().getCollection(collName);
        BasicDBObject document = new BasicDBObject();
        String date=year+"-"+month;
        JSONObject obj=new JSONObject();
        obj.put("date",date);

        document.put("year",year);
        document.put("month",month);
        document.put("source",source);

        int code;
        BasicDBObject doc;
        int row_doc;
        int col_doc;
        int[] result_doc;
        Set codes_set=new HashSet();

        List code_array=coll.find(document).toArray();
        for(int i=0;i<code_array.size();i++){

            doc= (BasicDBObject) code_array.get(i);
            row_doc=doc.getInt("row");
            col_doc=doc.getInt("col");

            result_doc=codeMapping50toN50(row_doc,col_doc,N);
            code=result_doc[2];
            codes_set.add(code);
        }

        JSONArray codes_list=new JSONArray();
        //System.out.println("codes_set.size():"+codes_set.size());
        Iterator<Integer> it=codes_set.iterator();
        while(it.hasNext())
        {
            int o=it.next();
            codes_list.add(o);
        }
        obj.put("codes",codes_list);

        return obj;
    }
    /**4、初始化 Dataset_time_codeprice 数据集*/
    public static void initDataset_time_codeprice(JSONObject date_codes){

        String date=date_codes.getString("date");
        JSONArray codes=date_codes.getJSONArray("codes");
        int code;
        double price;

        Map<Integer, Double> code_price = new HashMap<Integer, Double>();
        Map<String, Double> timevalue_map = new HashMap<String, Double>();
        for(int i=0;i<codes.size();i++){
            code=(int)codes.get(i);
            if(dataset.containsKey(""+code)){
                timevalue_map=dataset.get(""+code);
                price=timevalue_map.get(date);
                code_price.put(code,price);
            }
        }
        dataset_time_codeprice.put(date,code_price);
    }
    /**5、求待插值的月份与其他月份的相关性 R ,并且找出五个相关性最强的月份*/
    public static JSONObject findRelatedMonth(String[] lackvalue_months){

        String lackdata_month="";
        String related_month=" ";
        double r;

        JSONObject interpolation_singlemonth=new JSONObject();

        for(int i=0;i<lackvalue_months.length;i++){
            lackdata_month=lackvalue_months[i];

            List rlist=new ArrayList<>();
            for(int j=0;j<lackvalue_months.length;j++){

                if(j!=i){
                    related_month=lackvalue_months[j];
                    r=pearson(lackdata_month,related_month);

                    JSONObject r_adjacent_month=new JSONObject();
                    r_adjacent_month.put("month",related_month);
                    r_adjacent_month.put("r",r);
                    rlist.add(r_adjacent_month);
                }
            }
            //加入list之前先根据相关性进行排序
            Collections.sort(rlist, new UtilFile.RComparator());

            //取相关性最高的5个月份的数据
            List list_10=new ArrayList<>();
            if(rlist.size()>5){
                for(int rl=rlist.size()-1;rl>rlist.size()-6;rl--){
                    list_10.add(rlist.get(rl));
                }
            }else {
                list_10.addAll(rlist);//将rlist全部复制到list_10中
            }

            interpolation_singlemonth.put(lackdata_month,list_10);
        }
        return interpolation_singlemonth;
    }
    /**6、求得每个月的插值数据*/
    public static JSONObject monthsCovariance(JSONObject month_relatedMonth){

        String lackdata_month;
        JSONArray related_list;
        JSONObject related_month_json;
        String related_month;

        Iterator it=month_relatedMonth.keys();
        double lackdata_related_cov;

        JSONObject interpolation=new JSONObject();

        if(it.hasNext()) {
            while (it.hasNext()) {
/**==================================求方程的右边一列（n+1）*1 和期望比 ======================================================*/
                lackdata_month=(String) it.next();
                related_list=month_relatedMonth.getJSONArray(lackdata_month);

                int N=related_list.size();
                double[][] C_y_n0=new double[N+1][1];
                double[][] C_y_nn=new double[N+1][N+1];
                double[][] C_y_nn_inverse=new double[N+1][N+1];
                double b_n0=0;
                double[][] w=new double[N+1][1];

                for(int i=0;i<N;i++){
                    related_month_json=JSONObject.fromObject(related_list.get(i));
                    related_month=related_month_json.getString("month");

                    lackdata_related_cov=covariance(lackdata_month,related_month);
                    C_y_n0[i][0]=lackdata_related_cov;

                    /**求周围时间片段与缺失时间片段的期望比*/
                    b_n0=expectRatio(lackdata_month,related_month);
                    C_y_nn[i][N]=b_n0;
                    C_y_nn[N][i]=b_n0;
                }
                C_y_n0[N][0]=1;//最后一列为1
                C_y_nn[N][N]=0;//矩阵的第（N+1）行和（N+1）列为0
                //System.out.print("\n");
/**==================================求方程的左边矩阵边一列 （n+1）*（n+1） ======================================================*/

                covarianceMatrix(C_y_nn,related_list);//求协方差矩阵
                NiMatrix inverse_matrix = new NiMatrix();
                C_y_nn_inverse=inverse_matrix.getNiMatrix(C_y_nn);//求C_y_nn的逆矩阵

                /** 求权重w */
                w= SpatialInterpolation.marixMultiply(C_y_nn_inverse,C_y_n0);
                //print2DArray(w);


                JSONObject obj=new JSONObject();
                double y0=0;
                Set<String> it_codes=dataset.keySet();
                for (String code : it_codes) {
                    y0=y0_EstimatedValue(w,related_list,Integer.parseInt(code));
                    obj.put(code,y0);
                }
                interpolation.put(lackdata_month,obj);
                interpolation_grids.put(lackdata_month,obj);
            }
        }
        return interpolation;
    }
    /**7、计算该网格的真实值与插值的误差：MSE（均方误差）、RMSE（均方根误差）、MAE（平均绝对误差）*/
    public static double[] errorCalculation(String code){

        Map<String, Double> real_value_map=new HashMap<>();
        Map<String, Double> interpolation_value_map=new HashMap<>();
        if(dataset.containsKey(code)){
            real_value_map= dataset.get(code);
        }
        if(interpolation_result.containsKey(code)){
            interpolation_value_map=interpolation_result.get(code);
        }

        String date="";
        double real_price;
        double interpolation_price;
        double difference;
        double difference_2;
        double sum=0;
        int count=0;
        double total_difference=0;
        if(interpolation_value_map.size()!=0){
            for (Map.Entry<String, Double> p : real_value_map.entrySet()) {
                date=p.getKey();
                if (interpolation_value_map.containsKey(date)) {
                    real_price=real_value_map.get(date);
                    interpolation_price=interpolation_value_map.get(date);
                    difference=Math.abs(real_price-interpolation_price);
                    difference_2=Math.pow(difference,2);
                    sum+=difference_2;
                    count++;
                    total_difference+=difference;
                }
            }
        }

        double mse;
        double rmse;
        double mae;
        double[] error_test=new double[3];
        if(count!=0){
            mse=sum/count;
            rmse=Math.pow(mse,0.5);
            mae=total_difference/count;
        }else{
            mse=0;
            rmse=0;
            mae=0;
        }
        error_test[0]=mse;
        error_test[1]=rmse;
        error_test[2]=mae;

        return error_test;
    }
    /**8、将插值结果符合(即mse小于0.1)的网格进行插值操作:插值规则是如果该时间点的真实值缺乏，则用插值代替，否则采用真实值*/
    public static void addInterpolation(String code){

        Map<String, Double> real_value_map=new HashMap<>();
        Map<String, Double> interpolation_value_map=new HashMap<>();
        if(dataset.containsKey(code)&&interpolation_result.containsKey(code)){
            real_value_map=dataset.get(code);
            interpolation_value_map=interpolation_result.get(code);

            String date="";
            double real_price;
            double interpolation_price;

            JSONObject timeseries=new JSONObject();
            if(interpolation_value_map.size()!=0){
                for (Map.Entry<String, Double> p : interpolation_value_map.entrySet()) {

                    date=p.getKey();
                    if (real_value_map.containsKey(date)) {
                        real_price=real_value_map.get(date);
                        timeseries.put(date,real_price);

                        interpolation_price=interpolation_value_map.get(date);
                        //System.out.println(date+":"+real_price+" , "+interpolation_price);
                    }else{
                        interpolation_price=interpolation_value_map.get(date);
                        real_price=interpolation_price;//将真实值中没有的日期用插值中对应的日期的值来替代
                        timeseries.put(date,real_price);

                        //System.out.println(date+":"+real_price+" , "+interpolation_price);
                    }
                }
            }
            interpolation_value_grids.put(Integer.parseInt(code),timeseries);
        }
    }





    public static JSONObject getB(){
        JSONObject B=new JSONObject();

        String path="D:\\github.com\\bigdataXiang\\HousePriceServer\\src\\com\\reprocess\\grid_100\\interpolation\\";
        /**初始化数据集 dataset */
        Vector<String> gridmap= FileTool.Load(path+"gridmap.txt","utf-8");
        JSONObject code_timevalue;
        for(int i=0;i<gridmap.size();i++){
            code_timevalue=JSONObject.fromObject(gridmap.elementAt(i));
            initDataSet(code_timevalue);
        }

        /**初始化 dataset_time_codeprice数据集*/
        Vector<String> date_grid= FileTool.Load(path+"date_grid.txt","utf-8");
        JSONObject date_codes;
        for(int i=0;i<date_grid.size();i++){
            date_codes=JSONObject.fromObject(date_grid.elementAt(i));
            initDataset_time_codeprice(date_codes);
        }

        String[] dates={"2015-11","2015-10","2016-3","2016-2","2016-5","2015-12","2016-4","2016-1"};

        JSONObject month_relatedMonth=findRelatedMonth(dates);
        //System.out.println(month_relatedMonth);

        String key_code="";
        JSONArray value_objs;
        JSONObject obj;
        String code="";
        double r=0;

        Iterator it=month_relatedMonth.keys();
        if(it.hasNext()) {
            while (it.hasNext()) {

                key_code=(String) it.next();
                value_objs=month_relatedMonth.getJSONArray(key_code);

                int m=value_objs.size();
                double avenrage_r=0;
                double total_r=0;

                for(int i=0;i<m;i++){
                    obj=(JSONObject) value_objs.get(i);
                    code=obj.getString("month");
                    r=obj.getDouble("r");

                    total_r+=r;
                }
                avenrage_r=total_r/m;
                B.put(key_code,avenrage_r);
            }
        }
        return B;
    }

    /**3、初始化DataSet数据集*/
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
        dataset.put(code, timevalue_map);
    }

    /**5、求两两月份之间的皮尔逊值 r */
    public static double pearson(String self_month, String related_month){

        List<Integer> list = new ArrayList<Integer>();
        if(dataset_time_codeprice.containsKey(self_month)&&dataset_time_codeprice.containsKey(related_month)){

            for (Map.Entry<Integer, Double> p1 : dataset_time_codeprice.get(self_month).entrySet()) {
                if (dataset_time_codeprice.get(related_month).containsKey(p1.getKey())) {
                    list.add(p1.getKey());
                }
            }
        }

        int N = list.size();
        double r;

        if(N!=0){
            double sumX = 0.0;
            double sumY = 0.0;
            double sumX_Sq = 0.0;
            double sumY_Sq = 0.0;
            double sumXY = 0.0;

            for (Integer code : list) {
                Map<Integer, Double> p1Map = dataset_time_codeprice.get(self_month);
                Map<Integer, Double> p2Map = dataset_time_codeprice.get(related_month);

                sumX += p1Map.get(code);
                sumY += p2Map.get(code);
                sumX_Sq += Math.pow(p1Map.get(code), 2);
                sumY_Sq += Math.pow(p2Map.get(code), 2);
                sumXY += p1Map.get(code) * p2Map.get(code);
            }


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

    /**7、计算两个网格之间的协方差 用的是除以（N-1）*/
    public static double covariance(String month1, String month2){

        // 找出双方都有的网格code
        List<Integer> list = new ArrayList<Integer>();
        for (Map.Entry<Integer, Double> p1 : dataset_time_codeprice.get(month1).entrySet()) {
            if (dataset_time_codeprice.get(month2).containsKey(p1.getKey())) {
                list.add(p1.getKey());
            }
        }

        int N = list.size();
        double cov;
        double cov_temp;

        if(N!=0){
            double sumX = 0.0;
            double sumY = 0.0;
            double sumXY = 0.0;
            double avenrageX=0.0;
            double avenrageY=0.0;

            for (Integer code : list) {
                Map<Integer, Double> p1Map = dataset_time_codeprice.get(month1);
                Map<Integer, Double> p2Map = dataset_time_codeprice.get(month2);

                sumX += p1Map.get(code);
                sumY += p2Map.get(code);
                sumXY += p1Map.get(code) * p2Map.get(code);
            }

            avenrageX=sumX/N;
            avenrageY=sumY/N;

            /**用公式 cov(xy)=E(xy)-E(x)*E(y)=(1/n)*(x1*y1+...+xn*yn)-(1/(n*n))(x1+...+xn)(y1+...+yn)*/
            cov_temp=(1/(double)N);
            BigDecimal b = new BigDecimal(cov_temp*(sumXY - sumX * sumY / N));
            cov = b.setScale(6, BigDecimal.ROUND_HALF_UP).doubleValue();
            //System.out.println(cov);

            /**用公式 cov(xy)= (1/(N-1))*(sumXY-avenrageX*sumY)  */
            cov =(1/(double)(N-1))*(sumXY - avenrageX*sumY);
            //System.out.println(cov);

        }else {
            cov=0;
        }

        return cov;
    }

    /**9、求协方差矩阵*/
    public static void covarianceMatrix(double[][] C_y_nn,List related_list){

        String month_i="";
        String month_j="";
        double cov_ij;
        JSONObject related_month_json;

        for(int i=0;i<related_list.size();i++){

            related_month_json=JSONObject.fromObject(related_list.get(i));
            month_i=related_month_json.getString("month");

            for(int j=0;j<related_list.size();j++){

                related_month_json=JSONObject.fromObject(related_list.get(j));
                month_j=related_month_json.getString("month");

                cov_ij=covariance(month_i,month_j);
                C_y_nn[i][j]=cov_ij;
            }
        }
    }

    /**10、求数据时间缺失点的估计值*/
    public static double y0_EstimatedValue(double[][] w,JSONArray related_list,int code){

        double y0=0;
        int N=related_list.size();
        JSONObject related_month_json;
        String related_month;
        double avenrage_price=0;

        for(int i=0;i<N;i++){

            related_month_json=JSONObject.fromObject(related_list.get(i));
            related_month=related_month_json.getString("month");
            Map<Integer, Double> map=dataset_time_codeprice.get(related_month);

            for (Map.Entry<Integer, Double> p : map.entrySet()) {
                if(p.getKey().equals(code)){
                    avenrage_price=p.getValue()*w[i][0];
                }
            }
            y0+=avenrage_price;
        }
        return y0;
    }


    /**求周围时间片段和缺失时间片段的空间序列的期望比*/
    public static double expectRatio(String self_month, String related_month){

        double ratio=0;

        double sum_self_month = 0.0;
        double sum_related_month = 0.0;
        double avenrage_self_month=0.0;
        double avenrage_related_month=0.0;

        Map<Integer, Double> self_month_Map = dataset_time_codeprice.get(self_month);
        Map<Integer, Double> related_month_Map = dataset_time_codeprice.get(related_month);

        Collection self_month_values = self_month_Map.values();
        for (Object object_self_code : self_month_values)
        {
            sum_self_month+=(double)object_self_code;
        }
        avenrage_self_month=sum_self_month/self_month_values.size();

        Collection related_month_values = related_month_Map.values();
        for (Object object_related_code : related_month_values)
        {
            sum_related_month+=(double)object_related_code;
        }
        avenrage_related_month=sum_related_month/related_month_values.size();

        ratio=avenrage_related_month/avenrage_self_month;
        return ratio;
    }

    /**1、设置调用数据库的条件*/
    /*public static void conditions(){
        int year=2015;
        for(int month=10;month<=12;month++){
            JSONObject condition=new JSONObject();
            condition.put("year",year);
            condition.put("month",month);
            condition.put("source","woaiwojia");
            condition.put("export_collName","GridData_Resold_100");

            JSONObject result=new JSONObject();
            String date=year+"-"+month;
            List<Double> codes=CallMongo(condition,5);

            result.put("date",date);
            result.put("codes",codes);

            System.out.println(result);
        }

        year=2016;
        for(int month=1;month<=5;month++){
            JSONObject condition=new JSONObject();
            condition.put("year",year);
            condition.put("month",month);
            condition.put("source","woaiwojia");
            condition.put("export_collName","GridData_Resold_100");

            JSONObject result=new JSONObject();
            String date=year+"-"+month;
            List<Double> codes=CallMongo(condition,5);

            result.put("date",date);
            result.put("codes",codes);

            System.out.println(result);
        }
    }*/

    public static void print_dataset_time_codepriceMap(String date){

        Map<Integer, Double> map=dataset_time_codeprice.get(date);
        for (Map.Entry<Integer, Double> p : map.entrySet()) {
            System.out.print(p.getKey()+":"+p.getValue()+" ; ");
        }
        System.out.println("\n");
    }

}
