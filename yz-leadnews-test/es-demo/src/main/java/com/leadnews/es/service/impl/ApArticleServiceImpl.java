package com.leadnews.es.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.leadnews.es.mapper.ApArticleMapper;
import com.leadnews.es.service.ApArticleService;
import com.leadnews.model.article.pojos.ApArticle;
import org.springframework.stereotype.Service;

/**
* @author lihaohui
* @description 针对表【ap_article(文章信息表，存储已发布的文章)】的数据库操作Service实现
* @createDate 2023-08-28 18:54:49
*/
@Service
public class ApArticleServiceImpl extends ServiceImpl<ApArticleMapper, ApArticle>
    implements ApArticleService {

}




