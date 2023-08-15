package com.leadnews.minio.test;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.errors.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * @author lihaohui
 * @date 2023/8/15
 */
public class MinioTest {

    public static void main(String[] args) {
        try(FileInputStream fis = new FileInputStream("/Users/lihaohui/code/java/yz-leadnews/yz-leadnews-test/minio-demo/list.html")) {

            MinioClient client = MinioClient.builder().credentials("lee", "li123456").endpoint("http://10.211.55.8:9000").build();

            PutObjectArgs putObjectArgs = PutObjectArgs.builder()
                    .object("list.html")
                    .contentType("text/html")
                    .bucket("leadnews")
                    .stream(fis, fis.available(), -1)
                    .build();

            client.putObject(putObjectArgs);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
