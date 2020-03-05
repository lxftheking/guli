package com.atguigu.guli.service.sms.utils;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "aliyun.sms")
public class SmsProperties {
    private String regionid;
    private String keyid;
    private String keysecret;
    private String templatecode;
    private String signname;
}
