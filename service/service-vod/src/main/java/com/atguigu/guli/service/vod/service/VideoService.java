package com.atguigu.guli.service.vod.service;


import com.aliyuncs.exceptions.ClientException;

import java.io.InputStream;

public interface VideoService {

    public String uploadVideo(InputStream inputStream, String originalFileName);
    void removeVideo(String videoId) throws ClientException;
}
