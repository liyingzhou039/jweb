package com.jweb.system.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.jweb.system.interceptor.TokenInterceptor;

/**
 * @author zhuhai zhuhai@bzhcloud.com
 * @ClassName: WebConfig
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @date 2017年8月4日 上午11:34:14
 */
@Configuration
public class WebConfig extends WebMvcConfigurerAdapter {
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(getTokenInterceptor());
		super.addInterceptors(registry);
	}
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/**").addResourceLocations("classpath:/static/");
		super.addResourceHandlers(registry);
	}
	@Bean
	TokenInterceptor getTokenInterceptor() {
		return new TokenInterceptor();
	}
	
	
}
