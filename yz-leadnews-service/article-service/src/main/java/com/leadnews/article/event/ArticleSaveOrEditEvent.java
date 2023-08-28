package com.leadnews.article.event;

import com.leadnews.model.article.pojos.ApArticle;
import lombok.Data;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.time.Clock;

/**
 * 文章审核通过后，编辑或者保存的事件
 * @author lihaohui
 * @date 2023/8/29
 */
@Getter
public class ArticleSaveOrEditEvent extends ApplicationEvent {

    private ApArticle article;

    private String content;

    public ArticleSaveOrEditEvent(Object source, ApArticle article, String content) {
        super(source);
        this.article = article;
        this.content = content;
    }
}
