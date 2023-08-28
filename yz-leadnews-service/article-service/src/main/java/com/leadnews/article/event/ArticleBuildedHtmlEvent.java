package com.leadnews.article.event;

import com.leadnews.model.article.pojos.ApArticle;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * @author lihaohui
 * @date 2023/8/29
 */
@Getter
public class ArticleBuildedHtmlEvent extends ApplicationEvent {

    private ApArticle article;

    private String content;

    public ArticleBuildedHtmlEvent(Object source, ApArticle article, String content) {
        super(source);
        this.article = article;
        this.content = content;
    }
}
