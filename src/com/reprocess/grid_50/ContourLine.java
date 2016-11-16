package com.reprocess.grid_50;

import net.sf.json.JSONObject;
import utils.FileTool;

import java.util.*;

/**
 * 这一个类主要是生成等值线，数据源是SpatialInterpolation类中通过插值得到的
 * 时序性的房价数据，按照月份来生成等值线。
 */
public class ContourLine {

    public static void main(String[] args){
       // priceMatrix("2015-10");
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
    /**step_2:根据step_1中的结果提取特定价格阈值的等值线*/

    /**step_3:*/

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


}
