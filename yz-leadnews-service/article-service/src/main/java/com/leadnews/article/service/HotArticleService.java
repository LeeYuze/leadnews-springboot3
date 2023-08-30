package com.leadnews.article.service;

import com.leadnews.model.article.pojos.ApArticle;

/**
 * @author lihaohui
 * @date 2023/8/30
 */
public interface HotArticleService {

    /**
     * 计算热点文章
     */
    void computeHotArticle();

    /**
     * 计算文章的具体分值
     *
     * @param apArticle
     * @return
     */
    Integer computeScore(ApArticle apArticle);
}
