package com.leadnews.behavior.service;

import com.leadnews.model.behavior.dtos.LikesBehaviorDTO;
import com.leadnews.model.common.dtos.ResponseResult;

public interface ApLikesBehaviorService {
    /**
     * 点赞或取消点赞
     * @param dto
     * @return
     */
    public ResponseResult like(LikesBehaviorDTO dto);
}
