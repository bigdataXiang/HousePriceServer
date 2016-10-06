package com.reprocess.grid_100.ContourGeneration;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import utils.FileTool;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Created by ZhouXiang on 2016/10/4.
 */
public class DrawContour {
    public static void main(String[] args){
        initGridMatrix("D:\\中期考核\\等值线\\contour_");
    }

    /**初始化图像矩阵*/
    public static void initGridMatrix(String path){
        String poi="";
        JSONObject obj;
        int code;
        double price;
        Map<Integer,Double> gridprice=new HashMap<>();
        Map<Integer,Double> gridprice_copy=new HashMap<>();
        double[][] gridmatrix=new double[400][400];

        for(int i=6;i<=6;i++) {
            String file=path+i+".txt";
            Vector<String> pois= FileTool.Load(file,"utf-8");

            for(int j=0;j<pois.size();j++){
                poi=pois.elementAt(j);
                obj=JSONObject.fromObject(poi);
                code=obj.getInt("code");
                price=obj.getDouble("average_price");
                gridprice.put(code,price);
                gridprice_copy.put(code,price);
            }
        }

        //将二维数组的值填充好
        for(int row=1;row<=400;row++){
            for(int col=1;col<=400;col++){
                code=col+(row-1)*400;

                if(gridprice.containsKey(code)){
                    price=gridprice.get(code);
                    System.out.println(code+":"+price);
                    gridmatrix[row-1][col-1]=price;
                }else{
                    gridmatrix[row-1][col-1]=0;
                    gridprice.put(code,(double)0);
                    gridprice_copy.put(code,(double)0);
                }
            }
        }

        //选取种子点进行扩散："code":40961,"average_price":9.943082,"row":103,"col":161
        int target_row=102;
        int target_col=161;
        int target_code=40561;
        int front_target_code;
        double threshold_min;
        double target_threshold_min;
        Map<Integer,Double> finished_grid=new HashMap<>();//用来装已经分类的code
        Map<Double,JSONObject> price_block=new HashMap<>();//用来装不同的价格区域块的代码
        List<Integer> neighborhood=new ArrayList<>();//用来装根节点的八个周边节点
        JSONObject obj_neighborhood=new JSONObject();//用来记录每一个根节点的周边节点有哪些
        List<Integer> neighborhood_copy=new ArrayList<>();
        JSONObject code_rowcol=new JSONObject();//用来标记该编码对应的行列号
        int count=0;
        while (gridprice.size()!=0){

            /*if(finished_grid.containsKey(target_code)){

            }else {
            }*/

            if(count==0){
                count++;
                get8Neighborhood(gridprice,gridprice_copy,finished_grid,target_code,target_row,target_col,code_rowcol,obj_neighborhood);
            }else {
                /**将种子节点的八邻域分别进行遍历，作为新的种子节点*/

                front_target_code=target_code;//因为该种子节点已经遍历完毕，故该节点变为前节点
                neighborhood_copy=obj_neighborhood.getJSONArray(""+front_target_code);
                System.out.println(neighborhood_copy);
                int front_size=neighborhood_copy.size();

                //分别以周围的八邻域为根节点进行遍历
                while(front_size!=0){

                    target_code=neighborhood_copy.get(0);
                    JSONArray rc=code_rowcol.getJSONArray(""+target_code);
                    //System.out.println(rc);
                    target_row=(int)rc.get(0);
                    target_col=(int)rc.get(1);
                    get8Neighborhood(gridprice,gridprice_copy,finished_grid,target_code,target_row,target_col,code_rowcol,obj_neighborhood);

                    neighborhood_copy.remove(0);
                    System.out.println(neighborhood_copy);
                }
            }
        }
    }

    public static void get8Neighborhood(Map gridprice,Map gridprice_copy,Map finished_grid,int target_code,int target_row,int target_col,JSONObject code_rowcol,JSONObject obj_neighborhood){
        double target_price=(double)gridprice_copy.get(target_code);
        double target_threshold_min=Math.floor(target_price);

        finished_grid.put(target_code,target_price);
        gridprice.remove(target_code);

        JSONObject seed_blocks=new JSONObject();
        JSONArray code_block=new JSONArray();
        List<Integer> neighborhood=new ArrayList<>();
        for(int r=target_row-1;r<=target_row+1;r++){
            for(int c=target_col-1;c<=target_col+1;c++){
                int cd=c+(r-1)*400;
                if(cd!=target_code&&gridprice.containsKey(cd)){

                    neighborhood.add(cd);//该list记录了当前根节点的八邻域的网格，方便作为下一次的遍历的根
                    int[] rowcol={r,c};
                    code_rowcol.put(cd,rowcol);//将该网格的行列号同时也存下来

                    double cd_price=(double)gridprice.get(cd);
                    //System.out.println(cd_price);
                    //System.out.println(target_threshold_min);
                    //System.out.println(target_threshold_min+1.5);
                    if(cd_price>=target_threshold_min&&cd_price<(target_threshold_min+2)){

                        if(seed_blocks.containsKey(target_code)){
                            code_block=seed_blocks.getJSONArray(""+target_code);
                            code_block.add(cd);
                            seed_blocks.put(target_code,code_block);
                            finished_grid.put(cd,cd_price);
                            gridprice.remove(cd);
                        }else {
                            code_block.add(cd);
                            seed_blocks.put(target_code,code_block);
                            finished_grid.put(cd,cd_price);
                            gridprice.remove(cd);
                        }

                    }else {
                        System.out.println("不在同一个阈值范围内："+target_code+"——"+target_price+" "+cd+"——"+cd_price);
                    }
                }
            }
        }
        Collections.sort(neighborhood);
        obj_neighborhood.put(target_code,neighborhood);
    }

    public void DiffPoint(int x1,int y1,int x2,int y2){

    }
}