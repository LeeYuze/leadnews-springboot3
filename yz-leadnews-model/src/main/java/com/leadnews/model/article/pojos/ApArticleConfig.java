package com.leadnews.model.article.pojos;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * APP已发布文章配置表
 * @TableName ap_article_config
 */
@TableName(value ="ap_article_config")
@Data
public class ApArticleConfig implements Serializable {

    public ApArticleConfig(Long articleId){
        this.articleId = articleId;
        this.isComment = true;
        this.isForward = true;
        this.isDelete = false;
        this.isDown = false;
    }

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 文章ID
     */
    @TableField(value = "article_id")
    private Long articleId;

    /**
     * 是否可评论
     */
    @TableField(value = "is_comment")
    private Boolean isComment;

    /**
     * 是否转发
     */
    @TableField(value = "is_forward")
    private Boolean isForward;

    /**
     * 是否下架
     */
    @TableField(value = "is_down")
    private Boolean isDown;

    /**
     * 是否已删除
     */
    @TableField(value = "is_delete")
    private Boolean isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}