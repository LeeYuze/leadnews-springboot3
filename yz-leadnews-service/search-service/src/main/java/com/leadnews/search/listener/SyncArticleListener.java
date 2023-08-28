package com.leadnews.search.listener;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.IndexRequest;
import com.alibaba.fastjson2.JSON;
import com.leadnews.common.message.MqConstants;
import com.leadnews.model.search.vos.SearchArticleVO;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @author lihaohui
 * @date 2023/8/29
 */
@Component
@RequiredArgsConstructor
public class SyncArticleListener {
    private static final Logger logger = LoggerFactory.getLogger(SyncArticleListener.class);

    private final ElasticsearchClient esClient;

    @KafkaListener(topics = MqConstants.ARTICLE_ES_SYNC_TOPIC)
    public void onMessage(String message) {
        logger.info("同步文章到ES kafka消费 - message {}", message);
        SearchArticleVO searchArticleVO = JSON.parseObject(message, SearchArticleVO.class);

        IndexRequest.Builder<Object> indexRequestBuilder = new IndexRequest.Builder<>();

        IndexRequest<Object> indexRequest = indexRequestBuilder
                .index("app_info_article")
                .id(searchArticleVO.getId().toString())
                .document(searchArticleVO)
                .build();

        try {
            logger.info("同步文章到ES kafka消费 - 进行文章同步");
            esClient.index(indexRequest);
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("同步文章到ES kafka消费 - 文章同步失败");
        }
    }
}
