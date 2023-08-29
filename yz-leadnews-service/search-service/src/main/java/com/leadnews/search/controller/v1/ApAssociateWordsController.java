package com.leadnews.search.controller.v1;

import com.leadnews.model.common.dtos.ResponseResult;
import com.leadnews.model.search.dtos.UserSearchDTO;
import com.leadnews.search.service.ApAssociateWordsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author lihaohui
 * @date 2023/8/29
 */
@RestController
@RequestMapping("/api/v1/associate")
@RequiredArgsConstructor
public class ApAssociateWordsController {


    private final ApAssociateWordsService apAssociateWordsService;

    @PostMapping("/search")
    public ResponseResult findAssociate(@RequestBody UserSearchDTO userSearchDto) {
        return apAssociateWordsService.findAssociate(userSearchDto);
    }
}