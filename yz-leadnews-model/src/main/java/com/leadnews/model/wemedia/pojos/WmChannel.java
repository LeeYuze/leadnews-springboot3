package com.leadnews.model.wemedia.pojos;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 频道信息表
 * @TableName wm_channel
 */
@TableName(value ="wm_channel")
@Data
public class WmChannel implements Serializable {
    /**
     * 
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Object id;

    /**
     * 频道名称
     */
    @TableField(value = "name")
    private String name;

    /**
     * 频道描述
     */
    @TableField(value = "description")
    private String description;

    /**
     * 是否默认频道
     */
    @TableField(value = "is_default")
    private Integer isDefault;

    /**
     * 
     */
    @TableField(value = "status")
    private Integer status;

    /**
     * 默认排序
     */
    @TableField(value = "ord")
    private Integer ord;

    /**
     * 创建时间
     */
    @TableField(value = "created_time")
    private Date createdTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}