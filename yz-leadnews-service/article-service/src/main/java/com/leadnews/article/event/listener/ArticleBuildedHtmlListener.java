package com.leadnews.article.event.listener;

import com.alibaba.fastjson2.JSON;
import com.leadnews.article.event.ArticleBuildedHtmlEvent;
import com.leadnews.common.constants.message.MqConstants;
import com.leadnews.model.article.pojos.ApArticle;
import com.leadnews.model.search.vos.SearchArticleVO;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * @author lihaohui
 * @date 2023/8/29
 */
@Component
@RequiredArgsConstructor
public class ArticleBuildedHtmlListener {
    private static final Logger logger = LoggerFactory.getLogger(ArticleBuildedHtmlListener.class);

    private final KafkaTemplate<String, String> kafkaTemplate;

    @EventListener(ArticleBuildedHtmlEvent.class)
    public void createDocToEs(ArticleBuildedHtmlEvent event) {
        ApArticle article = event.getArticle();
        String content = event.getContent();
        SearchArticleVO searchArticleVO = new SearchArticleVO();
        BeanUtils.copyProperties(article, searchArticleVO);
        searchArticleVO.setContent(content);

        logger.info("文章Id：{}, 发送kafka主题{}, 将文章信息同步到Es", article.getId(), MqConstants.ARTICLE_ES_SYNC_TOPIC);

        kafkaTemplate.send(MqConstants.ARTICLE_ES_SYNC_TOPIC, JSON.toJSONString(searchArticleVO)).whenComplete((sr, er) -> {
            RecordMetadata recordMetadata = sr.getRecordMetadata();
            logger.info("topic {} partition {}", recordMetadata.topic(), recordMetadata.partition());
        });

    }
}
