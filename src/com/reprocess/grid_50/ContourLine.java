package com.reprocess.grid_50;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import utils.FileTool;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

/**
 * 这一个类主要是生成等值线，数据源是SpatialInterpolation类中通过插值得到的
 * 时序性的房价数据，按照月份来生成等值线。
 */
public class ContourLine {

    public static void main(String[] args){
         //priceMatrix("2015-11");
        //System.out.println(getAnglesCoor(5861597));
        //priceBlock("D:\\小论文\\等值线\\1_价格区块标记\\ContourLine-2015-10-区块.txt",20);
        //toArray("D:\\小论文\\等值线\\2_提取等值线\\坐标串_5.txt",5);
    }

    /**step_1:根据"D:\小论文\插值完善\所有的插值结果\"中六个文件生成每个月的价格矩阵的*/
    public static void priceMatrix(String month){
        String path="D:\\小论文\\插值完善\\所有的插值结果\\";
        String file=path+"All_failedcode_插值结果_融合.txt";
        Map<Integer,Double> code_price=new HashMap<>();
        listAssignment(file,code_price,month);
        System.out.println(code_price.size());

        file=path+"failed_interpolation_codes_插值结果_融合.txt";
        listAssignment(file,code_price,month);
        System.out.println(code_price.size());

        file=path+"full_value_grids.txt";
        listAssignment(file,code_price,month);
        System.out.println(code_price.size());

        file=path+"interpolation_value_grids.txt";
        listAssignment(file,code_price,month);
        System.out.println(code_price.size());

        file=path+"pearson_is_0_插值结果_融合.txt";
        listAssignment(file,code_price,month);
        System.out.println(code_price.size());

        file=path+"sparse_data_插值结果_融合.txt";
        listAssignment(file,code_price,month);
        System.out.println(code_price.size());

        file=path+"以点代面_插值结果_融合.txt";
        try{
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            for (String line = br.readLine(); line != null; line = br.readLine()) {
                String poi=line;
                String code=poi.substring(0,poi.indexOf(","));
                String timeserise=poi.substring(poi.indexOf(",")+",".length());
                JSONObject obj=JSONObject.fromObject(timeserise);

                double price=obj.getDouble(month);
                code_price.put(Integer.parseInt(code),price);
            }
        } catch (IOException e) {
          e.printStackTrace();
        }

    System.out.println(code_price.size());

        //将200km*200km范围内的格网看作是4000*4000的二维数组，从上至下对二维数组进行赋值
        //这里要考虑到编码系统里的行列号与数组里面的行列号的差别
        //编码系统的行列号的起始位置是左下角，而数组的行列号的起始位置是左上角
        int code;
        double price;
        double[][] gridmatrix=new double[4000][4000];
        int array_row=0;//其中array_row+row=4000
        //row、col指的是编码系统里的行列号
        //array_row、array_col指的是二维矩阵中的行列号
        for(int row=4000;row>=1;row--){
            String str="";
            int array_col=0;//其中array_col=col-1;
            for(int col=1;col<=4000;col++){
                code=col+(row-1)*4000;

                if(code_price.containsKey(code)){
                    price=code_price.get(code);
                    gridmatrix[array_row][array_col]=price;
                    //System.out.println(array_row+","+array_col+":"+gridmatrix[array_row][array_col]);

                }else{
                    gridmatrix[array_row][array_col]=0.0;
                }
                str+=gridmatrix[array_row][array_col]+",";
                array_col++;
            }
            FileTool.Dump(str,"D:\\小论文\\等值线\\1_价格区块标记\\ContourLine-"+month+".txt","utf-8");
            array_row++;
        }
    }
    /**step_2:根据汉青的pb.py程序生成ContourLine-2015-10-区块.txt文件*/

