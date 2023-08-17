package com.leadnews.wemedia.controller.v1;

import com.leadnews.model.common.dtos.ResponseResult;
import com.leadnews.model.wemedia.dtos.WmNewsDTO;
import com.leadnews.model.wemedia.dtos.WmNewsPageReqDTO;
import com.leadnews.wemedia.service.WmNewsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author lihaohui
 * @date 2023/8/17
 */
@RestController
@RequestMapping("/api/v1/news")
@RequiredArgsConstructor
public class WmNewsController {

    private final WmNewsService wmNewsService;

    @PostMapping("/list")
    public ResponseResult findAll(@RequestBody WmNewsPageReqDTO dto) {

        return wmNewsService.findAll(dto);
    }

    @PostMapping("/submit")
    public ResponseResult submitNews(@RequestBody WmNewsDTO dto){
        return wmNewsService.submitNews(dto);
    }

}