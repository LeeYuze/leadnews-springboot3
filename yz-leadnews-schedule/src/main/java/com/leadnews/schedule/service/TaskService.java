package com.leadnews.schedule.service;

import com.leadnews.model.schedule.dtos.Task;

/**
 * @author lihaohui
 * @date 2023/8/26
 */
public interface TaskService {

    /**
     * 添加任务
     *
     * @param task 任务对象
     * @return 任务id
     */
    Long addTask(Task task);

    /**
     * 取消任务
     *
     * @param taskId 任务id
     * @return 取消结果
     */
    void cancelTask(long taskId);

    /**
     * 按照类型和优先级来拉取任务
     *
     * @param type
     * @param priority
     * @return
     */
    Task poll(int type, int priority);
}
