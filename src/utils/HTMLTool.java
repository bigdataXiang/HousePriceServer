package utils;

import info.monitorenter.cpdetector.io.*;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;

public class HTMLTool {
	
	/*public static String autoDetectCharset(String url) {
		URL source = null;
		try { 
			source = new URL(url);
		} catch (MalformedURLException e) {
			e.printStackTrace();	
		} 
		
		CodepageDetectorProxy detector = CodepageDetectorProxy.getInstance();
		detector.add(new ParsingDetector(false));
		//detector.add(JChardetFacade.getInstance());
		detector.add(ASCIIDetector.getInstance());
		detector.add(UnicodeDetector.getInstance());
		
		Charset charset = null;
		try { 
			charset = detector.detectCodepage(source); 
		} catch (IOException e) {
			e.printStackTrace();	
		} 

		if (charset == null) { 
		    charset = Charset.defaultCharset();
		} 
		return charset.name(); 
	} */
	private static String fetchUrlHelper(String url, String charset, String method) throws IOException
	{
		/* StringBuffer的缓冲区大小 */
		int TRANSFER_SIZE = 4096; 

		/* 当前平台的行分隔符 */ 
		String lineSep = System.getProperty("line.separator");

		URL source = null;
		
		source = new URL(url);
		
		HttpURLConnection connection;
		connection = (HttpURLConnection) source.openConnection();
		connection.setRequestProperty("User-Agent", "Mozilla/4.7 (compatible; MSIE 5.0; Windows NT; DigExt)");
		// connection.setRequestProperty("User-Agent", "Mozilla/4.7");
			
		connection.setConnectTimeout(20000);  
		connection.setReadTimeout(300000);
            
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
            
        /* get 方式 */
        if (method.compareToIgnoreCase("get") == 0)
        {
        	connection.setDoOutput(false); // 设置不执行输出
            connection.setRequestMethod("GET");    			
        }
        else
        {
        	connection.setDoOutput(true); // 设置不执行输出
            connection.setRequestMethod("POST");
       	}
        connection.setDoInput(true); // 设置执行输入
        				
		connection.setUseCaches(false); // 设置不使用缓存
		connection.connect(); // 打开到此 URL 引用的资源的通信链接
		// BufferedInputStream bis = new BufferedInputStream(connection.getInputStream()); // 获取输入流
			
		BufferedReader reader = null;
		StringBuffer temp = new StringBuffer(TRANSFER_SIZE);
		InputStream is = connection.getInputStream();
		if (is != null)
		{
			// reader = new BufferedReader(new InputStreamReader(new GZIPInputStream(connection.getInputStream()), charset));

			reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), charset));
			String line = new String();

			while ((line = reader.readLine()) != null) { 
				temp.append(line); 
				temp.append(lineSep); 
			} 
					
			
		} 
		
		connection.disconnect(); 
		if (reader != null)
			reader.close(); 
			
		return temp.toString(); 
		
	}
	public static String fetchURL(String url, String charset, String method) {
		int cnt = 0; 
		for (; cnt < 10; cnt ++)
		{
			try {
				String rs = fetchUrlHelper(url, charset, method);
				
				return rs;
			}
			catch (UnsupportedEncodingException e) {
				System.out.println("网页访问错误:" + url);
			} 
			catch ( IOException ie)
			{
				System.out.println("网页访问错误:" + url + ": cause " + ie.getMessage());
			}
			
			try {
				Thread.sleep(800 * ((int) (Math
					.max(1, Math.random() * 3))));
			} catch (final InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		System.out.println("网页访问错误:" + url);
		return null;
	}
		
}
