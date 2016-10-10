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

        /*for(int i=6;i<7;i++){
            creatCounter(i);
        }*/

        //creatJsonToServer("D:\\中期考核\\等值线\\图像分割算法\\hanqing\\坐标串_6.txt",6);


        //test_dropDiagonal("D:\\中期考核\\等值线\\二维栅格数组_阈值化.txt");


        for(int i=2;i<12;i++){
            //creatJsonToServer("D:\\中期考核\\等值线\\图像分割算法\\hanqing\\hq\\坐标串_"+i+".txt",i);

            processHanQing("D:\\中期考核\\等值线\\图像分割算法\\hanqing\\resu_1010.txt",i);
        }



    }
    /**处理汉青的算法结果*/
    public static void processHanQing(String file,int gridvalue){
        //将每个网格点的坐标存在全局mapcode_vertex_coordinates中
        initGridMatrix("D:\\中期考核\\等值线\\contour_");

        //code_index
        //key:网格编码
        //value:编码为gridvalue的网格的区块标签
        Map<Integer,Integer> code_index=new HashMap<>();
        Vector<String> pois=FileTool.Load(file,"utf-8");
        int[][] blocks=new int[400][400];
        int length=pois.size();
        for(int i=0;i<length;i++){
            String[] array=pois.elementAt(i).split(",");
            int width=array.length;
            for(int j=0;j<width;j++){
                if(!array[j].equals("-1")){
                    //System.out.println(array[j]);
                    blocks[i][j]=Integer.parseInt(array[j]);
                    if(blocks[i][j]!=-1){
                        int value=blocks[i][j]/100000;
                        int index=blocks[i][j]%100000;
                        //int array_code=j+i*length;
                        //int code=array_codeTogrid_code(array_code,length);
                        if(value==gridvalue){
                            int row=400-i;
                            int col=j+1;
                            int code=(row-1)*400+col;
                           // System.out.println(code);
                            code_index.put(code,index);
                        }
                    }
                }
            }
        }

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
        int a=0;
        int count;
        int total=0;
        //System.out.println(ts.size());
        while(it_ts.hasNext())
        {
            tag=it_ts.next();
            //index_block包含的都是区块标签为tag的网格
            //key:网格编码
            //value:网格对应的区块标签,特殊情况，key=0时表示的是具有该区块标签的网格的个数
            Map<Integer,Integer> index_block=getIndexBlocks(code_index,tag);
            a=index_block.size();
            JSONObject obj;
            JSONObject corners;
            int grid_code;
            JSONObject result=new JSONObject();
            Iterator<Integer> it=index_block.keySet().iterator();

            count=0;
            while (it.hasNext()){

                grid_code=it.next();
                //System.out.println(grid_code);
                if(code_vertex_coordinates.containsKey(grid_code)){
                    count++;
                    obj=code_vertex_coordinates.get(grid_code);
                    corners=obj.getJSONObject("corners");
                    result.put(grid_code,corners);
                }
            }
            total+=a*count;
            //System.out.println(result);
            //FileTool.Dump(result.toString(),"D:\\中期考核\\等值线\\图像分割算法\\hanqing\\hq\\等值线_"+gridvalue+".txt","utf-8");
        }
        System.out.println(total);

    }

    public static void creatJsonToServer(String file,int index){

        Vector<String> jsons=FileTool.Load(file,"utf-8");
        JSONArray result_json=new JSONArray();
        for(int i=0;i<jsons.size();i++){
            String json=jsons.elementAt(i);
            result_json.add(json);
        }
        FileTool.Dump(result_json.toString(),"D:\\中期考核\\等值线\\图像分割算法\\hanqing\\hq\\等值线_"+index+"_json.txt","utf-8");
    }
    /**这是我写的算法，可能有点问题*/
    /*public static void creatCounter(int counter_value){
        int[][] gridmatrix=initGridMatrix("D:\\中期考核\\等值线\\contour_");


        for(int i=0;i<gridmatrix.length;i++){
            for(int j=0;j<gridmatrix[0].length;j++){
                if(gridmatrix[i][j]!=0){
                    System.out.print(gridmatrix[i][j]+" ");
                }
            }
            System.out.print("\n");
        }

        //获取价格为五万的标签块,除了五万，其他的都为0
        //code_index中：key:网格编码 value：第几个五万的区域块标签
        //如果value为0，表示该网格不是五万的值
        Map<Integer,Integer> code_index=getBlocks(gridmatrix,counter_value);
        System.out.println(code_index.size());

        Iterator<Integer> iterator=code_index.keySet().iterator();
        TreeSet ts=new TreeSet();
        List<Integer> codeis5=new ArrayList<>();
        while (iterator.hasNext()){
            int key=iterator.next();
            int value=code_index.get(key);
            if(value!=0){
                ts.add(value);
                codeis5.add(value);
            }
        }
        System.out.println("总共有"+ts.size()+"个区块");
        System.out.println("总共有"+codeis5.size()+"个网格");

        Iterator it_ts=ts.iterator();
        while(it_ts.hasNext())
        {
            int tag=(int)it_ts.next();
            System.out.println(tag);

            Map<Integer,Integer> index_block=getIndexBlocks(code_index,tag);
            //System.out.println(index_block.size());
            int cols=gridmatrix[0].length;

            Iterator<Integer> it=index_block.keySet().iterator();
            int array_code;
            int array_value;
            int grid_code;
            JSONObject obj;
            JSONObject corners;

            JSONObject result=new JSONObject();
            while (it.hasNext()){
                array_code=it.next();
                array_value=index_block.get(array_code);
                //System.out.println(array_value);

                grid_code=array_codeTogrid_code(array_code,cols);

                if(code_vertex_coordinates.containsKey(grid_code)){
                    obj=code_vertex_coordinates.get(grid_code);
                    corners=obj.getJSONObject("corners");
                    result.put(grid_code,corners);
                }
            }
            System.out.println(result);
            FileTool.Dump(result.toString(),"D:\\中期考核\\等值线\\等值线结果\\等值线_"+counter_value+".txt","utf-8");
        }
    }*/

    public static int array_codeTogrid_code(int array_code,int cols){
        int gridcode=0;
        int array_row=array_code/cols;//行
        int array_col=array_code%cols;//列

        int grid_row=400-array_row;
        int grid_col=array_col+1;
        gridcode=grid_col+(grid_row-1)*cols;

        return gridcode;
    }

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
        block.put(0,count);
        return block;
    }
    public static Map<Integer,Integer> getBlocks(int[][] blocks,int gridvalue){

        int rows=blocks.length;
        int cols=blocks[0].length;

        //将区域生成一个只有gridvalue值的二维数组，其他为0；
        for(int i=0;i<rows;i++){
            for(int j=0;j<cols;j++){

                if(blocks[i][j]==gridvalue){

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
                    //System.out.println("初始的标签值表示的是这个值出现的次数，应该总是比区块标签要大的？");
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
                                //System.out.println(code+"的left比top值大:"+top_value);
                                code_index.put(code,top_value);

                                Iterator it=code_index.keySet().iterator();
                                //不仅要改left值，还要改left的left值
                                //System.out.println("开始遍历该数据，但是这里的遍历有点问题：");
                                while (it.hasNext()){
                                    int it_key=(int)it.next();
                                    int it_value=code_index.get(it_key);
                                    //System.out.println(it_key+"："+it_value);
                                    //System.out.println("有没有可能存在两个网格的value值相等却这两个网格不联通的情况？");
                                    if(it_value==left_value){
                                        //System.out.println("将"+it_key+"的标签由"+it_value+"改成"+top_value);
                                        code_index.put(it_key,top_value);
                                    }

                                }
                            }else if(top_value>left_value){
                                //System.out.println(code+"的left比top值小:"+left_value);
                                code_index.put(code,left_value);
                                code_index.put(top,left_value);

                                //不仅要改top值，还要改top的top值
                                Iterator it=code_index.keySet().iterator();
                                //System.out.println("开始遍历该数据，但是这里的遍历有点问题：");
                                while (it.hasNext()){
                                    int it_key=(int)it.next();
                                    int it_value=code_index.get(it_key);
                                    //System.out.println(it_key+"："+it_value);
                                    //System.out.println("有没有可能存在两个网格的value值相等却这两个网格不联通的情况？");
                                    if(it_value==top_value){
                                        //System.out.println("将"+it_key+"的标签由"+it_value+"改成"+left_value);
                                        code_index.put(it_key,left_value);
                                    }
                                }
                            }else {
                                //System.out.println(code+"的left和top值一样:"+top_value);
                                code_index.put(code,top_value);
                            }
                        }else if(top_value!=0&&left_value==0){
                            top_value=code_index.get(top);
                            code_index.put(code,top_value);
                            //System.out.println(code+"仅有top值:"+top_value);

                        }else if(top_value==0&&left_value!=0){
                            left_value=code_index.get(left);
                            code_index.put(code,left_value);
                            //System.out.println(code+"仅有left值:"+left_value);
                        }

                    }else if(topbool&&!leftbool){
                        top_value=code_index.get(top);
                        if(top_value!=0){
                            //System.out.println(code+"仅有top值:"+top_value);
                            code_index.put(code,top_value);
                        }else {
                            //System.out.println(code+":"+index);
                        }
                    }else if(!topbool&&leftbool){
                        left_value=code_index.get(left);
                        if(left_value!=0){
                            //System.out.println(code+"仅有left值:"+left_value);
                            code_index.put(code,left_value);
                            /*int temp=code_index.get(code);
                            System.out.println("最终"+code+"的标签是"+temp);*/
                        }else {
                            //System.out.println(code+":"+index);
                        }

                    }

                    int temp=code_index.get(code);
                    //System.out.println("最终"+code+"的标签是"+temp);
                }else {
                    //System.out.println("最终"+code+"的标签是"+0);
                    code_index.put(code,0);
                }
            }
        }

        return code_index;
    }

    public static Map<Integer,JSONObject> code_vertex_coordinates=new HashMap<>();
    /**初始化图像矩阵*/
    public static double[][] initGridMatrix(String path){
        String poi="";
        JSONObject obj;
        int code;
        double price;
        Map<Integer,Double> gridprice=new HashMap<>();
        double[][] gridmatrix=new double[400][400];

        for(int i=2;i<=8;i++) {
            String file=path+i+".txt";
            Vector<String> pois= FileTool.Load(file,"utf-8");

            for(int j=0;j<pois.size();j++){
                poi=pois.elementAt(j);
                obj=JSONObject.fromObject(poi);
                code=obj.getInt("code");
                code_vertex_coordinates.put(code,obj);

                price=obj.getDouble("average_price");
                gridprice.put(code,price);
            }
        }

        //将二维数组的值填充好
        //由于本身的网格编码的行的递增顺序是从下到上的
        //而二维数组的行是从上到下的
        //故需要将两者协调一下，即将网格按照从上到下的顺序，而不是按照网格的大小顺序存储到二维数组中
        int array_row=0;//其中array_row+row=400
        for(int row=400;row>=1;row--){
            String str="";
            int array_col=0;//其中array_col=col-1;
            for(int col=1;col<=400;col++){
                code=col+(row-1)*400;

                if(gridprice.containsKey(code)){
                    price=gridprice.get(code);
                    gridmatrix[array_row][array_col]=price;
                    //System.out.println(array_row+","+array_col+":"+gridmatrix[array_row][array_col]);

                }else{
                    gridmatrix[array_row][array_col]=0.0;
                }
                str+=gridmatrix[array_row][array_col]+",";
                array_col++;
            }
            //FileTool.Dump(str,"D:\\中期考核\\等值线\\图像分割算法\\hanqing\\dt_1010.txt","utf-8");
            array_row++;
        }
        return gridmatrix;
    }

    public static Map<Integer,Integer> codeToRowCol(int code,int cols){
        Map<Integer,Integer> rowcol=new HashMap<>();
        int i=code/cols;//行
        int j=code%cols;//列
        rowcol.put(0,i);
        rowcol.put(1,j);

        return rowcol;
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
                                //不仅要改left值，还要改left的left值
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

                                //不仅要改top值，还要改top的top值
                                Iterator it=code_index.keySet().iterator();
                                System.out.println("开始遍历该数据，但是这里的遍历有点问题：");
                                while (it.hasNext()){
                                    int it_key=(int)it.next();
                                    int it_value=code_index.get(it_key);
                                    System.out.println(it_key+"："+it_value);
                                    System.out.println("有没有可能存在两个网格的value值相等却这两个网格不联通的情况？");
                                    if(it_value==top_value){
                                        System.out.println("将"+it_key+"的标签由"+it_value+"改成"+left_value);
                                        code_index.put(it_key,left_value);
                                    }
                                }
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



        for(int i=0;i<rows;i++) {
            String str="";
            for (int j = 0; j < cols; j++) {
                int code = j + i * cols;
                int tag=code_index.get(code);
                str+=tag+",";
            }
            //FileTool.Dump(str,"D:\\中期考核\\等值线\\结果1.txt","utf-8");
        }

        Iterator iterator=code_index.keySet().iterator();
        Map<Integer,Integer> block=new HashMap<>();//存储了所有标签为2的网格
        List<Integer> index_2=new ArrayList<>();
        while (iterator.hasNext()){
            int key=(int)iterator.next();
            int value=code_index.get(key);
            if(value==2){
                block.put(key,value);
                index_2.add(key);
                //System.out.println(key);
            }
        }

        List<Integer> boundary_grids=getGridBoundary(block,cols,rows);
        Map<Integer,Integer> boundary_block=new HashMap<>();
        for(int grid:boundary_grids){
            boundary_block.put(grid,2);
        }
        int max_code=boundary_grids.get(boundary_grids.size()-1);
        System.out.println(index_2);
        System.out.println(boundary_grids);
        Map<Integer,Integer> differ=getDiffrent(index_2,boundary_grids);

        Map<Integer,Integer> status=new HashMap<>();
        int count=boundary_grids.size();

        List<Integer> directions=new ArrayList<>();

        while (count!=0){
            if(count==boundary_grids.size()){
                status=getNextDireciton(max_code,rows,cols,boundary_block,status,differ);
                count--;
                directions.add(max_code);
                traversal_monitor.put(max_code,1);//记录第一个遍历点第一被遍历的情况
            }else {
                int next_code=status.get(0);
                System.out.println(next_code);
                if(traversal_monitor.containsKey(next_code)){
                    int frequency=traversal_monitor.get(next_code);
                    traversal_monitor.put(next_code,frequency++);
                }else {
                    traversal_monitor.put(next_code,1);
                }
                directions.add(next_code);
                status=getNextDireciton(next_code,rows,cols,boundary_block,status,differ);
                count--;
            }
        }

        System.out.println(directions);

    }

    public  static List<Integer> getGridBoundary(Map<Integer,Integer> block,int cols,int rows){

        //查找出边界网格：只要上下左右有一边为空值的网格即为边界网格
        List<Integer> boundary_grids=new ArrayList<>();
        Iterator it=block.keySet().iterator();

        while (it.hasNext()){
            int key=(int)it.next();
            int value=block.get(key);
            //if(key==151){
            int i=codeToRowCol(key,cols).get(0);//行
            int j=codeToRowCol(key,cols).get(1);//列

            int left=left_top_right_bottom(i,j,rows,cols).get(4);
            int top=left_top_right_bottom(i,j,rows,cols).get(2);
            int right=left_top_right_bottom(i,j,rows,cols).get(0);
            int bottom=left_top_right_bottom(i,j,rows,cols).get(6);

            boolean left_bool=block.containsKey(left);
            boolean right_bool=block.containsKey(right);
            boolean top_bool=block.containsKey(top);
            boolean bottom_bool=block.containsKey(bottom);
            if(left_bool&&right_bool&&top_bool&&bottom_bool){

            }else {
                boundary_grids.add(key);
            }



            //}

        }

        Collections.sort(boundary_grids);
        return boundary_grids;
    }



    /**获取八领域方向的code值*/
    public static Map<Integer,Integer> left_top_right_bottom(int row,int col,int rows,int cols){
        Map<Integer,Integer> around=new HashMap<>();

        int right=-1;
        if(col<cols-1){
            right=(col+1)+row*cols;
        }
        around.put(0,right);

        int right_top=-1;
        if(row>0&&col<cols-1){
            right_top=(col+1)+(row-1)*cols;
        }
        around.put(1,right_top);

        int top=-1;
        if(row>0){
            top=col+(row-1)*cols;
        }
        around.put(2,top);

        int left_top=-1;
        if(row>0&&col>0){
            left_top=(col-1)+(row-1)*cols;
        }
        around.put(3,left_top);

        int left=-1;
        if(col>0){
            left=(col-1)+row*cols;
        }
        around.put(4,left);

        int left_bottom=-1;
        if(col>0&&row<rows-1){
            left_bottom=(col-1)+(row+1)*cols;
        }
        around.put(5,left_bottom);

        int bottom=-1;
        if(row<rows-1){
            bottom=col+(row+1)*cols;
        }
        around.put(6,bottom);

        int right_bottom=-1;
        if(col<cols-1&&row<rows-1){
            right_bottom=(col+1)+(row+1)*cols;
        }
        around.put(7,right_bottom);

        return around;
    }

    /**定义一个遍历监视器，监视这个code总共有几次被遍历，一般遍历次数不超过两次*/
    public static Map<Integer,Integer> traversal_monitor=new HashMap<>();
    public static Map<Integer,Integer>  getNextDireciton(int code,int rows,int cols,Map block,Map<Integer,Integer> state, Map<Integer,Integer> differ){

        //Map<Integer,Integer> state=new HashMap<>();
        //key=0 value=下一个节点值
        //key=1 value=上一次移动的方向（left=0，top=1，right=2，bottom=3）
        //key=3 value=前一个节点值


        int next_code=0;
        //从右、上、左、下四个方向逆时针遍历
        int i=codeToRowCol(code,cols).get(0);//行
        int j=codeToRowCol(code,cols).get(1);//列

        int right=left_top_right_bottom(i,j,rows,cols).get(0);
        int right_top=left_top_right_bottom(i,j,rows,cols).get(1);
        int top=left_top_right_bottom(i,j,rows,cols).get(2);
        int left_top=left_top_right_bottom(i,j,rows,cols).get(3);
        int left=left_top_right_bottom(i,j,rows,cols).get(4);
        int left_bottom=left_top_right_bottom(i,j,rows,cols).get(5);
        int bottom=left_top_right_bottom(i,j,rows,cols).get(6);
        int right_bottom=left_top_right_bottom(i,j,rows,cols).get(7);

        int before_code=0;
        if(state.size()==0){

        }else {
            before_code = state.get(3);
        }

        //监视除了回的那条路还有没有别的路是通的
        int monitor=0;

        //判断该节点是否只有一条路可以通，
        // 如果是，则以后不能返回到这个点了，如果返回只会造成不断地重复
        int unique=0;

        int frequency=0;
        Map<Integer,Integer> adjacency=new HashMap<>();

        int move_direction=-1;
        if(state.size()==0){

        }else {
            move_direction=state.get(1);
        }

        //建立优先遍历序列
        int[] traversal_sequence={bottom,right_bottom,right,right_top,top,left_top,left,left_bottom};
        //通过判断当前的运动方向来决定谁最优先遍历
        if(move_direction==-1){

        }else if(move_direction==1){
            traversal_sequence[0]=1;
            traversal_sequence[1]=1;
            traversal_sequence[2]=1;
            traversal_sequence[3]=1;
            traversal_sequence[4]=1;
            traversal_sequence[5]=1;
            traversal_sequence[6]=1;
            traversal_sequence[7]=1;
        }


        //情况一：先检查除了原路返回和走已经走过的节点，能不能搜索到新的节点
        //除了保证下一个节点不是前一个节点外，还要保证下一个节点之前没有被遍历过
        if(block.containsKey(traversal_sequence[0])){
            //以下这两行是用来标记该节点的八邻域有哪些是有值的
            unique++;
            adjacency.put(6,traversal_sequence[0]);

            next_code=traversal_sequence[0];
            frequency=0;
            if(traversal_monitor.containsKey(next_code)){
                frequency=traversal_monitor.get(next_code);
            }
            if(next_code!=before_code&&frequency<1){
                state.put(0,next_code);
                state.put(1,6);
                state.put(3,code);
                state.put(4,unique);
                monitor++;
            }
        }
        if(block.containsKey(traversal_sequence[1])){
            unique++;
            adjacency.put(7,traversal_sequence[1]);

            if(monitor==0){
                unique++;
                next_code=traversal_sequence[1];
                frequency=0;
                if(traversal_monitor.containsKey(next_code)){
                    frequency=traversal_monitor.get(next_code);
                }
                if(next_code!=before_code&&frequency<1){
                    state.put(0,next_code);
                    state.put(1,7);
                    state.put(3,code);
                    state.put(4,unique);
                    monitor++;
                }
            }
        }
        if (block.containsKey(traversal_sequence[2])){
            unique++;
            adjacency.put(0,traversal_sequence[2]);

            if(monitor==0){
                next_code=traversal_sequence[2];
                frequency=0;
                if(traversal_monitor.containsKey(next_code)){
                    frequency=traversal_monitor.get(next_code);
                }
                if(next_code!=before_code&&frequency<1){
                    state.put(0,next_code);
                    state.put(1,0);
                    state.put(3,code);
                    state.put(4,unique);
                    monitor++;
                }
            }
        }
        if(block.containsKey(traversal_sequence[3])){
            unique++;
            adjacency.put(1,traversal_sequence[3]);

            if(monitor==0){
                next_code=traversal_sequence[3];
                frequency=0;
                if(traversal_monitor.containsKey(next_code)){
                    frequency=traversal_monitor.get(next_code);
                }
                if(next_code!=before_code&&frequency<1){
                    state.put(0,next_code);
                    state.put(1,1);
                    state.put(3,code);
                    state.put(4,unique);
                    monitor++;
                }
            }
        }
        if(block.containsKey(traversal_sequence[4])){
            unique++;
            adjacency.put(2,traversal_sequence[4]);

            if(monitor==0){
                next_code=traversal_sequence[4];
                frequency=0;
                if(traversal_monitor.containsKey(next_code)){
                    frequency=traversal_monitor.get(next_code);
                }
                if(next_code!=before_code&&frequency<1){
                    state.put(0,next_code);
                    state.put(1,2);
                    state.put(3,code);
                    state.put(4,unique);
                    monitor++;
                }
            }
        }
        if(block.containsKey(traversal_sequence[5])){
            unique++;
            adjacency.put(3,traversal_sequence[5]);

            if(monitor==0){
                next_code=traversal_sequence[5];
                frequency=0;
                if(traversal_monitor.containsKey(next_code)){
                    frequency=traversal_monitor.get(next_code);
                }
                if(next_code!=before_code&&frequency<1){
                    state.put(0,next_code);
                    state.put(1,3);
                    state.put(3,code);
                    state.put(4,unique);
                    monitor++;
                }
            }
        }
        if(block.containsKey(traversal_sequence[6])){
            unique++;
            adjacency.put(4,traversal_sequence[6]);

            if(monitor==0){
                next_code=traversal_sequence[6];
                frequency=0;
                if(traversal_monitor.containsKey(next_code)){
                    frequency=traversal_monitor.get(next_code);
                }
                if(next_code!=before_code&&frequency<1){
                    state.put(0,next_code);
                    state.put(1,4);
                    state.put(3,code);
                    state.put(4,unique);
                    monitor++;
                }
            }
        }
        if(block.containsKey(traversal_sequence[7])){
            unique++;
            adjacency.put(5,traversal_sequence[7]);

            if(monitor==0){
                next_code=traversal_sequence[7];
                frequency=0;
                if(traversal_monitor.containsKey(next_code)){
                    frequency=traversal_monitor.get(next_code);
                }
                if(next_code!=before_code&&frequency<1){
                    state.put(0,next_code);
                    state.put(1,5);
                    state.put(3,code);
                    state.put(4,unique);
                    monitor++;
                }
            }
        }

        //情况二：选择原路返回还是选择其他已经遍历过的节点？
        //判断这条路是否是唯一的路
        //如果是，返回去也没有用了，只能重复地往返
        //如果不是，则还可以回去看看
        if(monitor==0){
            int before_unique=state.get(4);

            //判断前一个根节点的路是否唯一，如果唯一就没有返回去的必要了，返回去只能造成不断地重复
            if(before_unique>1){
                next_code=before_code;
                int direction=state.get(1);
                int inver_direction=inverseDirection(direction);

                state.put(0,next_code);
                state.put(1,inver_direction);
                state.put(3,code);
                state.put(4,unique);
            }else {

                Iterator<Integer> it=adjacency.keySet().iterator();
                int direction;
                int adjacency_code;
                while (it.hasNext()){
                    direction=it.next();
                    adjacency_code=adjacency.get(direction);
                     if(adjacency_code!=before_code){
                         if(adjacency.size()==2){

                             next_code=adjacency_code;

                             state.put(0,next_code);
                             state.put(1,direction);
                             state.put(3,code);
                             state.put(4,unique);
                         }
                     }
                }
            }
        }

        return state;
    }

    public static int inverseDirection(int direction){
        int inverse=0;
        switch (direction){
            case 0:inverse=4;
                break;
            case 2:inverse=6;
                break;
            case 4:inverse=0;
                break;
            case 6:inverse=2;
                break;
        }
        return inverse;
    }

    /**找出两个list中不同的元素*/
    public static Map<Integer,Integer> getDiffrent(List<Integer> list1, List<Integer> list2)
    {
        Map<Integer,Integer> map = new HashMap<Integer,Integer>(list1.size()+list2.size());
        Map<Integer,Integer> diff = new HashMap<Integer,Integer>();
        List<Integer> maxList = list1;
        List<Integer> minList = list2;
        if(list2.size()>list1.size())
        {
            maxList = list2;
            minList = list1;
        }

        for (int index_max : maxList)
        {
            map.put(index_max, 1);
        }

        for (int index_min : minList)
        {
            Integer cc = map.get(index_min);
            if(cc!=null)
            {
                map.put(index_min, ++cc);
                continue;
            }
            map.put(index_min, 1);
        }

        for(Map.Entry<Integer, Integer> entry:map.entrySet())
        {
            if(entry.getValue()==1)
            {
                diff.put(entry.getKey(),1);
            }
        }
        return diff;
    }


}