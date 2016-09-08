package com.reprocess.grid_100.interpolation;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.svail.db.db;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import utils.FileTool;
import utils.UtilFile;

import java.math.BigDecimal;
import java.util.*;

import static com.reprocess.grid_100.GridMerge.codeMapping100toN00;
import static com.reprocess.grid_100.interpolation.SpatialInterpolation.print2DArray;
import static com.reprocess.grid_100.interpolation.SpatialInterpolation.printSeparator;

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
        /*for(int i=0;i<dates.length;i++){
            print_dataset_time_codepriceMap(dates[i]);
        }*/

        JSONObject month_relatedMonth=findRelatedMonth(dates);
        System.out.println(month_relatedMonth);
        /**{"2015-11":[{"month":"2016-3","r":0.5170835878659967},{"month":"2016-2","r":0.5175894584223188},{"month":"2016-1","r":0.6276630850560071},{"month":"2016-5","r":0.8789154107827456},{"month":"2016-4","r":0.8987492444845792},{"month":"2015-12","r":0.9475699488788474},{"month":"2015-10","r":0.9774082282553558}],"2015-10":[{"month":"2016-3","r":0.8638899406820885},{"month":"2016-5","r":0.89576340746832},{"month":"2016-4","r":0.9182462323539149},{"month":"2016-1","r":0.9554010049712885},{"month":"2015-12","r":0.9567238263907923},{"month":"2016-2","r":0.9738472223447068},{"month":"2015-11","r":0.9774082282553558}],"2016-3":[{"month":"2015-11","r":0.5170835878659967},{"month":"2015-10","r":0.8638899406820885},{"month":"2016-4","r":0.9657928519461697},{"month":"2016-5","r":0.977786624645688},{"month":"2016-1","r":0.983865782854527},{"month":"2015-12","r":0.9838777351981842},{"month":"2016-2","r":0.9989644964852342}],"2016-2":[{"month":"2015-11","r":0.5175894584223188},{"month":"2015-10","r":0.9738472223447068},{"month":"2016-1","r":0.9863335005006002},{"month":"2016-4","r":0.9917295899364954},{"month":"2016-5","r":0.9928566418160278},{"month":"2015-12","r":0.9934422798712635},{"month":"2016-3","r":0.9989644964852342}],"2016-5":[{"month":"2015-11","r":0.8789154107827456},{"month":"2015-10","r":0.89576340746832},{"month":"2016-3","r":0.977786624645688},{"month":"2016-2","r":0.9928566418160278},{"month":"2016-4","r":0.9948424026058356},{"month":"2016-1","r":0.9955541981135928},{"month":"2015-12","r":0.9979545102577707}],"2015-12":[{"month":"2015-11","r":0.9475699488788474},{"month":"2015-10","r":0.9567238263907923},{"month":"2016-3","r":0.9838777351981842},{"month":"2016-2","r":0.9934422798712635},{"month":"2016-5","r":0.9979545102577707},{"month":"2016-1","r":0.9980870316749922},{"month":"2016-4","r":0.9987907065874042}],"2016-4":[{"month":"2015-11","r":0.8987492444845794},{"month":"2015-10","r":0.9182462323539149},{"month":"2016-3","r":0.9657928519461697},{"month":"2016-2","r":0.9917295899364954},{"month":"2016-5","r":0.9948424026058356},{"month":"2015-12","r":0.9987907065874042},{"month":"2016-1","r":0.9994048435630356}],"2016-1":[{"month":"2015-11","r":0.6276630850560071},{"month":"2015-10","r":0.9554010049712885},{"month":"2016-3","r":0.983865782854527},{"month":"2016-2","r":0.9863335005006002},{"month":"2016-5","r":0.9955541981135928},{"month":"2015-12","r":0.9980870316749922},{"month":"2016-4","r":0.9994048435630356}]}*/
        /**类似于 {"44553":[{"code":"53372","r":0.9807099978382884},{"code":"42216","r":0.974114024343774},{"code":"26156","r":0.9730731425538015},{"code":"42571","r":0.9664590015520614},{"code":"40566","r":0.9651655514968203},{"code":"35764","r":0.9610575111073333},{"code":"40564","r":0.9570926671774441},{"code":"40148","r":0.9561987511124636},{"code":"34565","r":0.9513818128356463},{"code":"43760","r":0.9488360976500837}],"44563":[{"code":"52562","r":0.9991983364143582},{"code":"38193","r":0.997572697471155},{"code":"42579","r":0.9974925736920043},{"code":"57354","r":0.9957995823517444},{"code":"42546","r":0.9909684133727736},{"code":"29355","r":0.9902535528756519},{"code":"32975","r":0.9894926596003288},{"code":"40565","r":0.9890111578517118},{"code":"41754","r":0.9879312486158609},{"code":"36555","r":0.9854738946116217}],"44564":[{"code":"43772","r":0.9999999999999996},{"code":"47353","r":0.999999999999999},{"code":"33376","r":0.9999999999999967},{"code":"53746","r":0.999999999999996},{"code":"45377","r":0.9999999999999952},{"code":"26155","r":0.999999999999992},{"code":"34964","r":0.9999999999999919},{"code":"42588","r":0.9999999999999895},{"code":"49762","r":0.9999999999999886},{"code":"40583","r":0.9999999999999817}],"44566":[{"code":"44583","r":0.9722970948334144},{"code":"27756","r":0.9654040272265286},{"code":"26554","r":0.96397885764551},{"code":"32576","r":0.9510269916233014},{"code":"59350","r":0.9490089064532404},{"code":"44178","r":0.9397075966479892},{"code":"29786","r":0.9361560762767474},{"code":"40580","r":0.923311659751334},{"code":"53752","r":0.9137824342108447},{"code":"50557","r":0.9096425073168699}],"45364":[{"code":"42616","r":0.993297670437404},{"code":"38172","r":0.993178373867344},{"code":"43010","r":0.9931087886549327},{"code":"40614","r":0.9915805053019225},{"code":"37768","r":0.9915676718231557},{"code":"42156","r":0.9910488701133998},{"code":"38618","r":0.9908107998756097},{"code":"48562","r":0.9903454780081467},{"code":"36569","r":0.9900245217583101},{"code":"42942","r":0.9893626258982654}],"45365":[{"code":"57756","r":0.9984306124533444},{"code":"41342","r":0.9982296136372524},{"code":"33387","r":0.9956760475647024},{"code":"50564","r":0.9951206313467241},{"code":"43368","r":0.9951163263166255},{"code":"52546","r":0.9945980001963906},{"code":"60168","r":0.9941224400079157},{"code":"41358","r":0.9931357796552119},{"code":"46168","r":0.99307264878867},{"code":"47367","r":0.9930094258188386}],"46157":[{"code":"44183","r":0.9887755836121711},{"code":"38607","r":0.9854963987029852},{"code":"40597","r":0.984087048692331},{"code":"26957","r":0.9813328493941247},{"code":"52556","r":0.980462529221123},{"code":"46556","r":0.9803418261673766},{"code":"44168","r":0.9779311999995026},{"code":"43813","r":0.9773428768593809},{"code":"43757","r":0.9769455798700115},{"code":"26561","r":0.9769415835137651}],"46161":[{"code":"45352","r":0.9910831226250885},{"code":"54171","r":0.9822841730995888},{"code":"37774","r":0.9811889949509124},{"code":"50566","r":0.9804509602350868},{"code":"44963","r":0.9795861709949995},{"code":"55765","r":0.9773832015046394},{"code":"41365","r":0.9772968883692246},{"code":"32583","r":0.9763915966508686},{"code":"45764","r":0.9704004102554434},{"code":"38612","r":0.9694706168828698}],"46166":[{"code":"36559","r":0.9994484755771683},{"code":"40981","r":0.9994368207680371},{"code":"40597","r":0.997540440618928},{"code":"44183","r":0.9972769031179731},{"code":"23755","r":0.996386437173583},{"code":"30585","r":0.9961769877131361},{"code":"44172","r":0.9956798011950112},{"code":"41810","r":0.9955015095909163},{"code":"37770","r":0.9952517225356436},{"code":"46169","r":0.9952186197187957}],"44157":[{"code":"42959","r":0.9950770686500737},{"code":"52551","r":0.9947527033042161},{"code":"39351","r":0.9942938303936696},{"code":"45357","r":0.9915214094144376},{"code":"26560","r":0.991057410317747},{"code":"42617","r":0.9884657295097378},{"code":"31783","r":0.987863840503765},{"code":"31780","r":0.9875914621200795},{"code":"54543","r":0.98735937069895},{"code":"59793","r":0.9856551581057728}],"44161":[{"code":"26957","r":0.9998287782832981},{"code":"47365","r":0.999710383587756},{"code":"43779","r":0.9996250841677797},{"code":"50958","r":0.9996064442379009},{"code":"32180","r":0.9995188392831224},{"code":"49357","r":0.9995152720574961},{"code":"61368","r":0.9994733116861045},{"code":"45378","r":0.9993798669374603},{"code":"49766","r":0.999328533734176},{"code":"41381","r":0.9993207761122043}],"44163":[{"code":"52951","r":0.9517208757800147},{"code":"56172","r":0.9384383183857208},{"code":"42983","r":0.9369221191921782},{"code":"54968","r":0.9299721579944106},{"code":"41805","r":0.9279869830394931},{"code":"45361","r":0.9261203209430575},{"code":"36967","r":0.9218739300480717},{"code":"56993","r":0.9216392248001504},{"code":"42176","r":0.9213748709644608},{"code":"43382","r":0.9208482043785365}],"44165":[{"code":"37373","r":0.9999999999999539},{"code":"50179","r":0.9999999999999123},{"code":"45738","r":0.9999999999998047},{"code":"46173","r":0.9999999999997894},{"code":"34961","r":0.9999999999997707},{"code":"52974","r":0.9999999999996956},{"code":"46184","r":0.9999999999996912},{"code":"41752","r":0.9999999999995488},{"code":"31727","r":0.9999999999994962},{"code":"54973","r":0.9999999999994759}],"44954":[{"code":"31355","r":0.9941307644952886},{"code":"36547","r":0.9930984504842205},{"code":"40148","r":0.991829829956168},{"code":"44531","r":0.9894064649842629},{"code":"40974","r":0.9886448849373516},{"code":"53336","r":0.9877950255766775},{"code":"35758","r":0.9840955352076037},{"code":"42160","r":0.9830780822930382},{"code":"34565","r":0.9819753750270719},{"code":"41361","r":0.9807032441437733}],"45754":[{"code":"42149","r":0.9999986884861329},{"code":"41381","r":0.999992063923156},{"code":"54970","r":0.9999900924769428},{"code":"49357","r":0.9999724809974906},{"code":"45774","r":0.9999302186075725},{"code":"57362","r":0.9999213329719561},{"code":"48580","r":0.9999069741314355},{"code":"30956","r":0.9998942645387657},{"code":"44180","r":0.9998919340711112},{"code":"40938","r":0.999883018239082}],"45765":[{"code":"52961","r":0.9880728277752283},{"code":"42961","r":0.9835761913297537},{"code":"47381","r":0.983135908289625},{"code":"42151","r":0.9825159652535083},{"code":"45363","r":0.9824482507967962},{"code":"35751","r":0.982343579924824},{"code":"39370","r":0.9821787643260137},{"code":"43776","r":0.9817751753538025},{"code":"46556","r":0.9807897706278123},{"code":"42179","r":0.9798766604081367}],"46560":[{"code":"45375","r":0.9934153873865277},{"code":"25761","r":0.9933641648175884},{"code":"42194","r":0.9918720083619048},{"code":"44171","r":0.9911857526016654},{"code":"44182","r":0.9911121107708816},{"code":"41410","r":0.9899511785090922},{"code":"36138","r":0.9887602909685178},{"code":"43376","r":0.9886132943015036},{"code":"44570","r":0.9885341522122695},{"code":"41357","r":0.9874499875756628}],"46561":[{"code":"43333","r":0.9866417179227355},{"code":"54551","r":0.9836910875849394},{"code":"48980","r":0.9836828461091907},{"code":"44155","r":0.9830630478440083},{"code":"41396","r":0.9775916436723121},{"code":"59390","r":0.9653393191695071},{"code":"37753","r":0.9633658576464824},{"code":"45759","r":0.9615242077521753},{"code":"36150","r":0.9546031156862761},{"code":"32183","r":0.9538156831080518}]}*/

        monthsCovariance(month_relatedMonth);


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
                //System.out.println(timevalue_map.size());
                price=timevalue_map.get(date);
                code_price.put(code,price);
            }
        }
        dataset_time_codeprice.put(date,code_price);
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

    public static void monthsCovariance(JSONObject month_relatedMonth){

        String lackdata_month;
        JSONArray related_list;
        JSONObject related_month_json;
        String related_month;

        Iterator it=month_relatedMonth.keys();
        double lackdata_related_cov;

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
                System.out.print("\n");
/**==================================求方程的左边矩阵边一列 （n+1）*（n+1） ======================================================*/

                covarianceMatrix(C_y_nn,related_list);//求协方差矩阵
                NiMatrix inverse_matrix = new NiMatrix();
                C_y_nn_inverse=inverse_matrix.getNiMatrix(C_y_nn);//求C_y_nn的逆矩阵

                /** 求权重w */
                w=SpatialInterpolation.marixMultiply(C_y_nn_inverse,C_y_n0);
                //print2DArray(w);


                double y0=0;
                Set<String> it_codes=dataset.keySet();
                System.out.println("lackdata_month:"+lackdata_month);
                for (String code : it_codes) {
                    y0=y0_EstimatedValue(w,related_list,Integer.parseInt(code));
                    System.out.print(code+" : "+y0+" ; ");
                }
                System.out.print("\n");
                printSeparator(40);//打印分隔符



            }
        }
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


    public static void print_dataset_time_codepriceMap(String date){

        Map<Integer, Double> map=dataset_time_codeprice.get(date);
        for (Map.Entry<Integer, Double> p : map.entrySet()) {
            System.out.print(p.getKey()+":"+p.getValue()+" ; ");
        }
        System.out.println("\n");
    }

}
