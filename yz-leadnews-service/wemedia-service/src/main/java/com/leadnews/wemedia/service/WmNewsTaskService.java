package com.leadnews.wemedia.service;

import java.util.Date;

/**
 * @author lihaohui
 * @date 2023/8/26
 */
public interface WmNewsTaskService {

    /**
     * 添加任务到延迟队列中
     * @param id  文章的id
     * @param publishTime  发布的时间  可以做为任务的执行时间
     */
    void addNewsToTask(Integer id, Date publishTime);


    /**
     * 消费延迟队列数据
     */
    void scanNewsByTask();
}