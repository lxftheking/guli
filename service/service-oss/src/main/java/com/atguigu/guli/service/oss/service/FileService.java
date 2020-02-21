package com.atguigu.guli.service.oss.service;

import java.io.InputStream;

/**
 * @author helen
 * @since 2020/2/19
 */
public interface FileService {
    String upload(InputStream inputStream, String module, String filename);

    void removeFile(String url);
}
