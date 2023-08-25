package com.leadnews.article.feign;

import com.leadnews.apis.article.IArticleClient;
import com.leadnews.article.service.ApArticleService;
import com.leadnews.model.article.dtos.ArticleDTO;
import com.leadnews.model.common.dtos.ResponseResult;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author lihaohui
 * @date 2023/8/21
 */
@RestController
@RequiredArgsConstructor
public class ArticleClient implements IArticleClient {

    private final ApArticleService apArticleService;

    @Override
    @PostMapping("/api/v1/article/save")
    public ResponseResult saveArticle(@RequestBody ArticleDTO dto) {
        return apArticleService.saveArticle(dto);
    }
}
