package com.jweb.common.service;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.springframework.stereotype.Service;

import com.jweb.common.exception.BusiException;
import com.jweb.common.persistent.annotation.Field;
import com.jweb.common.util.ObjectUtil;
import com.jweb.common.util.StringUtil;
import com.jweb.common.validator.LchValidator;
 /** 
 * @ClassName: ValidatorService 
 * @Description: TODO
 * @author: liyz
 * @date: 2018年1月31日 下午4:19:36  
 */
@Service
public class ValidatorService {
	public <T> void check(T t) throws BusiException{
		LchValidator validator=new LchValidator();
		if(t==null) { return ;}
		java.lang.reflect.Field[] fields = t.getClass().getDeclaredFields();
		for (java.lang.reflect.Field field : fields) {
			Field fa = field.getAnnotation(Field.class);
			if(null==fa) continue;
			String zhFieldName = fa.name();
			String fieldName = field.getName();
			Method getter = null;
			String fieldValue = null;
			boolean required = false;
			String[] validType=null;
			try {
				try {
					getter = t.getClass().getDeclaredMethod("get" + StringUtil.firstUpperCase(fieldName));
				}catch(Exception ee){
					getter = t.getClass().getDeclaredMethod("is" + StringUtil.firstUpperCase(fieldName));
				}
				fieldValue = ObjectUtil.toStringValue(getter.invoke(t));
				required = fa.required();
				validType=fa.validType();
			} catch (Exception e) {
				e.printStackTrace();
				throw new BusiException("系统出错:"+e.getMessage());
			}
			if (required) {
				validator.required(zhFieldName, fieldValue);
			}
			//validType
			//方法名称1('','',...);方法名称2('','',...);...;方法名n;...
			if(null!=validType&&validType.length>0){
				for(String vt:validType){
					if(vt.startsWith("'")) {vt=vt.substring(1);}
					if(vt.endsWith("'")) {vt=vt.substring(0, vt.length()-1);}
					valid(zhFieldName,validator,fieldValue,vt);
				}
			}
		}
	}
	private  void valid(String zhFieldName,LchValidator validator,String fieldValue,String validType) throws BusiException{
		validType=validType.trim();
		if(validType.indexOf("(")==-1){
			try {
				Method validMethod=validator.getClass().getDeclaredMethod(validType, String.class,String.class);
				validMethod.invoke(validator,zhFieldName, fieldValue);
			}catch (InvocationTargetException e) {
				throw new BusiException(e.getTargetException().getMessage());
			}catch (NoSuchMethodException e) {
				throw new BusiException("验证方法不存在");
			} catch (Exception e) {
				throw new BusiException("系统错误");
			}
		}else if(validType.indexOf("(")!=-1&&validType.indexOf(")")!=-1){
			try {
				String method=validType.substring(0, validType.indexOf("("));
				String[] params=validType
						.substring(validType.indexOf("(")+1,validType.lastIndexOf(")"))
						.split(",");
				for(int i=0;i<params.length;i++){
					if(params[i].startsWith("\\'")) { params[i]=params[i].substring(1, params[i].length());}
					if(params[i].endsWith("\\'")) {params[i]=params[i].substring(0,params[i].length()-1);}
				}
				Method validMethod = validator.getClass().getDeclaredMethod(method, String.class,String.class,String[].class);
				validMethod.invoke(validator,zhFieldName,fieldValue,params);
			} catch (InvocationTargetException e) {
				throw new BusiException(e.getTargetException().getMessage());
			}catch (NoSuchMethodException e) {
				throw new BusiException("验证方法不存在");
			} catch (Exception e) {
				throw new BusiException("系统错误");
			}
		}
	}
}
