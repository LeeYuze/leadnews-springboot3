package com.leadnews.wemedia.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.leadnews.model.common.dtos.ResponseResult;
import com.leadnews.model.wemedia.dtos.WmNewsDTO;
import com.leadnews.model.wemedia.dtos.WmNewsPageReqDTO;
import com.leadnews.model.wemedia.pojos.WmNews;

/**
 * @author lihaohui
 * @description 针对表【wm_news(自媒体图文内容信息表)】的数据库操作Service
 * @createDate 2023-08-17 21:12:32
 */
public interface WmNewsService extends IService<WmNews> {
    /**
     * 查询文章
     *
     * @param dto
     * @return
     */
    ResponseResult findAll(WmNewsPageReqDTO dto);

    /**
     * 发布文章或保存草稿
     *
     * @param dto
     * @return
     */
    ResponseResult submitNews(WmNewsDTO dto);

    /**
     * 获取文章详情
     * @param id 文章id
     * @return
     */
    WmNews getOne(Long id);


    /**
     * 根据id，删除文章
     * @param id
     */
    void deleteById(Long id);


    /**
     * 文章上下架
     * @param dto
     */
    void downOrUp(WmNewsDTO dto);
}
