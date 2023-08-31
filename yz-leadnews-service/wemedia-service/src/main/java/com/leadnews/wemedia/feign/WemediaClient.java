package com.leadnews.wemedia.feign;

/**
 * @author lihaohui
 * @date 2023/8/30
 */

import com.leadnews.apis.wemedia.IWemediaClient;
import com.leadnews.model.common.dtos.ResponseResult;
import com.leadnews.wemedia.service.WmChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class WemediaClient implements IWemediaClient {


    private final WmChannelService wmChannelService;

    @GetMapping("/api/v1/channel/list")
    @Override
    public ResponseResult getChannels() {
        return ResponseResult.okResult(wmChannelService.findAll());
    }
}