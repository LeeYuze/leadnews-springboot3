package com.leadnews.es;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.BulkRequest;
import com.alibaba.fastjson2.JSON;
import com.leadnews.es.mapper.ApArticleMapper;
import com.leadnews.model.search.vos.SearchArticleVO;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

/**
 * @author lihaohui
 * @date 2023/8/28
 */
@SpringBootTest(classes = EsDemoApplication.class)
public class ApArticleTest {

    private static final Logger logger = LoggerFactory.getLogger(ApArticleTest.class);

    @Resource
    private ApArticleMapper apArticleMapper;

    @Resource
    private ElasticsearchClient esClient;

    /**
     * 注意：数据量的导入，如果数据量过大，需要分页导入
     *
     * @throws Exception
     */
    @Test
    public void init() throws Exception {

        //1.查询所有符合条件的文章数据
        List<SearchArticleVO> searchArticleVos = apArticleMapper.loadArticleList();

        logger.info("searchArticleVos:{}", searchArticleVos);

        //2.批量导入到es索引库

        esClient.bulk(builder -> {
            for (SearchArticleVO searchArticleVo : searchArticleVos) {
                builder.index("app_info_article")
                        .operations(ob -> {
                            ob.index(index -> index.id(searchArticleVo.getId().toString()).document(searchArticleVo));
                            return ob;
                        });
            }
            return builder;
        });

    }

}
