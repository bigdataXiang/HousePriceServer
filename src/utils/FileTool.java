package utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;


public class FileTool {

	 public static void copyFile(File sourceFile, File targetFile) throws IOException {
		 BufferedInputStream inBuff = null;
		 BufferedOutputStream outBuff = null;
		 try {
			 // 新建文件输入流并对它进行缓冲
			 inBuff = new BufferedInputStream(new FileInputStream(sourceFile));

			 // 新建文件输出流并对它进行缓冲
			 outBuff = new BufferedOutputStream(new FileOutputStream(targetFile));

			 // 缓冲数组
			 byte[] b = new byte[1024 * 5];
			 int len;
			 while ((len = inBuff.read(b)) != -1) {
				 outBuff.write(b, 0, len);
			 }
			 // 刷新此缓冲的输出流
			 outBuff.flush();
		 } finally {
			 // 关闭流
			 if (inBuff != null)
				 inBuff.close();
			 if (outBuff != null)
				 outBuff.close();
		 }
	 }

	/**
	 * 清空目录下所有的文件, 除子目录外
	 */
	public static void DeleteFiles(String folderName) {
		
		File folder = new File(folderName);
		
		String files [] = folder.list();
		
		for (int n = 0; n < files.length; n ++)
		{
			File subFolder = new File(folder + File.separator + files[n]);
			if (!subFolder.isDirectory())
			{
				File oldFile = new File(folder + File.separator + files[n]);
				oldFile.delete();
			}
		}
	}
	/*public static void Dump(JSONObject strs, String fileName, String format)
	{
		if (strs == null || ((List<String>) strs).size() == 0)
			return;
		
		OutputStreamWriter newFileWrite = null;
		
		try {
			File file= new File(fileName);	
			
			newFileWrite = new OutputStreamWriter(new FileOutputStream(file, true), format);
			Iterator<String> itr = ((List<String>) strs).iterator();
	        while (itr.hasNext())
	        {
	        	String sr = itr.next();
	        	try {
					newFileWrite.write(sr + "\r\n");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}	
	        }
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch(ClassCastException e){
			e.printStackTrace();
		}
		
		try {
			if (newFileWrite != null)
				newFileWrite.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}*/
	
