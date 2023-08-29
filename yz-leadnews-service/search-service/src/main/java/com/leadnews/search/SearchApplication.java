package com.leadnews.search;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.scheduling.annotation.EnableAsync;

import javax.sql.DataSource;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author lihaohui
 * @date 2023/8/29
 */
@EnableAsync
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class SearchApplication {
    private static final Logger logger = LoggerFactory.getLogger(SearchApplication.class);

    public static void main(String[] args) throws UnknownHostException {
        ConfigurableEnvironment env = SpringApplication.run(SearchApplication.class, args).getEnvironment();
        logger.info("\n----------------------------------------------------------\n\t" +
                        "Application '{}' is running! Access URLs:\n\t" +
                        "Local: \t\thttp://localhost:{}\n\t" +
                        "External: \thttp://{}:{}\n\t" +
                        "Doc: \thttp://{}:{}/doc.html\n" +
                        "----------------------------------------------------------",
                env.getProperty("spring.application.name"),
                env.getProperty("server.port"),
                InetAddress.getLocalHost().getHostAddress(),
                env.getProperty("server.port"),
                InetAddress.getLocalHost().getHostAddress(),
                env.getProperty("server.port"));
    }
}
