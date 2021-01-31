package com.cyaegha.tools;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by diyan on 2019/6/12. 如果一行文字长度超过了既定的宽度，那么主动执行换行操作
 */
public class PhotoUtils
{

	/**
	 * 根据str,font的样式等生成图片
	 * https://blog.csdn.net/sinat_28505133/article/details/54669111
	 *
	 * @param strArr       文字内容
	 * @param font         字体
	 * @param width        宽度
	 * @param image_height 图片高度
	 * @throws Exception
	 */
	public static List<BufferedImage> createImage(String[] strArr,Font font,int width,int image_height,int every_line,
			int line_height) throws Exception
	{

		int stringWidth=font.getSize();// 标点符号也算一个字
		int line_string_num=width%stringWidth==0? (width/stringWidth):(width/stringWidth)+1;
		System.out.println("每行="+line_string_num);

		List<String> listStr=new ArrayList<String>();
		List<String> newList=new ArrayList<String>();
		for(int h=0;h<strArr.length;h++)
		{
			listStr.add(strArr[h]);
		}
		for(int j=0;j<listStr.size();j++)
		{
			if(listStr.get(j).length()>line_string_num)
			{
				newList.add(listStr.get(j).substring(0,line_string_num));
				listStr.add(j+1,listStr.get(j).substring(line_string_num));
				listStr.set(j,listStr.get(j).substring(0,line_string_num));
			}else
			{
				newList.add(listStr.get(j));
			}
		}

		int a=newList.size();
		int b=every_line;
		int imgNum=a%b==0? (a/b):(a/b)+1;

		List<BufferedImage> images=new ArrayList<BufferedImage>();

		for(int m=0;m<imgNum;m++)
		{
			// 创建图片
			BufferedImage image=new BufferedImage(width,image_height,BufferedImage.TYPE_INT_BGR);
			Graphics g=image.getGraphics();
			g.setClip(0,0,width,image_height);
			g.setColor(Color.white); // 背景色白色
			g.fillRect(0,0,width,image_height);
			g.setColor(Color.black);// 字体颜色黑色
			g.setFont(font);// 设置画笔字体
			// 每张多少行，当到最后一张时判断是否填充满
			for(int i=0;i<every_line;i++)
			{
				int index=i+m*every_line;
				if(newList.size()-1>=index)
				{
					System.out.println("每行实际="+newList.get(index).length());
					g.drawString(newList.get(index),0,line_height*(i+1));
				}
			}
			g.dispose();
			images.add(image);// 输出png图片
		}
		return images;
	}

}
