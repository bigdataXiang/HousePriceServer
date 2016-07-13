package com.svail.handler;

import utils.UtilFile;

import java.io.File;

/**
 * Created by ZhouXiang on 2016/7/2.
 */
public class handler_index implements handler{

    @Override
    public String get(String path){
        File f = new File("view/生态文明/newtest.html");
        return UtilFile.getContent(f);
    }
}
