package com.reprocess.grid_100.ContourGeneration.water;

import java.io.File;
import java.io.IOException;
import java.net.URL;


public class Main {

	
	public static String path;/////���·��
	static{		
		URL url=new Main().getClass().getProtectionDomain().getCodeSource().getLocation();
    	path=url.getPath();///System.getProperty("user.dir");
    	path=path.substring(1, path.lastIndexOf("bin/"));
	}
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		WaterShed waterShed=new WaterShed();
		waterShed.startWatering(new File(path+"img/3.jpg"));
		waterShed.showWatershededImage(new File(path+"img/watershed.jpg"));
//		waterShed.showWatershededImage2(path+"img/watershed");
		
		System.out.println("done!");
	}

}
