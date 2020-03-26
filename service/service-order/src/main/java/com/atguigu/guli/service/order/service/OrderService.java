package com.atguigu.guli.service.order.service;

import com.atguigu.guli.service.order.entity.Order;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
 * <p>
 * 订单 服务类
 * </p>
 *
 * @author saber
 * @since 2020-03-07
 */
public interface OrderService extends IService<Order> {
    String saveOrder(String courseId, String memberId);
    Boolean isBuyByCourseId(String memberId, String courseId);
    Order getByOrderId(String orderId,String memberId);
    /**
     * 根据订单号查询订单
     * @param orderNo
     * @return
     */
    Order getOrderByOrderNo(String orderNo);

    /**
     *	更改订单状态
     * @param map
     */
    void updateOrderStatus(Map<String, String> map);
}
