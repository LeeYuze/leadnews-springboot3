package com.leadnews.article.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.leadnews.article.mapper.ApArticleConfigMapper;
import com.leadnews.article.mapper.ApArticleContentMapper;
import com.leadnews.article.mapper.ApArticleMapper;
import com.leadnews.article.service.ApArticleService;
import com.leadnews.common.article.ArticleConstants;
import com.leadnews.model.article.dtos.ArticleDTO;
import com.leadnews.model.article.dtos.ArticleHomeDTO;
import com.leadnews.model.article.pojos.ApArticle;
import com.leadnews.model.article.pojos.ApArticleConfig;
import com.leadnews.model.article.pojos.ApArticleContent;
import com.leadnews.model.common.dtos.ResponseResult;
import com.leadnews.model.common.enums.AppHttpCodeEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
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

    private final ApArticleConfigMapper apArticleConfigMapper;

    private final ApArticleContentMapper apArticleContentMapper;

    // 单页最大加载的数字
    private final static Integer MAX_PAGE_SIZE = 50;


    @Override
    public ResponseResult load(Integer loadtype, ArticleHomeDTO dto) {
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
        if (StrUtil.isEmpty(dto.getTag())) {
            dto.setTag(ArticleConstants.DEFAULT_TAG);
        }

        //时间校验
        if (dto.getMaxBehotTime() == null) {
            dto.setMaxBehotTime(new Date());
        }

        if (dto.getMinBehotTime() == null) {
            dto.setMinBehotTime(new Date());
        }

        //2.查询数据
        List<ApArticle> apArticles = apArticleMapper.loadArticleList(dto, loadtype);

        return ResponseResult.okResult(apArticles);
    }

    @Override
    public ResponseResult saveArticle(ArticleDTO dto) {
        //1.检查参数
        if (dto == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }

        ApArticle apArticle = new ApArticle();
        BeanUtils.copyProperties(dto, apArticle);

        //2.判断是否存在id
        if (dto.getId() == null) {
            //2.1 不存在id  保存  文章  文章配置  文章内容
            //保存文章
            save(apArticle);

            //保存配置
            ApArticleConfig apArticleConfig = new ApArticleConfig(apArticle.getId());
            apArticleConfigMapper.insert(apArticleConfig);

            //保存 文章内容
            ApArticleContent apArticleContent = new ApArticleContent();
            apArticleContent.setArticleId(apArticle.getId());
            apArticleContent.setContent(dto.getContent());
            apArticleContentMapper.insert(apArticleContent);
        } else {
            //2.2 存在id   修改  文章  文章内容

            //修改  文章
            updateById(apArticle);

            //修改文章内容
            ApArticleContent apArticleContent = apArticleContentMapper.selectOne(Wrappers.<ApArticleContent>lambdaQuery().eq(ApArticleContent::getArticleId, dto.getId()));
            apArticleContent.setContent(dto.getContent());
            apArticleContentMapper.updateById(apArticleContent);
        }

        return ResponseResult.okResult(apArticle.getId());
    }
}
