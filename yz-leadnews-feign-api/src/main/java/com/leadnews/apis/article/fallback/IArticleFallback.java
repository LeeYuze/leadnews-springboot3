package com.leadnews.apis.article.fallback;

import com.leadnews.apis.article.IArticleClient;
import com.leadnews.model.article.dtos.ArticleDTO;
import com.leadnews.model.common.dtos.ResponseResult;
import com.leadnews.model.common.enums.AppHttpCodeEnum;
import org.springframework.stereotype.Component;

/**
 * IArticleClient 降级处理
 * @author lihaohui
 * @date 2023/8/25
 */
@Component
public class IArticleFallback implements IArticleClient {
    @Override
    public ResponseResult saveArticle(ArticleDTO dto) {
        return ResponseResult.errorResult(AppHttpCodeEnum.SERVER_ERROR,"获取数据失败");
    }
}
