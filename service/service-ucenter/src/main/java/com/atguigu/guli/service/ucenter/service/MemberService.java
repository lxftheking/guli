package com.atguigu.guli.service.ucenter.service;

import com.atguigu.guli.service.base.dto.MemberDto;
import com.atguigu.guli.service.ucenter.entity.Member;
import com.atguigu.guli.service.ucenter.entity.vo.LoginInfoVo;
import com.atguigu.guli.service.ucenter.entity.vo.LoginVo;
import com.atguigu.guli.service.ucenter.entity.vo.RegisterVo;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 会员表 服务类
 * </p>
 *
 * @author saber
 * @since 2020-03-02
 */
public interface MemberService extends IService<Member> {


    Integer countRegisterByDay(String day);

    /**
     * 用户注册
     * @param registerVo
     */
    void register(RegisterVo registerVo);

    /**
     * 用户登录
     * @param loginVo
     * @return token
     */
    String login(LoginVo loginVo);

    /**
     * 根据token获取会员登录信息
     * @param jwtToken
     * @return 用户登录信息
     */
    LoginInfoVo getLoginInfoByJwtToken(String jwtToken);

    Member getByOpenid(String openid);


    /**
     * 根据会员id获取会员信息
     * @param memberId
     * @return
     */
    MemberDto getMemberDtoByMemberId(String memberId);

}
