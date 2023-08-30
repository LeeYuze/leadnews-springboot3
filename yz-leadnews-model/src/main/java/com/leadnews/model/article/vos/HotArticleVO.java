package com.leadnews.model.article.vos;

import com.leadnews.model.article.pojos.ApArticle;
import lombok.Data;

/**
 * @author lihaohui
 * @date 2023/8/30
 */
@Data
public class HotArticleVO extends ApArticle {

    private Integer score;
}
