package com.jweb.system.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

 /** 
 * @ClassName: ObjectUtil 
 * @Description: TODO
 * @author: liyz
 * @date: 2018年1月31日 下午4:20:36  
 */
public class ObjectUtil {
	public static  List<Class<?>> priTypes = new ArrayList<Class<?>>();
	static {
		priTypes.add(Integer.TYPE);priTypes.add(Integer.class);
		priTypes.add(Boolean.TYPE);priTypes.add(Boolean.class);
		priTypes.add(Long.TYPE);priTypes.add(Long.class);
		priTypes.add(Double.TYPE);priTypes.add(Double.class);
		priTypes.add(Float.TYPE);priTypes.add(Float.class);
		priTypes.add(Short.TYPE);priTypes.add(Short.class);
		priTypes.add(Byte.TYPE);priTypes.add(Byte.class);
		priTypes.add(String.class);	
	}
	@SuppressWarnings("unchecked")
	public static <T> T valueOfClass(Object o,Class<T> propType){
		if ( !propType.isPrimitive() && o == null ) {
            return null;
        }
        if(priTypes.contains(propType)){
        	return (T) o;
        }else if(Date.class.equals(propType)) {
        	if(o instanceof Long) {
        		return (T) new Date(Long.valueOf(String.valueOf(o)));
        	}else if(o instanceof Timestamp) {
        		return (T) new Date(((Timestamp) o).getTime());
        	}
        	return null;
        }else {
        	//其他数据类型的处理
        	return null;
        }
	}
	public static<T> Map<String,Object> toMap(T t){
		Map<String,Object> map=new HashMap<String,Object>(1);
		Class<?> ct=t.getClass();
		Field[] fields=ct.getDeclaredFields();
		
		for(Field f:fields){
			try {
				String fieldName=f.getName();
				String getterName = "get"
						+ StringUtil.firstUpperCase(fieldName);
				Method getter = ct.getDeclaredMethod(getterName);
				Object fieldValue = getter.invoke(t);
				if(fieldValue instanceof List){
					continue;
				}
				map.put(fieldName, fieldValue);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return map;
	}
	public static<T> String toSqlValue(T o){
		String value="";
		if(o==null){
			value= "NULL";
			return value;
		}
		Class<?> propType=o.getClass();
		if (propType.equals(String.class)) {
			value= "'"+((String) o)+"'";

        } else if (
            propType.equals(Integer.TYPE) || propType.equals(Integer.class)) {
        	value= o.toString();

        } else if (
            propType.equals(Boolean.TYPE) || propType.equals(Boolean.class)) {
        	value= Boolean.valueOf(o.toString()).toString();

        } else if (propType.equals(Long.TYPE) || propType.equals(Long.class)) {
        	value= Long.valueOf(o.toString()).toString();

        } else if (
            propType.equals(Double.TYPE) || propType.equals(Double.class)) {
        	value= Double.valueOf(o.toString()).toString();

        } else if (
            propType.equals(Float.TYPE) || propType.equals(Float.class)) {
        	value= Float.valueOf(o.toString()).toString();

        } else if (
            propType.equals(Short.TYPE) || propType.equals(Short.class)) {
        	value= Short.valueOf(o.toString()).toString();

        } else if (propType.equals(Byte.TYPE) || propType.equals(Byte.class)) {
        	value= Byte.valueOf(o.toString()).toString();

        } else if(propType.equals(java.util.Date.class)){
        	value= "'"+DateUtil.getFormatDate((Date)o, DateUtil.DEFAULT_FORMAT)+"'";
        }else {
        	value= "'"+o.toString()+"'";
        }
		//防止sql注入，将'、"、%进行转义
		value=value.replaceAll("'", "\\'");
		value=value.replaceAll("\"", "\\\"");
		value=value.replaceAll("%", "\\%");
		
		return value;
	}
	
	public static<T> String toStringValue(T o){
		String value="";
		if(o==null){
			return "";
		}
		Class<?> propType=o.getClass();
		if (propType.equals(String.class)) {
			value= (String) o;

        } else if (
            propType.equals(Integer.TYPE) || propType.equals(Integer.class)) {
        	value= o.toString();

        } else if (
            propType.equals(Boolean.TYPE) || propType.equals(Boolean.class)) {
        	value= Boolean.valueOf(o.toString()).toString();

        } else if (propType.equals(Long.TYPE) || propType.equals(Long.class)) {
        	value= Long.valueOf(o.toString()).toString();

        } else if (
            propType.equals(Double.TYPE) || propType.equals(Double.class)) {
        	value= Double.valueOf(o.toString()).toString();

        } else if (
            propType.equals(Float.TYPE) || propType.equals(Float.class)) {
        	value= Float.valueOf(o.toString()).toString();

        } else if (
            propType.equals(Short.TYPE) || propType.equals(Short.class)) {
        	value= Short.valueOf(o.toString()).toString();

        } else if (propType.equals(Byte.TYPE) || propType.equals(Byte.class)) {
        	value= Byte.valueOf(o.toString()).toString();

        } else if(propType.equals(java.util.Date.class)){
        	value= DateUtil.getFormatDate((Date)o, DateUtil.DEFAULT_FORMAT);
        }else {
        	value= o.toString();
        }
		
		return value;
	}
		
	public static void main(String[] args) {
	}
	
}