package com.leadnews.user.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.leadnews.user.handler.DefaultDBFieldHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author lihaohui
 * @date 2023/9/1
 */
@Configuration
public class MybatisConfiguration {
    @Bean
    public MetaObjectHandler defaultMetaObjectHandler(){
        return new DefaultDBFieldHandler(); // 自动填充参数类
    }
}
