package com.leadnews.search.service.impl;

import cn.hutool.core.util.StrUtil;
import com.leadnews.model.common.dtos.ResponseResult;
import com.leadnews.model.common.enums.AppHttpCodeEnum;
import com.leadnews.model.mongo.pojos.ApAssociateWords;
import com.leadnews.model.search.dtos.UserSearchDTO;
import com.leadnews.search.service.ApAssociateWordsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author lihaohui
 * @date 2023/8/29
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ApAssociateWordsServiceImpl implements ApAssociateWordsService {

    private final MongoTemplate mongoTemplate;

    @Override
    public ResponseResult findAssociate(UserSearchDTO dto) {
        //1 参数检查
        if(dto == null || StrUtil.isBlank(dto.getSearchWords())){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }

        //分页检查
        if (dto.getPageSize() > 20) {
            dto.setPageSize(20);
        }

        //3 执行查询 模糊查询
        Query query = Query.query(Criteria.where("associateWords").regex(".*?\\" + dto.getSearchWords() + ".*"));
        query.limit(dto.getPageSize());
        List<ApAssociateWords> wordsList = mongoTemplate.find(query, ApAssociateWords.class);

        return ResponseResult.okResult(wordsList);
    }
}
