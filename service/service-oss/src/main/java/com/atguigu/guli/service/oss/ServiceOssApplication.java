package com.atguigu.guli.service.oss;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author helen
 * @since 2020/2/19
 */
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)//取消数据源自动配置
@ComponentScan({"com.atguigu.guli"}) //扫描通用模块中的类和当前项目下的类
@EnableEurekaClient
public class ServiceOssApplication {

    public static void main(String[] args){
        SpringApplication.run(ServiceOssApplication.class, args);
    }
}
