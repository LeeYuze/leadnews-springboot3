package com.leadnews.tess4j.config;

import com.leadnews.tess4j.properties.Tess4jProperties;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author lihaohui
 * @date 2023/8/25
 */
@Configuration
@Import(Tess4jProperties.class)
public class Tess4jConfig {

    private final Tess4jProperties tess4jProperties;

    public Tess4jConfig(Tess4jProperties tess4jProperties) {
        this.tess4jProperties = tess4jProperties;
    }

    @Bean
    public ITesseract tesseract() {
        //创建Tesseract对象
        ITesseract tesseract = new Tesseract();

        tesseract.setDatapath(".");

        String dataPath = tess4jProperties.getDataPath();

        if (dataPath.startsWith("classpath:")) {
            dataPath = this.getClass().getClassLoader().getResource(dataPath.substring(dataPath.indexOf(":") + 1)).getPath();
        }

        //设置字体库路径
        tesseract.setDatapath(dataPath);

        //中文识别
        tesseract.setLanguage(tess4jProperties.getLanguage());

        return tesseract;
    }
}
