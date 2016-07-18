package com.svail.handler;

import com.svail.bean.Response;
import utils.UtilFile;

import java.io.File;

/**
 * Created by ZhouXiang on 2016/7/2.
 */
public class handler_static implements handler{

    @Override
    public Response get(String path){
        Response r= new Response();


        File f = new File(path);
        if(f.exists()) {
            String s = UtilFile.getContent(f);
            r.setContent(s);
            r.setCode(200);
        }else {
            r.setContent( "file not found!");
            r.setCode(404);
        }
        return r;
    }


}
