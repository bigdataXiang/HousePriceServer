package com.svail.handler;

import utils.UtilFile;

import java.io.File;

/**
 * Created by ZhouXiang on 2016/7/2.
 */
public class handler_static implements handler{

    @Override
    public String get(String path){
        File f = new File(path);
        String s=UtilFile.getContent(f);
        return s;
    }


}
