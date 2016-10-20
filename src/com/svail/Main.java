package com.svail;

import com.sun.net.httpserver.HttpServer;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

public class Main {

    public static void main(String[] args)  throws Exception{
        InetSocketAddress addr = new InetSocketAddress(8090);//
        HttpServer server =  HttpServer.create(addr, 0);
        //前缀
        server.createContext("/", new MainHandler());
        server.setExecutor(Executors.newCachedThreadPool());
        server.start();
        System.out.println("Server is listening on port 8090 ...");
    }
}
