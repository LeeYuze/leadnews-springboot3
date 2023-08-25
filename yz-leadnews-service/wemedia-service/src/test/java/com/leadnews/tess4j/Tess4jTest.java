package com.leadnews.tess4j;

import com.leadnews.tess4j.config.Tess4jClient;
import jakarta.annotation.Resource;
import net.sourceforge.tess4j.Tesseract;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.ResourceUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;

/**
 * @author lihaohui
 * @date 2023/8/25
 */
@SpringBootTest(classes = Tess4jTest.class)
public class Tess4jTest {

    @Resource
    private Tess4jClient tess4jClient;

//    @Test
    public void test() {
        try {
            File file = new File("/Users/lihaohui/Desktop/素材/tess4jdemo.png");

            BufferedImage bufferedImage = ImageIO.read(file);

            String res = tess4jClient.doOCR(bufferedImage);

            System.out.println(res);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
