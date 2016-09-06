package com.reprocess.grid_100.interpolation;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import utils.FileTool;
import utils.UtilFile;

import java.io.File;
import java.util.*;
import java.util.Map.Entry;
/**
 * Created by ZhouXiang on 2016/9/5.
 */
public class PearsonCorrelationScore {
    public static Map<String, Map<String, Double>> dataset = null;

    /**
     * 初始化数据集
     */
    public static void initDataSet(String selfcode,String adjacent_code,JSONObject self_timeseries,JSONObject adjacent_codedata) {
        dataset = new HashMap<String, Map<String, Double>>();

        // 初始化self_timeseries数据集
        Map<String, Double> selfMap = new HashMap<String, Double>();
        Iterator it = self_timeseries.keys();
        String key="";
        double value=0;
        if(it.hasNext()){
            while (it.hasNext()){
                key=(String) it.next();
                value=self_timeseries.getDouble(key);
                selfMap.put(key, value);
            }

        }
        dataset.put(selfcode, selfMap);

        // 初始化adjacent_codedata数据集
        Map<String, Double> compareMap = new HashMap<String, Double>();
        it = adjacent_codedata.keys();
        if(it.hasNext()){
            while (it.hasNext()){
                key=(String) it.next();
                //System.out.println(key);
                value=adjacent_codedata.getDouble(key);
                compareMap.put(key, value);
            }

        }
        dataset.put(adjacent_code, compareMap);
    }

    public Map<String, Map<String, Double>> getDataSet() {
        return dataset;
    }

