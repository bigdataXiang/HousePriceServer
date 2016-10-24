package utils;

import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;


public class Txt_Excel {
	public  double price;
	public  double area;
	public  double unit_price;
	public  int fitment;
	public  String rooms;
	public  String halls;
	public  String kitchen;
	public  String bathrooms;
	public  String   flooron;
	public  String  floors;
	public  double direction;
	public double ln_price;
	public double ln_unitprice;
	public String built_year;
	public String totalarea;
	public String households;
	public String volume_rate;
	public String park;
	public String green_rate;
	public static String Folder = "D:\\test\\anjuke_rentout_2015_1106.txt";

	public static void main(String argv[]) throws Exception {
		System.out.println("测试开始!");
		//writeExcel();
		showExcel();
		System.out.println("测试结束!");

		/*double e=Math.E;
		double result =test_Regression_Results(58,4,2,1,1,17.5,1);
		double price=Math.pow(e,result);
		System.out.println(price);
		result =test_Regression_Results(78,3,3,1,1,4,10);
		price=Math.pow(e,result);
		System.out.println(price);
		result =test_Regression_Results(60,4,2,1,1,1,6);
		price=Math.pow(e,result);
		System.out.println(price);*/

	}

	public static void writeExcel() throws IOException {

		//创建一个Excel(or new XSSFWorkbook())
		Workbook wb = new HSSFWorkbook();
		//创建表格
		Sheet sheet = wb.createSheet("出租房价");
		//创建行
		Row row = sheet.createRow(0);
		//设置行高
		row.setHeightInPoints(30);
		designExcel(sheet);

		Vector<String> rds = FileTool.Load(Folder, "UTF-8");

		JSONObject obj=new JSONObject();
		String element;
		int count=0;
		try{

			for (int i = 0; i < 70000; i++) {
				element = rds.elementAt(i);
				//System.out.println(element);
				obj = JSONObject.fromObject(element);
				Txt_Excel te=new Txt_Excel(obj);
				//System.out.println(te.built_year);
				if(te.built_year.length()!=0&&te.green_rate.length()!=0&&te.volume_rate.length()!=0&&te.flooron.length()!=0&&te.floors.length()!=0){
					count++;
					boolean year=Tool.judgeContainsStr(te.built_year);
					if(te.bathrooms.indexOf("室")==-1&&te.green_rate.length()!=0&&!year){
						row = sheet.createRow(count);
						int y=2016-Integer.parseInt(te.built_year);
						for (int k = 0; k <= 18; k++) {
							switch (k) {
								case 0:
									row.createCell(0).setCellValue(te.price);
									break;
								case 1:
									row.createCell(1).setCellValue(te.area);
									break;
								case 2:
									row.createCell(2).setCellValue(te.unit_price);
									break;
								case 3:
									row.createCell(3).setCellValue(te.fitment);
									break;
								case 4:
									row.createCell(4).setCellValue(te.rooms);
									break;
								case 5:
									row.createCell(5).setCellValue(te.halls);
									break;
								case 6:
									row.createCell(6).setCellValue(te.kitchen);
									break;
								case 7:
									row.createCell(7).setCellValue(te.bathrooms);
									break;
								case 8:
									row.createCell(8).setCellValue(te.flooron);
									break;
								case 9:
									row.createCell(9).setCellValue(te.floors);
									break;
								case 10:
									row.createCell(10).setCellValue(te.direction);
									break;
								case 11:
									row.createCell(11).setCellValue(te.ln_price);
									break;
								case 12:
									row.createCell(12).setCellValue(te.ln_unitprice);
									break;
								case 13:
									row.createCell(13).setCellValue(y);
									break;
								case 14:
									row.createCell(14).setCellValue(te.totalarea);
									break;
								case 15:
									row.createCell(15).setCellValue(te.households);
									break;
								case 16:
									row.createCell(16).setCellValue(te.volume_rate);
									break;
								case 17:
									row.createCell(17).setCellValue(te.park);
									break;
								case 18:
									row.createCell(18).setCellValue(te.green_rate);
									break;
							}

						}
					}

				}
			}

		}catch (NullPointerException e){
			System.out.println(obj);
		}

		FileOutputStream fos = new FileOutputStream(Folder.replace(".txt", "") + "_excel.csv");
		wb.write(fos);
		if (null != fos) {
			fos.close();
		}
	}

