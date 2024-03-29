package com.libbytian.pan;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;


@SpringBootApplication
@EnableAsync
@EnableTransactionManagement
@EnableDiscoveryClient
public class SearchpanApplication {

    public static void main(String[] args) {
        SpringApplication.run(SearchpanApplication.class, args);
    }

//    @PostConstruct
//    public void setProperties(){
//        System.setProperty("druid.mysql.usePingMethod","false");
//    }

    @Bean
    public RestTemplate restTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(60000);
        factory.setReadTimeout(10000);
        return new RestTemplate(factory);
    }
}
