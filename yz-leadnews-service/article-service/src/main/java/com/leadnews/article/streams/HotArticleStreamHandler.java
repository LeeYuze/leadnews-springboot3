package com.leadnews.article.streams;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.leadnews.common.constants.article.HotArticleConstants;
import com.leadnews.model.article.mess.ArticleVisitStreamMess;
import com.leadnews.model.article.mess.UpdateArticleMess;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * @author lihaohui
 * @date 2023/8/30
 */
@Slf4j
@Configuration
public class HotArticleStreamHandler {

    @Bean
    public KStream<String, String> kStream(StreamsBuilder streamsBuilder) {
        KStream<String, String> stream = streamsBuilder.stream(HotArticleConstants.HOT_ARTICLE_SCORE_TOPIC);

        stream.map((k, v) -> {
                    UpdateArticleMess mess = JSON.parseObject(v, UpdateArticleMess.class);
                    String key = mess.getArticleId().toString();
                    String value = mess.getType().name() + ":" + mess.getAdd();
                    return new KeyValue<>(key, value);
                })//按照文章id进行聚合
                .groupBy((key, value) -> key)
                .windowedBy(TimeWindows.ofSizeWithNoGrace(Duration.ofSeconds(10)))
                .aggregate(() -> "COLLECTION:0,COMMENT:0,LIKES:0,VIEWS:0", (key, value, aggregate) -> {
                    if (StrUtil.isBlank(value)) {
                        return aggregate;
                    }

                    Map<UpdateArticleMess.UpdateArticleType, Integer> map = new HashMap<>(5);

                    String[] aggAry = aggregate.split(",");
                    for (String s : aggAry) {
                        String[] ssp = s.split(":");
                        //COLLECTION
                        String k = ssp[0];
                        //0
                        int v = Integer.parseInt(ssp[1]);

                        UpdateArticleMess.UpdateArticleType type = UpdateArticleMess.UpdateArticleType.valueOf(k);

                        map.put(type, map.getOrDefault(type, 0) + v);
                    }

                    // 再将map里面的值 加 value里面的 add
                    String[] valAry = value.split(":");
                    UpdateArticleMess.UpdateArticleType type = UpdateArticleMess.UpdateArticleType.valueOf(valAry[0]);
                    map.put(type, map.get(type) + Integer.parseInt(valAry[1]));

                    Integer col = map.get(UpdateArticleMess.UpdateArticleType.COLLECTION);
                    Integer likes = map.get(UpdateArticleMess.UpdateArticleType.LIKES);
                    Integer views = map.get(UpdateArticleMess.UpdateArticleType.VIEWS);
                    Integer commont = map.get(UpdateArticleMess.UpdateArticleType.COMMENT);
                    String formatStr = String.format("COLLECTION:%d,COMMENT:%d,LIKES:%d,VIEWS:%d", col, commont, likes, views);

                    log.info("文章id:{}", key);
                    log.info("当前时间窗口内的消息处理结果:{}", formatStr);
                    return formatStr;
                }, Materialized.as("hot-atricle-stream-count-001"))
                .toStream()
                .map((k, v) -> new KeyValue<>(k.key(), convertArticleVisitStreamMess(k.key(), v)))
                .to(HotArticleConstants.HOT_ARTICLE_INCR_HANDLE_TOPIC);


        return stream;
    }

    private String convertArticleVisitStreamMess(String key, String value) {
        ArticleVisitStreamMess mess = new ArticleVisitStreamMess();
        mess.setArticleId(Long.valueOf(key));
        // COLLECTION:0,COMMENT:0,LIKES:0,VIEWS:0
        String[] valArr = value.split(",");
        for (String val : valArr) {
            // COLLECTION:0
            String[] split = val.split(":");
            String k = split[0];
            Integer v = Integer.parseInt(split[1]);
            UpdateArticleMess.UpdateArticleType type = UpdateArticleMess.UpdateArticleType.valueOf(k);

            switch (type) {
                case COLLECTION -> mess.setCollect(v);
                case COMMENT -> mess.setComment(v);
                case LIKES -> mess.setLike(v);
                case VIEWS -> mess.setView(v);
                default -> throw new RuntimeException("not type");
            }
        }
        String res = JSON.toJSONString(mess);
        log.info("聚合消息处理之后的结果为:{}", res);
        return res;
    }
}
