package com.leadnews.wemedia.service;

/**
 * @author lihaohui
 * @date 2023/8/21
 */
public interface WmNewsAutoScanService {
    /**
     * 自媒体文章审核
     * @param id  自媒体文章id
     */
    void autoScanWmNews(Integer id);
}
