package com.reprocess.grid_50;

import com.mongodb.*;
import com.reprocess.grid_100.interpolation.NiMatrix;
import com.reprocess.grid_100.util.RowColCalculation;
import com.svail.db.db;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import utils.FileTool;
import utils.UtilFile;

import java.math.BigDecimal;
import java.util.*;

import static com.reprocess.grid_100.util.RowColCalculation.Code_RowCol;
import static com.reprocess.grid_100.util.RowColCalculation.codeMapping50toN50;

/**
 * 由50m*50m的网格生成插值之后的50*50的插值后的网格
 * 原始数据：GridData_Resold_50
 */
public class SpatialInterpolation extends NiMatrix{
    /**参数说明：
     *jsonArray_map：用于存放北京区域内N50*N50分辨率时的每个网格的时序数据
     *dataset:存放的是jsonArray_map中所有code的数据。其中key是网格的code，value是网格对应的时间价格序列值,这样方便检索
     *pearson_is_0：用来存储与其他网格相关系数为0的网格的code，这一步是在step_3的findRelatedCode中实现的这些相关系数为0的网格的存储
     *sparse_data：用来存储时序数据太少的稀疏数据，在step_3中实现
     *full_value_grids：step_2中用来存储时序数据满格的数据
     *interpolation_value_grids： step_8()里addInterpolation方法中用来存储插值成功后的网格数据，其中value值是插值与真实值混合的结果
     *interpolation_grids：step_4中codesCovariance方法中用来存储插值成功后的网格数据，其中value值是插值的结果
     *interpolation_result：interpolation_grids的另外一种存储形式，在step_4中通过codesCovariance方法返回的spatial进行赋值
     *failed_interpolation_codes：step_6中用来存储那些虽然参与插值，但是插值结果后的mse过大导致失败的code
     *qualified_interpolation_codes：用来存储那些参与插值，且插值后的mse合格的code
     **/
    public static Map<Integer, JSONObject> jsonArray_map=new HashMap<>();
    public static Map<String, Map<String, Double>> dataset = new HashMap<>();
    public Map<String, Map<String, Double>> getDataSet() {
        return dataset;
    }
    public static Map<String, String> pearson_is_0=new HashMap<>();
    public static Map<Integer, JSONObject> sparse_data=new HashMap<>();
    public static Map<Integer, JSONObject> full_value_grids=new HashMap<>();
    public static Map<Integer, JSONObject> interpolation_value_grids=new HashMap<>();
    public static Map<Integer, JSONObject> interpolation_grids=new HashMap<>();
    public static Map<String, Map<String, Double>> interpolation_result= new HashMap<>();
    public static JSONArray failed_interpolation_codes=new JSONArray();
    public static JSONArray qualified_interpolation_codes=new JSONArray();

    public static String path="D:\\github.com\\bigdataXiang\\HousePriceServer\\src\\com\\reprocess\\grid_50\\";

    public static void main(String[] args){


        //getInterpolationResult();
        //System.out.println(step_10());
        getInterpolation();
        //reNeighborInterpolation();

    }


    /**整个插值的过程汇总，最后求得每一个网格插值前和插值后的值对比*/
    public static void getInterpolationResult(){

        step_1();

        JSONArray lack_value_grids=step_2();

        JSONObject code_relatedCode=step_3(lack_value_grids,1);

        JSONObject spatial=step_4(code_relatedCode);

        step_5(spatial);

        step_6();


      //  step_7(); 打印对比真实值与插值

        step_8();

        dumpInterpolationResult();


      //  step_9(); 将最终结果存于数据库中
    }

