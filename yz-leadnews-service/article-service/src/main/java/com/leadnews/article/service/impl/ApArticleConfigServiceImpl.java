package com.leadnews.article.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.leadnews.article.mapper.ApArticleConfigMapper;
import com.leadnews.article.service.ApArticleConfigService;
import com.leadnews.model.article.pojos.ApArticle;
import com.leadnews.model.article.pojos.ApArticleConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.sql.Wrapper;

/**
 * @author lihaohui
 * @description 针对表【ap_article_config(APP已发布文章配置表)】的数据库操作Service实现
 * @createDate 2023-08-28 16:11:00
 */
@Service
public class ApArticleConfigServiceImpl extends ServiceImpl<ApArticleConfigMapper, ApArticleConfig>
        implements ApArticleConfigService {

    private static final Logger logger = LoggerFactory.getLogger(ApArticleConfigServiceImpl.class);

    @Override
    public void upOrDownByArticleId(Long articleId, Integer enable) {

        boolean isDown = enable.equals(1) ? false : true;

        logger.info("设置文章上下架 articleid:{} isDown:{}", articleId, isDown);

        LambdaUpdateWrapper<ApArticleConfig> wrapper = Wrappers.<ApArticleConfig>lambdaUpdate().eq(ApArticleConfig::getArticleId, articleId).set(ApArticleConfig::getIsDown, isDown);
        this.update(wrapper);
    }
}




