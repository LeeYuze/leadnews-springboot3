package com.leadnews.article.service.impl;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSONArray;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.leadnews.article.event.ArticleBuildedHtmlEvent;
import com.leadnews.article.mapper.ApArticleContentMapper;
import com.leadnews.article.mapper.ApArticleMapper;
import com.leadnews.article.service.ArticleFreemarkerService;
import com.leadnews.minio.service.FileStorageService;
import com.leadnews.model.article.pojos.ApArticle;
import com.leadnews.model.article.pojos.ApArticleContent;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * @author lihaohui
 * @date 2023/8/29
 */
@Service
public class ArticleFreemarkerServiceImpl implements ArticleFreemarkerService {

    private static final Logger logger = LoggerFactory.getLogger(ArticleFreemarkerServiceImpl.class);


    @Resource
    private Configuration configuration;

    @Resource
    private FileStorageService fileStorageService;

    @Resource
    private ApArticleMapper apArticleMapper;

    @Resource
    private ApplicationEventPublisher applicationEventPublisher;

    @Override
    @Async
    @Transactional(rollbackFor = Exception.class)
    public void buildArticleToMinIO(Long articleId, String content) {

        try {
            Template template = configuration.getTemplate("article.ftl");

            JSONArray contentArr = JSONArray.parseArray(content);

            Map<String, Object> map = new HashMap<>();
            map.put("content", contentArr);

            StringWriter out = new StringWriter();

            template.process(map, out);

//        logger.info("文章content 解析成HTML：{}", out.toString());

            InputStream is = new ByteArrayInputStream(out.toString().getBytes());

            String upPath = fileStorageService.uploadHtmlFile("", articleId + ".html", is);

            ApArticle article = apArticleMapper.selectById(articleId);
            article.setStaticUrl(upPath);
            apArticleMapper.updateById(article);

            applicationEventPublisher.publishEvent(new ArticleBuildedHtmlEvent(this, article, content));

        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (TemplateException e) {
            throw new RuntimeException(e);
        }
    }
}
