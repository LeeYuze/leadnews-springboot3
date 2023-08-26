package com.leadnews.schedule.service.impl;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.xiaoymin.knife4j.core.util.StrUtil;
import com.leadnews.model.schedule.pojos.Taskinfo;
import com.leadnews.model.schedule.pojos.TaskinfoLogs;
import com.leadnews.schedule.common.constants.ScheduleConstants;
import com.leadnews.model.schedule.dtos.Task;
import com.leadnews.schedule.mapper.TaskinfoLogsMapper;
import com.leadnews.schedule.mapper.TaskinfoMapper;
import com.leadnews.schedule.service.TaskService;
import com.leadnews.schedule.utils.redis.CacheService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * @author lihaohui
 * @date 2023/8/26
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskinfoMapper taskinfoMapper;

    private final TaskinfoLogsMapper taskinfoLogsMapper;

    private final CacheService cacheService;

    @Scheduled(cron = "0 */5 * * * ?")
    @PostConstruct
    public void reloadData() {
        clearCache();
        log.info("数据库数据同步到缓存");
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, 5);

        // 查看小于未来5分钟的所有任务
        List<Taskinfo> allTasks = taskinfoMapper.selectList(Wrappers.<Taskinfo>lambdaQuery().lt(Taskinfo::getExecuteTime, calendar.getTime()));
        if (allTasks != null && !allTasks.isEmpty()) {
            for (Taskinfo taskinfo : allTasks) {
                Task task = new Task();
                BeanUtils.copyProperties(taskinfo, task);
                task.setExecuteTime(taskinfo.getExecuteTime().getTime());
                addTaskToCache(task);
            }
        }
    }

    private void clearCache() {
        // 删除缓存中未来数据集合和当前消费者队列的所有key
        Set<String> futurekeys = cacheService.scan(ScheduleConstants.FUTURE + "*");// future_
        Set<String> topickeys = cacheService.scan(ScheduleConstants.TOPIC + "*");// topic_
        cacheService.delete(futurekeys);
        cacheService.delete(topickeys);
    }

    @Scheduled(cron = "0 */1 * * * ?")
    public void refresh() {
        String token = cacheService.tryLock("FUTURE_TASK_SYNC", 1000 * 30);

        if (StrUtil.isBlank(token)) {
            return;
        }
        log.info("未来数据定时刷新---定时任务");

        // 获取所有未来数据集合的key值
        // future_*
        String futureCacheKey = ScheduleConstants.FUTURE + "*";
        Set<String> futureKeys = cacheService.scan(futureCacheKey);

        // future_250_250
        for (String futureKey : futureKeys) {

            String topicKey = ScheduleConstants.TOPIC + futureKey.split(ScheduleConstants.FUTURE)[1];

            //获取该组key下当前需要消费的任务数据
            Set<String> tasks = cacheService.zRangeByScore(futureKey, 0, System.currentTimeMillis());
            if (!tasks.isEmpty()) {
                //将这些任务数据添加到消费者队列中
                cacheService.refreshWithPipeline(futureKey, topicKey, tasks);
                log.info("成功的将{}下的当前需要执行的任务数据刷新到{}下", futureKey, topicKey);
            }
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Long addTask(Task task) {

        //1.添加任务到数据库中
        saveTaskToDb(task);

        //2.添加任务到redis
        addTaskToCache(task);

        return task.getTaskId();
    }

    @Override
    public void cancelTask(long taskId) {
        //删除任务，更新日志
        Task task = cancelTaskFromDb(taskId, ScheduleConstants.EXECUTED);

        cancelTaskFromCache(task);
    }

    @Override
    public Task poll(int type, int priority) {
        Task task = null;
        String key = type + "_" + priority;
        String taskJson = cacheService.lRightPop(ScheduleConstants.TOPIC + key);
        if (StrUtil.isNotBlank(taskJson)) {
            task = JSON.parseObject(taskJson, Task.class);

            log.info("获取到消费任务:{}", task);

            // 从数据库删除task信息
            removeTaskFromDb(task.getTaskId());

            // 修改task日志信息为已经执行
            updateTaskLogsStatus(task.getTaskId(), ScheduleConstants.EXECUTED);
        }
        return task;
    }

    /**
     * 删除redis中的任务数据
     *
     * @param task
     */
    private void cancelTaskFromCache(Task task) {

        String key = task.getTaskType() + "_" + task.getPriority();

        if (task.getExecuteTime() <= System.currentTimeMillis()) {
            cacheService.lRemove(ScheduleConstants.TOPIC + key, 0, JSON.toJSONString(task));
        } else {
            cacheService.zRemove(ScheduleConstants.FUTURE + key, JSON.toJSONString(task));
        }
    }

    private void removeTaskFromDb(Long taskId) {
        //删除任务
        taskinfoMapper.deleteById(taskId);
    }

    private void updateTaskLogsStatus(Long taskId, Integer status) {

        TaskinfoLogs taskinfoLogs = taskinfoLogsMapper.selectById(taskId);
        taskinfoLogs.setStatus(status);
        taskinfoLogsMapper.updateById(taskinfoLogs);
    }

    private Task getTaskByTaskLogs(Long taskId) {
        Task task = new Task();
        TaskinfoLogs taskinfoLogs = taskinfoLogsMapper.selectById(taskId);
        BeanUtils.copyProperties(taskinfoLogs, task);
        task.setExecuteTime(taskinfoLogs.getExecuteTime().getTime());
        return task;
    }

    /**
     * 取消任务，更新任务日志状态
     */
    private Task cancelTaskFromDb(Long taskId, Integer status) {

        removeTaskFromDb(taskId);

        updateTaskLogsStatus(taskId, status);

        Task task = getTaskByTaskLogs(taskId);

        return task;
    }


    /**
     * 把任务添加到redis中
     */
    private void addTaskToCache(Task task) {

        String key = task.getTaskType() + "_" + task.getPriority();

        //获取5分钟之后的时间  毫秒值
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, 5);
        long nextScheduleTime = calendar.getTimeInMillis();

        //2.1 如果任务的执行时间小于等于当前时间，存入list
        if (task.getExecuteTime() <= System.currentTimeMillis()) {
            cacheService.lLeftPush(ScheduleConstants.TOPIC + key, JSON.toJSONString(task));
        } else if (task.getExecuteTime() <= nextScheduleTime) {
            //2.2 如果任务的执行时间大于当前时间 && 小于等于预设时间（未来5分钟） 存入zset中
            cacheService.zAdd(ScheduleConstants.FUTURE + key, JSON.toJSONString(task), task.getExecuteTime());
        }

    }


    /**
     * 添加任务到数据库中
     */
    private void saveTaskToDb(Task task) {

        //保存任务表
        Taskinfo taskinfo = new Taskinfo();
        BeanUtils.copyProperties(task, taskinfo);
        taskinfo.setExecuteTime(new Date(task.getExecuteTime()));
        taskinfoMapper.insert(taskinfo);

        //设置taskID
        task.setTaskId(taskinfo.getTaskId());

        // 保存任务日志
        savaTaskLogs(taskinfo);
    }

    @Async
    public void savaTaskLogs(Taskinfo taskinfo) {
        //保存任务日志数据
        TaskinfoLogs taskinfoLogs = new TaskinfoLogs();
        BeanUtils.copyProperties(taskinfo, taskinfoLogs);
        taskinfoLogs.setVersion(1);
        taskinfoLogs.setStatus(ScheduleConstants.SCHEDULED);
        taskinfoLogsMapper.insert(taskinfoLogs);
    }
}
