package com.leadnews.article.service.impl;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.leadnews.article.event.ArticleSaveOrEditEvent;
import com.leadnews.article.mapper.ApArticleConfigMapper;
import com.leadnews.article.mapper.ApArticleContentMapper;
import com.leadnews.article.mapper.ApArticleMapper;
import com.leadnews.article.service.ApArticleService;
import com.leadnews.article.service.HotArticleService;
import com.leadnews.common.constants.article.ArticleConstants;
import com.leadnews.model.article.dtos.ArticleDTO;
import com.leadnews.model.article.dtos.ArticleHomeDTO;
import com.leadnews.model.article.mess.ArticleVisitStreamMess;
import com.leadnews.model.article.pojos.ApArticle;
import com.leadnews.model.article.pojos.ApArticleConfig;
import com.leadnews.model.article.pojos.ApArticleContent;
import com.leadnews.model.article.vos.HotArticleVO;
import com.leadnews.model.common.dtos.ResponseResult;
import com.leadnews.model.common.enums.AppHttpCodeEnum;
import com.leadnews.redis.utils.CacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author lihaohui
 * @date 2023/8/15
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ApArticleServiceImpl extends ServiceImpl<ApArticleMapper, ApArticle> implements ApArticleService {


    private final ApArticleMapper apArticleMapper;

    private final ApArticleConfigMapper apArticleConfigMapper;

    private final ApArticleContentMapper apArticleContentMapper;

    private final ApplicationEventPublisher applicationEventPublisher;

    private final CacheService cacheService;

    private final HotArticleService hotArticleService;


    // 单页最大加载的数字
    private final static Integer MAX_PAGE_SIZE = 50;


    @Override
    public ResponseResult load(Integer loadtype, ArticleHomeDTO dto) {
        //1.校验参数
        Integer size = dto.getSize();
        if (size == null || size == 0) {
            size = 10;
        }

        size = Math.min(size, MAX_PAGE_SIZE);
        dto.setSize(size);

        //类型参数检验
        if (!loadtype.equals(ArticleConstants.LOADTYPE_LOAD_MORE) && !loadtype.equals(ArticleConstants.LOADTYPE_LOAD_NEW)) {
            loadtype = ArticleConstants.LOADTYPE_LOAD_MORE;
        }

        //文章频道校验
        if (StrUtil.isEmpty(dto.getTag())) {
            dto.setTag(ArticleConstants.DEFAULT_TAG);
        }

        //时间校验
        if (dto.getMaxBehotTime() == null) {
            dto.setMaxBehotTime(new Date());
        }

        if (dto.getMinBehotTime() == null) {
            dto.setMinBehotTime(new Date());
        }

        //2.查询数据
        List<ApArticle> apArticles = apArticleMapper.loadArticleList(dto, loadtype);

        return ResponseResult.okResult(apArticles);
    }

    @Override
    public ResponseResult loadByHot(Integer loadtype, ArticleHomeDTO dto, boolean firstPage) {
        if (firstPage) {
            String jsonStr = cacheService.get(ArticleConstants.HOT_ARTICLE_FIRST_PAGE + dto.getTag());
            if (StrUtil.isNotBlank(jsonStr)) {
                List<HotArticleVO> hotArticleVoList = JSON.parseArray(jsonStr, HotArticleVO.class);
                ResponseResult responseResult = ResponseResult.okResult(hotArticleVoList);
                return responseResult;
            }
        }
        return load(loadtype, dto);
    }

    @Override
    public ResponseResult saveArticle(ArticleDTO dto) {
        //1.检查参数
        if (dto == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }

        ApArticle apArticle = new ApArticle();
        BeanUtils.copyProperties(dto, apArticle);

        //2.判断是否存在id
        if (dto.getId() == null) {
            //2.1 不存在id  保存  文章  文章配置  文章内容
            //保存文章
            save(apArticle);

            //保存配置
            ApArticleConfig apArticleConfig = new ApArticleConfig(apArticle.getId());
            apArticleConfigMapper.insert(apArticleConfig);

            //保存 文章内容
            ApArticleContent apArticleContent = new ApArticleContent();
            apArticleContent.setArticleId(apArticle.getId());
            apArticleContent.setContent(dto.getContent());
            apArticleContentMapper.insert(apArticleContent);
        } else {
            //2.2 存在id   修改  文章  文章内容

            //修改  文章
            updateById(apArticle);

            //修改文章内容
            ApArticleContent apArticleContent = apArticleContentMapper.selectOne(Wrappers.<ApArticleContent>lambdaQuery().eq(ApArticleContent::getArticleId, dto.getId()));
            apArticleContent.setContent(dto.getContent());
            apArticleContentMapper.updateById(apArticleContent);
        }

        applicationEventPublisher.publishEvent(new ArticleSaveOrEditEvent(this, apArticle, dto.getContent()));

        return ResponseResult.okResult(apArticle.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateScore(ArticleVisitStreamMess mess) {
        log.info("重新计算文章id:{} 分值:{}", mess.getArticleId(), mess);
        //1.更新文章的阅读、点赞、收藏、评论的数量 Mysql
        ApArticle apArticle = updateArticleToDb(mess);

        //2.计算文章的分值
        Integer score = hotArticleService.computeScore(apArticle);

        //3.替换当前文章对应频道的热点数据
        replaceDataToRedis(apArticle, score, ArticleConstants.HOT_ARTICLE_FIRST_PAGE + apArticle.getChannelId());

        //4.替换推荐对应的热点数据
        replaceDataToRedis(apArticle, score, ArticleConstants.HOT_ARTICLE_FIRST_PAGE + ArticleConstants.DEFAULT_TAG);
    }

    /**
     * 替换数据并且存入到redis
     */
    private void replaceDataToRedis(ApArticle apArticle, Integer score, String cachekey) {
        String articleListStr = cacheService.get(cachekey);
        if (StrUtil.isBlank(articleListStr)) {
            return;
        }

        List<HotArticleVO> hotArticleVoList = JSON.parseArray(articleListStr, HotArticleVO.class);
        boolean flag = true;

        //如果缓存中存在该文章，只更新分值
        for (HotArticleVO hotArticleVo : hotArticleVoList) {
            if (hotArticleVo.getId().equals(apArticle.getId())) {
                hotArticleVo.setScore(score);
                flag = false;
                break;
            }
        }

        if (flag) {
            //如果缓存中不存在，查询缓存中分值最小的一条数据，进行分值的比较，如果当前文章的分值大于缓存中的数据，就替换
            if (hotArticleVoList.size() >= 30) {
                hotArticleVoList = hotArticleVoList.stream().sorted(Comparator.comparing(HotArticleVO::getScore).reversed()).collect(Collectors.toList());
                HotArticleVO lastHot = hotArticleVoList.get(hotArticleVoList.size() - 1);
                if (lastHot.getScore() < score) {
                    hotArticleVoList.remove(lastHot);
                    HotArticleVO hot = new HotArticleVO();
                    BeanUtils.copyProperties(apArticle, hot);
                    hot.setScore(score);
                    hotArticleVoList.add(hot);
                }
            } else {
                HotArticleVO hot = new HotArticleVO();
                BeanUtils.copyProperties(apArticle, hot);
                hot.setScore(score);
                hotArticleVoList.add(hot);
            }
        }


        //缓存到redis
        hotArticleVoList = hotArticleVoList.stream().sorted(Comparator.comparing(HotArticleVO::getScore).reversed()).collect(Collectors.toList());
        cacheService.set(cachekey, JSON.toJSONString(hotArticleVoList));
    }

    /**
     * 更新文章行为数量
     *
     * @param mess
     */
    public ApArticle updateArticleToDb(ArticleVisitStreamMess mess) {
        ApArticle apArticle = getById(mess.getArticleId());
        apArticle.setCollection(apArticle.getCollection() == null ? 0 : apArticle.getCollection() + mess.getCollect());
        apArticle.setComment(apArticle.getComment() == null ? 0 : apArticle.getComment() + mess.getComment());
        apArticle.setLikes(apArticle.getLikes() == null ? 0 : apArticle.getLikes() + mess.getLike());
        apArticle.setViews(apArticle.getViews() == null ? 0 : apArticle.getViews() + mess.getView());
        updateById(apArticle);
        return apArticle;
    }


}
