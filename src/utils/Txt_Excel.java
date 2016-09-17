package utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Vector;

import net.sf.json.JSONObject;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;


public class Txt_Excel {
	public String TITLE;
	public double lng = 0.0;
	public double lat = 0.0;
	public String DIRECTION = null;
	public static Object INVALID_DEGREE;
	public String TIME;
	public String PRICE;
	public String HOUSE_TYPE;
	public String COMMUNITY;
	public String ADDRESS;
	public String PARTMENT;
	public String AREA;
	public String FLOOR;
	public String DECORATION;
	public String TRAFFIC;
	public String URL;
	public String date;
	public String COORDINATE;
	public String DEPOSIT;
	public String RENT_TYPE;
	public String PROPERTY_TYPE;
	public String LOCATION;
	public String SOURCE;
	public String DOWN_PAYMENT;
	public String UNIT_PRICE;
	public String ORIENTATION;
	public String FITMENT;
	public String GENERAL_SITUATION;
	public String infloor;
	public String totalfloor;
	public int roughcast;
	public int Simple_decoration;
	public int Fine_decoration;
	public int Luxury_decoration;
	public int Moderate_decoration;
	public int south;
	public int north;
	public int east;
	public int west;
	public int south_north;
	public int east_west;
	public int west_south;
	public int east_north;
	public int west_north;
	public int east_south;
	public String DEVELOPER;
	public String PROPERTY;
	public String PROPERTY_FEE;
	public String TOTAL_AREA;
	public String BUILT_YEAR;
	public String HOUSEHOLDS;
	public String VOLUME_RATE;
	public String PARK;
	public String GREEN_RATE;
	public Double LNG;
	public Double LAT;
	public String Name;
	public String PostCoor;
	public String PostCoorLN;
	public String PostCoorLA;
	public String PostReg;
	public String Code;
	public String CodeAddr;
	public String CodeCoor;
	public String CodeCoorLN;
	public String CodeCoorLA;
	public String CodeReg;
	public String CtfId;
	public String Home;
	public String Gender;
	public String Birth;
	public String PostAddr;
	public String Mobile;
	public static String Folder="D:\\test\\woaiwojia_rentout_2016_0401.txt";
	public static void main(String argv[]) throws Exception{
	    System.out.println("测试开始!");
	    writeExcel();
	    System.out.println("测试结束!");
	   
 }
   public static void writeExcel() throws IOException{  
        //创建一个Excel(or new XSSFWorkbook())  
        Workbook wb = new HSSFWorkbook();  
        //创建表格  
        Sheet sheet = wb.createSheet("人口样本");  
        //创建行  
        Row row = sheet.createRow(0);  
        //设置行高  
        row.setHeightInPoints(30); 
        Cell cell = row.createCell(0);
        for(int i=0;i<=13;i++)
        {
        	switch(i){
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
        		row.createCell(4).setCellValue("volume_rate");
        		break;
        	case 5:
        		row.createCell(5).setCellValue("green_rate");
        		break;
        	case 6:
        		row.createCell(6).setCellValue("rooms");
        		break;
        	case 7:
        		row.createCell(7).setCellValue("halls");
        		break;
        	case 8:
        		row.createCell(8).setCellValue("storeys");
        		break;
        	case 9:
        		row.createCell(9).setCellValue("direction");
        		break;
        	}
        }
    //标题设置完毕,下面开始填充数据
        Vector<String> rds = FileTool.Load(Folder,"UTF-8");
        for(int i=0;i<rds.size();i++)
        {
        	String element=rds.elementAt(i);
			JSONObject obj=JSONObject.fromObject(element);
        	row = sheet.createRow(i+1); 
        	for(int k=0;k<=13;k++)
        	{
        		switch(k){
        		case 0:
            		row.createCell(0).setCellValue(obj.getDouble(""));
            		break;
            	case 1:
            		row.createCell(1).setCellValue(obj.getDouble(""));
            		break;
            	case 2:
            		row.createCell(2).setCellValue(obj.getDouble(""));
            		break;
            	case 3:
            		row.createCell(3).setCellValue(obj.getDouble(""));
            		break;
            	case 4:
            		row.createCell(4).setCellValue(obj.getDouble(""));
            		break;
            	}
        		
        	}
        	
            
        }   
        FileOutputStream fos = new FileOutputStream(Folder.replace(".txt", "")+"_excel.csv");  
        wb.write(fos);  
        if(null != fos){  
            fos.close();  
        }  
    }


}