	public static void designExcel(Sheet sheet) {
		Row row = sheet.createRow(0);
		//设置行高
		row.setHeightInPoints(30);
		Cell cell = row.createCell(0);
		for (int i = 0; i <= 18; i++) {
			switch (i) {
				case 0:
					row.createCell(0).setCellValue("price");
					break;
				case 1:
					row.createCell(1).setCellValue("area");
					break;
				case 2:
					row.createCell(2).setCellValue("unit_price");
					break;
				case 3:
					row.createCell(3).setCellValue("fitment");
					break;
				case 4:
					row.createCell(4).setCellValue("rooms");
					break;
				case 5:
					row.createCell(5).setCellValue("halls");
					break;
				case 6:
					row.createCell(6).setCellValue("kitchen");
					break;
				case 7:
					row.createCell(7).setCellValue("bathroom");
					break;
				case 8:
					row.createCell(8).setCellValue("flooron");
					break;
				case 9:
					row.createCell(9).setCellValue("floors");
					break;
				case 10:
					row.createCell(10).setCellValue("direction");
					break;
				case 11:
					row.createCell(11).setCellValue("ln_price");
					break;
				case 12:
					row.createCell(12).setCellValue("ln_unitprice");
					break;
				case 13:
					row.createCell(13).setCellValue("built_year");
					break;
				case 14:
					row.createCell(14).setCellValue("totalarea");
					break;
				case 15:
					row.createCell(15).setCellValue("households");
					break;
				case 16:
					row.createCell(16).setCellValue("volume_rate");
					break;
				case 17:
					row.createCell(17).setCellValue("park");
					break;
				case 18:
					row.createCell(18).setCellValue("green_rate");
					break;
			}
		}
	}