	public static void Dump(Vector<String> strs, String fileName, String format)
	{
		if (strs == null || strs.size() == 0)
			return;
		
		OutputStreamWriter newFileWrite = null;
		
		try {
			File file= new File(fileName);	
			
			newFileWrite = new OutputStreamWriter(new FileOutputStream(file, true), format);
			Iterator<String> itr = strs.iterator();
	        while (itr.hasNext())
	        {
	        	String sr = itr.next();
	        	try {
					newFileWrite.write(sr + "\r\n");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}	
	        }
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			if (newFileWrite != null)
				newFileWrite.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	public static void Dump(Vector<String> strs, String fileName)
	{
		if (strs == null || strs.size() == 0)
			return;
		
		OutputStreamWriter newFileWrite = null;
		
		try {
			File file= new File(fileName);	
			
			newFileWrite = new OutputStreamWriter(new FileOutputStream(file, true), "utf-8");
			Iterator<String> itr = strs.iterator();
	        while (itr.hasNext())
	        {
	        	String sr = itr.next();
	        	try {
					newFileWrite.write(sr + "\r\n");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}	
	        }
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			if (newFileWrite != null)
				newFileWrite.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}

	/* 与原数据合并, 消除重复记录 */
	public static void DumpAndMerge(Set<String> strs, String fileName)
	{
		if (strs == null || strs.size() == 0)
			return;
		
		Vector<String> rds = Load(fileName, "UTF-8");
		if (rds != null)
		{
			Set<String> newSet = new TreeSet<String>();
			newSet.addAll(strs);
			File file = new File(fileName);
			file.delete();
			newSet.addAll(rds);
			Dump(newSet, fileName);
		}
		else
			Dump(strs, fileName);
	}

	public static void DumpAndMerge(String str, String fileName)
	{
		if (str == null)
			return;
		
		Vector<String> rds = Load(fileName, "UTF-8");
		if (rds != null)
		{
			Set<String> newSet = new TreeSet<String>();
			newSet.add(str);
			File file = new File(fileName);
			file.delete();
			newSet.addAll(rds);
			Dump(newSet, fileName);
		}
		else
		{
			Vector<String> strs = new Vector<String>();
			strs.add(str);
			Dump(strs, fileName);
		}
	}

	public static void Dump(Set<String> strs, String fileName)
	{
		if (strs == null || strs.size() == 0)
			return;
		
		OutputStreamWriter newFileWrite = null;
		
		try {
			File file= new File(fileName);	
			
			newFileWrite = new OutputStreamWriter(new FileOutputStream(file, true), "utf-8");
			Iterator<String> itr = strs.iterator();
	        while (itr.hasNext())
	        {
	        	String sr = itr.next();
	        	try {
					newFileWrite.write(sr + "\r\n");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}	
	        }
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			if (newFileWrite != null)
				newFileWrite.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	public static void Dump(String str, String fileName, String formate)
	{
		if (str == null)
			return;
		
		OutputStreamWriter newFileWrite = null;
		
		try {
			File file= new File(fileName);	
			
			newFileWrite = new OutputStreamWriter(new FileOutputStream(file, true), formate);
			try {
				newFileWrite.write(str + "\r\n");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			if (newFileWrite != null)
				newFileWrite.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	/**
	 *  找出filea文件相对于fileb的增量 */
	public static Vector<String> CompareFile(String filea, String fileb)
	{
		Vector<String> rdsa = Load(filea, "UTF-8");
		Vector<String> rdsb = Load(fileb, "UTF-8");
		
		Set<String> seta = new TreeSet<String>();
		if (rdsa != null)
			seta.addAll(rdsa);
		
		Set<String> setb = new TreeSet<String>();
		if (rdsb != null)
			setb.addAll(rdsb);
		
		Iterator<String> itr = seta.iterator();
		Vector<String> increasement = new Vector<String>();
		while (itr.hasNext())
		{
			String key = itr.next();
			
			if (setb.contains(key))
			{}
			else
			{
				increasement.add(key);
			}
		}
		return increasement;
		
	}
	public static void DumpComparion(String filea, String fileb, String target)
	{
		Vector<String> rdsa = Load(filea, "UTF-8");
		Vector<String> rdsb = Load(fileb, "UTF-8");
		
		Set<String> seta = new TreeSet<String>();
		if (rdsa != null)
			seta.addAll(rdsa);
		
		Set<String> setb = new TreeSet<String>();
		if (rdsb != null)
			setb.addAll(rdsb);
		
		Iterator<String> itr = seta.iterator();
		Vector<String> increasement = new Vector<String>();
		while (itr.hasNext())
		{
			String key = itr.next();
			
			if (setb.contains(key))
			{}
			else
			{
				increasement.add(key);
			}
		}
		
		Dump(increasement, target);
		
	}
	
	public static void Mark(String str, String fileName)
	{
		OutputStreamWriter newFileWrite = null;
		
		try {
			File file= new File(fileName);	
			
			newFileWrite = new OutputStreamWriter(new FileOutputStream(file), "utf-8");
			try {
				newFileWrite.write(str + "\r\n");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			if (newFileWrite != null)
				newFileWrite.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	public static Vector<String> Load(String fileName, String fileFormat)
	{
		File file = new File(fileName);
		if (!(fileName.endsWith(".json") ||fileName.endsWith(".txt") || fileName.endsWith(".TXT") ||  fileName.endsWith(".csv")  ||  fileName.endsWith(".log")||  fileName.endsWith(".kw") || fileName.endsWith(".XML")||  fileName.endsWith(".xml")))
			return null;
		
		// System.out.print(fileName + "\n");
		if (!file.exists() || file.isDirectory())
			return null;
		
		Vector<String> strs = new Vector<String>();
		
		InputStreamReader read = null;
		
		try {
			if (fileFormat == null)
				fileFormat = new String("gbk");
			
			read = new InputStreamReader(new FileInputStream(file), fileFormat);//"GBK"
			BufferedReader bufferedReader = new BufferedReader(read);
			
			String lineTXT = null;

			try {
				while ((lineTXT = bufferedReader.readLine()) != null) {
					if (lineTXT.length() > 0)
						strs.add(new String(lineTXT));
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (read != null)
				try {
					read.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		return strs;
	}
	
	public static Vector<String> Detect(String fileName, String fileFormat)
	{
		File file = new File(fileName);
		if (!(fileName.endsWith(".txt") || fileName.endsWith(".TXT") ||  fileName.endsWith(".csv")  ||  fileName.endsWith(".log")||  fileName.endsWith(".kw") || fileName.endsWith(".XML")||  fileName.endsWith(".xml")))
			return null;
		
		System.out.print(fileName + "\n");
		if (!file.exists() || file.isDirectory())
			return null;
		
		Vector<String> strs = new Vector<String>();
		
		InputStreamReader read = null;
		
		try {
			if (fileFormat == null)
				fileFormat = new String("gbk");
			
			read = new InputStreamReader(new FileInputStream(file), fileFormat);//"GBK"
			BufferedReader bufferedReader = new BufferedReader(read);
			
			String lineTXT = null;

			try {
				while ((lineTXT = bufferedReader.readLine()) != null) {
					if (lineTXT.length() > 0)
					{
						/*if (lineTXT.indexOf("|小学")  != -1 
							|| lineTXT.indexOf("|中学")  != -1 
							|| lineTXT.indexOf("|中心校")  != -1
							|| lineTXT.indexOf("|中心小学")  != -1
							)
							strs.add(new String(lineTXT));
						*/
						if (lineTXT.indexOf("|路")  != -1 
							|| lineTXT.indexOf("|村")  != -1
							|| lineTXT.indexOf("|乡")  != -1
							|| lineTXT.indexOf("|镇")  != -1
							|| lineTXT.indexOf("|街")  != -1)
						 strs.add(new String(lineTXT));
							
					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (read != null)
				try {
					read.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		return strs;
	}

	/*public static void main(String[] args)
	{
		*//*String oldFile = new String("D:\\tmp\\大众点评\\重庆\\shops_fetched_old.txt");
		String newFile = new String("D:\\tmp\\大众点评\\重庆\\shops_fetched.txt");
		DumpComparion(newFile, oldFile, "D:\\tmp\\大众点评\\重庆\\shops_fetched_increasement.txt" );		
		*//* // new String(result.getBytes("ISO-8859-1"),"UTF-8");
		String xml = HTMLTool.fetchURL("http://119.60.12.114:8080/ClientBin/Env-Publish-Province-RiaService-ProvincePublishDomainService.svc/binary/GetAQIDataByCityName?cityName=%e9%93%b6%e5%b7%9d", "ISO-8859-1", "get");
		
		Dump(Detect("D:\\tmp\\广东_分词结果.txt", "gb18030"), "D:\\tmp\\广东道路.txt");
	}*/

	
}
