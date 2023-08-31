package com.leadnews.wemedia.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.leadnews.apis.schedule.IScheduleClient;
import com.leadnews.model.common.dtos.ResponseResult;
import com.leadnews.model.common.enums.AppHttpCodeEnum;
import com.leadnews.model.common.enums.TaskTypeEnum;
import com.leadnews.model.schedule.dtos.Task;
import com.leadnews.model.wemedia.pojos.WmNews;
import com.leadnews.utils.common.ProtostuffUtil;
import com.leadnews.wemedia.service.WmNewsTaskService;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Objects;

/**
 * @author lihaohui
 * @date 2023/8/26
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class WmNewsTaskServiceImpl implements WmNewsTaskService {

    @Resource
    private IScheduleClient scheduleClient;

    @Resource
    private WmNewsAutoScanServiceImpl wmNewsAutoScanService;


    /**
     * 消费延迟队列数据
     */
    @Scheduled(fixedRate = 1000)
    @Override
    @SneakyThrows
    public void scanNewsByTask() {

        // log.info("文章审核---消费任务执行---begin---");

        ResponseResult responseResult = scheduleClient.poll(TaskTypeEnum.NEWS_SCAN_TIME.getTaskType(), TaskTypeEnum.NEWS_SCAN_TIME.getPriority());
        Object data = responseResult.getData();

        if (responseResult.getCode().equals(AppHttpCodeEnum.SUCCESS.getCode()) && Objects.nonNull(data)) {
            log.info("文章审核---消费任务执行---拿到任务---进行审核");

            log.info("任务对象feign data:{}", data);

            // jackJson的方式解码 因为openFeign默认用jackJson
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonStr = objectMapper.writeValueAsString(data);

            Task task = objectMapper.readValue(jsonStr, Task.class);
            log.info("解析json获取的task对象:{}", task);

            byte[] parameters = task.getParameters();
            WmNews wmNews = ProtostuffUtil.deserialize(parameters, WmNews.class);
            log.info(wmNews.getId() + "-----------");
            wmNewsAutoScanService.autoScanWmNews(wmNews.getId());
        }

        // log.info("文章审核---消费任务执行---end---");
    }


    @Override
    @Async
    public void addNewsToTask(Integer id, Date publishTime) {
        log.info("添加任务到延迟服务中----begin");

        Task task = new Task();
        task.setExecuteTime(publishTime.getTime());
        task.setTaskType(TaskTypeEnum.NEWS_SCAN_TIME.getTaskType());
        task.setPriority(TaskTypeEnum.NEWS_SCAN_TIME.getPriority());
        WmNews wmNews = new WmNews();
        wmNews.setId(id);
        task.setParameters(ProtostuffUtil.serialize(wmNews));

        scheduleClient.addTask(task);

        log.info("添加任务到延迟服务中----end");
    }
}
