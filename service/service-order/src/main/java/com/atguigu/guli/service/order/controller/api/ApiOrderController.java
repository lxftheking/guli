package com.atguigu.guli.service.order.controller.api;

import com.atguigu.guli.common.base.result.R;
import com.atguigu.guli.common.base.util.JwtUtils;
import com.atguigu.guli.service.order.entity.Order;
import com.atguigu.guli.service.order.service.OrderService;
import io.jsonwebtoken.Claims;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;

@Api
@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/api/order")
public class ApiOrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("auth/save/{courseId}")
    public R save(@PathVariable("courseId")String courseId, HttpServletRequest request){


        String token = request.getHeader("token");

        Claims claims = JwtUtils.parseJWT(token);
        String memberId = (String)claims.get("id");

        String orderId = orderService.saveOrder(courseId, memberId);

        return  R.ok().data("orderId",orderId);
    }

    @GetMapping("auth/is-buy/{courseId}")
    public R isBuyCourseId(@PathVariable("courseId")String courseId,HttpServletRequest request){

        String token = request.getHeader("token");
        System.out.println(token);
        Claims claims = JwtUtils.parseJWT(token);
        String memberId = (String)claims.get("id");
        Boolean buyByCourseId = orderService.isBuyByCourseId(memberId, courseId);
        return  R.ok().data("isBuy",buyByCourseId);
    }


    @GetMapping("auth/get/{orderId}")
    public R get(@PathVariable("orderId")String orderId,HttpServletRequest request){

        String token = request.getHeader("token");
        Claims claims = JwtUtils.parseJWT(token);
        String memberId = (String)claims.get("id");
        Order order = orderService.getByOrderId(orderId, memberId);

        return  R.ok().data("item",order);
    }




}
