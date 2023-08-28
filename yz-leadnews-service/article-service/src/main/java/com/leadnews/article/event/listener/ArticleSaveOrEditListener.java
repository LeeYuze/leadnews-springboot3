package com.leadnews.article.event.listener;

import com.leadnews.article.event.ArticleSaveOrEditEvent;
import com.leadnews.article.service.ArticleFreemarkerService;
import com.leadnews.model.article.pojos.ApArticle;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * @author lihaohui
 * @date 2023/8/29
 */
@Component
@RequiredArgsConstructor
public class ArticleSaveOrEditListener {

    private static final Logger logger = LoggerFactory.getLogger(ArticleSaveOrEditListener.class);

    private final ArticleFreemarkerService articleFreemarkerService;

    @EventListener(ArticleSaveOrEditEvent.class)
    public void buildStaticHtmlByFreemaker(ArticleSaveOrEditEvent event) {
        ApArticle article = event.getArticle();
        Long id = article.getId();
        String content = event.getContent();

        logger.info("文章id:{} 生成文章静态html，并上传到Minio", id);

        articleFreemarkerService.buildArticleToMinIO(id, content);
    }
}
