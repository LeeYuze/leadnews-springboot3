package com.leadnews.search.service;

import com.leadnews.model.common.dtos.ResponseResult;
import com.leadnews.model.search.dtos.UserSearchDTO;

/**
 * @author lihaohui
 * @date 2023/8/29
 */
public interface ApAssociateWordsService {
    /**
     联想词
     @return
     */
    ResponseResult findAssociate(UserSearchDTO dto);

}
