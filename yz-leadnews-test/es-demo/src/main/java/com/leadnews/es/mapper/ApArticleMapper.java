package com.leadnews.es.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.leadnews.model.article.pojos.ApArticle;
import com.leadnews.model.search.vos.SearchArticleVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
* @author lihaohui
* @description 针对表【ap_article(文章信息表，存储已发布的文章)】的数据库操作Mapper
* @createDate 2023-08-28 18:54:49
* @Entity generator.domain.ApArticle
*/
@Mapper
public interface ApArticleMapper extends BaseMapper<ApArticle> {

    List<SearchArticleVO> loadArticleList();
}




