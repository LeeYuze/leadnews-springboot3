package com.leadnews.article.job;

import com.leadnews.article.service.HotArticleService;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author lihaohui
 * @date 2023/8/30
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class ComputeHotArticleJob {

    private final HotArticleService hotArticleService;

    @XxlJob("computeHotArticleJob")
    public void handle() {
        log.info("热文章分值计算调度任务开始执行...");
        hotArticleService.computeHotArticle();
        log.info("热文章分值计算调度任务结束...");

    }
}