    /**step_3:根据step_1中的结果提取特定价格阈值的等值线*/
    public static void priceBlock(String file,int gridvalue){
        //将step1中的结果赋值到一个二维矩阵中去
        Vector<String> pois=FileTool.Load(file,"utf-8");
        int[][] price_matrix=new int[4000][4000];

        //code_index
        //key:网格编码
        //value:编码为gridvalue的网格的区块标签,即是第几个gridvalue块
        Map<Integer,Integer> code_index=new HashMap<>();

        for(int i=0;i<pois.size();i++){
            String[] array=pois.elementAt(i).split(",");

            for(int j=0;j<array.length;j++){

                if(!array[j].equals("-1")){
                    double temp=Double.parseDouble(array[j]);
                    price_matrix[i][j]=(int)temp;
                    if(price_matrix[i][j]!=-1){
                        int value=price_matrix[i][j]/100000;
                        int index=price_matrix[i][j]%100000;
                        if(value==gridvalue){
                            int row=4000-i;
                            int col=j+1;
                            int code=(row-1)*4000+col;
                            code_index.put(code,index);
                        }
                    }
                }
            }
        }


        //通过遍历code_index统计价格为gridvalue的格网数目和区块数目
        Iterator<Integer> iterator=code_index.keySet().iterator();
        TreeSet ts=new TreeSet();
        List<Integer> codeis=new ArrayList<>();
        while (iterator.hasNext()){
            int key=iterator.next();
            int value=code_index.get(key);
            if(value!=0){
                ts.add(value);
                codeis.add(value);
            }
        }
        System.out.println("等值线"+gridvalue+"总共有"+ts.size()+"个区块");
        System.out.println("等值线"+gridvalue+"总共有"+codeis.size()+"个网格");

        Iterator<Integer> it_ts=ts.iterator();
        int tag;
        int gridnum;
        int count;
        int total=0;
        while(it_ts.hasNext())
        {
            tag=it_ts.next();
            //index_block包含的都是区块标签为tag的网格
            //key:网格编码
            //value:网格对应的区块标签,特殊情况，key=0时表示的是具有该区块标签的网格的个数
            Map<Integer,Integer> index_block=getIndexBlocks(code_index,tag);
            gridnum=index_block.get(0);
            JSONObject corners;
            int grid_code;

            Iterator<Integer> it=index_block.keySet().iterator();
            count=0;
            JSONObject result=new JSONObject();
            while (it.hasNext()){
                grid_code=it.next();
                if(grid_code!=0){
                    count++;
                    corners=getAnglesCoor(grid_code);
                    result.put(grid_code,corners);
                }
            }
            total+=gridnum;
            FileTool.Dump(result.toString(),"D:\\小论文\\等值线\\2_提取等值线\\等值线_"+gridvalue+".txt","utf-8");

        }
    }
    /**step_4:生成等值线坐标串，此算法是python写的，详见“D:\小论文\等值线\3_生成等值线坐标串”中的jsonprocess.py*/

    /**step_5:将step_3中生成的文件“坐标串_4.txt”全部写到一个数组里面去，并存放在
     * 静态文件中进行展示*/
    public static void toArray(String file,int value){
        Vector<String> pois=FileTool.Load(file,"utf-8");
        JSONArray array=new JSONArray();
        for(int i=0;i<pois.size();i++){
            String poi=pois.elementAt(i);
            array.add(poi);
        }
        FileTool.Dump(array.toString(),"D:\\小论文\\等值线\\3_生成等值线坐标串\\坐标串_"+value+".txt","utf-8");
    }

    /**提取文件中指定月份的价格和code数据*/
    public static void listAssignment(String file,Map<Integer,Double> code_price,String month){
        Vector<String> pois= FileTool.Load(file,"utf-8");
        for(int i=0;i<pois.size();i++){
            String poi=pois.elementAt(i);
            String code=poi.substring(0,poi.indexOf(","));
            String timeserise=poi.substring(poi.indexOf(",")+",".length());
            JSONObject obj=JSONObject.fromObject(timeserise);

            double price=obj.getDouble(month);
            code_price.put(Integer.parseInt(code),price);
        }
    }

    /**根据格网code值计算该格网的四个角的经纬度,针对50m*50m的格网*/
    public static JSONObject getAnglesCoor(int code){
        int cols=4000;
        int row=code/cols+1;
        int col=code%cols;

        double width_50=5.892999999998593E-4;//每50m的经度差
        double length_50=4.501999999998674E-4;//每50m的纬度差

        double lng=115.417284;
        double lat=39.438283;

        JSONObject obj=new JSONObject();

        double[] southwest=new double[2];
        southwest[0]=lng+(col-1)*width_50;
        southwest[1]=lat+(row-1)*length_50;
        obj.put("southwest",southwest);

        double[] southeast=new double[2];
        southeast[0]=lng+(col)*width_50;
        southeast[1]=lat+(row-1)*length_50;
        obj.put("southeast",southeast);

        double[] northeast=new double[2];
        northeast[0]=lng+(col)*width_50;
        northeast[1]=lat+(row)*length_50;
        obj.put("northeast",northeast);

        double[] northwest=new double[2];
        northwest[0]=lng+(col-1)*width_50;
        northwest[1]=lat+(row)*length_50;
        obj.put("northwest",northwest);

        return obj;
    }

    /**找出code_index中value为指定标签的的格网code，并且存到block中，这个
     * block中的格网应该是连在一起的*/
    public static Map<Integer,Integer> getIndexBlocks(Map<Integer,Integer> code_index,int index){
        Iterator iterator=code_index.keySet().iterator();
        Map<Integer,Integer> block=new HashMap<>();//存储了所有标签为index的网格
        int count=0;
        while (iterator.hasNext()){
            int key=(int)iterator.next();
            int value=code_index.get(key);
            if(value==index){
                block.put(key,value);
                count++;
            }
        }
        block.put(0,count);//标记这个区块一共有多少个格网。
        return block;
    }

}
