package com.leadnews.apis.wemedia;

import com.leadnews.model.common.dtos.ResponseResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author lihaohui
 * @date 2023/8/30
 */
@FeignClient("leadnews-wemedia")
public interface IWemediaClient {

    @GetMapping("/api/v1/channel/list")
    ResponseResult getChannels();
}