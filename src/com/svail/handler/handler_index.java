package com.svail.handler;

import com.svail.bean.Response;
import utils.UtilFile;

import java.io.File;

/**
 * Created by ZhouXiang on 2016/7/2.
 */
public class handler_index implements handler{

    @Override
    public Response get(String path){
        File f = new File("view/生态文明/newtest.html");
        Response r= new Response();
        r.setCode(200);
        r.setContent( UtilFile.getContent(f));
        return r;
    }
}
