package com.leadnews.wemedia.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.leadnews.model.wemedia.pojos.WmChannel;

import java.util.List;

/**
 * @author lihaohui
 * @date 2023/8/17
 */
public interface WmChannelService extends IService<WmChannel> {

    /**
     * 查询所有文章频道
     * @return 文章频道列表
     */
    List<WmChannel> findAll();

}
