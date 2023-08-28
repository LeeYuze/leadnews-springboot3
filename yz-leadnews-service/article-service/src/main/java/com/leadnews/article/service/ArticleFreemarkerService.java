package com.leadnews.article.service;

/**
 * @author lihaohui
 * @date 2023/8/29
 */
public interface ArticleFreemarkerService {

    void buildArticleToMinIO(Long articleId, String content);
}
