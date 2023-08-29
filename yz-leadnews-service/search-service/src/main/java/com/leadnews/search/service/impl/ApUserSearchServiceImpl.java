package com.leadnews.search.service.impl;

import com.leadnews.model.common.dtos.ResponseResult;
import com.leadnews.model.search.pojos.ApUserSearch;
import com.leadnews.model.user.pojos.ApUser;
import com.leadnews.search.service.ApUserSearchService;
import com.leadnews.search.utils.thread.UserLocalUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @author lihaohui
 * @date 2023/8/29
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ApUserSearchServiceImpl implements ApUserSearchService {

    private final MongoTemplate mongoTemplate;

    @Override
    @Async
    public void insert(String keyword, Long userId) {
        Query query = Query.query(Criteria.where("userId").is(userId).and("keyword").is(keyword));
        ApUserSearch apUserSearch = mongoTemplate.findOne(query, ApUserSearch.class);

        // 存在 更新创建时间
        if (Objects.nonNull(apUserSearch)) {
            apUserSearch.setCreatedTime(new Date());
            mongoTemplate.save(apUserSearch);
            return;
        }

        // 历史记录保存最近10条，如果历史记录数量刚好是10那就删除最旧的
        deleteLastSearchHistory(userId, 10);

        // 保存进mongodb
        insertToMongo(keyword, userId);
    }

    @Override
    public ResponseResult findUserSearch() {
        //获取当前用户
        ApUser user = UserLocalUtil.getUser();
        Query query = Query.query(Criteria.where("userId").is(user.getId())).with(Sort.by(Sort.Direction.DESC, "createdTime"));

        //根据用户查询数据，按照时间倒序
        List<ApUserSearch> apUserSearches = mongoTemplate.find(query, ApUserSearch.class);
        return ResponseResult.okResult(apUserSearches);
    }

    public void deleteLastSearchHistory(Long userId, Integer maxSize) {
        Query query = Query.query(Criteria.where("userId").is(userId)).with(Sort.by(Sort.Direction.ASC, "createdTime"));

        List<ApUserSearch> apUserSearchList = mongoTemplate.find(query, ApUserSearch.class);

        if (apUserSearchList.size() < maxSize) {
            return;
        }

        String removeId = apUserSearchList.get(0).getId();
        mongoTemplate.remove(Query.query(Criteria.where("id").is(removeId)), ApUserSearch.class);
    }

    public void insertToMongo(String keyword, Long userId) {
        //3.不存在，判断当前历史记录总数量是否超过10
        ApUserSearch apUserSearch = new ApUserSearch();
        apUserSearch.setUserId(userId);
        apUserSearch.setKeyword(keyword);
        apUserSearch.setCreatedTime(new Date());
        mongoTemplate.save(apUserSearch);
    }
}