    /**
     * @param person1
     *            name
     * @param person2
     *            name
     * @return 皮尔逊相关度值 皮尔逊相关系数=协方差(x,y)/[标准差(x)*标准差(y)]
     */
    public static double sim_pearson(String person1, String person2) {
        // 找出双方都有的数据,（皮尔逊算法要求）
        List<String> list = new ArrayList<String>();
        for (Entry<String, Double> p1 : dataset.get(person1).entrySet()) {
            if (dataset.get(person2).containsKey(p1.getKey())) {
                list.add(p1.getKey());
            }
        }

        int N = list.size();
        double r;

        //有可能存在两组数就没有完全相同的时间点，故这里是需要判断的
        if(N!=0){
            double sumX = 0.0;
            double sumY = 0.0;
            double sumX_Sq = 0.0;
            double sumY_Sq = 0.0;
            double sumXY = 0.0;

            for (String name : list) {
                Map<String, Double> p1Map = dataset.get(person1);
                Map<String, Double> p2Map = dataset.get(person2);

                sumX += p1Map.get(name);
                sumY += p2Map.get(name);
                sumX_Sq += Math.pow(p1Map.get(name), 2);
                sumY_Sq += Math.pow(p2Map.get(name), 2);
                sumXY += p1Map.get(name) * p2Map.get(name);
            }

            //System.out.println("sumXY:"+sumXY);
            //System.out.println("sumX * sumY/N:"+sumX * sumY/N);
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



    public static void pearson(){
        String testpath="D:\\github.com\\bigdataXiang\\HousePriceServer\\src\\com\\reprocess\\grid_100\\interpolation\\";
        Vector<String> testfile= FileTool.Load(testpath+"testfile.txt","utf-8");
        String result=testfile.elementAt(0);
        // System.out.println(result);
        JSONObject single_code=JSONObject.fromObject(result);
        String interest_code="";
        JSONObject single_code_value=new JSONObject();
        JSONObject self_timeseries=new JSONObject();
        JSONArray adjacent_array=new JSONArray();
        JSONObject adjacent_codedata=new JSONObject();
        String adjacent_code="";
        JSONObject adjacent_code_timeseries=new JSONObject();
        String self_temp="";
        int self_row;
        int self_col;

        JSONObject interpolation_codes=new JSONObject();
        JSONObject interpolation_singlecode=new JSONObject();
        List<JSONObject> rlist;
        JSONObject r_adjacentcode;

        Iterator iterator=single_code.keys();
        Iterator adjacent_codes;
        if(iterator.hasNext()){
            while (iterator.hasNext()){

                interest_code=(String) iterator.next();
                single_code_value=single_code.getJSONObject(interest_code);
                self_temp=single_code_value.getString("self_timeseries");
                self_row=single_code_value.getInt("row");
                self_col=single_code_value.getInt("col");


                //if(interest_code.equals("45754")){


                rlist=new ArrayList<>();
                if(self_temp.length()!=0){

                    self_timeseries=single_code_value.getJSONObject("self_timeseries");
                    adjacent_array=single_code_value.getJSONArray("adjacent_array");

                    //System.out.println(interest_code+":"+self_timeseries);

                    for(int i=0;i<adjacent_array.size();i++) {

                        adjacent_codedata = adjacent_array.getJSONObject(i);
                        adjacent_codes = adjacent_codedata.keys();
                        while (adjacent_codes.hasNext()) {
                            adjacent_code = (String) adjacent_codes.next();//"44153"
                            //System.out.println(adjacent_code);
                        }

                        //if (adjacent_code.equals("44556")) {


                        //求解邻接code对应的时间序列
                        adjacent_code_timeseries = adjacent_codedata.getJSONObject(adjacent_code);
                        // System.out.println(adjacent_code+":"+adjacent_code_timeseries);

                        //初始化两个数据序列的值
                        initDataSet(interest_code, adjacent_code, self_timeseries, adjacent_code_timeseries);
                        //计算两者的皮尔逊相关系数
                        double r = sim_pearson(interest_code, adjacent_code);
                        // System.out.println(interest_code + "与" + adjacent_code + "的相关系数是：" + r);

                        r_adjacentcode = new JSONObject();
                        r_adjacentcode.put("code", adjacent_code);
                        r_adjacentcode.put("r",r);

                        //选取相关性大于0.5的网格
                        if(r>0.5){
                            rlist.add(r_adjacentcode);
                        }


                        //}

                    }
                }else{
                    //该网格一直没有数据的，说明该网格本身没有房产信息，插值意义不大
                    //System.out.println(single_code_value);
                }

                interpolation_singlecode.put("row",self_row);
                interpolation_singlecode.put("col",self_col);
                interpolation_singlecode.put("self_timeseries",self_timeseries);


                //加入list之前先根据相关性进行排序
                Collections.sort(rlist, new UtilFile.RComparator());
                interpolation_singlecode.put("rlist",rlist);
                interpolation_codes.put(interest_code,interpolation_singlecode);

                //}


            }
        }

        //System.out.println(interpolation_codes.toString());

        Iterator it=interpolation_codes.keys();
        String key="";
        JSONObject value;
        if(it.hasNext()){
            while(it.hasNext()){
                key=(String)it.next();
                value=interpolation_codes.getJSONObject(key);
                self_timeseries=value.getJSONObject("self_timeseries");
                rlist=value.getJSONArray("rlist");

                System.out.println(key+":"+self_timeseries);
                System.out.println("    "+rlist);
            }

        }

    }

    public static void main(String[] args) {

    }
    public static double covariance(String code1, String code2){

        // 找出双方都有的数据,（皮尔逊算法要求）
        List<String> list = new ArrayList<String>();
        for (Entry<String, Double> p1 : dataset.get(code1).entrySet()) {
            if (dataset.get(code2).containsKey(p1.getKey())) {
                list.add(p1.getKey());
            }
        }

        int N = list.size();
        double cov;

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

            cov =(1/N)*(sumXY - sumX * sumY / N) ;
        }else {
            cov=0;
        }

        return cov;
    }

    /**
     * 计算矩阵a与矩阵b的乘积
     * @param a
     * @param b
     * @return
     */
    public static int[][] marixMultiply(int a[][], int b[][]) {
        if (a == null || b == null || a[0].length != b.length) {
            throw new IllegalArgumentException("matrix is illegal");
        }
        int row = a.length;
        int column = b[0].length;
        int multiplyC = a[0].length;
        int[][] result = new int[row][column];

        for (int m = 0; m < row; m++)
            for (int n = 0; n < column; n++)
                for (int i = 0; i < multiplyC; i++) {
                    result[m][n] += a[m][i] * b[i][n];
                }
        return result;
    }



}
