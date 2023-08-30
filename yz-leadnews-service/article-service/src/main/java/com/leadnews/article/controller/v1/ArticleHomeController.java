package com.leadnews.article.controller.v1;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.leadnews.article.service.ApArticleService;
import com.leadnews.common.article.ArticleConstants;
import com.leadnews.model.article.dtos.ArticleHomeDTO;
import com.leadnews.model.article.vos.HotArticleVO;
import com.leadnews.model.common.dtos.ResponseResult;
import com.leadnews.redis.utils.CacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author lihaohui
 * @date 2023/8/15
 */
@RestController
@RequestMapping("/api/v1/article")
@RequiredArgsConstructor
public class ArticleHomeController {

    private final ApArticleService apArticleService;

    @PostMapping("/load")
    public ResponseResult load(@RequestBody ArticleHomeDTO dto) {
        return apArticleService.loadByHot(ArticleConstants.LOADTYPE_LOAD_MORE, dto, true);
    }

    @PostMapping("/loadmore")
    public ResponseResult loadMore(@RequestBody ArticleHomeDTO dto) {
        return apArticleService.load(ArticleConstants.LOADTYPE_LOAD_MORE, dto);
    }

    @PostMapping("/loadnew")
    public ResponseResult loadNew(@RequestBody ArticleHomeDTO dto) {
        return apArticleService.load(ArticleConstants.LOADTYPE_LOAD_NEW, dto);
    }
}
