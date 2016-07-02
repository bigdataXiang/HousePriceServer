package com.svail.handler;

import utils.UtilFile;

import java.io.File;

/**
 * Created by ZhouXiang on 2016/7/2.
 */
public class handler_index {
    public String get(){
        File f = new File("view/index.html");
        return UtilFile.getContent(f);
    }
}
