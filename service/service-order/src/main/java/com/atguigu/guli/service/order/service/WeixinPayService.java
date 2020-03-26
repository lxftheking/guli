package com.atguigu.guli.service.order.service;


import java.util.Map;

public interface WeixinPayService {
    /**
     * 根据订单号下单，生成支付链接
     * @param orderNo
     * @return
     */
    Map<String, Object> createNative(String orderNo, String remoteAddr);
    /**
     * 根据订单号去微信服务器查询支付状态
     * @param orderNo
     * @return
     */
    Map<String, String> queryPayStatus(String orderNo);
}
