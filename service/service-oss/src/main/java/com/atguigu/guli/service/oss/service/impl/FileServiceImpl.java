package com.atguigu.guli.service.oss.service.impl;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.atguigu.guli.service.oss.service.FileService;
import com.atguigu.guli.service.oss.util.OssProperties;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.UUID;

/**
 * @author helen
 * @since 2020/2/19
 */
@Service
public class FileServiceImpl implements FileService {

    @Autowired
    private OssProperties ossProperties;

    /**
     *
     * @param inputStream
     * @param module 所属模块
     * @param filename 原始文件名
     * @return 文件的url地址
     */
    @Override
    public String upload(InputStream inputStream, String module, String filename) {

        String endpoint = ossProperties.getEndpoint();
        String keyid = ossProperties.getKeyid();
        String keysecret = ossProperties.getKeysecret();
        String bucketname = ossProperties.getBucketname();
        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, keyid, keysecret);

        //文件名
        String newFileName = UUID.randomUUID().toString();
        String fileExtention = filename.substring(filename.lastIndexOf("."));

        //上传文件
        //yourObjectName ： bucket下的  [路径 + 文件名]
        String dateFolder = new DateTime().toString("yyyy/MM/dd");// yyyy/MM/dd
//        String yourObjectName = module + "/" + dateFolder + "/" +  newFileName + fileExtention;
        String yourObjectName = new StringBuffer()
                .append(module)
                .append("/")
                .append(dateFolder)
                .append("/")
                .append(newFileName)
                .append(fileExtention)
                .toString();
        ossClient.putObject(bucketname, yourObjectName, inputStream);

        // 关闭OSSClient。
        ossClient.shutdown();

        //图片的url
        //https://inline-teach-file-190805.oss-cn-beijing.aliyuncs.com/avatar/default.jpg
        return new StringBuffer().append("https://")
                .append(bucketname)
                .append(".")
                .append(endpoint)
                .append("/")
                .append(yourObjectName)
                .toString();
    }

    @Override
    public void removeFile(String url) {
        String endpoint = ossProperties.getEndpoint();
        String keyid = ossProperties.getKeyid();
        String keysecret = ossProperties.getKeysecret();
        String bucketname = ossProperties.getBucketname();

        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, keyid, keysecret);

        //url：
        //https://inline-teach-file-190805.oss-cn-beijing.aliyuncs.com/avatar/2020/02/19/09eea761-032b-48f0-8541-dcb9ad2a53f1.png
        //yourObjectName：avatar/2020/02/19/09eea761-032b-48f0-8541-dcb9ad2a53f1.png
        String host = "https://" + bucketname + "." + endpoint + "/";
        String objectName = url.substring(host.length());

        // 删除文件。如需删除文件夹，请将ObjectName设置为对应的文件夹名称。如果文件夹非空，则需要将文件夹下的所有object删除后才能删除该文件夹。
        ossClient.deleteObject(bucketname, objectName);

        // 关闭OSSClient。
        ossClient.shutdown();
    }
}
