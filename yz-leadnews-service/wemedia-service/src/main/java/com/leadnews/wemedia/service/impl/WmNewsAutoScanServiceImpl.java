package com.leadnews.wemedia.service.impl;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.nacos.api.naming.pojo.healthcheck.impl.Http;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.leadnews.apis.article.IArticleClient;
import com.leadnews.common.wemedia.WmNewsStatus;
import com.leadnews.model.article.dtos.ArticleDTO;
import com.leadnews.model.common.dtos.ResponseResult;
import com.leadnews.model.common.enums.AppHttpCodeEnum;
import com.leadnews.model.wemedia.pojos.WmChannel;
import com.leadnews.model.wemedia.pojos.WmNews;
import com.leadnews.model.wemedia.pojos.WmSensitive;
import com.leadnews.model.wemedia.pojos.WmUser;
import com.leadnews.wemedia.mapper.WmChannelMapper;
import com.leadnews.wemedia.mapper.WmNewsMapper;
import com.leadnews.wemedia.mapper.WmSensitiveMapper;
import com.leadnews.wemedia.mapper.WmUserMapper;
import com.leadnews.wemedia.service.WmChannelService;
import com.leadnews.wemedia.service.WmNewsAutoScanService;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author lihaohui
 * @date 2023/8/21
 */
@Service
@RequiredArgsConstructor
public class WmNewsAutoScanServiceImpl implements WmNewsAutoScanService {

    private final WmNewsMapper wmNewsMapper;

    private final WmChannelMapper wmChannelMapper;

    private final WmUserMapper wmUserMapper;

    @Resource
    private IArticleClient articleClient;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void autoScanWmNews(Integer id) {
        //1.查询自媒体文章
        WmNews wmNews = wmNewsMapper.selectById(id);
        if (wmNews == null) {
            throw new RuntimeException("WmNewsAutoScanServiceImpl-文章不存在");
        }

        // 如果订单状态是提交
        if (wmNews.getStatus().equals(WmNewsStatus.SUMMIT)) {
            // 从内容中提取纯文本内容和图片
            // Map<String, Object> textAndImages = handleTextAndImages(wmNews);

            //2.审核文本内容  阿里云接口
            // 由于阿里云需要企业认证 所以直接通过

            //3.审核图片  阿里云接口
            // 由于阿里云需要企业认证 所以直接通过

            //4.审核成功，保存app端的相关的文章数据
            ResponseResult responseResult = saveAppArticle(wmNews);
            if (!responseResult.getCode().equals(AppHttpCodeEnum.SUCCESS.getCode())) {
                throw new RuntimeException("WmNewsAutoScanServiceImpl-文章审核，保存app端相关文章数据失败");
            }

            //回填article_id
            wmNews.setArticleId((Long) responseResult.getData());
            updateWmNews(wmNews, WmNewsStatus.PUBLISH, "审核成功");
        }
    }

    private void updateWmNews(WmNews wmNews, Integer status, String reason) {
        wmNews.setStatus(status);
        wmNews.setReason(reason);
        wmNewsMapper.updateById(wmNews);
    }

    /**
     * 保存app端相关的文章数据
     *
     * @param wmNews
     */
    private ResponseResult saveAppArticle(WmNews wmNews) {

        ArticleDTO dto = new ArticleDTO();
        //属性的拷贝
        BeanUtils.copyProperties(wmNews, dto);
        //文章的布局
        dto.setLayout(wmNews.getType());
        //频道
        WmChannel wmChannel = wmChannelMapper.selectById(wmNews.getChannelId());
        if (wmChannel != null) {
            dto.setChannelName(wmChannel.getName());
        }

        //作者
        dto.setAuthorId(wmNews.getUserId());
        WmUser wmUser = wmUserMapper.selectById(wmNews.getUserId());
        if (wmUser != null) {
            dto.setAuthorName(wmUser.getName());
        }

        //设置文章id
        if (wmNews.getArticleId() != null) {
            dto.setId(wmNews.getArticleId());
        }
        dto.setCreatedTime(new Date());

        ResponseResult responseResult = articleClient.saveArticle(dto);
        return responseResult;

    }

    /**
     * 1。从自媒体文章的内容中提取文本和图片
     * 2.提取文章的封面图片
     *
     * @param wmNews
     * @return
     */
    private Map<String, Object> handleTextAndImages(WmNews wmNews) {

        //存储纯文本内容
        StringBuilder stringBuilder = new StringBuilder();

        List<String> images = new ArrayList<>();

        //1。从自媒体文章的内容中提取文本和图片
        if (StrUtil.isNotBlank(wmNews.getContent())) {
            JSONArray jsonArray = JSONArray.parseArray(wmNews.getContent());
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String type = jsonObject.getString("type");
                String value = jsonObject.getString("value");
                if ("text".equals(type)) {
                    stringBuilder.append(value);
                } else {
                    images.add(value);
                }
            }
        }
        //2.提取文章的封面图片
        if (StrUtil.isNotBlank(wmNews.getImages())) {
            String[] split = wmNews.getImages().split(",");
            images.addAll(Arrays.asList(split));
        }

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("content", stringBuilder.toString());
        resultMap.put("images", images);
        return resultMap;

    }
}
