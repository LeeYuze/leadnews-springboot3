package com.leadnews.model.schedule.dtos;

import lombok.Data;

import java.io.Serializable;

/**
 * 任务信息
 * @author lihaohui
 * @date 2023/8/26
 */
@Data
public class Task implements Serializable {
    /**
     * 任务id
     */
    private Long taskId;
    /**
     * 类型
     */
    private Integer taskType;

    /**
     * 优先级
     */
    private Integer priority;

    /**
     * 执行id
     */
    private long executeTime;

    /**
     * task参数
     */
    private byte[] parameters;
}
