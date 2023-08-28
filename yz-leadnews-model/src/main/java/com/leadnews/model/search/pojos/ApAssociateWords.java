package com.leadnews.model.search.pojos;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 联想词表
 * @author itheima
 */
@Data
public class ApAssociateWords implements Serializable {
    private static final long serialVersionUID = 1L;
    private String id;
    /**
     * 联想词
     */
    private String associateWords;
    /**
     * 创建时间
     */
    private Date createdTime;
}