package com.leadnews.model.wemedia.pojos;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 自媒体图文内容信息表
 * @TableName wm_news
 */
@TableName(value ="wm_news")
@Data
public class WmNews implements Serializable {
    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 自媒体用户ID
     */
    @TableField(value = "user_id")
    private Object userId;

    /**
     * 标题
     */
    @TableField(value = "title")
    private String title;

    /**
     * 图文内容
     */
    @TableField(value = "content")
    private String content;

    /**
     * 文章布局
            0 无图文章
            1 单图文章
            3 多图文章
     */
    @TableField(value = "type")
    private Integer type;

    /**
     * 图文频道ID
     */
    @TableField(value = "channel_id")
    private Object channelId;

    /**
     * 
     */
    @TableField(value = "labels")
    private String labels;

    /**
     * 创建时间
     */
    @TableField(value = "created_time")
    private Date createdTime;

    /**
     * 提交时间
     */
    @TableField(value = "submited_time")
    private Date submitedTime;

    /**
     * 当前状态
            0 草稿
            1 提交（待审核）
            2 审核失败
            3 人工审核
            4 人工审核通过
            8 审核通过（待发布）
            9 已发布
     */
    @TableField(value = "status")
    private Integer status;

    /**
     * 定时发布时间，不定时则为空
     */
    @TableField(value = "publish_time")
    private Date publishTime;

    /**
     * 拒绝理由
     */
    @TableField(value = "reason")
    private String reason;

    /**
     * 发布库文章ID
     */
    @TableField(value = "article_id")
    private Long articleId;

    /**
     * //图片用逗号分隔
     */
    @TableField(value = "images")
    private String images;

    /**
     * 
     */
    @TableField(value = "enable")
    private Integer enable;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}