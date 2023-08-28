package com.leadnews.wemedia.service.impl;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.leadnews.common.execption.CustomException;
import com.leadnews.common.message.MqConstants;
import com.leadnews.common.wemedia.WemediaConstants;
import com.leadnews.common.wemedia.WmNewsStatus;
import com.leadnews.model.common.dtos.PageResponseResult;
import com.leadnews.model.common.dtos.ResponseResult;
import com.leadnews.model.common.enums.AppHttpCodeEnum;
import com.leadnews.model.wemedia.dtos.WmNewsDTO;
import com.leadnews.model.wemedia.dtos.WmNewsPageReqDTO;
import com.leadnews.model.wemedia.pojos.WmMaterial;
import com.leadnews.model.wemedia.pojos.WmNews;
import com.leadnews.model.wemedia.pojos.WmNewsMaterial;
import com.leadnews.model.wemedia.pojos.WmUser;
import com.leadnews.wemedia.mapper.WmMaterialMapper;
import com.leadnews.wemedia.mapper.WmNewsMapper;
import com.leadnews.wemedia.mapper.WmNewsMaterialMapper;
import com.leadnews.wemedia.service.WmNewsAutoScanService;
import com.leadnews.wemedia.service.WmNewsService;
import com.leadnews.wemedia.service.WmNewsTaskService;
import com.leadnews.wemedia.utils.thread.WmUserLocalUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author lihaohui
 * @description 针对表【wm_news(自媒体图文内容信息表)】的数据库操作Service实现
 * @createDate 2023-08-17 21:12:32
 */
