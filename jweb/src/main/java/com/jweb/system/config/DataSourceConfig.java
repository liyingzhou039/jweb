package com.jweb.system.config;

import java.sql.SQLException;

import javax.sql.DataSource;

import com.jweb.system.util.StringUtil;
import org.springframework.boot.bind.RelaxedPropertyResolver;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import com.alibaba.druid.pool.DruidDataSource;

@Configuration
public class DataSourceConfig implements EnvironmentAware{
	 
	private RelaxedPropertyResolver propertyResolver;

	private String driveClassName;
	private String url;
	private String userName;
	private String password;
	   
	private String filters;
	private String maxActive;
	private String initialSize;
	private String maxWait;
	private String minIdle;
	private String timeBetweenEvictionRunsMillis;
	private String minEvictableIdleTimeMillis;
	private String validationQuery;
	private String testWhileIdle;
	private String testOnBorrow;
	private String testOnReturn;
	private String poolPreparedStatements;
	private String maxOpenPreparedStatements;
	@Override
    public void setEnvironment(Environment environment) {
        this.propertyResolver = new RelaxedPropertyResolver(environment, null);
        this.url = propertyResolver.getProperty("spring.datasource.url");
        this.userName= propertyResolver.getProperty("spring.datasource.username");
        this.password = propertyResolver.getProperty("spring.datasource.password");
        this.driveClassName = propertyResolver.getProperty("spring.datasource.driver-class-name");
        this.filters = propertyResolver.getProperty("spring.datasource.filters");
        this.maxActive = propertyResolver.getProperty("spring.datasource.maxActive");
        this.initialSize = propertyResolver.getProperty("spring.datasource.initialSize");
        this.maxWait = propertyResolver.getProperty("spring.datasource.maxWait");
        this.minIdle = propertyResolver.getProperty("spring.datasource.minIdle");
        this.timeBetweenEvictionRunsMillis = propertyResolver.getProperty("spring.datasource.timeBetweenEvictionRunsMillis");
        this.minEvictableIdleTimeMillis = propertyResolver.getProperty("spring.datasource.minEvictableIdleTimeMillis");
        this.validationQuery = propertyResolver.getProperty("spring.datasource.validationQuery");
        this.testWhileIdle = propertyResolver.getProperty("spring.datasource.testWhileIdle");
        this.testOnBorrow = propertyResolver.getProperty("spring.datasource.testOnBorrow");
        this.testOnReturn = propertyResolver.getProperty("spring.datasource.testOnReturn");
        this.poolPreparedStatements = propertyResolver.getProperty("spring.datasource.poolPreparedStatements");
        this.maxOpenPreparedStatements = propertyResolver.getProperty("spring.datasource.maxOpenPreparedStatements");
    }
	@Bean
    public DataSource druidDataSource() {
        DruidDataSource druidDataSource = new DruidDataSource();
        druidDataSource.setUrl(url);
        druidDataSource.setUsername(userName);
        druidDataSource.setPassword(password);
        druidDataSource.setDriverClassName(StringUtil.notNull(driveClassName)?driveClassName:"com.mysql.jdbc.Driver");
        druidDataSource.setMaxActive(StringUtil.notNull(maxActive)? Integer.parseInt(maxActive):10);
        druidDataSource.setInitialSize(StringUtil.notNull(initialSize)? Integer.parseInt(initialSize):1);
        druidDataSource.setMaxWait(StringUtil.notNull(maxWait)? Integer.parseInt(maxWait):60000);
        druidDataSource.setMinIdle(StringUtil.notNull(minIdle)? Integer.parseInt(minIdle):3);
        druidDataSource.setTimeBetweenEvictionRunsMillis(StringUtil.notNull(timeBetweenEvictionRunsMillis)?
                Integer.parseInt(timeBetweenEvictionRunsMillis):60000);
        druidDataSource.setMinEvictableIdleTimeMillis(StringUtil.notNull(minEvictableIdleTimeMillis)?
                Integer.parseInt(minEvictableIdleTimeMillis):300000);
        druidDataSource.setValidationQuery(StringUtil.notNull(validationQuery)?validationQuery:"select 'x'");
        druidDataSource.setTestWhileIdle(StringUtil.notNull(testWhileIdle)? Boolean.parseBoolean(testWhileIdle):true);
        druidDataSource.setTestOnBorrow(StringUtil.notNull(testOnBorrow)? Boolean.parseBoolean(testOnBorrow):false);
        druidDataSource.setTestOnReturn(StringUtil.notNull(testOnReturn)? Boolean.parseBoolean(testOnReturn):false);
        druidDataSource.setPoolPreparedStatements(StringUtil.notNull(poolPreparedStatements)? Boolean.parseBoolean(poolPreparedStatements):true);
        druidDataSource.setMaxOpenPreparedStatements(StringUtil.notNull(maxOpenPreparedStatements)? Integer.parseInt(maxOpenPreparedStatements):20);

        try {
            druidDataSource.setFilters(StringUtil.notNull(filters)?filters:"stat, wall");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return druidDataSource;
    }
}