	Txt_Excel(JSONObject obj)throws IOException{
		try{

			price=obj.getDouble("price");
			ln_price=Math.log(obj.getDouble("price"));
			area=obj.getDouble("area");
			unit_price= obj.getDouble("unit_price");
			ln_unitprice=Math.log(obj.getDouble("unit_price"));
			if(obj.containsKey("built_year")){
				built_year=obj.getString("built_year");
				if(built_year.indexOf("-")!=-1){
					built_year=built_year.substring(0,built_year.indexOf("-"));
				}
			}else {
				built_year="";
			}
			if(obj.containsKey("direction")){
				direction=setDirection(obj.getString("direction"));
			}else {
				direction=0;
			}
			//"storeys":{"flooron":"下部","floors":"21"}
			if(obj.containsKey("storeys")){
				JSONObject storeys=obj.getJSONObject("storeys");
				if(storeys.containsKey("flooron")){
					flooron=storeys.getString("flooron");
				}else {
					flooron="";
				}
				if(storeys.containsKey("floors")){
					floors=storeys.getString("floors");
				}else {
					floors="";
				}

			}

			if(obj.containsKey("fitment")){
				fitment=setFitment(obj.getString("fitment"));
			}else{
				fitment=0;
			}

			if(obj.containsKey("totalarea")){
				totalarea=obj.getString("totalarea").replace("(小型小区)","").replace("(中型小区)","").replace("(大型小区)","");
				if(totalarea.indexOf("万")!=-1){
					totalarea=totalarea.replace("万","");
					int ta=(int)Double.parseDouble(totalarea)*10000;
					totalarea=ta+"";
				}
			}else {
				totalarea="";
			}

			if(obj.containsKey("households")){
				households=obj.getString("households").replace("户","");
			}else {
				households="";
			}

			if(obj.containsKey("volume_rate")){
				if(!obj.getString("volume_rate").equals("null")){
					volume_rate=obj.getString("volume_rate");
				}else {
					volume_rate="";
				}

			}else{
				volume_rate="";
			}

			if(obj.containsKey("park")){
				park=obj.getString("park");
			}else {
				park="";
			}

			if(obj.containsKey("green_rate")){
				if(!obj.getString("green_rate").equals("null")){
					green_rate=obj.getString("green_rate").replace("%","");
				}else {
					green_rate="";
				}

			}else {
				green_rate="";
			}

			//"layout":{"rooms":"2","halls":"1","kitchen":"","bathrooms":"1"},
			JSONObject layout=obj.getJSONObject("layout");
			if(layout.getString("rooms").length()!=0){
				rooms=layout.getString("rooms");
			}else {
				rooms="";
			}
			if(layout.getString("halls").length()!=0){
				halls=layout.getString("halls");
			}else {
				halls="";
			}
			if(layout.getString("kitchen").length()!=0){
				kitchen=layout.getString("kitchen");
			}else {
				kitchen="";
			}
			if(layout.getString("bathrooms").length()!=0){
				bathrooms=layout.getString("bathrooms");
			}else {
				bathrooms="";
			}


		}catch (JSONException e){

			System.out.println(e.getMessage());
			//System.out.println(obj);
		}

	}
	public static double setDirection(String direction){
		double value=0;

		switch (direction){
			case "南北": value=10;
				break;
			case "南": value=9;
				break;
			case "东南": value=8;
				break;
			case "西南": value=7;
				break;
			case "东西": value=6;
				break;
			case "东": value=5;
				break;
			case "西": value=4;
				break;
			case "东北": value=-3;
				break;
			case "西北": value=2;
				break;
			case "北": value=1;
				break;
		}
		return value;
	}
	public static int setFitment(String fitment){
		int value=0;

		switch (fitment){
			case "豪华": value=5;
				break;
			case "精装": value=4;
				break;
			case "简装": value=3;
				break;
			case "普装": value=2;
				break;
			case "毛坯": value=1;
				break;
			case "精装修": value=4;
				break;
			case "普通装修": value=2;
				break;
			case "豪华装修": value=5;
				break;
			case "简单装修": value=3;
				break;
	}
		return value;
	}
	public static double setFloor(String flooron,int floors){
		double value=0;
		double ratio=0;
		switch (flooron){
			case "上部":ratio=1;
				break;
			case "中部":ratio=2;
				break;
			case "下部":ratio=3;
				break;
		}

		//6层：3-4；12层：8-10；18层：13-15；33层：26-28
		if(ratio==1){
			value=((1.0/3.0)*floors)/2;
		}else if(ratio==2){
			value=(2.0/3.0)*floors;
		}else if(ratio==3){
			value=((3.0/3.0)*floors+(2.0/3.0)*floors)/2;
		}


		return value;
	}
	public static double test_Regression_Results(double area,double fitment,double rooms,double halls,double bathroom,double floor,double direction){

		//double result=4.223+(-0.005)*area+0.011*fitment+(-0.01)*rooms+0.247*halls+0.221*bathroom+0.007*floor+(-0.029)*direction;
		double result=7.346+(6.27240697056394E-5)*area+0.05*fitment+(0.041)*rooms+0.334*halls+0.320*bathroom+0.02*floor+(0.001)*direction;

		//System.out.println(result);
		return result;
	}

	public static  void showExcel() throws Exception {
		String path="D:\\能源所\\汇总（22个）\\数据完全（19个）\\福建省\\";
		HSSFWorkbook workbook=new HSSFWorkbook(new FileInputStream(new File(path+"福建省数据.xls")));
		HSSFSheet sheet=null;
		try{
			for (int i = 0; i < workbook.getNumberOfSheets(); i++) {//获取每个Sheet表
				sheet=workbook.getSheetAt(i);
				String name=sheet.getSheetName();
				if(name.indexOf("禁止开发")==-1){
					for (int j = 0; j < sheet.getPhysicalNumberOfRows(); j++) {//获取每行
						HSSFRow row=sheet.getRow(j);
						String poi="";
						for (int k = 0; k < row.getPhysicalNumberOfCells(); k++) {//获取每个单元格
							poi+=row.getCell(k)+",";
						}
						FileTool.Dump(poi,path+name+".txt","utf-8");
						System.out.println(poi);
					}
					System.out.println("---Sheet表"+i+":"+name+"处理完毕---");
				}
			}
		}catch (NullPointerException e){
			System.out.println(e.getMessage());
		}
	}

}
