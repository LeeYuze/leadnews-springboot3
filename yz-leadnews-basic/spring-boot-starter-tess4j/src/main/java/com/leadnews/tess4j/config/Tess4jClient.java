package com.leadnews.tess4j.config;

import com.leadnews.tess4j.properties.Tess4jProperties;
import jakarta.annotation.Resource;
import lombok.Data;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.awt.image.BufferedImage;

/**
 * @author lihaohui
 * @date 2023/8/25
 */
@Component
@ConditionalOnClass(ITesseract.class)
public class Tess4jClient {

    private final ITesseract tesseract;

    public Tess4jClient(ITesseract tesseract) {
        this.tesseract = tesseract;
    }

    public String doOCR(BufferedImage image) throws TesseractException {


        //执行ocr识别
        String result = tesseract.doOCR(image);

        //替换回车和tal键  使结果为一行
        result = result.replaceAll("\\r|\\n", "-").replaceAll(" ", "");

        return result;
    }

}
