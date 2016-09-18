package utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Vector;

import net.sf.json.JSONException;
import net.sf.json.JSONObject;
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
	public  double volume_rate;
	public  double green_rate;
	public  int rooms;
	public  int halls;
	public  int kitchen;
	public  int bathrooms;
	public  double  floor;
	public  double direction;
	public static String Folder = "D:\\test\\woaiwojia_rentout_2016_0401.txt";

	public static void main(String argv[]) throws Exception {
		System.out.println("测试开始!");
		writeExcel();
		System.out.println("测试结束!");

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

		JSONObject obj;
		String element;
		for (int i = 0; i < rds.size(); i++) {
			element = rds.elementAt(i);
			//System.out.println(element);
			obj = JSONObject.fromObject(element);
			Txt_Excel te=new Txt_Excel(obj);
			row = sheet.createRow(i + 1);
			for (int k = 0; k <= 11; k++) {
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
					case 6:
						row.createCell(6).setCellValue(te.rooms);
						break;
					case 7:
						row.createCell(7).setCellValue(te.halls);
						break;
					case 8:
						row.createCell(8).setCellValue(te.kitchen);
						break;
					case 9:
						row.createCell(9).setCellValue(te.bathrooms);
						break;
					case 10:
						row.createCell(10).setCellValue(te.floor);
						break;
					case 11:
						row.createCell(11).setCellValue(te.direction);
						break;
				}

			}


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
		for (int i = 0; i <= 11; i++) {
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
				case 6:
					row.createCell(6).setCellValue("rooms");
					break;
				case 7:
					row.createCell(7).setCellValue("halls");
					break;
				case 8:
					row.createCell(8).setCellValue("kitchen");
					break;
				case 9:
					row.createCell(9).setCellValue("bathroom");
					break;
				case 10:
					row.createCell(10).setCellValue("floor");
					break;
				case 11:
					row.createCell(11).setCellValue("direction");
					break;
			}
		}
	}

	Txt_Excel(JSONObject obj)throws IOException{
		try{

			price=obj.getDouble("price");
			area=obj.getDouble("area");
			unit_price= Math.log(obj.getDouble("unit_price"));

			if(obj.containsKey("fitment")){
				fitment=setFitment(obj.getString("fitment"));
			}else{
				fitment=0;
			}

			/*if(obj.containsKey("volume_rate")){
				if(!obj.getString("volume_rate").equals("null")){
					volume_rate=obj.getDouble("volume_rate");
				}else {
					volume_rate=0;
				}

			}else{
				volume_rate=0;
			}

			if(obj.containsKey("green_rate")){
				if(!obj.getString("green_rate").equals("null")){
					green_rate=obj.getDouble("green_rate");
				}else {
					green_rate=0;
				}

			}else {
				green_rate=0;
			}*/

			//"layout":{"rooms":"2","halls":"1","kitchen":"","bathrooms":"1"},
			JSONObject layout=obj.getJSONObject("layout");
			if(layout.getString("rooms").length()!=0){
				rooms=layout.getInt("rooms");
			}else {
				rooms=0;
			}
			if(layout.getString("halls").length()!=0){
				halls=layout.getInt("halls");
			}else {
				halls=0;
			}
			if(layout.getString("kitchen").length()!=0){
				kitchen=layout.getInt("kitchen");
			}else {
				kitchen=0;
			}
			if(layout.getString("bathrooms").length()!=0){
				bathrooms=layout.getInt("bathrooms");
			}else {
				bathrooms=0;
			}

			//"storeys":{"flooron":"下部","floors":"21"}
			JSONObject storeys=obj.getJSONObject("storeys");
			String flooron=storeys.getString("flooron");
			int floors=storeys.getInt("floors");
			floor=setFloor(flooron,floors);

			direction=setDirection(obj.getString("direction"));
			if(obj.containsKey("direction")){
				direction=setDirection(obj.getString("direction"));
			}else {
				direction=0;
			}

		}catch (JSONException e){

			System.out.println(e.getMessage());
			//System.out.println(obj);
		}

	}
	public static double setDirection(String direction){
		double value=0;

		switch (direction){
			case "南北": value=2;
				break;
			case "南": value=1.5;
				break;
			case "东南": value=1;
				break;
			case "西南": value=0.5;
				break;
			case "东西": value=0.1;
				break;
			case "东": value=-0.5;
				break;
			case "西": value=-1;
				break;
			case "东北": value=-1.5;
				break;
			case "西北": value=-2;
				break;
			case "北": value=-2.5;
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
		if(floors<=6){
			if(ratio==1){
				value=102;
			}else if(ratio==2){
				value=109;
			}else{
				value=87;
			}
		}else if(floors>6&&floors<=12){
			if(ratio==1){
				value=96;
			}else if(ratio==2){
				value=103;
			}else{
				value=105;
			}
		}else if(floors>12&&floors<18){
			if(ratio==1){
				value=94;
			}else if(ratio==2){
				value=103;
			}else{
				value=105;
			}
		}else if(floors>18){
			if(ratio==1){
				value=96;
			}else if(ratio==2){
				value=104;
			}else{
				value=102;
			}
		}
		return value;
	}
}
