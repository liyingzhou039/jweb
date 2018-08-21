package com.jweb.common.validator;

import java.util.regex.Pattern;

import com.jweb.common.exception.BusiException;


 /** 
 * @ClassName: LchValidator 
 * @Description: TODO
 * @author: liyz
 * @date: 2018年1月31日 下午4:21:09  
 */
public class LchValidator {
	public  void required(String zhField,String value) throws BusiException{
		if(null==value || value.trim().equals("")){
			throw new BusiException(zhField+"不能为空");
		}
	}
	public  void length(String zhField,String value,String[] params) throws BusiException{
		if(null!=value){
			int len=value.length();
			if(params!=null&&params.length==2){
				int min=Integer.parseInt(params[0]);
				int max=Integer.parseInt(params[1]);
				if(len<min||len>max){
					throw new BusiException(zhField+"长度必须介于["+min+","+max+"]之间");
				}
			}else{
				throw new BusiException(zhField+"检验参数不正确");
			}
		}
	}
	public void complexValid(String zhField,String value,String[] params) throws BusiException{
		if(null!=value){
			if(params!=null&&params.length==2){
				String reg=params[0];
				String msg=params[1];
				if(!Pattern.matches(reg, value)){
					throw new BusiException(zhField+msg);
				}
			}else{
				throw new BusiException(zhField+"检验参数不正确");
			}
		}
	}
}
