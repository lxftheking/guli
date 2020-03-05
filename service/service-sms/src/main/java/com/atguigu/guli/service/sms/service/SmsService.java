package com.atguigu.guli.service.sms.service;

import java.util.Map;

public interface SmsService {

    void send(String phone, Map<String,String> map);
}
