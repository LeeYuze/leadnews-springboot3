package com.leadnews.search.controller.v1;

import com.leadnews.model.common.dtos.ResponseResult;
import com.leadnews.model.search.dtos.UserSearchDTO;
import com.leadnews.search.service.ArticleSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * @author lihaohui
 * @date 2023/8/29
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/article/search")
public class ArticleSearchController {

    private final ArticleSearchService articleSearchService;

    @PostMapping("/search")
    public ResponseResult search(@RequestBody UserSearchDTO dto) throws IOException {
        return articleSearchService.search(dto);
    }
}
