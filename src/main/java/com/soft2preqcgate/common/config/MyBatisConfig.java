package com.soft2preqcgate.common.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * Mapper 인터페이스 스캔 범위를 고정한다.
 * XML은 application.yaml의 mybatis.mapper-locations(classpath:mapper/**&#47;*.xml)에서 로딩한다.
 */
@Configuration
@MapperScan("com.soft2preqcgate.**.mapper")
public class MyBatisConfig {
}