    /**step_1:先生成整个北京区域内的每个网格的时序数据，存放刚到jsonArray_map中,使得全局变量jsonArray_map有值*/
    public static void step_1(){

        JSONObject condition=new JSONObject();
        condition.put("N",1);
        condition.put("source","woaiwojia");
        condition.put("export_collName","GridData_Resold_50");
        getAllGridSeriesValue(condition);
    }
    /**step_2:返回有缺失值的网格的编码，并且初始化数据集dataset*/
    public static JSONArray step_2(){

        JSONArray lack_value_grids=new JSONArray();
        int code;
        JSONObject date_price;
        int keys_size;

        //遍历全局变量jsonArray_map
        for (Map.Entry<Integer, JSONObject> entry : jsonArray_map.entrySet()) {
            code=entry.getKey();
            date_price=entry.getValue();
            keys_size=date_price.size();
            //System.out.println(date_price);

            //(1)返回有缺失的网格编码：如果不足八个月的数据，就要在后面进行插值
            if(keys_size!=8){
                lack_value_grids.add(code);
            }else {
                full_value_grids.put(code,date_price);
            }

            //(2)初始化数据集dataset
            initDataSet(""+code,date_price,dataset);
        }
        System.out.println("所有不足八个月的数据lack_value_grids的网格数目有"+lack_value_grids.size());
        return lack_value_grids;
    }
    /**step_3:计算有缺失数据的网格与全部网格的相关系数 r ,并且返回相关性最强的20个*/
    public static JSONObject step_3(JSONArray lack_value_grids,int datesnum){

        JSONArray to_be_interpolated=new JSONArray();
        int code;
        int size;
        JSONObject code_relatedCode=new JSONObject();
        for(int i=0;i<lack_value_grids.size();i++){
            code=(int)lack_value_grids.get(i);
            size=jsonArray_map.get(code).size();

            //如果待插值的网格本身数据稀疏，则不参与插值，而是存在全局变量sparse_data中，因为这样的插值效果不太好
            if(size>datesnum){
                to_be_interpolated.add(code);
            }else {
                sparse_data.put(code,jsonArray_map.get(code));
            }
        }

        code_relatedCode=findRelatedCode(to_be_interpolated);
        return code_relatedCode;

        //findRelatedCode方法中还实现将与所有网格的相关系数为0的网格code存放在pearson_is_0中
    }
    /**step_4:计算所有有缺失数据的网格的插值结果，并且将最终的结果存一份存到interpolation_result中*/
    //FileTool.Dump
    public static JSONObject step_4( JSONObject code_relatedCode){
        JSONObject spatial=codesCovariance(code_relatedCode);

        Iterator iterator=spatial.keys();
        String code;
        JSONObject timeseries;
        if(iterator.hasNext()){
            while (iterator.hasNext()){
                code=iterator.next().toString();
                timeseries=spatial.getJSONObject(code);
                initDataSet(""+code,timeseries,interpolation_result);
            }
        }
        return spatial;
    }
    /**step_5:统计插值情况，检查是否有遗漏的点*/
    public static void step_5(JSONObject spatial){
        int jsonArray_map_size=jsonArray_map.size();
        int spatial_size=spatial.size();
        int sparse_data_size=sparse_data.size();
        int pearson_is_0_size=pearson_is_0.size();

        int total=spatial_size+sparse_data_size+pearson_is_0_size;
        System.out.println("总共有数据的网格有：");
        System.out.println("jsonArray_map_size:"+jsonArray_map_size);

        System.out.println("\n其中：");
        System.out.println("  数据有缺失的网格有：");
        System.out.println("  lack_value_grids:"+step_2().size());
        System.out.println("  插值成功的网格有：");
        System.out.println("  spatial_size:"+spatial_size);
        System.out.println("  相关系数为0的网格有：");
        System.out.println("  pearson_is_0_size:"+pearson_is_0_size);
        System.out.println("  数据稀疏无法插值的网格有：");
        System.out.println("  sparse_data_size:"+sparse_data_size);
        System.out.println("total:"+total);

        System.out.println("\n此外：");
        System.out.println("  数据满格的网格有："+full_value_grids.size());

    }
    /**step_6:计算interpolation_result中每个网格插值前后的mse的值，并且将mse的值较大的挑选出来,存在failed_interpolation_codes中*/
    //FileTool.Dump
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

