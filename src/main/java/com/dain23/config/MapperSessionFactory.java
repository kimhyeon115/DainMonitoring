package com.dain23.config;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.boot.autoconfigure.SpringBootVFS;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

@SpringBootApplication
@Configuration
@MapperScan("com.dain23.**.mapper.**")
public class MapperSessionFactory {
	
	@Value("${mybatis.config-location}")
	private String configFile;
	
	@Bean
	public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {
		SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
		sessionFactory.setDataSource(dataSource);
		sessionFactory.setVfs(SpringBootVFS.class);
		Resource configRes = new PathMatchingResourcePatternResolver().getResource(configFile);
		sessionFactory.setConfigLocation(configRes);
		
		return sessionFactory.getObject();
	}

}