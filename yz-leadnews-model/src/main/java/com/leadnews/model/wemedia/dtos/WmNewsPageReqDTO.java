package com.leadnews.model.wemedia.dtos;

import com.leadnews.model.common.dtos.PageRequestDTO;
import lombok.Data;

import java.util.Date;

/**
 * @author lihaohui
 * @date 2023/8/17
 */
@Data
public class WmNewsPageReqDTO extends PageRequestDTO {

    /**
     * 状态
     */
    private Integer status;

    /**
     * 开始时间
     */
    private Date beginPubDate;

    /**
     * 结束时间
     */
    private Date endPubDate;

    /**
     * 所属频道ID
     */
    private Integer channelId;

    /**
     * 关键字
     */
    private String keyword;
}
