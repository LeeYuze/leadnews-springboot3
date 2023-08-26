package com.leadnews.schedule.feign;

import com.alibaba.fastjson2.JSON;
import com.leadnews.apis.schedule.IScheduleClient;
import com.leadnews.model.common.dtos.ResponseResult;
import com.leadnews.model.schedule.dtos.Task;
import com.leadnews.schedule.service.TaskService;
import io.swagger.v3.core.util.Json;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * @author lihaohui
 * @date 2023/8/26
 */
@RestController
@RequiredArgsConstructor
public class ScheduleClient implements IScheduleClient {

    private final TaskService taskService;

    /**
     * 添加任务
     *
     * @param task 任务对象
     * @return 任务id
     */
    @PostMapping("/api/v1/task/add")
    @Override
    public ResponseResult addTask(@RequestBody Task task) {
        return ResponseResult.okResult(taskService.addTask(task));
    }

    /**
     * 取消任务
     *
     * @param taskId 任务id
     * @return 取消结果
     */
    @GetMapping("/api/v1/task/cancel/{taskId}")
    @Override
    public ResponseResult cancelTask(@PathVariable("taskId") long taskId) {
        taskService.cancelTask(taskId);
        return ResponseResult.okResult();
    }

    /**
     * 按照类型和优先级来拉取任务
     *
     * @param type
     * @param priority
     * @return
     */
    @GetMapping("/api/v1/task/poll/{type}/{priority}")
    @Override
    public ResponseResult poll(@PathVariable("type") int type, @PathVariable("priority") int priority) {
        Task task = taskService.poll(type, priority);
        return ResponseResult.okResult(task);
    }
}
