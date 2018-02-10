package com.jweb.system.util;

import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.List;
import java.util.Set;


 /** 
 * @ClassName: StringUtil 
 * @Description: TODO
 * @author: liyz
 * @date: 2018年1月31日 下午4:21:03  
 */
public class StringUtil {
	public static boolean notNull(String str) {
		if(str!=null&&!"".equals(str.trim())) {
			return true;
		}
		return false;
	}
	public static String toCamel(String name) {
		if(null == name ) {
			return null;
		}
		String camel ="";
		String[] words = name.split("_");
		for(String word:words) {
			camel+=firstUpperCase(word.toLowerCase());
		}
		return firstLowerCase(camel);
	}
	
	public static String toSlide(String name) {
		if(null == name ) {
			return null;
		}
		String slide="";
		for(int i=0;i<name.length();i++) {
			if(Character.isUpperCase(name.charAt(i))){
				slide+="_";
			}
			slide+=name.charAt(i);
		}
		slide = slide.toLowerCase();
		return slide;
	}
	public static String firstUpperCase(String string){
		String r="";
		if(null!=string&&!"".equals(string)){
			r=string.substring(0,1).toUpperCase().concat(string.substring(1));
		}
		return r;
	}
	
	public static String firstLowerCase(String string){
		String r="";
		if(null!=string&&!"".equals(string)){
			r=string.substring(0,1).toLowerCase().concat(string.substring(1));
		}
		return r;
	}
	public static String split(Set<String> sets, String spliter) {
		String s="";
		for (Iterator<String> it = sets.iterator(); it.hasNext();) {
			if(s.length()>0){
				s+=spliter;
			}
			s+=it.next();
		}
		return s;
	}
	public static String splitString(List<String> ls, String spliter) {
		String s="";
		for (String str:ls) {
			if(s.length()>0){
				s+=spliter;
			}
			s+="'"+str+"'";
		}
		return s;
	}
	
	public static String toMoneyString(double money){
		String t=Math.abs(Math.round(money*100)/100.0f)+"";
		String l="";
		String r="";
		int p=t.indexOf(".");
		if(p<0){
			r="00";
			l=t;
		}else{
			l=t.substring(0,p);
			r=t.substring(p+1);
		}
		if(r.length()==1){
			r+="0";
		}else if(r.length()>=2){
			r=r.substring(0,2);
		}
		StringBuffer temp=new StringBuffer();
		for(int i=0;i<l.length();i++){
			if(i%3==0&&i!=0){
				temp.append(",");
			}
			temp.append(l.charAt(l.length()-1-i));
		}
		l=temp.toString();
		return money<0?("-"+l+"."+r):(l+"."+r);
	}
	public static String toFileSizeString(double fileSize)
	{
		String unit="B";
		double size=0.0d;
		size=fileSize<0?0:fileSize;
		if(fileSize>=1024){
			unit="KB";
			size=fileSize/1024;
		}
		if(fileSize>=1024*1024){
			unit="MB";
			size=fileSize/(1024*1024);
		}
		if(fileSize>=1024*1024*1024){
			unit="GB";
			size=fileSize/(1024*1024*1024);
		}
		if(fileSize>=1024*1024*1024*1024d){
			unit="TB";
			size=fileSize/(1024*1024*1024*1024);
		}
		return new DecimalFormat("#0.0").format(size)+" "+unit;
	}
	public static void main(String[] args) {
		/*System.out.println(toFileSizeString(0.0));
		System.out.println(toFileSizeString(1024));
		System.out.println(toFileSizeString(1000000d));
		System.out.println(toFileSizeString(23568000000d));*/
		
		String a ="userName";
		System.out.println(toSlide(a));
		
		System.out.println(toCamel(toSlide(toSlide(a))));
	}
}
