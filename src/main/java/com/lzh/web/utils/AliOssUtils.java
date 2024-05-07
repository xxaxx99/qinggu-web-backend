package com.lzh.web.utils;

import com.alibaba.excel.util.IoUtils;
import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.GetObjectRequest;
import com.aliyun.oss.model.OSSObject;
import com.aliyun.oss.model.PutObjectRequest;
import com.lzh.web.common.ErrorCode;
import com.lzh.web.config.OssClientConfig;
import com.lzh.web.exception.BusinessException;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.InputStream;

/**
 * oss文件上传工具类
 * @author lzh
 */

@Component
public class AliOssUtils {

    // Endpoint以华东1（杭州）为例，其它Region请按实际情况填写。
    private static final String ENDPOINT = OssClientConfig.END_POINT;
    // 从环境变量中获取访问凭证。运行本代码示例之前，请确保已设置环境变量OSS_ACCESS_KEY_ID和OSS_ACCESS_KEY_SECRET。
    private static final String ACCESS_KEY_ID = OssClientConfig.ACCESS_KEY_ID;
    private static final String ACCESS_KEY_SECRET = OssClientConfig.ACCESS_KEY_SECRET;
    // 填写Bucket名称
    private static final String BUCKET_NAME = OssClientConfig.BUCKET_NAME;

    public static String uploadFile(String objectName, InputStream inputStream) {

        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(ENDPOINT, ACCESS_KEY_ID,ACCESS_KEY_SECRET);
        String url = "";
        try {
            // 创建PutObjectRequest对象。
            PutObjectRequest putObjectRequest = new PutObjectRequest(BUCKET_NAME, objectName,inputStream);

            ossClient.putObject(putObjectRequest);
            // https://partner-bucket-xx.oss-cn-beijing.aliyuncs.com/56817c30-06fd-4496-981b-6b0693de77e1.png
            url = "https://" + BUCKET_NAME + "." + ENDPOINT.substring(ENDPOINT.lastIndexOf("/") + 1) +"/" + objectName;
        } catch (OSSException oe) {
            System.out.println("Caught an OSSException, which means your request made it to OSS, "
                    + "but was rejected with an error response for some reason.");
            System.out.println("Error Message:" + oe.getErrorMessage());
            System.out.println("Error Code:" + oe.getErrorCode());
            System.out.println("Request ID:" + oe.getRequestId());
            System.out.println("Host ID:" + oe.getHostId());
        } catch (ClientException ce) {
            System.out.println("Caught an ClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with OSS, "
                    + "such as not being able to access the network.");
            System.out.println("Error Message:" + ce.getMessage());
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
        return url;
    }

    public static void downloadFile(String filepath, HttpServletResponse response) throws Exception {

        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(ENDPOINT, ACCESS_KEY_ID,ACCESS_KEY_SECRET);
        InputStream ossObjectInput = null;
        try {
            String desiredFilePath = splitPath(filepath);
            GetObjectRequest objectRequest = new GetObjectRequest(BUCKET_NAME, desiredFilePath);
            OSSObject getObjectRequest = ossClient.getObject(objectRequest);
            ossObjectInput = getObjectRequest.getObjectContent();
            // 处理下载到的流
            byte[] bytes = IoUtils.toByteArray(ossObjectInput);
            // 设置响应头
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment; filename=" + desiredFilePath);
            // 写入响应
            response.getOutputStream().write(bytes);
            response.getOutputStream().flush();
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "下载失败");
        } finally {
            if (ossObjectInput != null) {
                ossObjectInput.close();
            }
        }
    }

    public static void downloadToLocal(String filePath,String localFilePath) throws Exception {

        File downloadFile = new File(localFilePath);
        String desiredFilePath = splitPath(filePath);
        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(ENDPOINT, ACCESS_KEY_ID,ACCESS_KEY_SECRET);
        try {
            // 下载Object到本地文件，并保存到指定的本地路径中。如果指定的本地文件存在会覆盖，不存在则新建。
            // 如果未指定本地路径，则下载后的文件默认保存到示例程序所属项目对应本地路径中。
            ossClient.getObject(new GetObjectRequest(BUCKET_NAME, desiredFilePath), downloadFile);
        } catch (OSSException oe) {
            System.out.println("Caught an OSSException, which means your request made it to OSS, "
                    + "but was rejected with an error response for some reason.");
            System.out.println("Error Message:" + oe.getErrorMessage());
            System.out.println("Error Code:" + oe.getErrorCode());
            System.out.println("Request ID:" + oe.getRequestId());
            System.out.println("Host ID:" + oe.getHostId());
        } catch (ClientException ce) {
            System.out.println("Caught an ClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with OSS, "
                    + "such as not being able to access the network.");
            System.out.println("Error Message:" + ce.getMessage());
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
    }

    public static String splitPath (String filePath){
        // 使用 "/" 进行分割
        String[] parts = filePath.split("/");
        // 获取倒数第二个部分和最后一个部分
        String lastPart = parts[parts.length - 1];
        String secondLastPart = parts[parts.length - 2];
        String thirdLastPart = parts[parts.length - 3];
        // 将结果合并
        return thirdLastPart + "/" + secondLastPart + "/" + lastPart;
    }

}
