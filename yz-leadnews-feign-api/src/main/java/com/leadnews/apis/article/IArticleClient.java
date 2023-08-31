package com.leadnews.apis.article;

import com.leadnews.apis.article.fallback.IArticleFallback;
import com.leadnews.model.article.dtos.ArticleDTO;
import com.leadnews.model.common.dtos.ResponseResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author lihaohui
 * @date 2023/8/21
 */
@FeignClient(value = "leadnews-article",fallback = IArticleFallback.class)
public interface IArticleClient {

    @PostMapping("/api/v1/article/save")
    ResponseResult saveArticle(@RequestBody ArticleDTO dto) ;
}