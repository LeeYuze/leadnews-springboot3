package com.leadnews.model.behavior.dtos;

import lombok.Data;
import org.hibernate.validator.constraints.Range;

@Data
public class LikesBehaviorDTO {
    // 设备ID
    Long equipmentId;

    // 文章、动态、评论等ID
    Long articleId;

    /**
     * 喜欢内容类型
     * 0文章
     * 1动态
     * 2评论
     */
    Short type;

    /**
     * 喜欢操作方式
     * 0 点赞
     * 1 取消点赞
     */
    Short operation;
}