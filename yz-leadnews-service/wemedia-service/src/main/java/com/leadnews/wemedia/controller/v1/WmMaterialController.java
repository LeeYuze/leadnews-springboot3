package com.leadnews.wemedia.controller.v1;

import com.leadnews.model.common.dtos.ResponseResult;
import com.leadnews.wemedia.service.WmMaterialService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author lihaohui
 * @date 2023/8/17
 */
@RestController
@RequestMapping("/api/v1/material")
@RequiredArgsConstructor
public class WmMaterialController {

    private final WmMaterialService wmMaterialService;

    @PostMapping("/upload_picture")
    public ResponseResult uploadPicture(MultipartFile multipartFile) {
        return wmMaterialService.uploadPicture(multipartFile);
    }
}
