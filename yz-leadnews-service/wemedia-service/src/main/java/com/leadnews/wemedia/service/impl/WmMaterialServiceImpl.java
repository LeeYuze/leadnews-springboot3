package com.leadnews.wemedia.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.leadnews.minio.service.FileStorageService;
import com.leadnews.model.common.dtos.ResponseResult;
import com.leadnews.model.common.enums.AppHttpCodeEnum;
import com.leadnews.model.wemedia.pojos.WmMaterial;
import com.leadnews.wemedia.mapper.WmMaterialMapper;
import com.leadnews.wemedia.service.WmMaterialService;
import com.leadnews.wemedia.utils.thread.WmUserLocalUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;
import java.util.UUID;

/**
 * @author lihaohui
 * @date 2023/8/17
 */
@Service
@RequiredArgsConstructor
public class WmMaterialServiceImpl extends ServiceImpl<WmMaterialMapper, WmMaterial> implements WmMaterialService {

    private static final Logger logger = LoggerFactory.getLogger(WmMaterialServiceImpl.class);


    private final FileStorageService fileStorageService;

    @Override
    public ResponseResult uploadPicture(MultipartFile multipartFile) {
        //1.检查参数
        if (multipartFile == null || multipartFile.getSize() == 0) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }

        //2.上传图片到minIO中
        String fileName = UUID.randomUUID().toString().replace("-", "");
        //aa.jpg
        String originalFilename = multipartFile.getOriginalFilename();

        String postfix = originalFilename.substring(originalFilename.lastIndexOf("."));


        fileName = fileName + postfix;

        logger.info("生成新的文件名：{}", fileName);

        String upFilePath = "";
        try {
            upFilePath = fileStorageService.uploadImgFile("", fileName, multipartFile.getInputStream());

            logger.info("上传文件成功，文件存储地址：{}", upFilePath);

        } catch (IOException e) {
            logger.error("上传文件失败！");
            throw new RuntimeException(e);
        }

        WmMaterial wmMaterial = processUploadSuccessSave(upFilePath);

        return ResponseResult.okResult(wmMaterial);
    }

    private WmMaterial processUploadSuccessSave(String upFilePath) {
        WmMaterial wmMaterial = new WmMaterial();
        wmMaterial.setUserId(WmUserLocalUtil.getUser().getId());
        wmMaterial.setUrl(upFilePath);
        wmMaterial.setIsCollection(0);
        wmMaterial.setType(0);
        wmMaterial.setCreatedTime(new Date());
        save(wmMaterial);

        return wmMaterial;
    }
}
