package com.leadnews.model.article.dtos;

import com.leadnews.model.article.pojos.ApArticle;
import lombok.Data;

/**
 * @author lihaohui
 * @date 2023/8/21
 */
@Data
public class ArticleDTO extends ApArticle {
    /**
     * 文章内容
     */
    private String content;
}
