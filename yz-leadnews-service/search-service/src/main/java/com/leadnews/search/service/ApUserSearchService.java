package com.leadnews.search.service;

import com.leadnews.model.common.dtos.ResponseResult;

/**
 * @author lihaohui
 * @date 2023/8/29
 */
public interface ApUserSearchService {

    /**
     * 保存用户搜索历史记录
     * @param keyword
     * @param userId
     */
    void insert(String keyword,Long userId);

    /**
     查询搜索历史
     @return
     */
    ResponseResult findUserSearch();
}
