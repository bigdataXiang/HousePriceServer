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


    /**
     * 确定参与数据缺失点插值的点
     * @return
     */
    public static JSONObject pearson(){
        //1.从静态文本中读取文件，确定待插值的网格和用于插值的网格
        String testpath="D:\\github.com\\bigdataXiang\\HousePriceServer\\src\\com\\reprocess\\grid_100\\interpolation\\";
        Vector<String> testfile= FileTool.Load(testpath+"testfile.txt","utf-8");
        String result=testfile.elementAt(0);
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

        System.out.println(interpolation_codes.toString());

        Iterator it=interpolation_codes.keys();
        String key="";
        JSONObject value;
        JSONObject valublecode;

        JSONObject interpolation=new JSONObject();
        if(it.hasNext()){
            while(it.hasNext()){
                key=(String)it.next();
                value=interpolation_codes.getJSONObject(key);
                self_timeseries=value.getJSONObject("self_timeseries");
                rlist=value.getJSONArray("rlist");

                //System.out.println(key+":"+self_timeseries);


                //取rllist中相关系数最高的五个点
                JSONArray interpolation_list_5=new JSONArray();
                if(rlist.size()>5){
                    //System.out.println("与"+key+"相关性高的code：");
                    for(int i=rlist.size()-1;i>rlist.size()-6;i--){
                        valublecode=rlist.get(i);
                        //System.out.println(valublecode);
                        interpolation_list_5.add(valublecode);
                    }
                }

                interpolation.put(key,interpolation_list_5);
            }
        }
        System.out.println(interpolation);
        return interpolation;

    }

    public static void singleCodeInterpolation(JSONObject interpolation){

        Iterator it =interpolation.keys();
        String key;
        JSONArray interpolation_list_5;
        JSONObject adjacent_obj;
        String adjacent_code;
        if(it.hasNext()){
            while (it.hasNext()){
                key=(String) it.next();
                interpolation_list_5=interpolation.getJSONArray(key);

                for(int i=0;i<interpolation_list_5.size();i++){
                    adjacent_obj=interpolation_list_5.getJSONObject(i);
                    adjacent_code=adjacent_obj.getString("code");
                    System.out.println(dataset.get(adjacent_code));
                }
            }

        }

    }

    public static void main(String[] args) {
        JSONObject interpolation=pearson();//{"44553":[{"code":"45754","r":0.9999999999999893},{"code":"46166","r":0.9999999999985891},{"code":"44564","r":0.999999999998465},{"code":"44161","r":0.9846171279165659},{"code":"44954","r":0.9163949063528937}],"44556":[],"44563":[{"code":"45754","r":1.0000000000000444},{"code":"46166","r":0.9999999999999745},{"code":"44162","r":0.9599756760643429},{"code":"45359","r":0.9521491602945367},{"code":"45762","r":0.9383323643385016}],"44564":[{"code":"45762","r":1.0000026691445016},{"code":"44956","r":1.0000000002730443},{"code":"46157","r":1.0000000000026537},{"code":"44166","r":1.0000000000015612},{"code":"44158","r":1.000000000001283}],"44566":[{"code":"44165","r":1.0000000000042069},{"code":"45756","r":0.8842457658458388},{"code":"46561","r":0.8754487612251354},{"code":"45754","r":0.8409790784268929},{"code":"44966","r":0.8147627294467269}],"45364":[{"code":"45754","r":0.99306695535799},{"code":"46560","r":0.9884262893664505},{"code":"44957","r":0.985856582385948},{"code":"44158","r":0.9849361610580533},{"code":"44153","r":0.9757599572519818}],"45365":[{"code":"44165","r":1.0000000000084608},{"code":"44161","r":0.9922210843042336},{"code":"44160","r":0.9895786953388341},{"code":"44960","r":0.9891613622901583},{"code":"45360","r":0.9878234141303601}],"46157":[{"code":"44564","r":1.0000000000026537},{"code":"45754","r":0.9999694497454086},{"code":"46166","r":0.9994568151676683},{"code":"44161","r":0.9912070160300842},{"code":"46556","r":0.9803418261673766}],"46159":[],"46161":[{"code":"44165","r":1.0000000000043985},{"code":"44963","r":0.9795861709949995},{"code":"45764","r":0.9704004102554434},{"code":"44154","r":0.9669236806904087},{"code":"45754","r":0.965574912255726}],"46166":[{"code":"44563","r":0.9999999999999745},{"code":"44564","r":0.9999999999986026},{"code":"44553","r":0.9999999999985891},{"code":"46157","r":0.9994568151676683},{"code":"46560","r":0.9873056220676827}],"44157":[{"code":"44161","r":0.99999999999974},{"code":"45357","r":0.9915214094144376},{"code":"46160","r":0.9703461609793591},{"code":"45761","r":0.9659581197619368},{"code":"46560","r":0.9609280085862177}],"44161":[{"code":"44165","r":1.0000000000001643},{"code":"44954","r":0.9999999999999504},{"code":"44157","r":0.99999999999974},{"code":"45754","r":0.999323908783843},{"code":"45760","r":0.9984569309603111}],"44163":[{"code":"44564","r":1.0000000000002387},{"code":"45361","r":0.9261203209430575},{"code":"46166","r":0.9069735099222169},{"code":"46563","r":0.9037304141708133},{"code":"44156","r":0.8889123546053517}],"44165":[{"code":"46167","r":1.000000003534472},{"code":"44958","r":1.0000000028753162},{"code":"46160","r":1.0000000000554132},{"code":"46162","r":1.0000000000184186},{"code":"45760","r":1.0000000000154314}],"44954":[{"code":"44161","r":0.9999999999999504},{"code":"44962","r":0.9689241035197159},{"code":"44958","r":0.966411234688561},{"code":"44164","r":0.9466094074980667},{"code":"45364","r":0.9377222688709478}],"45754":[{"code":"44563","r":1.0000000000000444},{"code":"44553","r":0.9999999999999893},{"code":"46157","r":0.9999694497454086},{"code":"46564","r":0.9998642097965291},{"code":"45760","r":0.9997259291978358}],"45765":[{"code":"44564","r":1.0000000000002087},{"code":"45754","r":0.9959893785731951},{"code":"45363","r":0.9824482507967962},{"code":"46556","r":0.9807897706278123},{"code":"46157","r":0.9769073325860704}],"46560":[{"code":"44564","r":0.9999999999998891},{"code":"45754","r":0.9903774310257425},{"code":"45364","r":0.9884262893664505},{"code":"46166","r":0.9873056220676827},{"code":"46564","r":0.9854209777663695}],"46561":[{"code":"44165","r":1.0000000000045615},{"code":"44155","r":0.9830630478440083},{"code":"45754","r":0.9676491057196981},{"code":"45759","r":0.9615242077521753},{"code":"44966","r":0.9121363440448407}],"44565":[],"44953":[],"44955":[],"45356":[]}

        singleCodeInterpolation(interpolation);
    }
    public static double covariance(String code1, String code2){

        // 找出双方都有的数据
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
