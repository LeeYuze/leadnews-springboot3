package com.leadnews.model.search.dtos;

import lombok.Data;

@Data
public class HistorySearchDTO {

    // 设备ID
    Integer equipmentId;
    /**
     * 接收搜索历史记录id
     */
    String id;
}