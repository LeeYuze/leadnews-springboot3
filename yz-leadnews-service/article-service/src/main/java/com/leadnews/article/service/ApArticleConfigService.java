package com.leadnews.article.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.leadnews.model.article.pojos.ApArticleConfig;

import java.util.Map;

/**
* @author lihaohui
* @description 针对表【ap_article_config(APP已发布文章配置表)】的数据库操作Service
* @createDate 2023-08-28 16:11:00
*/
public interface ApArticleConfigService extends IService<ApArticleConfig> {

   void upOrDownByArticleId(Long articleId, Integer enable);
}
