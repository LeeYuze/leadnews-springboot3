package com.leadnews.wemedia.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.leadnews.model.wemedia.pojos.WmChannel;
import com.leadnews.wemedia.mapper.WmChannelMapper;
import com.leadnews.wemedia.service.WmChannelService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author lihaohui
 * @date 2023/8/17
 */
@Service
public class WmChannelServiceImpl extends ServiceImpl<WmChannelMapper, WmChannel> implements WmChannelService {
    @Override
    public List<WmChannel> findAll() {
        return list();
    }


}
