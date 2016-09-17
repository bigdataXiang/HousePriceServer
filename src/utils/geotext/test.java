package utils.geotext;


        import java.io.BufferedWriter;
        import java.io.File;
        import java.io.FileOutputStream;
        import java.io.IOException;
        import java.io.OutputStreamWriter;
        import java.util.Vector;

        import org.apache.poi.hssf.usermodel.HSSFWorkbook;
        import org.apache.poi.ss.usermodel.Cell;
        import org.apache.poi.ss.usermodel.Row;
        import org.apache.poi.ss.usermodel.Sheet;
        import org.apache.poi.ss.usermodel.Workbook;
        import utils.FileTool;

public class test {
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
    public static String Folder="D:/zhouxiang/人口数据/宾馆数据/人口统计/人口样本.txt";
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
                    row.createCell(0).setCellValue("Name");
                    break;
                case 1:
                    row.createCell(1).setCellValue("PostCoorLN");
                    break;
                case 2:
                    row.createCell(2).setCellValue("PostCoorLA");
                    break;
                case 3:
                    row.createCell(3).setCellValue("PostReg");
                    break;
                case 4:
                    row.createCell(4).setCellValue("Code");
                    break;
                case 5:
                    row.createCell(5).setCellValue("CodeCoorLN");
                    break;
                case 6:
                    row.createCell(6).setCellValue("CodeCoorLA");
                    break;
                case 7:
                    row.createCell(7).setCellValue("CodeReg");
                    break;
                case 8:
                    row.createCell(8).setCellValue("CtfId");
                    break;
                case 9:
                    row.createCell(9).setCellValue("Home");
                    break;
                case 10:
                    row.createCell(10).setCellValue("Gender");
                    break;
                case 11:
                    row.createCell(11).setCellValue("Birth");
                    break;
                case 12:
                    row.createCell(12).setCellValue("PostAddr");
                    break;
                case 13:
                    row.createCell(13).setCellValue("Mobile");
                    break;
            }
        }
        //标题设置完毕,下面开始填充数据
        //Vector<String> rds = FileTool.Load(Folder,"UTF-8");
        for(int i=0;i<10;i++)
        {
            /*String element=rds.elementAt(i);
            test poi = new test(element);
            row = sheet.createRow(i+1);*/
            for(int k=0;k<=13;k++)
            {
                switch(k){
                    case 0:
                        row.createCell(0).setCellValue(1);
                        break;
                    case 1:
                        row.createCell(1).setCellValue(1);
                        break;
                    case 2:
                        row.createCell(2).setCellValue(1);
                        break;
                }

            }


        }
        FileOutputStream fos = new FileOutputStream("D:\\test\\"+"_excel.csv");
        wb.write(fos);
        if(null != fos){
            fos.close();
        }
    }
    test(String line) throws IOException
    {
        try{
            if(line.indexOf("<Name>")!=-1)
                Name=getStrByKey(line,"<Name>","</Name>");
            if(line.indexOf("<PostCoor>")!=-1){
                PostCoor=getStrByKey(line,"<PostCoor>","</PostCoor>").replace("&nbsp;", "").replace("&amp;","").replace("nb;","").replace("nbsp","").replace("|","");
                String[] arry=PostCoor.split(";");
                PostCoorLN=arry[0];
                PostCoorLA=arry[1];

            }
            if(line.indexOf("<PostReg>")!=-1)
                PostReg=getStrByKey(line,"<PostReg>","</PostReg>").replace("&nbsp;", "").replace("&amp;","").replace("nb;","").replace("nbsp","").replace("|","").replace(";","");
            if(line.indexOf("<Code>")!=-1)
                Code= getStrByKey(line,"<Code>","</Code>").replace("[面议]","").replace("[押一付三]","").replace("元/月","").replace("&nbsp;", "").replace("&amp;","").replace("nb;","").replace("nbsp","").replace("|","").replace(";","");
            if(line.indexOf("<CodeCoor>")!=-1){
                CodeCoor=getStrByKey(line,"<CodeCoor>","</CodeCoor>").replace("&nbsp;", "").replace("&amp;","").replace("nb;","").replace("nbsp","").replace("|","");
                String[] arry=CodeCoor.split(";");
                CodeCoorLN=arry[0];
                CodeCoorLA=arry[1];
            }
            if(line.indexOf("<CodeReg>")!=-1)
                CodeReg=getStrByKey(line,"<CodeReg>","</CodeReg>").replace("&nbsp;", "").replace("&amp;","").replace("nb;","").replace("nbsp","").replace("|","").replace(";","");
            if(line.indexOf("<CtfId>")!=-1)
                CtfId=getStrByKey(line,"<CtfId>","</CtfId>").replace("(地图)","").replace("&nbsp;", "").replace("&amp;","").replace("nb;","").replace("nbsp","").replace("|","").replace(";","");
            if(line.indexOf("<Home>")!=-1)
                Home=getStrByKey(line,"<Home>","</Home>").replace("(地图)","").replace("&nbsp;", "").replace("&amp;","").replace("nb;","").replace("nbsp","").replace("|","").replace(";","");
            if(line.indexOf("<Gender>")!=-1)
                Gender=getStrByKey(line,"<Gender>","</Gender>").replace("(地图)","").replace("&nbsp;", "").replace("&amp;","").replace("nb;","").replace("nbsp","").replace("|","").replace(";","");
            if(line.indexOf("<Birth>")!=-1)
                Birth=getStrByKey(line,"<Birth>","</Birth>").replace("(地图)","").replace("&nbsp;", "").replace("&amp;","").replace("nb;","").replace("nbsp","").replace("|","").replace(";","");
            if(line.indexOf("<PostAddr>")!=-1)
                PostAddr=getStrByKey(line,"<PostAddr>","</PostAddr>").replace("(小型小区)","").replace("(中型小区)","").replace("(大型小区)","").replace("&nbsp;", "").replace("&amp;","").replace("nb;","").replace("nbsp","").replace("|","").replace(";","");
            if(line.indexOf("<Mobile>")!=-1)
                Mobile=getStrByKey(line,"<Mobile>","</Mobile>").replace("户","").replace("&nbsp;", "").replace("&amp;","").replace("nb;","").replace("nbsp","").replace("|","").replace(";","");
        }catch (NullPointerException e) {

            e.printStackTrace();
            System.out.println(e.getMessage());
            write_append(line,Folder.replace(".txt", "")+"_exception.txt");
        }

    }

    //提取每个标签里的内容
    public static String getStrByKey(String sContent, String sStart, String sEnd) {
        String sOut ="";
        int fromIndex = 0;
        int iBegin = 0;
        int iEnd = 0;
        int iStart=sContent.indexOf("</Version>");
        String temp=" ";
        if (iStart < 0) {
            return null;
        }
        for (int i = 0; i < iStart; i++) {
            // 找出某位置，并找出该位置后的最近的一个匹配
            iBegin = sContent.indexOf(sStart, fromIndex);
            if (iBegin >= 0)
            {
                iEnd = sContent.indexOf(sEnd, iBegin + sStart.length());
                if (iEnd <= iBegin)
                {
                    return null;
                }
            }
            else
            {
                return sOut;
            }
            if (iEnd > 0&&iEnd!=iBegin + sStart.length())
            {
                sOut += sContent.substring(iBegin + sStart.length(), iEnd);
            }
            else
                return temp;
            if (iEnd > 0)
            {
                fromIndex = iEnd + sEnd.length();
            }
        }
        return sOut;
    }
    public static void write_append(String line,String pathname)  throws IOException
    {
        try
        {

            File writefile=new File(pathname);
            if(!writefile.exists())
            {
                writefile.createNewFile();
            }
            OutputStreamWriter write = new OutputStreamWriter(new FileOutputStream(writefile,true),"UTF-8");
            BufferedWriter writer = new BufferedWriter(write);
            writer.write(line);
            writer.write("\r\n");
            writer.close();
        }catch(Exception e) {
            e.printStackTrace();

        }
    }


}
