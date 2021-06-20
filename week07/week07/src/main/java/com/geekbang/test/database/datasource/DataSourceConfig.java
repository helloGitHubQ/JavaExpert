package com.geekbang.test.database.datasource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.annotation.Resource;
import javax.sql.DataSource;

/**
 * 主从数据库配置：1主2从
 */
@Configuration
public class DataSourceConfig {

    @Resource
    Environment env;

    @Primary
    @Bean(name = "master")
    public DataSource masterDataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(env.getProperty("master.datasource.driver-class-name"));
        dataSource.setUrl(env.getProperty("master.datasource.url"));
        dataSource.setUsername(env.getProperty("master.datasource.username"));
        dataSource.setPassword(env.getProperty("master.datasource.password"));
        return dataSource;

    }

    @Bean(name = "slave1")
    public DataSource slave1DataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(env.getProperty("slave1.datasource.driver-class-name"));
        dataSource.setUrl(env.getProperty("slave1.datasource.url"));
        dataSource.setUsername(env.getProperty("slave1.datasource.username"));
        dataSource.setPassword(env.getProperty("slave1.datasource.password"));
        return dataSource;
    }

    @Bean(name = "slave2")
    public DataSource slave2DataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(env.getProperty("slave2.datasource.driver-class-name"));
        dataSource.setUrl(env.getProperty("slave2.datasource.url"));
        dataSource.setUsername(env.getProperty("slave2.datasource.username"));
        dataSource.setPassword(env.getProperty("slave2.datasource.password"));
        return dataSource;
    }
}
