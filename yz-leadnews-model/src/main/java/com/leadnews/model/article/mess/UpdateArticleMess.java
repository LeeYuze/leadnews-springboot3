package com.leadnews.model.article.mess;

/**
 * @author lihaohui
 * @date 2023/8/30
 */

import lombok.Data;

@Data
public class UpdateArticleMess {

    /**
     * 修改文章的字段类型
     */
    private UpdateArticleType type;
    /**
     * 文章ID
     */
    private Long articleId;
    /**
     * 修改数据的增量，可为正负
     */
    private Integer add;

    public enum UpdateArticleType {
        COLLECTION, COMMENT, LIKES, VIEWS;
    }
}