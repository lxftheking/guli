package com.atguigu.guli.service.vod.service;


import com.aliyuncs.exceptions.ClientException;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

public interface VideoService {

    public String uploadVideo(InputStream inputStream, String originalFileName);
    void removeVideo(String videoId) throws ClientException;
    Map<String ,Object> getVideoUploadAuthAndAddress(String title, String filename) throws ClientException;

    Map<String, Object> refreshVideoUploadAuth(String videoId) throws ClientException;

    String getVideoPlayAuth(String videoSourceId) throws ClientException;

    void removeVideoByIdList(List<String> videoSourceIdList) throws ClientException;
}
