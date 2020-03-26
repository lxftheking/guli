package com.atguigu.guli.service.order.service.impl;

import com.atguigu.guli.common.base.result.ResultCodeEnum;
import com.atguigu.guli.common.base.util.ExceptionUtils;
import com.atguigu.guli.service.base.exception.GuliException;
import com.atguigu.guli.service.order.entity.Order;
import com.atguigu.guli.service.order.service.OrderService;
import com.atguigu.guli.service.order.service.WeixinPayService;
import com.atguigu.guli.service.order.utils.HttpClient;
import com.atguigu.guli.service.order.utils.WeixinPayProperties;
import com.github.wxpay.sdk.WXPayUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class WeixinPayServiceImpl implements WeixinPayService {

    @Autowired
    private OrderService orderService;

    @Autowired
    private WeixinPayProperties properties;

    @Override
    public Map<String, Object> createNative(String orderNo, String remoteAddr) {

        try {
            Order order = orderService.getOrderByOrderNo(orderNo);
            HashMap<String, String> params = new HashMap<>();
            params.put("appid",properties.getAppid());
            params.put("mch_id",properties.getPartner());
            params.put("nonce_str", WXPayUtil.generateNonceStr());
            params.put("body",order.getCourseTitle());
            params.put("out_trade_no",orderNo);

            String totalFee=order.getTotalFee().multiply(new BigDecimal("100")).intValue()+"";
            params.put("total_fee",totalFee);

            params.put("spbill_create_ip", remoteAddr);
            params.put("notify_url", properties.getNotifyurl());
            params.put("trade_type", "NATIVE");

            String xmlParams = WXPayUtil.generateSignedXml(params, properties.getPartnerkey());

            HttpClient client = new HttpClient("https://api.mch.weixin.qq.com/pay/unifiedorder");

            client.setXmlParam(xmlParams);
            client.setHttps(true);
            //发送请求
            client.post();


            String resultXml = client.getContent();
            Map<String, String> resultMap = WXPayUtil.xmlToMap(resultXml);

            if("FAIL".equals(resultMap.get("return_code")) || "FAIL".equals(resultMap.get("result_code"))) {
                log.error("微信支付统一下单错误 - "
                        + "return_code: " + resultMap.get("return_code")
                        + "return_msg: " + resultMap.get("return_msg")
                        + "result_code: " + resultMap.get("result_code")
                        + "err_code: " + resultMap.get("err_code")
                        + "err_code_des: " + resultMap.get("err_code_des"));
                throw new GuliException(ResultCodeEnum.PAY_UNIFIEDORDER_ERROR);
            }

            Map<String, Object> map = new HashMap<>();
            map.put("result_code", resultMap.get("result_code"));
            map.put("code_url", resultMap.get("code_url"));
            map.put("course_id", order.getCourseId());
            map.put("total_fee", order.getTotalFee());
            map.put("out_trade_no", orderNo);

            return  map;
        } catch (Exception e) {
            log.error(ExceptionUtils.getMessage(e));
            throw new GuliException(ResultCodeEnum.PAY_UNIFIEDORDER_ERROR);
        }
    }

    @Override
    public Map<String, String> queryPayStatus(String orderNo) {
        try {
            //1、封装参数
            Map<String, String> m = new HashMap<>();
            m.put("appid", properties.getAppid());
            m.put("mch_id", properties.getPartner());
            m.put("out_trade_no", orderNo);
            m.put("nonce_str", WXPayUtil.generateNonceStr());

            //2、设置请求
            HttpClient client = new HttpClient("https://api.mch.weixin.qq.com/pay/orderquery");
            client.setXmlParam(WXPayUtil.generateSignedXml(m, properties.getPartnerkey()));
            client.setHttps(true);
            client.post();

            //3、返回第三方的数据
            String xml = client.getContent();
            Map<String, String> resultMap = WXPayUtil.xmlToMap(xml);

            if("FAIL".equals(resultMap.get("return_code")) || "FAIL".equals(resultMap.get("result_code")) ){
                log.error("查询支付结果错误 - " +
                        "return_msg: " + resultMap.get("return_msg") + ", " +
                        "err_code: " + resultMap.get("err_code") + ", " +
                        "err_code_des: " + resultMap.get("err_code_des"));
                throw new GuliException(ResultCodeEnum.PAY_ORDERQUERY_ERROR);
            }

            //4、返回map
            return resultMap;
        } catch (Exception e) {
            log.error(ExceptionUtils.getMessage(e));
            throw new GuliException(ResultCodeEnum.PAY_ORDERQUERY_ERROR);
        }
    }
}
