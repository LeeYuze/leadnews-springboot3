package com.leadnews.wemedia.controller.v1;

import com.leadnews.model.common.dtos.ResponseResult;
import com.leadnews.model.common.enums.AppHttpCodeEnum;
import com.leadnews.model.wemedia.dtos.WmMaterialDTO;
import com.leadnews.wemedia.service.WmMaterialService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
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

    @PostMapping("/list")
    public ResponseResult findList(@RequestBody WmMaterialDTO dto){
        return wmMaterialService.findList(dto);
    }

    @GetMapping("/del_picture/{id}")
    public ResponseResult delete(@PathVariable Long id) {

        wmMaterialService.deleteById(id);

        return ResponseResult.okResult();
    }

    @GetMapping("/cancel_collect/{id}")
    public ResponseResult cancelCollect(@PathVariable Long id) {
        if (id == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }

        wmMaterialService.cancel_collect(id);

        return ResponseResult.okResult();
    }

    @GetMapping("/collect/{id}")
    public ResponseResult collect(@PathVariable Long id) {
        if (id == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }

        wmMaterialService.collect(id);

        return ResponseResult.okResult();
    }
}
