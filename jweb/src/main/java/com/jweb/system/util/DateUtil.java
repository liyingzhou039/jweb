package com.jweb.system.util;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

 /** 
 * @ClassName: DateUtil 
 * @Description: TODO
 * @author: liyz
 * @date: 2018年1月31日 下午4:20:30  
 */
public class DateUtil {
	public static String DEFAULT_FORMAT="yyyy-MM-dd HH:mm:ss";
	public static String getFormatDate(Date date,String format){
		SimpleDateFormat df=new SimpleDateFormat(format);
		return df.format(date);
	}
	public static Timestamp  toTimestamp(Date date){
		Timestamp ts=new Timestamp(date.getTime());
		return ts;
	}
	public static void main(String[] args) {
		System.out.println(toTimestamp(new Date()));
	}
	public static Date toDate(String dateStr){
		return toDate(dateStr,DEFAULT_FORMAT);
	}
	public static Date toDate(String dateStr,String format){
		SimpleDateFormat df=new SimpleDateFormat(format);
		try {
			return df.parse(dateStr);
		} catch (ParseException e) {
			return null;
		}
	}
}
