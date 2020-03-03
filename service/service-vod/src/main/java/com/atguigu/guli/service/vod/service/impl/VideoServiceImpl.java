package com.atguigu.guli.service.vod.service.impl;


import com.aliyun.vod.upload.impl.UploadVideoImpl;
import com.aliyun.vod.upload.req.UploadStreamRequest;
import com.aliyun.vod.upload.resp.UploadStreamResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.vod.model.v20170321.*;
import com.atguigu.guli.common.base.result.ResultCodeEnum;
import com.atguigu.guli.service.base.exception.GuliException;
import com.atguigu.guli.service.vod.service.VideoService;
import com.atguigu.guli.service.vod.utils.AliyunVodSDKUtils;
import com.atguigu.guli.service.vod.utils.VideoProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class VideoServiceImpl implements VideoService {

    @Autowired
    private VideoProperties properties;


    @Override
    public String uploadVideo(InputStream inputStream, String originalFileName) {

        String accessKeyId=properties.getKeyid();


        String accessKeySecret=properties.getKeysecret();

        String title = originalFileName.substring(0, originalFileName.lastIndexOf("."));

        UploadStreamRequest request = new UploadStreamRequest(accessKeyId, accessKeySecret, title, originalFileName, inputStream);

        UploadVideoImpl uploader = new UploadVideoImpl();
        UploadStreamResponse response = uploader.uploadStream(request);
        System.out.print("RequestId=" + response.getRequestId() + "\n");  //请求视频点播服务的请求ID
        if (StringUtils.isEmpty(response.getVideoId())) {
        //如果设置回调URL无效，不影响视频上传，可以返回VideoId同时会返回错误码。其他情况上传失败时，VideoId为空，此时需要根据返回错误码分析具体错误原因
            log.error("文件上传出错"+response.getCode()+"\t"+response.getMessage());
           throw new GuliException(ResultCodeEnum.VIDEO_UPLOAD_ALIYUN_ERROR);
        }
        return  response.getVideoId();
    }

    @Override
    public void removeVideo(String videoId) throws ClientException {

        DefaultAcsClient client = AliyunVodSDKUtils.initVodClient(
                properties.getKeyid(),
                properties.getKeysecret());

        DeleteVideoRequest request = new DeleteVideoRequest();
        request.setVideoIds(videoId);
        /*DeleteVideoResponse response = */

            client.getAcsResponse(request);

    }

    @Override
    public Map<String, Object> getVideoUploadAuthAndAddress(String title, String filename) throws ClientException {

        DefaultAcsClient client = AliyunVodSDKUtils.initVodClient(
                properties.getKeyid(),
                properties.getKeysecret());

        CreateUploadVideoRequest request = new CreateUploadVideoRequest();
        request.setTitle(title);
        request.setFileName(filename);
        /*DeleteVideoResponse response = */

        CreateUploadVideoResponse response = client.getAcsResponse(request);
        Map<String, Object> map = new HashMap<>();
        map.put("videoId",response.getVideoId());
        map.put("uploadAuth",response.getUploadAuth());
        map.put("uploadAddress",response.getUploadAddress());


        return map;
    }

    @Override
    public Map<String, Object> refreshVideoUploadAuth(String videoId) throws ClientException {

        DefaultAcsClient client = AliyunVodSDKUtils.initVodClient(
                properties.getKeyid(),
                properties.getKeysecret());

        RefreshUploadVideoRequest request = new RefreshUploadVideoRequest();
        request.setVideoId("VideoId");
        RefreshUploadVideoResponse response = client.getAcsResponse(request);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("videoId", response.getVideoId());
        map.put("uploadAddress", response.getUploadAddress());
        map.put("uploadAuth", response.getUploadAuth());

        return map;
    }

    @Override
    public String getVideoPlayAuth(String videoSourceId) throws ClientException {
        //初始化client对象
        DefaultAcsClient client = AliyunVodSDKUtils.initVodClient(
                properties.getKeyid(),
                properties.getKeysecret());

        //创建请求对象
        GetVideoPlayAuthRequest request = new GetVideoPlayAuthRequest ();
        request.setVideoId(videoSourceId);

        //获取响应
        GetVideoPlayAuthResponse response = client.getAcsResponse(request);

        return response.getPlayAuth();
    }

    @Override
    public void removeVideoByIdList(List<String> videoSourceIdList) throws ClientException {

        //初始化client对象
        DefaultAcsClient client = AliyunVodSDKUtils.initVodClient(
                properties.getKeyid(),
                properties.getKeysecret());

        //创建请求对象
        DeleteVideoRequest request = new DeleteVideoRequest();
        int size = videoSourceIdList.size();
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i <size ; i++) {
            buffer.append(videoSourceIdList.get(i));
            if(i==size-1||i%20==19){
                request.setVideoIds(buffer.toString());
                DeleteVideoResponse response = client.getAcsResponse(request);
                buffer=new StringBuffer();
            }else if(i%20<19){
                buffer.append(",");
            }
        }

    }
}
