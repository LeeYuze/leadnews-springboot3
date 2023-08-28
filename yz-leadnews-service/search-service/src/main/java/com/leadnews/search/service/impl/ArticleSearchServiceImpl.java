package com.leadnews.search.service.impl;

import cn.hutool.core.util.StrUtil;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.ElasticsearchException;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.json.JsonData;
import com.leadnews.model.common.dtos.ResponseResult;
import com.leadnews.model.common.enums.AppHttpCodeEnum;
import com.leadnews.model.search.dtos.UserSearchDTO;
import com.leadnews.search.service.ArticleSearchService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author lihaohui
 * @date 2023/8/29
 */
@Service
@RequiredArgsConstructor
public class ArticleSearchServiceImpl implements ArticleSearchService {

    private static final Logger logger = LoggerFactory.getLogger(ArticleSearchServiceImpl.class);


    private final ElasticsearchClient elasticsearchClient;


    @Override
    public ResponseResult search(UserSearchDTO userSearchDto) {

        //1.检查参数
        if (userSearchDto == null || StrUtil.isBlank(userSearchDto.getSearchWords())) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }

        SearchRequest.Builder searchBuilder = new SearchRequest.Builder();

        searchBuilder.query(query -> {
            query.bool(bool -> {
                //关键字的分词之后查询
                bool.must(must -> {
                    must.match(m -> m.field("title").query(userSearchDto.getSearchWords()));
                    must.match(m -> m.field("content").query(userSearchDto.getSearchWords()));
                    return must;
                });

                //查询小于mindate的数据
                bool.filter(fileter -> {
                    fileter.range(range -> range.field("publishTime").lte(JsonData.of(userSearchDto.getMinBehotTime().getTime())));
                    return fileter;
                });
                return bool;
            });
            return query;
        });

        //分页查询
        searchBuilder.from(0);
        searchBuilder.size(userSearchDto.getPageSize());

        //按照发布时间倒序查询
        searchBuilder.sort(sort -> sort.field(f -> f.field("publishTime").order(SortOrder.Desc)));

        //设置高亮  title
        searchBuilder.highlight(highlight -> highlight
                .preTags("<font style='color: red; font-size: inherit;'>")
                .postTags("</font>")
                .requireFieldMatch(false)
                .fields("title", f -> f)
        );

        SearchRequest searchRequest = searchBuilder.build();

        List<Map> list = new ArrayList<>();

        try {
            SearchResponse<Map> response = elasticsearchClient.search(searchRequest, Map.class);

            logger.info("search result {}", response);

            for (Hit<Map> hit : response.hits().hits()) {

                logger.info("search hit {}", hit);

                Map map = hit.source();

                logger.info("source {}", map);

                if (!hit.highlight().isEmpty()) {
                    logger.info("highlight {}", hit.highlight());
                    List<String> titleList = hit.highlight().get("title");
                    String hightlightTitle = StrUtil.join("", titleList);
                    map.put("h_title", hightlightTitle);
                } else {
                    map.put("h_title", map.get("title"));
                }

                list.add(map);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ElasticsearchException e) {
            throw new RuntimeException(e);
        }

        return ResponseResult.okResult(list);
    }
}
