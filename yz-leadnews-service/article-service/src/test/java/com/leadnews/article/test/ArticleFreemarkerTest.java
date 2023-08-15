package com.leadnews.article.test;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSONArray;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.leadnews.article.mapper.ApArticleContentMapper;
import com.leadnews.article.mapper.ApArticleMapper;
import com.leadnews.minio.service.FileStorageService;
import com.leadnews.model.article.pojos.ApArticle;
import com.leadnews.model.article.pojos.ApArticleContent;
import freemarker.template.Configuration;
import freemarker.template.Template;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author lihaohui
 * @date 2023/8/15
 */
@SpringBootTest
public class ArticleFreemarkerTest {

    private static final Logger logger = LoggerFactory.getLogger(ArticleFreemarkerTest.class);

    @Resource
    private Configuration configuration;

    @Resource
    private FileStorageService fileStorageService;

    @Resource
    private ApArticleMapper apArticleMapper;

    @Resource
    private ApArticleContentMapper apArticleContentMapper;

    @Test
    public void createStaticHtml() throws Exception {
        ApArticleContent apArticleContent = apArticleContentMapper.selectOne(Wrappers.<ApArticleContent>lambdaQuery().eq(ApArticleContent::getArticleId, 1302862387124125698L));
        if (apArticleContent == null || StrUtil.isBlank(apArticleContent.getContent())) return;

        logger.info("文章内容：{}", apArticleContent);

        Template template = configuration.getTemplate("article.ftl");

        JSONArray content = JSONArray.parseArray(apArticleContent.getContent());

        logger.info("文章内容 解析JSON：{}", content);

        Map<String, Object> map = new HashMap<>();
        map.put("content", content);

        StringWriter out = new StringWriter();

        template.process(map, out);

//        logger.info("文章content 解析成HTML：{}", out.toString());

        InputStream is = new ByteArrayInputStream(out.toString().getBytes());

        String upPath = fileStorageService.uploadHtmlFile("", apArticleContent.getArticleId() + ".html", is);

        ApArticle article = new ApArticle();
        article.setId(apArticleContent.getArticleId());
        article.setStaticUrl(upPath);
        apArticleMapper.updateById(article);

    }

}
