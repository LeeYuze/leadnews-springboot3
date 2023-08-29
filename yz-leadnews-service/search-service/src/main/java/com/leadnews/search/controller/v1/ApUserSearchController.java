package com.leadnews.search.controller.v1;

import com.leadnews.model.common.dtos.ResponseResult;
import com.leadnews.model.search.dtos.HistorySearchDTO;
import com.leadnews.search.service.ApUserSearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author lihaohui
 * @date 2023/8/29
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/history")
@RequiredArgsConstructor
public class ApUserSearchController{

    private final ApUserSearchService apUserSearchService;

    @PostMapping("/load")
    public ResponseResult findUserSearch() {
        return apUserSearchService.findUserSearch();
    }

    @PostMapping("/del")
    public ResponseResult delUserSearch(@RequestBody HistorySearchDTO historySearchDto) {
        return apUserSearchService.delUserSearch(historySearchDto);
    }
}