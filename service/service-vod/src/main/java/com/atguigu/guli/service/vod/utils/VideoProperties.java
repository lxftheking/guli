package com.atguigu.guli.service.vod.utils;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "aliyun.vod.file")
public class VideoProperties {

    private String keyid;
    private String keysecret;

}
