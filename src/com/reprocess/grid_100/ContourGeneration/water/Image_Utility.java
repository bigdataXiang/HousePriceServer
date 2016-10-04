package com.reprocess.grid_100.ContourGeneration.water;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.GregorianCalendar;

import javax.imageio.ImageIO;

/**
 * 数字图像处理辅助类
 * @author Administrator
 *
 */
public class Image_Utility {
	
/*	///拉普拉斯算子
	private static int[][] lablas={
		{0,-1,0},
		{-1,5,-1,},
	
		
		{0,-1,0}};
*/

	///soble算子
	private static int [][]sobleX={{-1,0,1},
							{-2,0,2},
							{-1,0,1}};
	
	private static int [][]sobleY={{1,2,1},
							{0,0,0},
							{-1,-2,-1}};
	
	/**
	 * 图像soble算子梯度化轮廓灰度图提取,不进行二值化处理
	 * @param sourceImage
	 * @param threshold
	 * @return
	 */
	public static BufferedImage sobleTran(BufferedImage sourceImage,int threshold){
		int width=sourceImage.getWidth();
		int height=sourceImage.getHeight();
		BufferedImage targetImage=new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		
		for(int j=0;j<height;j++){
			for(int i=0;i<width;i++){
				
				int rgb=0;				
				if(i>0&&j>0&&j<height-1&i<width-1){
					
					int grayRGB0=sourceImage.getRGB(i-1, j-1)>>16;
					int grayRGB1=sourceImage.getRGB(i-1, j)>>16;
					int grayRGB2=sourceImage.getRGB(i-1, j+1)>>16; 
					int grayRGB3=sourceImage.getRGB(i, j-1)>>16;
					int grayRGB4=sourceImage.getRGB(i, j)>>16;
					int grayRGB5=sourceImage.getRGB(i, j+1)>>16; 
					int grayRGB6=sourceImage.getRGB(i+1, j-1)>>16;
					int grayRGB7=sourceImage.getRGB(i+1, j)>>16;
					int grayRGB8=sourceImage.getRGB(i+1, j+1)>>16; 
					
				//int result=(int) Math.sqrt((grayRGB0-grayRGB1)*(grayRGB0-grayRGB1)+(grayRGB0-grayRGB1)*(grayRGB0-grayRGB2));///梯度处理
					//int result=Math.abs(grayRGB5-grayRGB4)+Math.abs(grayRGB7-grayRGB4);///梯度处理
				
					///soble算子获取梯度
					int result=0;
					int dx=sobleX[0][0]*grayRGB0+sobleX[0][1]*grayRGB1+sobleX[0][2]*grayRGB2
							+sobleX[1][0]*grayRGB3+sobleX[1][1]*grayRGB4+sobleX[1][2]*grayRGB5
							+sobleX[2][0]*grayRGB6+sobleX[2][1]*grayRGB7+sobleX[2][2]*grayRGB8;
					int dy=sobleY[0][0]*grayRGB0+sobleY[0][1]*grayRGB1+sobleY[0][2]*grayRGB2
							+sobleY[1][0]*grayRGB3+sobleY[1][1]*grayRGB4+sobleY[1][2]*grayRGB5
							+sobleY[2][0]*grayRGB6+sobleY[2][1]*grayRGB7+sobleY[2][2]*grayRGB8;
					result=(int) Math.sqrt(dx*dx+dy*dy);
			/*		result=dx>dy?dx:dy;
					result=(int) Math.sqrt(result)+100;*/
					if(result<=threshold){
						///此处阈值设为??实验效果最好
						rgb=0;
						//System.out.print(0);
					}else{
						int grayRGB=result;
						rgb=(grayRGB<<16)|(grayRGB<<8)|grayRGB;
//						int grayRGB=255;
//						rgb=(grayRGB<<16)|(grayRGB<<8)|grayRGB;
						
					}
					
				}else{
					rgb=sourceImage.getRGB(i, j);
				}
				targetImage.setRGB(i, j, rgb);
				
			}
		}
		
		
		return targetImage;
		
	}
	/**
	 * 数组转为灰度图像
	 * @param sourceArray
	 * @return
	 */
	public static BufferedImage doubleArrayToGreyImage(double[][] sourceArray){
		int width=sourceArray[0].length;
		int height=sourceArray.length;
		BufferedImage targetImage=new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		
		for(int j=0;j<height;j++){
			for(int i=0;i<width;i++){
				int greyRGB=(int) sourceArray[j][i];
				int rgb=(greyRGB<<16)|(greyRGB<<8)|greyRGB;
				
				targetImage.setRGB(i, j, rgb);
			}
		}	
		
		return targetImage;
	}
	/**
	 * 灰度图像提取数组
	 * @param image
	 * @return int[][]数组
	 */
	public static double[][] imageToDoubleArray(BufferedImage image){
		
		int width=image.getWidth();
		int height=image.getHeight();
		
		double[][] result=new double[height][width];
		for(int j=0;j<height;j++){
			for(int i=0;i<width;i++){
				int rgb=image.getRGB(i, j);
				int r=(rgb>>16)&0xFF;
				int g=(rgb>>8)&0xFF;
				int b=rgb&0xFF;
				
				int grey=(int) (0.3*r+0.59*g+0.11*b);
//				System.out.println(grey);
				result[j][i]=grey;
				
			}
		}
		 return result ;
	}
	
	/**
	 * * 将图像进行高斯模糊：先利用模糊函数计算高斯模板矩阵，然后进行卷积运算。
	 *  
	 *	@高斯模糊 :高斯模糊是一种图像滤波器，它使用正态分布(高斯函数)计算模糊模板，并使用该模板与原图像做卷积运算，达到模糊图像的目的。
	 *	在实际应用中，在计算高斯函数的离散近似时，在大概3σ距离之外的像素都可以看作不起作用，这些像素的计算也就可以忽略。
	 *	通常，图像处理程序只需要计算的矩阵就可以保证相关像素影响。
	 *  
	 * @param source
	 * @param index 表示不同的sigma对应的模板
	 * @return double[][] 模糊后的图像信息矩阵
	 */
	public static double[][] gaussTran(double[][] source,int index){
		
		int height=source.length;
		int width=source[0].length;
		///保存高斯过滤后的结果
		double[][] result=new double[height][width];
	
		
		double[] template=GaussTemplate1D.gettemplateX_Y(index);
		int tWH=template.length;///模板维数
		
		for(int i=0;i<height;i++){
			for(int j=0;j<width;j++){
				
				///进行模糊处理——————卷积运算
				double sum=0.0;///卷积结果
				for(int m=0;m<tWH;m++){
						///计算与模板对应的图像上的位置
						int x=j-(int)tWH/2+m;
						int y=i;//-(int)tWH/2+m;
						
						//如果模板数据没有超过边界
						if(x>=0&&x<width){
							sum=sum+source[y][x]*template[m];
						}
				}
				
				for(int m=0;m<tWH;m++){
					///计算与模板对应的图像上的位置
					int x=j;
					int y=i-(int)tWH/2+m;//-(int)tWH/2+m;
					
					//如果模板数据没有超过边界
					if(y>=0&&y<height){
						sum=sum+source[y][x]*template[m];
					}
				}
				result[i][j]=sum/2;
				
			}
		}
		int i=0;
		i++;
		return result;
	}
	

	public static void imageToFile(BufferedImage image,File file){
		
		try {
			if(!file.exists()){
				file.createNewFile();
			}
			
			ImageIO.write(image, "png", file);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

	
}
