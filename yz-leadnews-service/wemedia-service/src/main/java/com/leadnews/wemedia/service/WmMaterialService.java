package com.leadnews.wemedia.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.leadnews.model.common.dtos.ResponseResult;
import com.leadnews.model.wemedia.dtos.WmMaterialDTO;
import com.leadnews.model.wemedia.pojos.WmMaterial;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author lihaohui
 * @date 2023/8/17
 */
public interface WmMaterialService extends IService<WmMaterial> {
    /**
     * 图片上传
     *
     * @param multipartFile
     * @return
     */
    ResponseResult uploadPicture(MultipartFile multipartFile);

    /**
     * 素材列表查询
     *
     * @param dto
     * @return
     */
     ResponseResult findList(WmMaterialDTO dto);


    /**
     * 收藏素材
     * @param id 素材id
     * @return 是否成功
     */
     void collect(Long id);

    /**
     * 取消收藏素材
     * @param id 素材id
     */
    void  cancel_collect(Long id);


    /**
     * 根据id，删除素材
     * @param id 素材id
     */
    void deleteById(Long id);
}
