package com.changgou.goods.controller;

import com.changgou.file.FastDFSFile;
import com.changgou.util.FastDFSUtil;
import entity.Result;
import entity.StatusCode;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author chx
 * @version 1.0
 * @description: TODO
 * @date 2020/10/8 0008 15:37
 */
@RestController
@RequestMapping(value = "/upload")
@CrossOrigin
public class FileUploadController {
    /**
     * 文件上传
     */
    @PostMapping
    public Result upload(@RequestParam(value = "file")MultipartFile file)throws Exception{
        FastDFSFile fastDFSFile = new FastDFSFile(
                file.getOriginalFilename(),
                file.getBytes(),
                StringUtils.getFilenameExtension(file.getOriginalFilename())
        );
        String[] uploads = FastDFSUtil.upload(fastDFSFile);
        String url = FastDFSUtil.getTrackerInfo()+"/"+uploads[0]+"/"+uploads[1];
        return  new Result(true, StatusCode.OK,"上传成功!",url);
    }


}