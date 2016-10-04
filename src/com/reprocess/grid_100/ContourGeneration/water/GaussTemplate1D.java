package com.reprocess.grid_100.ContourGeneration.water;

/**
 * 一维高斯模板类
 * @author Administrator
 *
 */
public final class GaussTemplate1D {
	/**
	 * sigma:1.6
	 */
	private static double[] templateX_Y0={0.0018898135906746216,0.01096042101911263,0.04301195907007542,0.11421020967515196,0.2051985803570411,0.24945803257588858,0.2051985803570411,0.11421020967515196,0.04301195907007542,0.01096042101911263,0.0018898135906746216};
	
	/**
	 * sigma:1.2262734984654078
	 */
	private static double[] templateX_Y1={0.001591943773381469,0.016321372317542313,0.08605524561506513,0.2333403013736628,0.3253822738406966,0.2333403013736628,0.08605524561506513,0.016321372317542313,0.001591943773381469};
	
	/**
	 * sigma:1.5450077936447955
	 */
	private static double[] templateX_Y2={0.0013736752000677237,0.009049075291685508,0.03920906951013616,0.11174576176591304,0.2094776895158822,0.2582894574326309,0.2094776895158822,0.11174576176591304,0.03920906951013616,0.009049075291685508,0.0013736752000677237};
	
	/**
	 * sigma:1.9465878414647124
	 */
	private static double[] templateX_Y3={0.0017738231933873902,0.00757330310795316,0.02483400194234688,0.06254526606198144,0.1209841673012846,0.17974176291200747,0.205095350962078,0.17974176291200747,0.1209841673012846,0.06254526606198144,0.02483400194234688,0.00757330310795316,0.0017738231933873902};
	
	/**
	 * sigma:2.4525469969308156
	 */
	private static double[] templateX_Y4={7.968609236745192E-4,0.002772669071248276,0.008169800993012816,0.02038556956040408,0.04307568122346389,0.07707958916034698,0.11680046192093232,0.14988137264970466,0.1628728499180994,0.14988137264970466,0.11680046192093232,0.07707958916034698,0.04307568122346389,0.02038556956040408,0.008169800993012816,0.002772669071248276};
	
	/**
	 * sigma:3.0900155872895905
	 */
	private static double[] templateX_Y5={6.875504209566184E-4,0.0018595456766296074,0.004529233348436208,0.009934776233057956,0.019624880936688967,0.03491174196593651,0.05593086745577397,0.08069509525828301,0.10484754591228278,0.12268315848327596,0.12927875903831346,0.12268315848327596,0.10484754591228278,0.08069509525828301,0.05593086745577397,0.03491174196593651,0.019624880936688967,0.009934776233057956,0.004529233348436208,0.0018595456766296074};

	
	/**
	 * 
	 * 根據i的值來獲取不同的高斯模板
	 * @param i
	 * @return
	 */
	public static double[] gettemplateX_Y(int i){
		switch(i){
		case 0:
			return templateX_Y0;
		case 1:
			return templateX_Y1;
		case 2:
			return templateX_Y2;
		case 3:
			return templateX_Y3;
		case 4:
			return templateX_Y4;
		case 5:
			return templateX_Y5;
		default: return null;
		}
	}
	
	
	
	
	
}
