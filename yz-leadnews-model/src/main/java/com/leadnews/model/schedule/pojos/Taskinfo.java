package com.leadnews.model.schedule.pojos;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 
 * @TableName taskinfo
 */
@TableName(value ="taskinfo")
@Data
public class Taskinfo implements Serializable {
    /**
     * 任务id
     */
    @TableId(value = "task_id")
    private Long taskId;

    /**
     * 执行时间
     */
    @TableField(value = "execute_time")
    private Date executeTime;

    /**
     * 优先级
     */
    @TableField(value = "priority")
    private Integer priority;

    /**
     * 任务类型
     */
    @TableField(value = "task_type")
    private Integer taskType;

    /**
     * 参数
     */
    @TableField(value = "parameters")
    private byte[] parameters;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}