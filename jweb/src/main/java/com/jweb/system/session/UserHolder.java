package com.jweb.system.session;

/**
 * @ClassName: AbstractUserHolder
 * @Description: TODO
 * @author: liyz
 * @date: 2018年1月31日 下午4:19:43 
 * @param <U> 
 */
public interface UserHolder<U> {
	public abstract String put(String key,U userEntity);
	public abstract U get(String token);
	public abstract U remove(String token);
}
