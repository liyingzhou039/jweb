package com.jweb.common.session;

import com.jweb.sys.dto.identity.LoginUser;

/**
 * @ClassName: Session 
 * @Description: TODO
 * @author: liyz
 * @date: 2018年1月31日 下午4:20:09  
 */
public class Session {
	public static final String TOKEN_NAME ="_token";
	public static final long EXPIRED_TIME =30*60*1000;
	public static final String FROM_TYPE_AJAX ="ajax";
	public static final String FROM ="_from";
	private static ThreadLocal<String> tokenThreadLocal = new ThreadLocal<String>() {
        @Override
		protected String initialValue() {
            return null;
        }
    };
	private static UserHolder<LoginUser> userHolder = new MapUserHolder<>();

	public static void setUserHolder(UserHolder<LoginUser> holder){
		userHolder = holder;
	}
	
	public static void setCurrentToken(String token) {
		tokenThreadLocal.set(token);
	}
	public static LoginUser getCurrentUser() {
		String token = tokenThreadLocal.get();
		return userHolder.get(token);
	}
	public static String setCurrentUser(LoginUser user) {
		String key = user.getId();
		String token = userHolder.put(key,user);
		tokenThreadLocal.set(token);
		return token;
	}
	public static void removeCurrentUser() {
		String token = tokenThreadLocal.get();
		userHolder.remove(token);
		tokenThreadLocal.remove();
	}
}
