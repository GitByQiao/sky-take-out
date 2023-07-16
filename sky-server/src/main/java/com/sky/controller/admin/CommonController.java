package com.sky.controller.admin;

import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.result.Result;
import com.sky.utils.AliOssUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RestController
@ResponseBody
@Slf4j
@Api(tags = "通用接口")
@RequestMapping(value = "/admin/common")
public class CommonController {
    @Autowired
    private AliOssUtil aliOssUtil;

    @ApiOperation(value = "文件上传")
    @PostMapping("/upload")
    public Result<String> upload(MultipartFile file) {

        try {
            //log.info("是否有阿里云文件上传工具类对象：{}",aliOssUtil);
            String originalFilename = file.getOriginalFilename();
            String extendName = originalFilename.substring(originalFilename.lastIndexOf("."));
            log.info("文件名字上传：{}", originalFilename);
            UUID uuid = UUID.randomUUID();
            String objectName = uuid.toString() + extendName;
            String uploadPath = aliOssUtil.upload(file.getBytes(), objectName);
            return Result.success(uploadPath);
        } catch (IOException e) {
            log.info("文件上传结果：{}",e.toString());
        }
        return Result.error(MessageConstant.UPLOAD_FAILED);
    }
}
