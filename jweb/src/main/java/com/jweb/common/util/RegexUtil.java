package com.jweb.common.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

 /** 
 * @ClassName: RegexUtil 
 * @Description: TODO
 * @author: liyz
 * @date: 2018年1月31日 下午4:20:49  
 */
public class RegexUtil {
	public static List<String> getRegText(String regEx, String src) {
		List<String> rls = new ArrayList<String>();
		Pattern pat = Pattern.compile(regEx);
		Matcher mat = pat.matcher(src);
		while (mat.find()) {
			for (int i = 1; i <= mat.groupCount(); i++) {
				rls.add(mat.group(i));
			}
		}
		return rls;
	}
	public static void main(String[] args){
		List<String> validTypes=RegexUtil.getRegText("([a-zA-Z][a-zA-Z0-9_]+(\\[.*\\])?)\\s*,?","[complexValid['^[a-zA-Z]+$','必须是英文'],length[0,5]");
		System.out.println(validTypes);
	}
}
