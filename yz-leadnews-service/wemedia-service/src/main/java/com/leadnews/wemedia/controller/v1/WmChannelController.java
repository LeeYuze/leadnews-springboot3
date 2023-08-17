package com.leadnews.wemedia.controller.v1;

import com.leadnews.model.common.dtos.ResponseResult;
import com.leadnews.model.wemedia.pojos.WmChannel;
import com.leadnews.wemedia.service.WmChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author lihaohui
 * @date 2023/8/17
 */
@RestController
@RequestMapping("/api/v1/channel")
@RequiredArgsConstructor
public class WmChannelController {

    private final WmChannelService wmChannelService;

    @GetMapping("/channels")
    public ResponseResult findAll(){

        List<WmChannel> wmChannels = wmChannelService.findAll();

        return ResponseResult.okResult(wmChannels);
    }
}
