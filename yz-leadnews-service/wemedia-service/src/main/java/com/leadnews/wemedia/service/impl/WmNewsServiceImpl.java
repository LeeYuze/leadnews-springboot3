package com.leadnews.wemedia.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.leadnews.model.common.dtos.PageResponseResult;
import com.leadnews.model.common.dtos.ResponseResult;
import com.leadnews.model.common.enums.AppHttpCodeEnum;
import com.leadnews.model.wemedia.dtos.WmNewsPageReqDTO;
import com.leadnews.model.wemedia.pojos.WmNews;
import com.leadnews.model.wemedia.pojos.WmUser;
import com.leadnews.wemedia.mapper.WmNewsMapper;
import com.leadnews.wemedia.service.WmNewsService;
import com.leadnews.wemedia.utils.thread.WmUserLocalUtil;
import org.springframework.stereotype.Service;

/**
 * @author lihaohui
 * @description 针对表【wm_news(自媒体图文内容信息表)】的数据库操作Service实现
 * @createDate 2023-08-17 21:12:32
 */
@Service
public class WmNewsServiceImpl extends ServiceImpl<WmNewsMapper, WmNews>
        implements WmNewsService {


    @Override
    public ResponseResult findAll(WmNewsPageReqDTO dto) {
        //1.检查参数
        if (dto == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }

        //分页参数检查
        dto.checkParam();

        //获取当前登录人的信息
        WmUser user = WmUserLocalUtil.getUser();
        if (user == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.NEED_LOGIN);
        }

        //2.分页条件查询
        IPage page = new Page(dto.getPage(), dto.getSize());
        LambdaQueryWrapper<WmNews> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        //状态精确查询
        if (dto.getStatus() != null) {
            lambdaQueryWrapper.eq(WmNews::getStatus, dto.getStatus());
        }

        //频道精确查询
        if (dto.getChannelId() != null) {
            lambdaQueryWrapper.eq(WmNews::getChannelId, dto.getChannelId());
        }

        //时间范围查询
        if (dto.getBeginPubDate() != null && dto.getEndPubDate() != null) {
            lambdaQueryWrapper.between(WmNews::getPublishTime, dto.getBeginPubDate(), dto.getEndPubDate());
        }

        //关键字模糊查询
        if (StrUtil.isNotBlank(dto.getKeyword())) {
            lambdaQueryWrapper.like(WmNews::getTitle, dto.getKeyword());
        }

        //查询当前登录用户的文章
        lambdaQueryWrapper.eq(WmNews::getUserId, user.getId());

        //发布时间倒序查询
        lambdaQueryWrapper.orderByDesc(WmNews::getCreatedTime);

        page = page(page, lambdaQueryWrapper);

        //3.结果返回
        ResponseResult responseResult = new PageResponseResult(dto.getPage(), dto.getSize(), page.getTotal());

        responseResult.setData(page.getRecords());

        return responseResult;
    }
}




