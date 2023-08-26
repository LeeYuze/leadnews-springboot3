package com.leadnews.schedule.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.leadnews.model.schedule.pojos.Taskinfo;
import com.leadnews.schedule.mapper.TaskinfoMapper;
import com.leadnews.schedule.service.TaskinfoService;
import org.springframework.stereotype.Service;

/**
* @author lihaohui
* @description 针对表【taskinfo】的数据库操作Service实现
* @createDate 2023-08-25 22:02:22
*/
@Service
public class TaskinfoServiceImpl extends ServiceImpl<TaskinfoMapper, Taskinfo>
    implements TaskinfoService {

}




