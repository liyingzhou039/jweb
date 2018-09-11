package com.jweb.common.session;

import com.jweb.common.util.JsonUtil;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.Set;

/** 
 * @ClassName: MapUserHolder 
 * @Description: TODO
 * @author: liyz
 * @date: 2018年1月31日 下午4:20:01 
 * @param  
 */
public class RedisUserHolder<U> implements UserHolder<U>{
	private JedisPool pool;
	private final String tokenPrefix = "_token:";
	private final String keyPrefix ="_key:";
	
	private Class<?> holderClass = null;
	
	public RedisUserHolder(JedisPool pool, Class<?> hClass){
		this.holderClass = hClass;
		this.pool = pool;
	}

	@Override
	public String put(String key,U user) {
		Jedis jedis = pool.getResource();
		try {
			String entityJson = jedis.get(keyPrefix+key);
			UserEntity<Object> entity = null;
			if(entityJson!=null) {
				entity = JsonUtil.jsonToBean(entityJson,new UserEntity<Object>().getClass());
			}
			//如果还不存在或者已经过期,重新生成
			if(entity==null || entity.isExpired()) {
				if(entity!=null) {
					//清除原来的用户实体，需要清除原来的token-key关系
					jedis.del(tokenPrefix+entity.getToken());
				}
				entity = new UserEntity<Object>(user,Session.EXPIRED_TIME);
				//添加token-key关系
				jedis.set(tokenPrefix+entity.getToken(), key);
				jedis.set(keyPrefix+key, JsonUtil.beanToJson(entity));
			}
			Set<String> set = jedis.keys(tokenPrefix+"*"); 
			System.out.println("当前在线:"+set.size());
			System.out.println("用户登录:"+JsonUtil.beanToJson(user));
			UserEntity<Object> en = JsonUtil.jsonToBean(jedis.get(keyPrefix+key),new UserEntity<Object>().getClass());
			return en.getToken();
		}finally {
			try {
                jedis.close();
            } catch (Exception e) {}
		}
	}

	@Override
	public U get(String token) {
		Jedis jedis = pool.getResource();
		try {
			String key = jedis.get(tokenPrefix+token);
			String entityJson = jedis.get(keyPrefix+key);
			UserEntity<Object> entity = null;
			if(entityJson!=null) {
				entity = JsonUtil.jsonToBean(entityJson,new UserEntity<Object>().getClass());
			}
			
			if(entity!=null && entity.isExpired()) {
				//已经过期则清除
				jedis.del(tokenPrefix+entity.getToken());
				jedis.del(keyPrefix+key);
			}else if(entity!=null){
				//没过期则延长过期时间
				entity.setEndTime(System.currentTimeMillis()+Session.EXPIRED_TIME);
			}
			String enJson = jedis.get(keyPrefix+key);
			
			if(enJson==null) {
				return null;
			}else {
				entity = JsonUtil.jsonToBean(enJson,new UserEntity<Object>().getClass());
				Object userObject = entity.getUser();
				return (U) JsonUtil.jsonToBean(JsonUtil.beanToJson(userObject), holderClass);
			}
		}finally {
			try {
                jedis.close();
            } catch (Exception e) {}
		}
	}

	@Override
	public U remove(String token) {
		Jedis jedis = pool.getResource();
		try {
			String key = jedis.get(tokenPrefix+token);
			String entityJson = jedis.get(keyPrefix+key);
			UserEntity<Object> entity = null;
			if(entityJson!=null) {
				entity = JsonUtil.jsonToBean(entityJson,new UserEntity<Object>().getClass());
			}
			if(entity!=null) {
				jedis.del(tokenPrefix+entity.getToken());
				jedis.del(keyPrefix+key);
				Object userObject = entity.getUser();
				return (U) JsonUtil.jsonToBean(JsonUtil.beanToJson(userObject), holderClass);
			}else {
				return null;
			}
		}finally {
			try {
	            jedis.close();
	        } catch (Exception e) {}
		}
	}
}
