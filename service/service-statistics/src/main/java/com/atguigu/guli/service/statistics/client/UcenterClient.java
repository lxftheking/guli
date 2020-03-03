package com.atguigu.guli.service.statistics.client;

import com.atguigu.guli.common.base.result.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Component
@FeignClient("guli-ucenter")
public interface UcenterClient {
    @GetMapping(value = "/admin/ucenter/member/count-register/{day}")
    public R countRegisterByDay(@PathVariable String day);
}
