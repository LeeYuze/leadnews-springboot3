package com.leadnews.minio.service.impl;

import com.leadnews.minio.config.MinioConfig;
import com.leadnews.minio.config.MinioConfigProperties;
import com.leadnews.minio.service.FileStorageService;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import jakarta.annotation.Resource;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author lihaohui
 * @date 2023/8/15
 */
@Service
@Import(MinioConfig.class)
public class MinioFileStorageService implements FileStorageService {

    private static final Logger logger = LoggerFactory.getLogger(MinioFileStorageService.class);

    @Resource
    private MinioClient minioClient;

    @Resource
    private MinioConfigProperties minioConfigProperties;


    private final static String SEPARATOR = "/";

    /**
     * @param dirPath
     * @param filename yyyy/mm/dd/file.jpg
     * @return
     */
    public String builderFilePath(String dirPath, String filename) {
        StringBuilder stringBuilder = new StringBuilder(50);
        if (StringUtils.hasLength(dirPath)) {
            stringBuilder.append(dirPath).append(SEPARATOR);
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        String todayStr = sdf.format(new Date());
        stringBuilder.append(todayStr).append(SEPARATOR);
        stringBuilder.append(filename);
        return stringBuilder.toString();
    }

    @Override
    public String uploadImgFile(String prefix, String filename, InputStream inputStream) {
        return updateFile(prefix, filename, "image/jpg", inputStream);
    }

    @NotNull
    private String updateFile(String prefix, String filename, String contentType, InputStream inputStream) {
        String filePath = builderFilePath(prefix, filename);
        try {
            PutObjectArgs putObjectArgs = PutObjectArgs.builder()
                    .object(filePath)
                    .contentType(contentType)
                    .bucket(minioConfigProperties.getBucket())
                    .stream(inputStream, inputStream.available(), -1)
                    .build();
            minioClient.putObject(putObjectArgs);

            return getRealFilePath(filePath);

        } catch (Exception e) {
            logger.error("minio put file error.", e);
            throw new RuntimeException("上传文件失败");
        }
    }

    /**
     * 获取上传成功后的文件路径
     *
     * @param filePath 文件路径
     * @return
     */
    @NotNull
    private String getRealFilePath(String filePath) {
        StringBuilder urlPath = new StringBuilder(minioConfigProperties.getReadPath());
        urlPath.append(SEPARATOR + minioConfigProperties.getBucket());
        urlPath.append(SEPARATOR);
        urlPath.append(filePath);
        return urlPath.toString();
    }

    @Override
    public String uploadHtmlFile(String prefix, String filename, InputStream inputStream) {
        return updateFile(prefix, filename, "text/html", inputStream);
    }

    @Override
    public void delete(String pathUrl) {
        String key = pathUrl.replace(minioConfigProperties.getEndpoint() + "/", "");
        int index = key.indexOf(SEPARATOR);
        String bucket = key.substring(0, index);
        String filePath = key.substring(index + 1);
        // 删除Objects
        RemoveObjectArgs removeObjectArgs = RemoveObjectArgs.builder().bucket(bucket).object(filePath).build();

        try {
            minioClient.removeObject(removeObjectArgs);
        } catch (Exception e) {
            logger.error("minio remove file error.  pathUrl:{}", pathUrl);
            e.printStackTrace();
            throw new RuntimeException("文件删除失败");
        }

    }

    @Override
    public byte[] downLoadFile(String pathUrl) {
        String key = pathUrl.replace(minioConfigProperties.getEndpoint() + "/", "");
        int index = key.indexOf(SEPARATOR);
        String filePath = key.substring(index + 1);

        GetObjectArgs getObjectArgs = GetObjectArgs.builder().bucket(minioConfigProperties.getBucket()).object(filePath).build();

        try (InputStream inputStream = minioClient.getObject(getObjectArgs)) {

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byte[] buff = new byte[100];
            int rc = 0;
            while (true) {
                try {
                    if (!((rc = inputStream.read(buff, 0, 100)) > 0)) break;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                byteArrayOutputStream.write(buff, 0, rc);
            }

            return byteArrayOutputStream.toByteArray();
        } catch (Exception e) {
            logger.error("minio down file error.  pathUrl:{}", pathUrl);
            e.printStackTrace();
        }

        return null;
    }
}
