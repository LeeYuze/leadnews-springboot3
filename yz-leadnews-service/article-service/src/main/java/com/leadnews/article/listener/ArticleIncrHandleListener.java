package com.leadnews.article.listener;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.leadnews.article.service.ApArticleService;
import com.leadnews.common.constants.article.HotArticleConstants;
import com.leadnews.model.article.mess.ArticleVisitStreamMess;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * @author lihaohui
 * @date 2023/8/30
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class ArticleIncrHandleListener {

    private final ApArticleService apArticleService;

    @KafkaListener(topics = HotArticleConstants.HOT_ARTICLE_INCR_HANDLE_TOPIC)
    public void onMessage(String mess){
        if(StrUtil.isBlank(mess)){
          return;
        }

        ArticleVisitStreamMess articleVisitStreamMess = JSON.parseObject(mess, ArticleVisitStreamMess.class);
        apArticleService.updateScore(articleVisitStreamMess);
    }
}
