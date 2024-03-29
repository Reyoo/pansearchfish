package top.findfish.crawler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;


/**
 * @author sun7127@126.com
 */
@SpringBootApplication
@EnableDiscoveryClient
public class FindfishCrawlerApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(FindfishCrawlerApplication.class, args);
    }
    @Bean
    public RestTemplate restTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(60000);
        factory.setReadTimeout(30000);
        return new RestTemplate(factory);
    }

}
