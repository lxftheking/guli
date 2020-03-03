package com.atguigu.guli.service.vod.controller.api;


import com.aliyuncs.exceptions.ClientException;
import com.atguigu.guli.common.base.result.R;
import com.atguigu.guli.common.base.result.ResultCodeEnum;
import com.atguigu.guli.common.base.util.ExceptionUtils;
import com.atguigu.guli.service.base.exception.GuliException;
import com.atguigu.guli.service.vod.service.VideoService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 讲师 前端控制器
 * </p>
 *
 * @author Helen
 * @since 2020-02-12
 */
@RestController
@CrossOrigin
@Api(description="课程")
@Slf4j
@RequestMapping("/api/vod/video")
public class ApiVideoController {


    @Autowired
    private VideoService videoService;

   @GetMapping("get-play-auth/{videoSourceId}")
    public R  getPlayAuthByVideoSourceId(@PathVariable("videoSourceId") String videoSourceId){

       try {
           String playAuth= videoService.getVideoPlayAuth(videoSourceId);
           return  R.ok().data("playAuth",playAuth);
       } catch (ClientException e) {
          log.error(ExceptionUtils.getMessage(e));
          throw new GuliException(ResultCodeEnum.FETCH_PLAYAUTH_ERROR);
       }
   }




}

