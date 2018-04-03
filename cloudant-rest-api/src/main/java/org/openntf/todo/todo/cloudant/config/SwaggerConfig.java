package org.openntf.todo.todo.cloudant.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import static springfox.documentation.builders.PathSelectors.regex;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

  @Bean
  public Docket api() {
    return new Docket(DocumentationType.SWAGGER_2)
      .apiInfo(apiInfo())
      .select()
      .paths(regex("/v1.*"))
      .build();
  }

  private ApiInfo apiInfo() {
    return new ApiInfoBuilder()
      .title("Cloudant Todo Service")
      .description("Cloudant Todo Service")
      .license("Apache License Version 2.0")
      .version("2.0")

      .build();
  }
}
