package com.leadnews.wemedia.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.leadnews.model.wemedia.pojos.WmNewsMaterial;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author lihaohui
 * @description 针对表【wm_news_material(自媒体图文引用素材信息表)】的数据库操作Mapper
 * @createDate 2023-08-17 21:26:18
 * @Entity generator.domain.WmNewsMaterial
 */
@Mapper
public interface WmNewsMaterialMapper extends BaseMapper<WmNewsMaterial> {

    /**
     * 保存素材和文章的关系
     * @param materialIds 素材ids
     * @param newsId 文章id
     * @param type 0-内容 1-封面
     */
    void saveRelations(@Param("materialIds") List<Integer> materialIds, @Param("newsId") Integer newsId, @Param("type") Integer type);

}




