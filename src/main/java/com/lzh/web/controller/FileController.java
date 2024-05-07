package com.lzh.web.controller;

import cn.hutool.core.io.FileUtil;
import com.lzh.web.common.BaseResponse;
import com.lzh.web.common.ErrorCode;
import com.lzh.web.common.ResultUtils;
import com.lzh.web.exception.BusinessException;
import com.lzh.web.model.entity.User;
import com.lzh.web.model.enums.FileUploadBizEnum;
import com.lzh.web.service.UserService;
import com.lzh.web.utils.AliOssUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;

/**
 * 文件接口
 * @author lzh
 */
@RestController
@RequestMapping("/file")
@Slf4j
public class FileController {

    @Resource
    private UserService userService;

    /**
     * 文件上传
     * @param multipartFile 文件
     * @param biz 业务类型
     * @param request HttpServletRequest
     * @return url
     */
    @PostMapping("/upload")
    public BaseResponse<String> uploadFile(@RequestPart("file") MultipartFile multipartFile,
                                           String biz, HttpServletRequest request) {
        FileUploadBizEnum fileUploadBizEnum = FileUploadBizEnum.getEnumByValue(biz);
        if (fileUploadBizEnum == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        validFile(multipartFile, fileUploadBizEnum);
        User loginUser = userService.getLoginUser(request);
        // 文件目录：根据业务、用户来划分
        String uuid = RandomStringUtils.randomAlphanumeric(8);
        String filename = uuid + "-" + multipartFile.getOriginalFilename();
        String filepath = String.format("%s/%s/%s", fileUploadBizEnum.getValue(), loginUser.getId(), filename);
        try {
            // 上传文件
            String url = AliOssUtils.uploadFile(filepath, multipartFile.getInputStream());
            // 返回可访问地址
            if (fileUploadBizEnum == FileUploadBizEnum.USER_AVATAR) {
                loginUser.setUserAvatar(url);
                userService.updateById(loginUser);
            }
            return ResultUtils.success(url);
        } catch (Exception e) {
            log.error("file upload error, filepath = " + filepath, e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "上传失败");
        }
    }

    /**
     * 测试文件下载
     *
     * @param filepath
     * @param response
     * @return
     */
    @GetMapping("/download")
    public void DownloadFile(String filepath, HttpServletResponse response) throws Exception {
        AliOssUtils.downloadFile(filepath,response);
    }

    /**
     * 校验文件
     * @param multipartFile 文件
     * @param fileUploadBizEnum 业务类型
     */
    private void validFile(MultipartFile multipartFile, FileUploadBizEnum fileUploadBizEnum) {
        // 文件大小
        long fileSize = multipartFile.getSize();
        // 文件后缀
        String fileSuffix = FileUtil.getSuffix(multipartFile.getOriginalFilename());
        final long ONE_M = 1024 * 1024L;
        if (FileUploadBizEnum.USER_AVATAR.equals(fileUploadBizEnum)) {
            if (fileSize > ONE_M) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "文件大小不能超过 1M");
            }
            if (!Arrays.asList("jpeg", "jpg", "svg", "png", "webp").contains(fileSuffix)) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "文件类型错误");
            }
        }
    }
}
