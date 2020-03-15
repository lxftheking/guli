package com.atguigu.guli.service.order.client;

import com.atguigu.guli.service.base.dto.MemberDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Component
@FeignClient(value = "guli-ucenter")
public interface MemberClient {

    /**
     * 根据会员id查询会员信息
     * @param memberId
     * @return
     */
    @GetMapping(value = "/api/ucenter/member/inner/get-member-dto/{memberId}")
    MemberDto getMemberDtoByMemberId(@PathVariable(value = "memberId") String memberId);
}