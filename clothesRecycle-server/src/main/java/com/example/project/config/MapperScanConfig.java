package com.example.project.config;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Configuration;

/**
 * Mapper 扫描配置。
 */
@Configuration
@ConditionalOnBean(SqlSessionFactory.class)
@MapperScan("com.example.project.mapper")
public class MapperScanConfig {
}
