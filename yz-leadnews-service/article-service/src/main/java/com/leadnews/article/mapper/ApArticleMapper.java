package com.leadnews.article.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.leadnews.model.article.dtos.ArticleHomeDTO;
import com.leadnews.model.article.pojos.ApArticle;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * @author lihaohui
 * @date 2023/8/15
 */
@Mapper
public interface ApArticleMapper extends BaseMapper<ApArticle> {
    List<ApArticle> loadArticleList(@Param("dto") ArticleHomeDTO dto, @Param("type") Integer type);

    List<ApArticle> findArticleListByDay(@Param("dayParam") Date dayParam);

}
