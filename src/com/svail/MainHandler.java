package com.svail;

import com.reprocess.grid_100.CallGridCurve;
import com.reprocess.grid_100.CallInterestGrid;
import com.reprocess.grid_100.CallPriceAcceleration;
import com.reprocess.grid_100.ContourGeneration.ContourGeneration;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.svail.bean.Response;
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

        Response response;
        switch (url.getPath()){
            case "/api":response=new handler_api().get(url.getPath());
                break;
            case "/index":response=new handler_index().get(url.getPath());
                break;
            case "/info":response=new handler_info().get(url.getPath());
                break;
            case "/price":response=new CallGridCurve().get(body);
                break;
            case "/gridcolor":response=new CallInterestGrid().get(body);
                break;
            case "/gridcolor_interpolation":response=new ContourGeneration().get(body);
                break;
            case "/gridacceleration":response=new CallPriceAcceleration().get(body);
                break;
            case "/gridcolor_50":response=new com.reprocess.grid_50.CallInterestGrid().get(body);
                break;
            case "/gridacceleration_50":response=new com.reprocess.grid_50.CallPriceAcceleration().get(body);
                break;
            case "/pricecurve_50":response=new com.reprocess.grid_50.CallGridCurve().get(body);
                break;
            case "/investment":response=new com.reprocess.grid_50.GridFeatureStatistics().get(body);
                break;


            //以下这三个不需要用
            /*case "/grid1000":response=new handler_1000().get(url.getPath(),body);
                break;
            case "/grid2000":response=new handler_2000().get(url.getPath(),body);
                break;
            case "/grid3000":response=new handler_3000().get(url.getPath(),body);
                break;*/
            default:response= new handler_static().get(url.getPath().substring(1));
                break;
        }
        System.out.println(response.getCode()+"\t"+requestMethod+"\t"+ url.getPath()+"\t"+body);
        UtilHttp.setResponse(exchange,response.getCode(),response.getContent());
    }
}