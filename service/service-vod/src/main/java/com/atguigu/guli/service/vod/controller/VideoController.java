package com.atguigu.guli.service.vod.controller;

import com.aliyuncs.exceptions.ClientException;
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

import java.util.List;
import java.util.Map;

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
        try {

            System.out.println("111");
        } catch (Exception e) {
            log.error(ExceptionUtils.getMessage(e));
            throw new GuliException(ResultCodeEnum.VIDEO_UPLOAD_TOMCAT_ERROR);
        }
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

    @DeleteMapping("remove")
    public R removeVideoByIdList(
            @ApiParam(name="videoSourceId", value = "阿里云视频文件的id", required = true)
            @RequestBody List<String> videoSourceIdList){

        try {
            videoService.removeVideoByIdList(videoSourceIdList);
            return  R.ok().message("视频删除成功");
        } catch (Exception e) {
            log.error(ExceptionUtils.getMessage(e));
            throw new GuliException(ResultCodeEnum.VIDEO_DELETE_ALIYUN_ERROR);
        }
    }

    /**
     * 该接口不会真正上传视频文件，您需要获得 上传地址和凭证 后，使用上传SDK进行文件上传。
     * @param title
     * @param fileName
     * @return
     */
    @GetMapping("get-upload-auth-and-address/{title}/{fileName}")
    public R getVideoUploadAuthAndAddress(
            @ApiParam(name="title", value = "视频文件标题", required = true)
            @PathVariable String title,

            @ApiParam(name="fileName", value = "视频文件名称", required = true)
            @PathVariable String fileName){
        try {
            Map<String, Object> map = videoService.getVideoUploadAuthAndAddress(title, fileName);
            return  R.ok().data(map).message("获取视频上传地址和凭证成功");
        } catch (ClientException e) {
            log.error(ExceptionUtils.getMessage(e));
            throw new GuliException(ResultCodeEnum.FETCH_VIDEO_UPLOADAUTH_ERROR);
        }
    }

    /**
     * 刷新视频上传地址和凭证
     *
     * @param videoId
     * @return
     */
    @GetMapping("refresh-upload-auth/{videoId}")
    public R refreshVideoUploadAuth(
            @ApiParam(name = "videoId", value = "云视频id", required = true)
            @PathVariable String videoId) {

        try {
            Map<String, Object> map = videoService.refreshVideoUploadAuth(videoId);
            return R.ok().data(map).message("刷新视频上传地址和凭证成功");
        } catch (ClientException e) {
            log.error(ExceptionUtils.getMessage(e));
            throw new GuliException(ResultCodeEnum.REFRESH_VIDEO_UPLOADAUTH_ERROR);
        }
    }

}
