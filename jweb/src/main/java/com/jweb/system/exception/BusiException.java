package com.jweb.system.exception;

 /** 
 * @ClassName: BusiException 
 * @Description: TODO
 * @author: liyz
 * @date: 2018年1月31日 下午4:16:40  
 */
public class BusiException extends Exception {
	private static final long serialVersionUID = 1L;
	public BusiException(String err){
		super(err);
	}
}
