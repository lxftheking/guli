package com.atguigu.guli.service.sms.service.impl;

import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.atguigu.guli.common.base.result.ResultCodeEnum;
import com.atguigu.guli.common.base.util.ExceptionUtils;
import com.atguigu.guli.common.base.util.FormUtils;
import com.atguigu.guli.service.base.exception.GuliException;
import com.atguigu.guli.service.sms.service.SmsService;
import com.atguigu.guli.service.sms.utils.SmsProperties;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class SmsServiceImpl  implements SmsService {

    @Autowired
    private SmsProperties smsProperties;

    @Override
    public void send(String phone, Map<String, String> map) {

        if(StringUtils.isEmpty(phone)|| !FormUtils.isMobile(phone)){
            throw new GuliException(ResultCodeEnum.LOGIN_PHONE_ERROR);
        }

        DefaultProfile profile = DefaultProfile.getProfile("cn-hangzhou", smsProperties.getKeyid(), smsProperties.getKeysecret());
        IAcsClient client = new DefaultAcsClient(profile);

        CommonRequest request = new CommonRequest();
        request.setSysMethod(MethodType.POST);
        request.setSysDomain("dysmsapi.aliyuncs.com");
        request.setSysVersion("2017-05-25");
        request.setSysAction("SendSms");
        request.putQueryParameter("RegionId", smsProperties.getRegionid());
        request.putQueryParameter("PhoneNumbers", phone);
        request.putQueryParameter("SignName", smsProperties.getSignname());
        request.putQueryParameter("TemplateCode", smsProperties.getTemplatecode());
        Gson gson = new Gson();

        request.putQueryParameter("TemplateParam",gson.toJson(map));
        try {
            CommonResponse response = client.getCommonResponse(request);
            String data = response.getData();
            HashMap<String,String> hashMap = gson.fromJson(data, HashMap.class);
            String code = hashMap.get("Code");
            String message = hashMap.get("Message");
            //配置参考：短信服务->系统设置->国内消息设置
            //错误码参考：
            //https://help.aliyun.com/document_detail/101346.html?spm=a2c4g.11186623.6.613.3f6e2246sDg6Ry
            //控制所有短信流向限制（一分钟一条、一个小时五条、一天十条）
            if("isv.BUSINESS_LIMIT_CONTROL".equals(code)){
                log.error("短信发送过于频繁 " + "【code】" + code + ", 【message】" + message);
                throw new GuliException(ResultCodeEnum.SMS_SEND_ERROR_BUSINESS_LIMIT_CONTROL);
            }

            if(!"OK".equals(code)){
                log.error("短信发送失败 " + " - code: " + code + ", message: " + message);
                throw new GuliException(ResultCodeEnum.SMS_SEND_ERROR);
            }

        } catch (ServerException e) {
            log.error(ExceptionUtils.getMessage(e));
            throw new GuliException(ResultCodeEnum.SMS_SEND_ERROR);
        } catch (ClientException e) {
            log.error(ExceptionUtils.getMessage(e));
            throw new GuliException(ResultCodeEnum.SMS_SEND_ERROR);
        }

    }
}
