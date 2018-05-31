package com.jweb.system.boot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import com.jweb.system.service.BeanService;
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
	@Override
	public void run(String... args){
		System.out.println(">>>>>>>>>>>>>>>>>创建表<<<<<<<<<<<<<<<");
		beanService.createBeanTables();
	}
}
