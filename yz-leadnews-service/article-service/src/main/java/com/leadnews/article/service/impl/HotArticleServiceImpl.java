package com.leadnews.article.service.impl;

import com.alibaba.fastjson2.JSON;
import com.leadnews.apis.wemedia.IWemediaClient;
import com.leadnews.article.mapper.ApArticleMapper;
import com.leadnews.article.service.HotArticleService;
import com.leadnews.common.constants.article.ArticleConstants;
import com.leadnews.model.article.pojos.ApArticle;
import com.leadnews.model.article.vos.HotArticleVO;
import com.leadnews.model.common.dtos.ResponseResult;
import com.leadnews.model.common.enums.AppHttpCodeEnum;
import com.leadnews.model.wemedia.pojos.WmChannel;
import com.leadnews.redis.utils.CacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author lihaohui
 * @date 2023/8/30
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class HotArticleServiceImpl implements HotArticleService {

    private final ApArticleMapper apArticleMapper;

    private final CacheService cacheService;

    private final IWemediaClient wemediaClient;

    @Override
    public void computeHotArticle() {

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -5);
        Date date = calendar.getTime();

        //1.查询前5天的文章数据
        List<ApArticle> apArticleList = apArticleMapper.findArticleListByLast5days(date);

        //2.计算文章的分值
        List<HotArticleVO> hotArticleVoList = computeHotArticle(apArticleList);

        //3.为每个频道缓存30条分值较高的文章
        cacheTagToRedis(hotArticleVoList);
    }

    private void cacheTagToRedis(List<HotArticleVO> hotArticleVoList) {
        ResponseResult responseResult = wemediaClient.getChannels();
        if (!responseResult.getCode().equals(AppHttpCodeEnum.SUCCESS.getCode())) {
            return;
        }

        String channelJson = JSON.toJSONString(responseResult.getData());
        log.info("请求所有的频道信息 - json:{}", channelJson);

        List<WmChannel> wmChannels = JSON.parseArray(channelJson, WmChannel.class);

        for (WmChannel wmChannel : wmChannels) {
            List<HotArticleVO> hotArticleVos = hotArticleVoList.stream().filter(x -> x.getChannelId().equals(wmChannel.getId())).collect(Collectors.toList());
            //给文章进行排序，取30条分值较高的文章存入redis  key：频道id   value：30条分值较高的文章
            log.info("频道id:{} 缓存该频道 - 30条分值高的文章", wmChannel.getId());
            sortAndCache(hotArticleVos, ArticleConstants.HOT_ARTICLE_FIRST_PAGE + wmChannel.getId());
        }

        log.info("所有频道 缓存30条分值高的文章");
        //设置推荐数据
        //给文章进行排序，取30条分值较高的文章存入redis  key：频道id   value：30条分值较高的文章
        sortAndCache(hotArticleVoList, ArticleConstants.HOT_ARTICLE_FIRST_PAGE + ArticleConstants.DEFAULT_TAG);
    }

    /**
     * 排序并且缓存数据
     *
     * @param hotArticleVos
     * @param key
     */
    private void sortAndCache(List<HotArticleVO> hotArticleVos, String key) {
        hotArticleVos = hotArticleVos.stream().sorted(Comparator.comparing(HotArticleVO::getScore).reversed()).collect(Collectors.toList());
        if (hotArticleVos.size() > 30) {
            hotArticleVos = hotArticleVos.subList(0, 30);
        }
        cacheService.set(key, JSON.toJSONString(hotArticleVos));
    }


    private List<HotArticleVO> computeHotArticle(List<ApArticle> apArticleList) {
        List<HotArticleVO> hotArticleVoList = new ArrayList<>();
        for (ApArticle apArticle : apArticleList) {
            HotArticleVO hot = new HotArticleVO();
            BeanUtils.copyProperties(apArticle, hot);
            Integer score = computeScore(apArticle);
            log.info("文章id:{} 热度分数:{}", apArticle.getId(), score);
            hot.setScore(score);
            hotArticleVoList.add(hot);
        }

        return hotArticleVoList;
    }

    /**
     * 计算文章的具体分值
     *
     * @param apArticle
     * @return
     */
    public Integer computeScore(ApArticle apArticle) {
        Integer scere = 0;
        if (apArticle.getLikes() != null) {
            scere += apArticle.getLikes() * ArticleConstants.HOT_ARTICLE_LIKE_WEIGHT;
        }
        if (apArticle.getViews() != null) {
            scere += apArticle.getViews();
        }
        if (apArticle.getComment() != null) {
            scere += apArticle.getComment() * ArticleConstants.HOT_ARTICLE_COMMENT_WEIGHT;
        }
        if (apArticle.getCollection() != null) {
            scere += apArticle.getCollection() * ArticleConstants.HOT_ARTICLE_COLLECTION_WEIGHT;
        }

        return scere;
    }
}
