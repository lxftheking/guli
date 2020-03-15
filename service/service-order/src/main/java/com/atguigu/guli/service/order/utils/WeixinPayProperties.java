package com.atguigu.guli.service.order.utils;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
//注意prefix要写到最后一个 "." 符号之前
@ConfigurationProperties(prefix="weixin.pay")
public class WeixinPayProperties {
    private String appid;
    private String partner;
    private String partnerkey;
    private String notifyurl;
}