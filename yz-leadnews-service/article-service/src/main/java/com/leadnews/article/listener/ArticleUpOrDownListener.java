package com.leadnews.article.listener;

import com.alibaba.fastjson2.JSON;
import com.github.xiaoymin.knife4j.core.util.StrUtil;
import com.leadnews.article.service.ApArticleConfigService;
import com.leadnews.common.message.MqConstants;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author lihaohui
 * @date 2023/8/28
 */
@Component
@RequiredArgsConstructor
public class ArticleUpOrDownListener {
    private static final Logger logger = LoggerFactory.getLogger(ArticleUpOrDownListener.class);

    private final ApArticleConfigService apArticleConfigService;

    @KafkaListener(topics = MqConstants.WM_NEWS_UP_OR_DOWN_TOPIC)
    public void onMessage(String message) {
        if (StrUtil.isNotBlank(message)) {
            Map map = JSON.parseObject(message, Map.class);
            Long articleId = (Long) map.get("articleId");
            Integer enable = (Integer) map.get("enable");

            apArticleConfigService.upOrDownByArticleId(articleId, enable);

            logger.info("article端文章配置修改 articleId={} enable={}", articleId, enable);
        }
    }
}
