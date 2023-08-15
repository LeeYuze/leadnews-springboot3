package com.leadnews.article.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.leadnews.article.mapper.ApArticleMapper;
import com.leadnews.article.service.ApArticleService;
import com.leadnews.common.article.ArticleConstants;
import com.leadnews.model.article.dtos.ArticleHomeDTO;
import com.leadnews.model.article.pojos.ApArticle;
import com.leadnews.model.common.dtos.ResponseResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @author lihaohui
 * @date 2023/8/15
 */
@Service
@RequiredArgsConstructor
public class ApArticleServiceImpl extends ServiceImpl<ApArticleMapper, ApArticle> implements ApArticleService {

    private final ApArticleMapper apArticleMapper;

    // 单页最大加载的数字
    private final static short MAX_PAGE_SIZE = 50;


    @Override
    public ResponseResult load(Short loadtype, ArticleHomeDTO dto) {
        //1.校验参数
        Integer size = dto.getSize();
        if (size == null || size == 0) {
            size = 10;
        }

        size = Math.min(size, MAX_PAGE_SIZE);
        dto.setSize(size);

        //类型参数检验
        if (!loadtype.equals(ArticleConstants.LOADTYPE_LOAD_MORE) && !loadtype.equals(ArticleConstants.LOADTYPE_LOAD_NEW)) {
            loadtype = ArticleConstants.LOADTYPE_LOAD_MORE;
        }

        //文章频道校验
        if(StrUtil.isEmpty(dto.getTag())){
            dto.setTag(ArticleConstants.DEFAULT_TAG);
        }

        //时间校验
        if(dto.getMaxBehotTime() == null) {
            dto.setMaxBehotTime(new Date());
        }

        if(dto.getMinBehotTime() == null) {
            dto.setMinBehotTime(new Date());
        }

        //2.查询数据
        List<ApArticle> apArticles = apArticleMapper.loadArticleList(dto, loadtype);

        return ResponseResult.okResult(apArticles);
    }
}
