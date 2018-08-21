package com.jweb.common.config;

import com.jweb.sys.interceptor.CorsInterceptor;
import com.jweb.sys.interceptor.IspInterceptor;
import com.jweb.sys.interceptor.TokenInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
@Configuration
public class WebConfig extends WebMvcConfigurerAdapter {
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(getCorsInterceptor());
		registry.addInterceptor(getTokenInterceptor());
		registry.addInterceptor(getIspInterceptor());
		super.addInterceptors(registry);
	}
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/**").addResourceLocations("classpath:/static/");
		super.addResourceHandlers(registry);
	}
	@Bean
	CorsInterceptor getCorsInterceptor(){
		return new CorsInterceptor();
	}
	@Bean
	TokenInterceptor getTokenInterceptor() {
		return new TokenInterceptor();
	}
	@Bean
	IspInterceptor getIspInterceptor() {
		return new IspInterceptor();
	}
}
