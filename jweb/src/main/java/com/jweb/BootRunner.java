package com.jweb;

import com.jweb.common.service.BeanService;
import com.jweb.common.session.RedisUserHolder;
import com.jweb.common.session.Session;
import com.jweb.sys.dto.identity.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * @ClassName: BootRunner 
 * @Description: TODO
 * @author: liyz
 * @date: 2018年1月31日 下午4:19:16  
 */
@Component
public class BootRunner implements CommandLineRunner{
	@Autowired
	BeanService beanService;
	@Autowired
	JedisPool jedisPool;
	@Override
	public void run(String... args){
		System.out.println(">>>>>>>>>>>>>>>Session<<<<<<<<<<<<<<<<");
		if(null!=jedisPool) {
			Jedis jedis = null;
			String test = null;
			try {
				jedis= jedisPool.getResource();
				final String teskKey = "_test_connection";
				jedis.set(teskKey, teskKey);
				test = jedis.get(teskKey);
				jedis.del(teskKey);
			}catch(Exception e) {
				//e.printStackTrace();
			} finally {
				try {
					jedis.close();
				} catch (Exception e) {
				}
			}
			//如果redis可用，使用redis作为session
			if(test!=null) {
				Session.setUserHolder(new RedisUserHolder<LoginUser>(jedisPool,LoginUser.class));
				System.out.println("Redis:"+jedisPool.toString());
			}else {
				System.out.println("Map");
			}
		}
		System.out.println(">>>>>>>>>>>>>>>>>创建表<<<<<<<<<<<<<<<");
		beanService.createTables();
	}
}