                //FileTool.Dump(code,"D:\\小论文\\插值完善\\mae大于1.txt","utf-8");
                //String str=code+","+mse+","+rmse+","+mae;
                //FileTool.Dump(str,"D:\\小论文\\插值完善\\插值误差.txt","utf-8");
            }else {
                qualified_interpolation_codes.add(code);
            }
            /*String str=code+","+mse+","+rmse+","+mae;
            FileTool.Dump(str,"D:\\中期考核\\grid50\\插值误差.txt","utf-8");
            */
        }

        System.out.println("mae>1："+failed_interpolation_codes.size());
        System.out.println("mae<1："+qualified_interpolation_codes.size());
    }
    /**step_7:比较mse、mae的值较大的code的真实值和插值，并且将其打印出来*/
    //FileTool.Dump
    public static void step_7(){
        int size=failed_interpolation_codes.size();
        String code;
        for(int i=0;i<size;i++){
            code=failed_interpolation_codes.getString(i);
            compareFailedCode(code);
        }
    }


    /**step_8:将插值结果符合(即mse小于0.25、或者mae小于1)的网格进行插值操作*/
    public static void step_8(){
        int size=qualified_interpolation_codes.size();
        String code;
        for(int i=0;i<size;i++){
            code=qualified_interpolation_codes.getString(i);
            addInterpolation(code);
        }
    }
    /**step_9:将插值后的结果转换成网格的形式存储于MongoDB(GridData_Resold_50_Interpolation表)中*/
    /**数据库中的数据有三种：
     * 一种是插值前就是满格的数据
     * 二种是插值后不超过限插的满格插值数据
     * 三种是排除以上两种情况的数据，存储其本身的真实值
     * */
    public static void step_9(){
        toMongoDB("GridData_Resold_50_Interpolation_1114",1,"woaiwojia");
    }

    /**step_10:计算空间插值的权重A*/
    public static JSONObject step_10(){

        /**1、初始化数据集*/
        step_1();
        JSONArray lack_value_grids=step_2();
        /**2、计算有缺失数据的网格与全部网格的相关系数 r ,并且返回相关性最强的10个*/
        JSONObject code_relatedCode=step_3(lack_value_grids,1);

        Iterator it=code_relatedCode.keys();
        String key_code="";
        JSONArray value_objs;
        JSONObject obj;
        String code="";
        double r=0;

        JSONObject A=new JSONObject();
        if(it.hasNext()){
            while(it.hasNext()){
                key_code=(String) it.next();
                value_objs=code_relatedCode.getJSONArray(key_code);

                int n=value_objs.size();
                double avenrage_r=0;
                double total_r=0;
                for(int i=0;i<n;i++){
                    obj=(JSONObject) value_objs.get(i);
                    code=obj.getString("code");
                    r=obj.getDouble("r");

                    total_r+=r;
                }

                avenrage_r=total_r/n;
                A.put(key_code,avenrage_r);
            }
        }

        return A;
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
            result_doc= RowColCalculation.codeMapping50toN50(row_doc,col_doc,N);
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
    /**2、设置好调用数据库的参数*/
    public static JSONObject condition(double west,double east,double south,double north){
        double width=0.0011785999999997187;//每100m的经度差
        double length=9.003999999997348E-4;//每100m的纬度差

        int colmin=(int)Math.ceil((west-115.417284)/width);
        int colmax=(int)Math.ceil((east-115.417284)/width);
        int rowmin=(int)Math.ceil((south-39.438283)/length);
        int rowmax=(int)Math.ceil((north-39.438283)/length);

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

            //将doc中的row、col、code从50分辨率的转换成N50分辨率的
            result_doc=codeMapping50toN50(row_doc,col_doc,N);
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

        //统计gridmap中一共有多少个网格
        //System.out.println("共有"+gridmap.size()+"个网格");


        String date;
        JSONObject obj;
        Map<String,List> timeprice_map= new HashMap<>();
        List<Double> average_price_list=new ArrayList<>();
        double average_price;
        List<JSONObject> timeseries_price=new ArrayList<>();
        JSONObject date_price;
        JSONObject totalgrid=new JSONObject();//存放的是所有网格的唯一时间价格数据
        Map<String,String> codekey= new HashMap<>();//主要是用来存放那些有数据的网格，好到时候通过遍历找出那些空值的网格

        //对gridmap中每个网格的doc作操作
        Iterator it=gridmap.keySet().iterator();
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
                String codeindex=""+(j + (4000/N) * (i - 1));
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
    /**4、初始化DataSet数据集:dataset的key为code值，value为一个子map(key:date,value:price)*/
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
    /**6、计算lackdata_code与全区域的其他网格的皮尔逊系数 r ，并且返回10个相关系数最高的值，有些code之间的相关系数高是因为本身数据量少，故要做二次筛选
     * 此方法还将网格与其他网格的相关系数为0的code存于pearson_is_0中*/
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

                    //选取相关性大于0.9的网格
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
           // System.out.println(rlist);

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
                pearson_is_0.put(lackdata_code,"");
            }else {
                //System.out.println(lackdata_code+" : "+list_10.size()+" "+list_10);
                interpolation_singlecode.put(lackdata_code,list_10);
            }
        }
        return interpolation_singlecode;
    }
    /**7、计算两个网格之间的协方差 用的是除以（N-1）*/
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
            double avenrageX=0.0;
            double avenrageY=0.0;

            for (String name : list) {
                Map<String, Double> p1Map = dataset.get(code1);
                Map<String, Double> p2Map = dataset.get(code2);

                sumX += p1Map.get(name);
                sumY += p2Map.get(name);
                sumXY += p1Map.get(name) * p2Map.get(name);
            }
            /*System.out.println(sumXY);
            System.out.println(sumX * sumY / N);*/

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
    /**8、计算单个缺失数据的网格与其他网格的相关性系数*/
    public static JSONObject codesCovariance(JSONObject code_relatedCode){

        String lackdata_code;
        JSONArray related_list;
        JSONObject related_code_json;
        String related_code;
        Iterator it=code_relatedCode.keys();
        double lackdata_related_cov;

        JSONObject interpolation=new JSONObject();

        if(it.hasNext()){
            while(it.hasNext()){
/**==================================求方程的右边一列（n+1）*1 和期望比 ======================================================*/
                lackdata_code=(String) it.next();
                related_list=code_relatedCode.getJSONArray(lackdata_code);

                int N=related_list.size();
                double[][] C_y_n0=new double[N+1][1];
                double[][] C_y_nn=new double[N+1][N+1];
                double[][] C_y_nn_inverse=new double[N+1][N+1];
                double b_n0=0;
                double[][] w=new double[N+1][1];

                for(int i=0;i<N;i++){
                    related_code_json=JSONObject.fromObject(related_list.get(i));
                    related_code=related_code_json.getString("code");

                    lackdata_related_cov=covariance(lackdata_code,related_code);
                    C_y_n0[i][0]=lackdata_related_cov;

                    /**求周围点与缺失点的期望比*/
                    b_n0=expectRatio(lackdata_code,related_code);
                    C_y_nn[i][N]=b_n0;
                    C_y_nn[N][i]=b_n0;
                }
                C_y_n0[N][0]=1;//最后一列为1
                C_y_nn[N][N]=0;//矩阵的第（N+1）行和（N+1）列为0
/**==================================求方程的左边矩阵边一列 （n+1）*（n+1） ======================================================*/

                covarianceMatrix(C_y_nn,related_list);//求协方差矩阵

                NiMatrix inverse_matrix = new NiMatrix();
                C_y_nn_inverse=inverse_matrix.getNiMatrix(C_y_nn);//求C_y_nn的逆矩阵


                /** 求权重w */
                w=marixMultiply(C_y_nn_inverse,C_y_n0);

                String[] dates={"2015-11","2015-10","2016-3","2016-2","2016-5","2015-12","2016-4","2016-1"};
                double y0=0;

                JSONObject obj=new JSONObject();

                for (int i=0;i<dates.length;i++){
                    y0=y0_EstimatedValue(w,related_list,dates[i]);

                    obj.put(dates[i],y0);
                }
                interpolation.put(lackdata_code,obj);
                interpolation_grids.put(Integer.parseInt(lackdata_code),obj);

                FileTool.Dump(lackdata_code+":"+obj.toString(),"D:\\中期考核\\grid50\\所有网格的插值结果.txt","utf-8");
          }
        }

        return interpolation;
    }
    /**9、求协方差矩阵*/
    public static void covarianceMatrix(double[][] C_y_nn,List related_list){

        String code_i="";
        String code_j="";
        double cov_ij;
        JSONObject related_code_json;

        for(int i=0;i<related_list.size();i++){

            related_code_json=JSONObject.fromObject(related_list.get(i));
            code_i=related_code_json.getString("code");

            for(int j=0;j<related_list.size();j++){

                related_code_json=JSONObject.fromObject(related_list.get(j));
                code_j=related_code_json.getString("code");

                cov_ij=covariance(code_i,code_j);
                C_y_nn[i][j]=cov_ij;
            }
        }
    }
    /**10、求周围采样点和缺失数据点的时间序列的期望比*/
    public static double expectRatio(String self_code, String related_code){

        double ratio=0;

        double sum_self_code = 0.0;
        double sum_related_code = 0.0;
        double avenrage_self_code=0.0;
        double avenrage_related_code=0.0;

        Map<String, Double> self_code_Map = dataset.get(self_code);
        Map<String, Double> related_code_Map = dataset.get(related_code);

        Collection self_code_values = self_code_Map.values();
        for (Object object_self_code : self_code_values)
        {
            sum_self_code+=(double)object_self_code;
        }
        avenrage_self_code=sum_self_code/self_code_values.size();

        Collection related_code_values = related_code_Map.values();
        for (Object object_related_code : related_code_values)
        {
            sum_related_code+=(double)object_related_code;
        }
        avenrage_related_code=sum_related_code/related_code_values.size();

        ratio=avenrage_related_code/avenrage_self_code;

        //System.out.println(ratio);

        return ratio;
    }
    /**11、打印dataset里面的map值*/
    public static void printDataSetMap(String dataset_key){
        Map<String, Double> map=dataset.get(dataset_key);
        for (Map.Entry<String, Double> p : map.entrySet()) {
            System.out.print(p.getKey()+" : "+p.getValue()+" ; ");
        }
        System.out.println("\n");
    }
    /**12、打印二维数组*/
    public static void print2DArray(double[][] C_y_nn){
        for(int i=0;i<C_y_nn.length;i++){
            for(int j=0;j<C_y_nn[i].length;j++){
              System.out.print(C_y_nn[i][j]+" , ");
            }
            System.out.print("\n");
        }
    }
    /**13、计算矩阵a与矩阵b的乘积*/
    public static double[][] marixMultiply(double a[][], double b[][]) {
        if (a == null || b == null || a[0].length != b.length) {
            throw new IllegalArgumentException("matrix is illegal");
        }
        int row = a.length;
        int column = b[0].length;
        int multiplyC = a[0].length;
        double[][] result = new double[row][column];

        for (int m = 0; m < row; m++)
            for (int n = 0; n < column; n++)
                for (int i = 0; i < multiplyC; i++) {
                    result[m][n] += a[m][i] * b[i][n];
                }
        return result;
    }
    /**14、打印分隔符*/
    public static void printSeparator(int N){
        StringBuffer space= new StringBuffer();
        for(int i= 0;i<N;i++)
        {
            space.append("-");
            space.append("-");
            space.append("*");
            space.append("-");
            space.append("-");
        }
        System.out.println(space.toString());
    }
    /**15、求数据缺失点的估计值*/
    public static double y0_EstimatedValue(double[][] w,JSONArray related_list,String date){

        double y0=0;
        int N=related_list.size();
        JSONObject related_code_json;
        String related_code;
        double avenrage_price=0;

        for(int i=0;i<N;i++){

            related_code_json=JSONObject.fromObject(related_list.get(i));
            related_code=related_code_json.getString("code");
            Map<String, Double> map=dataset.get(related_code);

            for (Map.Entry<String, Double> p : map.entrySet()) {
                if(p.getKey().equals(date)){
                    //System.out.println(w[i][0]);
                    //System.out.println(p.getValue());
                    avenrage_price=p.getValue()*w[i][0];
                }
            }
            y0+=avenrage_price;
        }
        return y0;
    }
    /**16、计算出时空插值中空间插值的权重 A */
    public static JSONObject getA(){

        String path="D:\\github.com\\bigdataXiang\\HousePriceServer\\src\\com\\reprocess\\grid_100\\interpolation\\";
        /**4、初始化数据集*/
        Vector<String> gridmap=FileTool.Load(path+"gridmap.txt","utf-8");
        JSONObject code_timevalue;
        for(int i=0;i<gridmap.size();i++){
            code_timevalue=JSONObject.fromObject(gridmap.elementAt(i));
            String code=code_timevalue.getString("code");
            JSONObject timeseries=code_timevalue.getJSONObject("timeseries");
            initDataSet(code,timeseries,dataset);
        }


        /**5、6、计算有缺失数据的网格与全部网格的相关系数 r ,并且返回相关性最强的20个*/
        Vector<String> grids= FileTool.Load(path+"lackvalue_grid.txt","utf-8");
        JSONArray lackvalue_grids=JSONArray.fromObject(grids.elementAt(0));
        JSONObject code_relatedCode=findRelatedCode(lackvalue_grids);

        //System.out.println(code_relatedCode);

        Iterator it=code_relatedCode.keys();
        String key_code="";
        JSONArray value_objs;
        JSONObject obj;
        String code="";
        double r=0;

        JSONObject A=new JSONObject();
        if(it.hasNext()){
            while(it.hasNext()){
                key_code=(String) it.next();
                value_objs=code_relatedCode.getJSONArray(key_code);

                int n=value_objs.size();
                double avenrage_r=0;
                double total_r=0;
                for(int i=0;i<n;i++){
                    obj=(JSONObject) value_objs.get(i);
                    code=obj.getString("code");
                    r=obj.getDouble("r");

                    total_r+=r;
                }

                avenrage_r=total_r/n;
                A.put(key_code,avenrage_r);
            }
        }

        return A;
    }
    /**17、计算该网格的真实值与插值的误差：MSE（均方误差）、RMSE（均方根误差）、MAE（平均绝对误差）*/
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
    /**18、比较mse的值较大的code的真实值和插值，并且将其打印出来*/
    public static void compareFailedCode(String code){
        Map<String, Double> real_value_map=new HashMap<>();
        Map<String, Double> interpolation_value_map=new HashMap<>();
        if(dataset.containsKey(code)&&interpolation_result.containsKey(code)){
            real_value_map=dataset.get(code);
            interpolation_value_map=interpolation_result.get(code);

            String date="";
            double real_price;
            double interpolation_price;

            //System.out.println(code+":");
            if(interpolation_value_map.size()!=0){
                for (Map.Entry<String, Double> p : real_value_map.entrySet()) {
                    date=p.getKey();
                    if (interpolation_value_map.containsKey(date)) {
                        real_price=real_value_map.get(date);
                        interpolation_price=interpolation_value_map.get(date);
                        String str=code+","+date+":"+real_price+" , "+interpolation_price;
                        //System.out.println(str);
                        //FileTool.Dump(str,"D:\\小论文\\插值完善\\mse的值较大的code的真实值和插值对比.txt","utf-8");
                    }
                }
            }
            //System.out.print("\n");
            //printSeparator(40);//打印分隔符
        }
    }
    /**19、将插值结果符合(即mse小于0.1)的网格进行插值操作:插值规则是如果该时间点的真实值缺乏，则用插值代替，否则采用真实值*/
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
    /**20、将数据导入mongodb中*/
    public static void toMongoDB(String collName,int N,String source){

        try {
            System.out.println("运行开始:");
            DBCollection coll = db.getDB().getCollection(collName);
            BasicDBObject document;

            int code;
            JSONObject obj;
            String date;
            int year;
            int month;
            double price;
            int row;
            int col;

            //导入原本就满格的数据
            int count=0;
            for (Map.Entry<Integer, JSONObject> entry : full_value_grids.entrySet()) {
                code=entry.getKey();
                int[] rowcol= Code_RowCol(code,N);
                row=rowcol[0];
                col=rowcol[1];
                obj=entry.getValue();
                Iterator<String> it=obj.keySet().iterator();
                while (it.hasNext()){
                    date=it.next();
                    year=Integer.parseInt(date.substring(0,date.indexOf("-")));
                    month=Integer.parseInt(date.substring(date.indexOf("-")+"-".length()));
                    price=obj.getDouble(date);

                    document=new BasicDBObject();
                    document.put("code",code);
                    document.put("row",row);
                    document.put("col",col);
                    document.put("month",month);
                    document.put("year",year);
                    document.put("price",price);
                    document.put("source",source);

                    DBCursor rls =coll.find(document);
                    if(rls == null || rls.size() == 0){
                        coll.insert(document);
                        count++;
                    }else{
                        System.out.println("exist!");
                    }
                }
            }
            System.out.println("导入原本就满格的数据"+count+"条");

            //导入插值后满格的数据
            count=0;
            for (Map.Entry<Integer, JSONObject> entry : interpolation_value_grids.entrySet()) {
                code=entry.getKey();
                int[] rowcol= Code_RowCol(code,N);
                row=rowcol[0];
                col=rowcol[1];
                obj=entry.getValue();
                Iterator<String> it=obj.keySet().iterator();
                while (it.hasNext()){
                    date=it.next();
                    year=Integer.parseInt(date.substring(0,date.indexOf("-")));
                    month=Integer.parseInt(date.substring(date.indexOf("-")+"-".length()));
                    price=obj.getDouble(date);

                    document=new BasicDBObject();
                    document.put("code",code);
                    document.put("row",row);
                    document.put("col",col);
                    document.put("month",month);
                    document.put("year",year);
                    document.put("price",price);
                    document.put("source",source);

                    DBCursor rls =coll.find(document);
                    if(rls == null || rls.size() == 0){
                        coll.insert(document);
                        count++;
                    }else{
                        System.out.println("exist!");
                    }
                }
            }
            System.out.println("导入插值后满格的数据"+count+"条");

            //导入插值失败的网格的真实数据
            count=0;
            for (Map.Entry<Integer, JSONObject> entry : jsonArray_map.entrySet()) {
                 code= entry.getKey();
                if(!(full_value_grids.containsKey(code))&&!(interpolation_value_grids.containsKey(code))){
                    int[] rowcol= Code_RowCol(code,N);
                    row=rowcol[0];
                    col=rowcol[1];
                    if(jsonArray_map.containsKey(code)){
                        obj=jsonArray_map.get(code);
                        Iterator<String> it=obj.keySet().iterator();
                        while (it.hasNext()){
                            date=it.next();
                            year=Integer.parseInt(date.substring(0,date.indexOf("-")));
                            month=Integer.parseInt(date.substring(date.indexOf("-")+"-".length()));
                            price=obj.getDouble(date);

                            document=new BasicDBObject();
                            document.put("code",code);
                            document.put("row",row);
                            document.put("col",col);
                            document.put("month",month);
                            document.put("year",year);
                            document.put("price",price);
                            document.put("source",source);

                            DBCursor rls =coll.find(document);
                            if(rls == null || rls.size() == 0){
                                coll.insert(document);
                                count++;
                            }else{
                                System.out.println("exist!");
                            }
                        }
                    }
                }
            }
            System.out.println("导入插值失败的网格的真实数据"+count+"条");
        } catch (MongoException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NullPointerException e) {
            // TODO Auto-generated catch block
            System.out.println("发生异常的原因为 :"+e.getMessage());
            e.printStackTrace();
        }
    }
    /**21、不能用线性插值的数据：从sparse_data、failed_interpolation_codes和pearson_is_0中找到插值不成功的原始网格，并且将原始数据写于本地文件*/
    public static void reinterpolation(){
        //sparse_data:稀疏数据
        int code;
        JSONObject obj;
        for(Map.Entry<Integer, JSONObject>entry: sparse_data.entrySet()){
            code=entry.getKey();
            obj=entry.getValue();
            //FileTool.Dump(code+","+obj,"D:\\小论文\\插值完善\\sparse_data.txt","utf-8");
        }

        //failed_interpolation_codes：mae>1的网格
        for(int i=0;i<failed_interpolation_codes.size();i++){
            code=failed_interpolation_codes.getInt(i);
            obj=jsonArray_map.get(code);
            //FileTool.Dump(code+","+obj,"D:\\小论文\\插值完善\\failed_interpolation_codes.txt","utf-8");
        }

        //pearson_is_0_size:与其他网格相关系数为0网格
        for(Map.Entry<String,String>entry:pearson_is_0.entrySet()){
            code=Integer.parseInt(entry.getKey());
            obj=jsonArray_map.get(code);
            //FileTool.Dump(code+","+obj,"D:\\小论文\\插值完善\\pearson_is_0.txt","utf-8");
        }
    }
    /**22、将21中生成的本地文件用于邻近插值测试*/
    public static void neighborInterpolation(){
        Vector<String> pois=FileTool.Load("D:\\小论文\\插值完善\\failed_interpolation_codes.txt","utf-8");
        for(int i=0;i<pois.size();i++){
            String poi=pois.elementAt(i);
            int code=Integer.parseInt(poi.substring(0,poi.indexOf(",")));
            String timeserise=poi.substring(poi.indexOf(",")+",".length());
            JSONObject obj=JSONObject.fromObject(timeserise);
            String str=findNeighborCode(code);
            str+=";"+obj;
            FileTool.Dump(str,"D:\\小论文\\插值完善\\failed_interpolation_codes_插值结果.txt","utf-8");

        }
        pois=FileTool.Load("D:\\小论文\\插值完善\\pearson_is_0.txt","utf-8");
        for(int i=0;i<pois.size();i++){
            String poi=pois.elementAt(i);
            int code=Integer.parseInt(poi.substring(0,poi.indexOf(",")));
            String timeserise=poi.substring(poi.indexOf(",")+",".length());
            JSONObject obj=JSONObject.fromObject(timeserise);
            String str=findNeighborCode(code);
            str+=";"+obj;
            FileTool.Dump(str,"D:\\小论文\\插值完善\\pearson_is_0_插值结果.txt","utf-8");

        }
        pois=FileTool.Load("D:\\小论文\\插值完善\\sparse_data.txt","utf-8");
        for(int i=0;i<pois.size();i++){
            String poi=pois.elementAt(i);
            int code=Integer.parseInt(poi.substring(0,poi.indexOf(",")));
            String timeserise=poi.substring(poi.indexOf(",")+",".length());
            JSONObject obj=JSONObject.fromObject(timeserise);
            String str=findNeighborCode(code);
            str+=";"+obj;
            FileTool.Dump(str,"D:\\小论文\\插值完善\\sparse_data_插值结果.txt","utf-8");

        }
    }

    /**23、找出某个code中的邻近的插值满格的code，并用该code的值作为插值网格*/
    public static String findNeighborCode(int code){
        int[] rowcol=Code_RowCol(code,1);
        int row=rowcol[0];
        int col=rowcol[1];
        List<JSONObject> neighbor_codes=new ArrayList<>();

        //设置一个深度，该深度表示需要插值的网格的周围deep个网格的数据的遍历，
        //如果找到有数据的网格，则跳出深度循环
        int deep;
        for(deep=1;deep<20;deep++){

            for(int r=row-deep;r<=row+deep;r++){
                for(int c=col-deep;c<=col+deep;c++){
                    int neighbor_code=(r-1)*4000+c;
                    if(neighbor_code!=code){
                        //检查周围有没有本身数据满格的网格或者通过插值数据满格的网格
                        if(interpolation_value_grids.containsKey(neighbor_code)){
                            JSONObject obj=interpolation_value_grids.get(neighbor_code);
                            neighbor_codes.add(obj);
                        }

                        if(full_value_grids.containsKey(neighbor_code)){
                            JSONObject obj=full_value_grids.get(neighbor_code);
                            neighbor_codes.add(obj);
                        }
                    }
                }
            }

            if(neighbor_codes.size()!=0){
                break;
            }

        }
        String str=deep+";"+code+";"+neighbor_codes;

        return str;
    }
    public static String findNeighborCode(int code,Map<String,JSONObject> map){
        int[] rowcol=Code_RowCol(code,1);
        int row=rowcol[0];
        int col=rowcol[1];
        List<JSONObject> neighbor_codes=new ArrayList<>();

        //设置一个深度，该深度表示需要插值的网格的周围deep个网格的数据的遍历，
        //如果找到有数据的网格，则跳出深度循环
        int deep;
        for(deep=1;deep<40;deep++){

            for(int r=row-deep;r<=row+deep;r++){
                for(int c=col-deep;c<=col+deep;c++){
                    int neighbor_code=(r-1)*4000+c;
                    if(neighbor_code!=code){
                        //检查周围有没有本身数据满格的网格或者通过插值数据满格的网格
                        if(map.containsKey(""+neighbor_code)){
                            JSONObject obj=map.get(""+neighbor_code);
                            neighbor_codes.add(obj);
                        }
                    }
                }
            }

            if(neighbor_codes.size()!=0){
                break;
            }

        }
        String str=deep+";"+code+";"+neighbor_codes;

        return str;
    }

    /**24、利用22方法中的结果"_插值结果.txt"作为插值的源数据*/
    public static void getInterpolation(){
        List<Integer> failedcode=new ArrayList<>();
        String[] dates={"2015-10","2015-11","2015-12","2016-1","2016-2","2016-3","2016-4","2016-5"};

        Vector<String> pois=FileTool.Load("D:\\小论文\\插值完善\\所有的插值结果\\All_failedcode_插值结果.txt","utf-8");
        for(int i=0;i<pois.size();i++){
            String poi=pois.elementAt(i);
            String[] array=poi.split(";");
            int code=Integer.parseInt(array[1]);
            JSONArray neighbor=JSONArray.fromObject(array[2]);
            double total_10=0;
            double total_11=0;
            double total_12=0;
            double total_1=0;
            double total_2=0;
            double total_3=0;
            double total_4=0;
            double total_5=0;

            int size=neighbor.size();
            if(size!=0){
                for(int j=0;j<size;j++){
                    JSONObject obj=(JSONObject)neighbor.get(j);
                    total_10+=obj.getDouble(dates[0]);
                    total_11+=obj.getDouble(dates[1]);
                    total_12+=obj.getDouble(dates[2]);
                    total_1+=obj.getDouble(dates[3]);
                    total_2+=obj.getDouble(dates[4]);
                    total_3+=obj.getDouble(dates[5]);
                    total_4+=obj.getDouble(dates[6]);
                    total_5+=obj.getDouble(dates[7]);
                }

                double avenrage_10=total_10/size;
                double avenrage_11=total_11/size;
                double avenrage_12=total_12/size;
                double avenrage_1=total_1/size;
                double avenrage_2=total_2/size;
                double avenrage_3=total_3/size;
                double avenrage_4=total_4/size;
                double avenrage_5=total_5/size;

                JSONObject interpolation_result=new JSONObject();
                interpolation_result.put(dates[0],avenrage_10);
                interpolation_result.put(dates[1],avenrage_11);
                interpolation_result.put(dates[2],avenrage_12);
                interpolation_result.put(dates[3],avenrage_1);
                interpolation_result.put(dates[4],avenrage_2);
                interpolation_result.put(dates[5],avenrage_3);
                interpolation_result.put(dates[6],avenrage_4);
                interpolation_result.put(dates[7],avenrage_5);

                FileTool.Dump(code+","+interpolation_result,"D:\\小论文\\插值完善\\所有的插值结果\\All_failedcode_插值结果_融合.txt","utf-8");

            }else {
                failedcode.add(code);
                FileTool.Dump(code+","+interpolation_result,"D:\\小论文\\插值完善\\所有的插值结果\\All_failedcode_插值结果_failedcode.txt","utf-8");
            }

        }

    }

    /**25、对于周边20*50m范围内一个邻接数据都没有的，再次用第一次和第二次的插值结果实现插值*/
    public static void reNeighborInterpolation(){
        Vector<String> failed_interpolation=FileTool.Load("D:\\小论文\\插值完善\\failed_interpolation_codes_插值结果_融合.txt","utf-8");
        Vector<String> pearson_is_0=FileTool.Load("D:\\小论文\\插值完善\\pearson_is_0_插值结果_融合.txt","utf-8");
        Vector<String> sparse_data=FileTool.Load("D:\\小论文\\插值完善\\sparse_data_插值结果_融合.txt","utf-8");
        Vector<String> full_value_grids=FileTool.Load("D:\\小论文\\插值完善\\full_value_grids.txt","utf-8");
        Vector<String> interpolation_value_grids=FileTool.Load("D:\\小论文\\插值完善\\interpolation_value_grids.txt","utf-8");


        Map<String,JSONObject> map=new HashMap<>();
        for(int i=0;i<failed_interpolation.size();i++){
            String poi=failed_interpolation.elementAt(i);
            String code=poi.substring(0,poi.indexOf(","));
            String timeserise=poi.substring(poi.indexOf(",")+",".length());
            JSONObject obj=JSONObject.fromObject(timeserise);
            map.put(code,obj);
        }

        for(int i=0;i<pearson_is_0.size();i++){
            String poi=pearson_is_0.elementAt(i);
            String code=poi.substring(0,poi.indexOf(","));
            String timeserise=poi.substring(poi.indexOf(",")+",".length());
            JSONObject obj=JSONObject.fromObject(timeserise);
            map.put(code,obj);
        }

        for(int i=0;i<sparse_data.size();i++){
            String poi=sparse_data.elementAt(i);
            String code=poi.substring(0,poi.indexOf(","));
            String timeserise=poi.substring(poi.indexOf(",")+",".length());
            JSONObject obj=JSONObject.fromObject(timeserise);
            map.put(code,obj);
        }

        for(int i=0;i<full_value_grids.size();i++){
            String poi=full_value_grids.elementAt(i);
            String code=poi.substring(0,poi.indexOf(";"));
            String timeserise=poi.substring(poi.indexOf(";")+";".length());
            JSONObject obj=JSONObject.fromObject(timeserise);
            map.put(code,obj);
        }

        for(int i=0;i<interpolation_value_grids.size();i++){
            String poi=interpolation_value_grids.elementAt(i);
            String code=poi.substring(0,poi.indexOf(";"));
            String timeserise=poi.substring(poi.indexOf(";")+";".length());
            JSONObject obj=JSONObject.fromObject(timeserise);
            map.put(code,obj);
        }


        failed_interpolation=FileTool.Load("D:\\小论文\\插值完善\\failed_interpolation_codes_插值结果_failedcode.txt","utf-8");
        pearson_is_0=FileTool.Load("D:\\小论文\\插值完善\\pearson_is_0_插值结果_failedcode.txt","utf-8");
        sparse_data=FileTool.Load("D:\\小论文\\插值完善\\sparse_data_插值结果_failedcode.txt","utf-8");
        List<String> failedcodes=new ArrayList<>();
        for(int i=0;i<failed_interpolation.size();i++) {
            String poi = failed_interpolation.elementAt(i);
            failedcodes.add(poi);
        }
        for(int i=0;i<pearson_is_0.size();i++) {
            String poi = pearson_is_0.elementAt(i);
            failedcodes.add(poi);
        }
        for(int i=0;i<sparse_data.size();i++) {
            String poi = sparse_data.elementAt(i);
            failedcodes.add(poi);
        }


        for(int i=0;i<failedcodes.size();i++){
            int code=Integer.parseInt(failedcodes.get(i).replace(",{}",""));
            String str=findNeighborCode(code,map);
            FileTool.Dump(str,"D:\\小论文\\插值完善\\All_failedcode_插值结果.txt","utf-8");
        }


    }

    /**26、将full_value_grids和interpolation_value_grids的数据写下来*/
    public static void dumpInterpolationResult(){
        int code;
        JSONObject date_price;
        for (Map.Entry<Integer, JSONObject> entry : full_value_grids.entrySet()) {
            code = entry.getKey();
            date_price = entry.getValue();

            FileTool.Dump(code+";"+date_price,"D:\\小论文\\插值完善\\full_value_grids.txt","utf-8");
        }

        for (Map.Entry<Integer, JSONObject> entry : interpolation_value_grids.entrySet()) {
            code = entry.getKey();
            date_price = entry.getValue();

            FileTool.Dump(code+";"+date_price,"D:\\小论文\\插值完善\\interpolation_value_grids.txt","utf-8");
        }
    }
}