@Service
@RequiredArgsConstructor
public class WmNewsServiceImpl extends ServiceImpl<WmNewsMapper, WmNews>
        implements WmNewsService {

    private final WmNewsMaterialMapper wmNewsMaterialMapper;

    private final WmMaterialMapper wmMaterialMapper;

    private final WmNewsTaskService wmNewsTaskService;

    private final KafkaTemplate<String, String> kafkaTemplate;

    @Override
    public ResponseResult findAll(WmNewsPageReqDTO dto) {
        //1.检查参数
        if (dto == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }

        //分页参数检查
        dto.checkParam();

        //获取当前登录人的信息
        WmUser user = WmUserLocalUtil.getUser();
        if (user == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.NEED_LOGIN);
        }

        //2.分页条件查询
        IPage page = new Page(dto.getPage(), dto.getSize());
        LambdaQueryWrapper<WmNews> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        //状态精确查询
        if (dto.getStatus() != null) {
            lambdaQueryWrapper.eq(WmNews::getStatus, dto.getStatus());
        }

        //频道精确查询
        if (dto.getChannelId() != null) {
            lambdaQueryWrapper.eq(WmNews::getChannelId, dto.getChannelId());
        }

        //时间范围查询
        if (dto.getBeginPubDate() != null && dto.getEndPubDate() != null) {
            lambdaQueryWrapper.between(WmNews::getPublishTime, dto.getBeginPubDate(), dto.getEndPubDate());
        }

        //关键字模糊查询
        if (StrUtil.isNotBlank(dto.getKeyword())) {
            lambdaQueryWrapper.like(WmNews::getTitle, dto.getKeyword());
        }

        //查询当前登录用户的文章
        lambdaQueryWrapper.eq(WmNews::getUserId, user.getId());

        //发布时间倒序查询
        lambdaQueryWrapper.orderByDesc(WmNews::getCreatedTime);

        page = page(page, lambdaQueryWrapper);

        //3.结果返回
        ResponseResult responseResult = new PageResponseResult(dto.getPage(), dto.getSize(), page.getTotal());

        responseResult.setData(page.getRecords());

        return responseResult;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult submitNews(WmNewsDTO dto) {

        // 0.条件判断
        if (dto == null || dto.getContent() == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }

        // 1.保存或修改文章
        WmNews wmNews = new WmNews();

        // 属性拷贝 属性名词和类型相同才能拷贝
        BeanUtils.copyProperties(dto, wmNews);

        // 封面图片  list ---> string
        // [xx.jpg,x1.jpg] ---> xx.jpg,x1.jpg 以","存储在数据库
        if (dto.getImages() != null && dto.getImages().size() > 0) {
            String imageStr = StrUtil.join(",", dto.getImages());
            wmNews.setImages(imageStr);
        }

        // 如果当前封面类型为自动 -1
        if (dto.getType().equals(WemediaConstants.WM_NEWS_TYPE_AUTO)) {
            wmNews.setType(null);
        }

        saveOrUpdateWmNews(wmNews);

        //2.判断是否为草稿  如果为草稿结束当前方法
        if (dto.getStatus().equals(WmNewsStatus.DRAFT)) {
            return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
        }

        //3.不是草稿，保存文章内容图片与素材的关系
        // 获取到文章内容中的图片信息
        List<String> materials = ectractUrlInfo(dto.getContent());

        saveRelativeInfoForContent(materials, wmNews.getId());

        //4.不是草稿，保存文章封面图片与素材的关系，如果当前布局是自动，需要匹配封面图片
        saveRelativeInfoForCover(dto, wmNews, materials);


        //审核文章
//        wmNewsAutoScanService.autoScanWmNews(wmNews.getId());
        wmNewsTaskService.addNewsToTask(wmNews.getId(), wmNews.getPublishTime());

        return ResponseResult.okResult();
    }

    @Override
    public WmNews getOne(Long id) {
        WmNews wmNews = getById(id);

        if (wmNews == null) {
            throw new CustomException(AppHttpCodeEnum.DATA_NOT_EXIST);
        }

        return wmNews;
    }

    @Override
    public void deleteById(Long id) {
        if (id == null) {
            throw new CustomException(AppHttpCodeEnum.PARAM_INVALID);
        }

        WmNews wmNews = getById(id);
        if (wmNews == null) {
            throw new CustomException(AppHttpCodeEnum.DATA_NOT_EXIST);
        }

        if (WmNewsStatus.PUBLISH.equals(wmNews.getStatus())) {
            throw new CustomException(AppHttpCodeEnum.NEWS_PUBLISH_UN_DELETE);
        }

        this.removeById(id);
    }

    @Override
    public void downOrUp(WmNewsDTO dto) {
        if (dto.getId() == null) {
            throw new CustomException(AppHttpCodeEnum.PARAM_INVALID);
        }

        WmNews wmNews = getById(dto.getId());
        if (wmNews == null) {
            throw new CustomException(AppHttpCodeEnum.DATA_NOT_EXIST);
        }

        if (!(WmNewsStatus.PUBLISH.equals(wmNews.getStatus()))) {
            throw new CustomException(AppHttpCodeEnum.NEWS_NOT_PUBLISH_DOWN_OR_UP_FAIL);
        }

        wmNews.setEnable(dto.getEnable());

        // 发布到kafka，由article端消费
        if (Objects.nonNull(wmNews.getArticleId())) {
            Map<String, Object> map = new HashMap<>();
            map.put("articleId", wmNews.getArticleId());
            map.put("enable", dto.getEnable());
            String jsonString = JSON.toJSONString(map);
            kafkaTemplate.send(MqConstants.WM_NEWS_UP_OR_DOWN_TOPIC, jsonString);
        }

        this.updateById(wmNews);
    }

    /**
     * 第一个功能：如果当前封面类型为自动，则设置封面类型的数据
     * 匹配规则：
     * 1，如果内容图片大于等于1，小于3  单图  type 1
     * 2，如果内容图片大于等于3  多图  type 3
     * 3，如果内容没有图片，无图  type 0
     * <p>
     * 第二个功能：保存封面图片与素材的关系
     *
     * @param dto
     * @param wmNews
     * @param materials
     */
    private void saveRelativeInfoForCover(WmNewsDTO dto, WmNews wmNews, List<String> materials) {

        List<String> images = dto.getImages();

        //如果当前封面类型为自动，则设置封面类型的数据
        if (dto.getType().equals(WemediaConstants.WM_NEWS_TYPE_AUTO)) {
            //多图
            if (materials.size() >= 3) {
                wmNews.setType(WemediaConstants.WM_NEWS_MANY_IMAGE);
                images = materials.stream().limit(3).collect(Collectors.toList());
            } else if (materials.size() >= 1 && materials.size() < 3) {
                //单图
                wmNews.setType(WemediaConstants.WM_NEWS_SINGLE_IMAGE);
                images = materials.stream().limit(1).collect(Collectors.toList());
            } else {
                //无图
                wmNews.setType(WemediaConstants.WM_NEWS_NONE_IMAGE);
            }

            //修改文章
            if (images != null && images.size() > 0) {
                wmNews.setImages(StrUtil.join(",", images));
            }

            updateById(wmNews);
        }

        if (images != null && images.size() > 0) {
            saveRelativeInfo(images, wmNews.getId(), WemediaConstants.WM_COVER_REFERENCE);
        }

    }

    /**
     * 处理文章内容图片与素材的关系
     *
     * @param materials
     * @param newsId
     */
    private void saveRelativeInfoForContent(List<String> materials, Integer newsId) {
        saveRelativeInfo(materials, newsId, WemediaConstants.WM_CONTENT_REFERENCE);
    }

    /**
     * 保存文章图片与素材的关系到数据库中
     *
     * @param materials
     * @param newsId
     * @param type
     */
    private void saveRelativeInfo(List<String> materials, Integer newsId, Integer type) {
        if (materials != null && !materials.isEmpty()) {
            //通过图片的url查询素材的id
            List<WmMaterial> dbMaterials = wmMaterialMapper.selectList(Wrappers.<WmMaterial>lambdaQuery().in(WmMaterial::getUrl, materials));

            //判断素材是否有效
            if (dbMaterials == null || dbMaterials.size() == 0) {
                //手动抛出异常   第一个功能：能够提示调用者素材失效了，第二个功能，进行数据的回滚
                throw new CustomException(AppHttpCodeEnum.MATERIASL_REFERENCE_FAIL);
            }

            if (materials.size() != dbMaterials.size()) {
                throw new CustomException(AppHttpCodeEnum.MATERIASL_REFERENCE_FAIL);
            }

            List<Long> idList = dbMaterials.stream().map(WmMaterial::getId).collect(Collectors.toList());

            //批量保存
            wmNewsMaterialMapper.saveRelations(idList, newsId, type);
        }

    }


    /**
     * 提取文章内容中的图片信息
     *
     * @param content
     * @return
     */
    private List<String> ectractUrlInfo(String content) {
        List<String> materials = new ArrayList<>();

        List<Map> maps = JSON.parseArray(content, Map.class);
        for (Map map : maps) {
            if ("image".equals(map.get("type"))) {
                String imgUrl = (String) map.get("value");
                materials.add(imgUrl);
            }
        }

        return materials;
    }

    /**
     * 保存或修改文章
     *
     * @param wmNews
     */
    private void saveOrUpdateWmNews(WmNews wmNews) {
        // 补全属性
        wmNews.setUserId(WmUserLocalUtil.getUser().getId());
        wmNews.setCreatedTime(new Date());
        wmNews.setSubmitedTime(new Date());

        // 默认为发布
        wmNews.setEnable(WmNewsStatus.SUMMIT);

        if (wmNews.getId() == null) {
            //保存
            save(wmNews);
        } else {
            //修改
            //删除文章图片与素材的关系
            wmNewsMaterialMapper.delete(Wrappers.<WmNewsMaterial>lambdaQuery().eq(WmNewsMaterial::getNewsId, wmNews.getId()));
            updateById(wmNews);
        }

    }
}




