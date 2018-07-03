package com.jweb.system.config;

import java.sql.SQLException;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.sqlite.SQLiteDataSource;
import com.alibaba.druid.pool.DruidDataSource;
import com.jweb.system.persistent.dialect.AbstractLchDialect;
import com.jweb.system.persistent.dialect.MysqlDialect;
import com.jweb.system.persistent.dialect.SqliteDialect;

/**
 * @author  liyz
 */
@Configuration
public class DataSourceConfig{
	@Value("${spring.datasource.type:org.sqlite.SQLiteDataSource}")
    private String type;
	@Value("${spring.datasource.driverClassName:org.sqlite.JDBC}")
	private String driveClassName;
	@Value("${pring.datasource.url:jdbc:sqlite:jweb.db}")
	private String url;
	@Value("${spring.datasource.username:}")
	private String userName;
    @Value("${spring.datasource.password:}")
	private String password;
    @Value("${spring.datasource.filters:stat, wall}")
	private String filters;
    @Value("${spring.datasource.maxActive:10}")
	private int maxActive;
    @Value("${spring.datasource.initialSize:1}")
	private int initialSize;
    @Value("${spring.datasource.maxWait:60000}")
	private int maxWait;
    @Value("${spring.datasource.minIdle:3}")
	private int minIdle;
    @Value("${spring.datasource.timeBetweenEvictionRunsMillis:60000}")
	private long timeBetweenEvictionRunsMillis;
    @Value("${spring.datasource.minEvictableIdleTimeMillis:300000}")
	private long minEvictableIdleTimeMillis;
    @Value("${spring.datasource.validationQuery:select 'x'}")
	private String validationQuery;
    @Value("${spring.datasource.testWhileIdle:true}")
	private boolean testWhileIdle;
    @Value("${spring.datasource.testOnBorrow:false}")
	private boolean testOnBorrow;
    @Value("${spring.datasource.testOnReturn:false}")
	private boolean testOnReturn;
    @Value("${spring.datasource.poolPreparedStatements:true}")
	private boolean poolPreparedStatements;
    @Value("${spring.datasource.maxOpenPreparedStatements:20}")
	private int maxOpenPreparedStatements;
	private DataSource datasource;

	@Bean
	public AbstractLchDialect dialect() {
	    final  String source = "org.sqlite.SQLiteDataSource";
		if(source.equals(this.type)) {
			return new SqliteDialect();
		}else {
			return new MysqlDialect();
		}
	}
	@Bean
	public DataSource getDataSource() {
		if(null==datasource) datasource = dataSource();
		return datasource;
	}
    public DataSource dataSource() {
        final  String source = "org.sqlite.SQLiteDataSource";
		if(source.equals(this.type)) {
			SQLiteDataSource sqliteDatasource = new SQLiteDataSource();
			sqliteDatasource.setUrl(this.url);
			return sqliteDatasource;
		}
        DruidDataSource druidDataSource = new DruidDataSource();
        druidDataSource.setUrl(url);
        druidDataSource.setUsername(userName);
        druidDataSource.setPassword(password);
        druidDataSource.setDriverClassName(driveClassName);
        druidDataSource.setMaxActive(maxActive);
        druidDataSource.setInitialSize(initialSize);
        druidDataSource.setMaxWait(maxWait);
        druidDataSource.setMinIdle(minIdle);
        druidDataSource.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);
        druidDataSource.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
        druidDataSource.setValidationQuery(validationQuery);
        druidDataSource.setTestWhileIdle(testWhileIdle);
        druidDataSource.setTestOnBorrow(testOnBorrow);
        druidDataSource.setTestOnReturn(testOnReturn);
        druidDataSource.setPoolPreparedStatements(poolPreparedStatements);
        druidDataSource.setMaxOpenPreparedStatements(maxOpenPreparedStatements);

        try {
            druidDataSource.setFilters(filters);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return druidDataSource;
    }
}
