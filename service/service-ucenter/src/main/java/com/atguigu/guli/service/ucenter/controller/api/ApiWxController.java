package com.atguigu.guli.service.ucenter.controller.api;

import com.atguigu.guli.common.base.result.ResultCodeEnum;
import com.atguigu.guli.common.base.util.ExceptionUtils;
import com.atguigu.guli.common.base.util.JwtUtils;
import com.atguigu.guli.service.base.exception.GuliException;
import com.atguigu.guli.service.ucenter.entity.Member;
import com.atguigu.guli.service.ucenter.service.MemberService;
import com.atguigu.guli.service.ucenter.utils.HttpClientUtils;
import com.atguigu.guli.service.ucenter.utils.UcenterProperties;
import com.google.gson.Gson;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Api
@Slf4j
@CrossOrigin
@Controller
@RequestMapping("/api/ucenter/wx")
public class ApiWxController {

    @Autowired
    private UcenterProperties properties;

    @Autowired
    private MemberService memberService;

    @GetMapping("/login")
    public String genQrConnect(HttpSession session){

        String baseUrl = "https://open.weixin.qq.com/connect/qrconnect" +
                "?appid=%s" +
                "&redirect_uri=%s" +
                "&response_type=code" +
                "&scope=snsapi_login" +
                "&state=%s" +
                "#wechat_redirect";

        //处理回调url
        String redirecturi = "";

        try {
            redirecturi = URLEncoder.encode(properties.getRedirecturi(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            log.error(ExceptionUtils.getMessage(e));
            throw new GuliException(ResultCodeEnum.URL_ENCODE_ERROR);
        }

        String state = UUID.randomUUID().toString();
        session.setAttribute("wx-open-state",state);
        String qrcodeUrl = String.format(
                baseUrl,
                properties.getAppid(),
                redirecturi,
                state);

        return  "redirect:"+qrcodeUrl;
    }



    @GetMapping("/callback")
    public String callback(String code,String state,HttpSession session) {

        if (StringUtils.isEmpty(code) || StringUtils.isEmpty(state)) {
            log.error("非法参数");
            throw new GuliException(ResultCodeEnum.ILLEGAL_CALLBACK_REQUEST_ERROR);
        }

        String sessionState = (String) session.getAttribute("wx-open-state");

        if (!state.equals(sessionState)) {
            log.error("非法请求回调");
            throw new GuliException(ResultCodeEnum.ILLEGAL_CALLBACK_REQUEST_ERROR);
        }
        //携带授权临时票据code，和appid以及appsecret请求access_token
        String baseAccessTokenUrl = "https://api.weixin.qq.com/sns/oauth2/access_token" +
                "?appid=%s" +
                "&secret=%s" +
                "&code=%s" +
                "&grant_type=authorization_code";
        String accessTokenuUrl = String.format(baseAccessTokenUrl, properties.getAppid(), properties.getAppsecret(), code);

        String result = "";

        try {
            result = HttpClientUtils.get(accessTokenuUrl);

        } catch (Exception e) {
            log.error("获取access_token失败");
            throw new GuliException(ResultCodeEnum.FETCH_ACCESSTOKEN_FAILD);
        }

        Gson gson = new Gson();
        HashMap<String, Object> map = gson.fromJson(result, HashMap.class);
        Object errcodeObj = map.get("errcode");
        if (errcodeObj != null) {  //若出现错误码
            String errmsg = (String) map.get("errmsg");
            Double errcode = (Double) errcodeObj;
            log.error("获取access_token失败 - " + "message: " + errmsg + ", errcode: " + errcode);
            throw new GuliException(ResultCodeEnum.FETCH_ACCESSTOKEN_FAILD);
        }
        //微信获取access_token响应成功
        String accessToken = (String) map.get("access_token");
        String openid = (String) map.get("openid");

        Member member = memberService.getByOpenid(openid);
        if (member == null) {
            //向微信的资源服务器发起请求，获取当前用户的用户信息
            String baseUserInfoUrl = "https://api.weixin.qq.com/sns/userinfo" +
                    "?access_token=%s" +
                    "&openid=%s";
            String userInfoUrl = String.format(
                    baseUserInfoUrl,
                    accessToken,
                    openid);
            String resultUserInfo = null;

            try {
                resultUserInfo = HttpClientUtils.get(userInfoUrl);
            } catch (Exception e) {
                log.error(ExceptionUtils.getMessage(e));
                throw new GuliException(ResultCodeEnum.FETCH_USERINFO_ERROR);
            }
            HashMap<String, Object> resultUserInfoMap = gson.fromJson(resultUserInfo, HashMap.class);
            if (resultUserInfoMap.get("errcode") != null) {
                log.error("获取用户信息失败" + "，message：" + resultUserInfoMap.get("errmsg"));
                throw new GuliException(ResultCodeEnum.FETCH_USERINFO_ERROR);
            }
            String nickname = (String)resultUserInfoMap.get("nickname");
            String headimgurl = (String)resultUserInfoMap.get("headimgurl");
            Double sex = (Double)resultUserInfoMap.get("sex");

            member = new Member();
            member.setOpenid(openid);
            member.setNickname(nickname);
            member.setAvatar(headimgurl);
            member.setSex(sex.intValue());
            memberService.save(member);
        }

        //生成并颁发jwt
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", member.getId());
        claims.put("nickname", member.getNickname());
        claims.put("avatar", member.getAvatar());
        String token = JwtUtils.generateJWT(claims);
        return "redirect:http://localhost:3000?token=" + token;
    }

}
