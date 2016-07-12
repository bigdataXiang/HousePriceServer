package com.svail;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.svail.handler.*;
import utils.UtilHttp;

import java.io.*;
import java.net.URI;

/**
 * Created by timeloveboy on 16/5/30.
 */
public class MainHandler implements HttpHandler {

    /**
     * /api?level=10&location=13.332
     */
    public void handle(HttpExchange exchange) throws IOException {
        String requestMethod = exchange.getRequestMethod();
        URI url = exchange.getRequestURI();//获取url链接饿信息
        String body = UtilHttp.getBody(exchange);
        String response="";
        switch (url.getPath()){
            case "/api":response=new handler_api().get(url.getPath());
                break;
            case "/index":response=new handler_index().get(url.getPath());
                break;
            case "/info":response=new handler_info().get(url.getPath());
                break;
            case "/price":response=new handler_gridprice().get(url.getPath(),body);
                break;
            case "/gridcolor":response=new handler_gridcolor().get(url.getPath());
                break;
            default:response= new handler_static().get(url.getPath().substring(1));
                break;
        }
        UtilHttp.setResponse(exchange,200,response);
    }
}