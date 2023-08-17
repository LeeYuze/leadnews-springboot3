package com.leadnews.model.wemedia.pojos;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 自媒体图文引用素材信息表
 * @TableName wm_news_material
 */
@TableName(value ="wm_news_material")
@Data
public class WmNewsMaterial implements Serializable {
    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Object id;

    /**
     * 素材ID
     */
    @TableField(value = "material_id")
    private Object materialId;

    /**
     * 图文ID
     */
    @TableField(value = "news_id")
    private Object newsId;

    /**
     * 引用类型
            0 内容引用
            1 主图引用
     */
    @TableField(value = "type")
    private Integer type;

    /**
     * 引用排序
     */
    @TableField(value = "ord")
    private Integer ord;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}