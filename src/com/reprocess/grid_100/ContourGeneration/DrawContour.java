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
        //("D:\\中期考核\\等值线\\contour_");
        //int[][] block=new int[4][4];
        //System.out.println(block[0][1]);
        test_dropDiagonal("D:\\中期考核\\等值线\\二维栅格数组_阈值化_部分.txt");
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

        for(int i=2;i<=8;i++) {
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
        for(int row=100;row<=130;row++){
            String str="";
            for(int col=140;col<=170;col++){
                code=col+(row-1)*400;

                if(gridprice.containsKey(code)){
                    price=gridprice.get(code);
                    //System.out.println(code+":"+price);
                    gridmatrix[row-1][col-1]=price;

                }else{
                    gridmatrix[row-1][col-1]=0;
                    price=0;
                    gridprice.put(code,(double)0);
                    gridprice_copy.put(code,(double)0);
                }
                int p=(int)Math.floor(price);
                str+=p+",";
            }
            FileTool.Dump(str,"D:\\中期考核\\等值线\\二维栅格数组_阈值化_部分.txt","utf-8");
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
                    if(cd_price>=target_threshold_min&&cd_price<(target_threshold_min+1)){

                        System.out.println("在同一个阈值范围内："+cd);
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

    public static void test(String file){
        Vector<String> pois=FileTool.Load(file,"utf-8");
        int rows=pois.size();
        String[] poi=pois.elementAt(0).split(",");
        int cols=poi.length;

        //将区域生成一个只有五万的二维数组，其他为0；
        int[][] blocks=new int[rows][cols];
        for(int i=0;i<rows;i++){
            String[] temp=pois.elementAt(i).split(",");
            for(int j=0;j<cols;j++){
                blocks[i][j]=Integer.parseInt(temp[j]);
                if(blocks[i][j]==5){

                }else {
                    blocks[i][j]=0;
                }
            }
        }

        int index=0;
        Map<Integer,Integer> code_index=new HashMap<>();
        for(int i=0;i<rows;i++){
            for(int j=0;j<cols;j++){
                int code=j+i*cols;
                int price=blocks[i][j];

                if(price!=0){
                    index++;
                    code_index.put(code,index);

                    int left_top=-1;
                    if(i>0&&j>0){
                        left_top=(j-1)+(i-1)*cols;
                    }
                    int top=-1;
                    if(i>0){
                        top=j+(i-1)*cols;
                    }
                    int right_top=-1;
                    if(i>0&&j<cols-1){
                        right_top=(j+1)+(i-1)*cols;
                    }
                    int left=-1;
                    if(j>0){
                        left=(j-1)+i*cols;
                    }

                    int value;
                    if(code_index.containsKey(left_top)){
                        value=code_index.get(left_top);
                        if(value!=0){
                            System.out.println(code+":"+value);
                            code_index.put(code,value);
                        }
                    }
                    if(code_index.containsKey(top)){
                        value=code_index.get(top);
                        if(value!=0){
                            System.out.println(code+":"+value);
                            code_index.put(code,value);
                        }
                    }
                    if(code_index.containsKey(left)){
                        value=code_index.get(left);
                        if(value!=0){
                            System.out.println(code+":"+value);
                            code_index.put(code,value);
                        }
                    }
                    if(code_index.containsKey(right_top)){
                        value=code_index.get(right_top);
                        if(value!=0){

                            //如果在遍历右上角时，map中对应的该code的值已经更新
                            //说明左边部分（左、左上、上）有值,此时需要将右上的标记也要更新
                            //否则只需要根据右上的值进行code的值的更新
                            if(code_index.get(code)!=index){
                                int value1=code_index.get(code);
                                System.out.println("将"+right_top+"的标签"+value+"改成"+value1);
                                code_index.put(right_top,value1);
                            }else {
                                System.out.println(code+":"+value);
                                code_index.put(code,value);
                            }
                        }
                    }

                }else {
                    code_index.put(code,0);
                }
            }
        }
    }

    //不考虑对角线上的网格
    public static void test_dropDiagonal(String file){
        Vector<String> pois=FileTool.Load(file,"utf-8");
        int rows=pois.size();
        String[] poi=pois.elementAt(0).split(",");
        int cols=poi.length;

        //将区域生成一个只有五万的二维数组，其他为0；
        int[][] blocks=new int[rows][cols];
        for(int i=0;i<rows;i++){
            String[] temp=pois.elementAt(i).split(",");
            for(int j=0;j<cols;j++){
                blocks[i][j]=Integer.parseInt(temp[j]);
                if(blocks[i][j]==5){

                }else {
                    blocks[i][j]=0;
                }
            }
        }

        int index=0;
        Map<Integer,Integer> code_index=new HashMap<>();
        for(int i=0;i<rows;i++){
            for(int j=0;j<cols;j++){
                int code=j+i*cols;
                int price=blocks[i][j];

                if(price!=0){
                    index++;
                    System.out.println("初始的标签值表示的是这个值出现的次数，应该总是比区块标签要大的？");
                    code_index.put(code,index);

                    int top=-1;
                    if(i>0){
                        top=j+(i-1)*cols;
                    }

                    int left=-1;
                    if(j>0){
                        left=(j-1)+i*cols;
                    }

                    int left_value;
                    int top_value;
                    boolean topbool=code_index.containsKey(top);
                    boolean leftbool=code_index.containsKey(left);
                    if(topbool&&leftbool){
                        top_value=code_index.get(top);
                        left_value=code_index.get(left);
                        if(top_value!=0&&left_value!=0){
                            if(top_value<left_value){
                                System.out.println(code+"的left比top值大:"+top_value);
                                code_index.put(code,top_value);

                                Iterator it=code_index.keySet().iterator();
                                System.out.println("开始遍历该数据，但是这里的遍历有点问题：");
                                while (it.hasNext()){
                                    int it_key=(int)it.next();
                                    int it_value=code_index.get(it_key);
                                    System.out.println(it_key+"："+it_value);
                                    System.out.println("有没有可能存在两个网格的value值相等却这两个网格不联通的情况？");
                                    if(it_value==left_value){
                                        System.out.println("将"+it_key+"的标签由"+it_value+"改成"+top_value);
                                        code_index.put(it_key,top_value);
                                    }

                                }
                            }else if(top_value>left_value){
                                System.out.println(code+"的left比top值小:"+left_value);
                                code_index.put(code,left_value);
                                code_index.put(top,left_value);
                            }else {
                                System.out.println(code+"的left和top值一样:"+top_value);
                                code_index.put(code,top_value);
                            }
                        }else if(top_value!=0&&left_value==0){
                            top_value=code_index.get(top);
                            code_index.put(code,top_value);
                            System.out.println(code+"仅有top值:"+top_value);

                        }else if(top_value==0&&left_value!=0){
                            left_value=code_index.get(left);
                            code_index.put(code,left_value);
                            System.out.println(code+"仅有left值:"+left_value);
                        }

                    }else if(topbool&&!leftbool){
                        top_value=code_index.get(top);
                        if(top_value!=0){
                            System.out.println(code+"仅有top值:"+top_value);
                            code_index.put(code,top_value);
                        }else {
                            System.out.println(code+":"+index);
                        }
                    }else if(!topbool&&leftbool){
                        left_value=code_index.get(left);
                        if(left_value!=0){
                            System.out.println(code+"仅有left值:"+left_value);
                            code_index.put(code,left_value);
                            /*int temp=code_index.get(code);
                            System.out.println("最终"+code+"的标签是"+temp);*/
                        }else {
                            System.out.println(code+":"+index);
                        }

                    }

                    int temp=code_index.get(code);
                    System.out.println("最终"+code+"的标签是"+temp);
                }else {
                    System.out.println("最终"+code+"的标签是"+0);
                    code_index.put(code,0);
                }
            }
        }
    }
}