package com.jweb.system.session;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.jweb.system.util.JsonUtil;
/** 
 * @ClassName: MapUserHolder 
 * @Description: TODO
 * @author: liyz
 * @date: 2018年1月31日 下午4:20:01 
 * @param  
 */
public class MapUserHolder<U> implements UserHolder<U>{
	
	private Map<String,UserEntity<U>> holder = new HashMap<String,UserEntity<U>>(100);
	private Map<String,String> tokens = new HashMap<String,String>(100);

	@Override
	public String put(String key,U user) {
		UserEntity<U> entity = holder.get(key);
		//如果还不存在或者已经过期,重新生成
		if(entity==null || entity.isExpired()) {
			if(entity!=null) {
				//清除原来的用户实体，需要清除原来的token-key关系
				tokens.remove(entity.getToken());
			}
			entity = new UserEntity<U>(user,Session.EXPIRED_TIME);
			//添加token-key关系
			tokens.put(entity.getToken(), key);
			holder.put(key, entity);
		}
		System.out.println("当前在线:"+holder.keySet().size());
		System.out.println("用户登录:"+JsonUtil.beanToJson(user));
		return holder.get(key).getToken();
	}

	@Override
	public U get(String token) {
		String key = tokens.get(token);
		
		UserEntity<U> entity = holder.get(key);
		
		if(entity!=null && entity.isExpired()) {
			//已经过期则清除
			tokens.remove(entity.getToken());
			holder.remove(key);
		}else if(entity!=null){
			//没过期则延长过期时间
			entity.setEndTime(System.currentTimeMillis()+Session.EXPIRED_TIME);
		}
		
		entity = holder.get(key);
		if(entity==null) {
			return null;
		}else {
			return entity.getUser();
		}
	}

	@Override
	public U remove(String token) {
		String key = tokens.get(token);
		UserEntity<U> entity = holder.get(key);
		if(entity!=null) {
			tokens.remove(entity.getToken());
			holder.remove(key);
			return entity.getUser();
		}else {
			return null;
		}
	}
	
	public static void main(String[] args) throws InterruptedException {
		UserHolder<TestUser> holder = new MapUserHolder<TestUser>();
		
		TestUser user1 = new TestUser("user1","pass1");
		TestUser user2 = new TestUser("user2","pass2");
		TestUser user3 = new TestUser("user3","pass3");
		String token1 = holder.put(user1.getName(), user1);
		Thread.sleep(1000);
		String token2 = holder.put(user2.getName(), user2);
		Thread.sleep(1000);
		String token3 = holder.put(user3.getName(), user3);
		
		System.out.println("user1："+JsonUtil.beanToJson(holder.get(token1)));
		System.out.println("user2："+JsonUtil.beanToJson(holder.get(token2)));
		System.out.println("user3："+JsonUtil.beanToJson(holder.get(token3)));
		Thread.sleep(1000);
		System.out.println("user1："+JsonUtil.beanToJson(holder.get(token1)));
		System.out.println("user2："+JsonUtil.beanToJson(holder.get(token2)));
		System.out.println("user3："+JsonUtil.beanToJson(holder.get(token3)));
	}
}
