package com.atguigu.guli.service.vod.controller;

import com.atguigu.guli.common.base.result.R;
import com.atguigu.guli.common.base.result.ResultCodeEnum;
import com.atguigu.guli.common.base.util.ExceptionUtils;
import com.atguigu.guli.service.base.exception.GuliException;
import com.atguigu.guli.service.vod.service.VideoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Api(description="阿里云视频点播")
@CrossOrigin //跨域
@RestController
@RequestMapping("/admin/vod/video")
@Slf4j
public class VideoController {

    @Autowired
    private VideoService videoService;

    @GetMapping
    public void test(){
        System.out.println("111");
    }


    @ApiOperation(value = "上传视频")
    @PostMapping("upload")
    public R uploadVideo(@RequestParam("file") MultipartFile file) {

        try{
            String videoId = videoService.uploadVideo(file.getInputStream(), file.getOriginalFilename());

            return   R.ok().message("上传成功").data("videoId",videoId);
        }catch (Exception e){
            log.error(ExceptionUtils.getMessage(e));
            throw new GuliException(ResultCodeEnum.VIDEO_UPLOAD_TOMCAT_ERROR);
        }
    }


    @DeleteMapping("remove/{vodId}")
    public R removeVideo(
            @ApiParam(name="vodId", value="阿里云视频id", required = true)
            @PathVariable String vodId){

        try {
            videoService.removeVideo(vodId);
            return R.ok().message("视频删除成功");
        } catch (Exception e) {
            log.error(ExceptionUtils.getMessage(e));
            throw new GuliException(ResultCodeEnum.VIDEO_DELETE_ALIYUN_ERROR);
        }
    }

}
