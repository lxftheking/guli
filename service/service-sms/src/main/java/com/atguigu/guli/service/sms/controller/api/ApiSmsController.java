package com.atguigu.guli.service.sms.controller.api;

import com.atguigu.guli.common.base.result.R;
import com.atguigu.guli.common.base.util.RandomUtils;
import com.atguigu.guli.service.sms.service.SmsService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Api(value = "短信管理")
@CrossOrigin
@RestController
@RequestMapping("api/sms")
public class ApiSmsController {

    @Autowired
    private SmsService smsService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @GetMapping("send/{phone}")
    public R send(@PathVariable("phone")String phone){

        String code = RandomUtils.getFourBitRandom();
        Map<String, String> hashMap = new HashMap<>();
        hashMap.put("code",code);
       // smsService.send(phone,hashMap);

        redisTemplate.opsForValue().set("code:"+phone,code,5, TimeUnit.MINUTES);

        return  R.ok().message("成功");
    }


}
