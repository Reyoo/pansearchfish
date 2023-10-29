package com.libbytian.pan.system.config;

import io.swagger.annotations.ApiOperation;
import org.springframework.boot.SpringBootConfiguration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.oas.annotations.EnableOpenApi;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

@SpringBootConfiguration
@EnableOpenApi
public class Swagger3Config {
    public Docket createRestApi() {
        return new Docket(DocumentationType.OAS_30)
                .apiInfo(apiInfo())
                .enable(false) // ture 启用Swagger3.0， false 禁用（生产环境要禁用）
                .select()
                .apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class))  // 扫描的路径使用@Api的controller
                .paths(PathSelectors.any()) // 指定路径处理PathSelectors.any()代表所有的路径
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("findFish")
                .description("开源项目")
                .contact(new Contact("findFish", "https://findfish.top", "sun7127@126.com"))
                .version("1.0")
                .build();
    }
}

