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
	public double ln_price;
	public double ln_unitprice;
	public static String Folder = "D:\\test\\woaiwojia_rentout_2016_0401.txt";

	public static void main(String argv[]) throws Exception {
		/*System.out.println("测试开始!");
		writeExcel();
		System.out.println("测试结束!");*/

		double e=Math.E;
		double result =test_Regression_Results(58,4,2,1,1,17.5,1);
		double price=Math.pow(e,result);
		System.out.println(price);
		result =test_Regression_Results(78,3,3,1,1,4,10);
		price=Math.pow(e,result);
		System.out.println(price);
		result =test_Regression_Results(60,4,2,1,1,1,6);
		price=Math.pow(e,result);
		System.out.println(price);

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
						row.createCell(8).setCellValue(te.floor);
						break;
					case 9:
						row.createCell(9).setCellValue(te.direction);
						break;
					case 10:
						row.createCell(10).setCellValue(te.ln_price);
						break;
					case 11:
						row.createCell(11).setCellValue(te.ln_unitprice);
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
					row.createCell(8).setCellValue("floor");
					break;
				case 9:
					row.createCell(9).setCellValue("direction");
					break;
				case 10:
					row.createCell(10).setCellValue("ln_price");
					break;
				case 11:
					row.createCell(11).setCellValue("ln_unitprice");
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

}
