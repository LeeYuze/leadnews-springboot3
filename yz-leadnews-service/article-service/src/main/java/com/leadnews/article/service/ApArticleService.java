package com.leadnews.article.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.leadnews.model.article.dtos.ArticleDTO;
import com.leadnews.model.article.dtos.ArticleHomeDTO;
import com.leadnews.model.article.pojos.ApArticle;
import com.leadnews.model.common.dtos.ResponseResult;

/**
 * @author lihaohui
 * @date 2023/8/15
 */
public interface ApArticleService extends IService<ApArticle> {
    /**
     * 根据参数加载文章列表
     * @param loadtype 1为加载更多  2为加载最新
     * @param dto
     * @return
     */
    ResponseResult load(Integer loadtype, ArticleHomeDTO dto);

    /**
     * 保存app端相关文章
     * @param dto
     * @return
     */
    ResponseResult saveArticle(ArticleDTO dto) ;
}
