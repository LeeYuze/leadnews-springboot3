package com.leadnews.model.search.pojos;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * APP用户搜索信息表
 * @author itheima
 */
@Data
public class ApUserSearch implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 主键
     */
    private String id;
    /**
     * 行为实体id
     */
    private String entryId;
    /**
     * 搜索词
     */
    private String keyword;
    /**
     * 创建时间
     */
    private Date createdTime;

}