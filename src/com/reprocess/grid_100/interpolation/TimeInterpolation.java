package com.reprocess.grid_100.interpolation;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.reprocess.grid_100.Code_Price_RowCol;
import com.svail.db.db;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import utils.FileTool;
import utils.UtilFile;

import java.util.*;

import static com.reprocess.grid_100.GridMerge.codeMapping100toN00;

/**
 * Created by ZhouXiang on 2016/9/8.
 */
public class TimeInterpolation {
    public static Map<String, Map<String, Double>> dataset = new HashMap<>();//dataset的key是网格的code，value是网格对应的时间价格序列值
    public static Map<String, Map<Integer, Double>> dataset_time_codeprice = new HashMap<>();//dataset的key是月份，value是该月每个网格对应的价格

    public static void main(String[] args){

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
        for(int i=0;i<dates.length;i++){
            print_dataset_time_codepriceMap(dates[i]);
        }

        System.out.println(findRelatedMonth(dates));


    }

    /**1、设置调用数据库的条件*/
    public static void conditions(){
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
    }

    /**2、按月份调用每个月都数值的网格code*/
    public static List CallMongo(JSONObject condition, int N){

        String collName=condition.getString("export_collName");
        DBCollection coll = db.getDB().getCollection(collName);
        BasicDBObject document = new BasicDBObject();

        int year=condition.getInt("year");
        int month=condition.getInt("month");
        String source=condition.getString("source");
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

            result_doc=codeMapping100toN00(row_doc,col_doc,N);
            code=result_doc[2];
            codes_set.add(code);
        }

        List<Double> codes_list=new ArrayList<>();
        //System.out.println("codes_set.size():"+codes_set.size());
        Iterator it=codes_set.iterator();
        while(it.hasNext())
        {
            Object o=it.next();
            codes_list.add(Double.parseDouble(o.toString()));
        }
        //System.out.print("\n");
        //printSeparator(40);

        return codes_list;
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

    /**4、初始化 Dataset_time_codeprice 数据集*/
    public static void initDataset_time_codeprice(JSONObject date_codes){

        String date=date_codes.getString("date");
        JSONArray codes=date_codes.getJSONArray("codes");
        int code;
        double price;

        Map<Integer, Double> code_price = new HashMap<Integer, Double>();
        Map<String, Double> timevalue_map = new HashMap<String, Double>();
        String key;
        for(int i=0;i<codes.size();i++){
            code=(int)codes.get(i);
            key=""+code;
            if(dataset.containsKey(key)){
                timevalue_map=dataset.get(key);
                System.out.println(timevalue_map.size());
                price=timevalue_map.get(date);
                code_price.put(code,price);
            }
        }
        dataset_time_codeprice.put(date,code_price);
    }

    /**5、求两两月份之间的皮尔逊值 r */
    public static double pearson(String self_month, String related_month){

        List<String> list = new ArrayList<String>();
        if(dataset.containsKey(self_month)&&dataset.containsKey(related_month)){

            for (Map.Entry<String, Double> p1 : dataset.get(self_month).entrySet()) {
                if (dataset.get(related_month).containsKey(p1.getKey())) {
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

            for (String code : list) {
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

    /**6、求待插值的月份与其他月份的相关性 R ,并且找出五个相关性最强的月份*/
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

            interpolation_singlemonth.put(lackdata_month,rlist);
        }
        return interpolation_singlemonth;
    }



    public static void print_dataset_time_codepriceMap(String date){

        Map<Integer, Double> map=dataset_time_codeprice.get(date);
        for (Map.Entry<Integer, Double> p : map.entrySet()) {
            System.out.print(p.getKey()+":"+p.getValue()+" ; ");
        }
        System.out.println("\n");
    }


}
