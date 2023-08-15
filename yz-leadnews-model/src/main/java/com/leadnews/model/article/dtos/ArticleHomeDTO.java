package com.leadnews.model.article.dtos;

import lombok.Data;

import java.util.Date;

/**
 * @author lihaohui
 * @date 2023/8/15
 */
@Data
public class ArticleHomeDTO {

    // 最大时间
    Date maxBehotTime;

    // 最小时间
    Date minBehotTime;

    // 分页size
    Integer size;

    // 频道ID
    String tag;
}
