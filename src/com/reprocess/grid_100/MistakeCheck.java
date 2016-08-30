package com.reprocess.grid_100;

import utils.FileTool;

import java.util.Vector;

/**
 * Created by ZhouXiang on 2016/8/30.
 */
public class MistakeCheck {
    public static void main(String args[]){
        Vector<String> increment= FileTool.CompareFile("E:\\房地产可视化\\to100\\test1.txt",
                "E:\\房地产可视化\\to100\\test2.txt");
        for(int i=0;i<increment.size();i++){
            System.out.println(increment.elementAt(i));
            FileTool.Dump(increment.elementAt(i),"E:\\房地产可视化\\to100\\第一种文件相对第二种文件的增量.txt", "utf-8");
        }
        System.out.println("总共有"+increment.size()+"条记录");

    }
}
