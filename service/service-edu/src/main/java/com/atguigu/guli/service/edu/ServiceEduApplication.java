package com.atguigu.guli.service.edu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author helen
 * @since 2020/2/12
 */
@SpringBootApplication
@ComponentScan({"com.atguigu.guli"}) //扫描通用模块中的类和当前项目下的类
public class ServiceEduApplication {
    public static void main(String[] args){
        SpringApplication.run(ServiceEduApplication.class);
    }
}
