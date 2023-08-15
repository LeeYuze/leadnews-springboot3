package com.leadnews.article.test;

import com.leadnews.minio.service.FileStorageService;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * @author lihaohui
 * @date 2023/8/15
 */
@SpringBootTest
public class MinioTest {

    @Resource
    private FileStorageService fileStorageService;

    @Test
    public void testUploadFile() {
        try (FileInputStream fis = new FileInputStream("/Users/lihaohui/Downloads/testa.jpeg")) {
            String filePath = fileStorageService.uploadImgFile("", "testa.jpeg", fis);
            System.out.println(filePath);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
