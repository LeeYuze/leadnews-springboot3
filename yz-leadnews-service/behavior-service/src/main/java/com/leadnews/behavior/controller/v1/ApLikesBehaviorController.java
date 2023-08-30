package com.leadnews.behavior.controller.v1;


import com.leadnews.behavior.service.ApLikesBehaviorService;
import com.leadnews.model.behavior.dtos.LikesBehaviorDTO;
import com.leadnews.model.common.dtos.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/likes_behavior")
public class ApLikesBehaviorController {

    @Autowired
    private ApLikesBehaviorService apLikesBehaviorService;

    /**
     * 点赞或取消点赞
     * @param dto
     * @return
     */
    @PostMapping
    public ResponseResult like(@RequestBody @Validated LikesBehaviorDTO dto) {
        return apLikesBehaviorService.like(dto);
    }
}
