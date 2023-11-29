package br.com.monteiro.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    OpenAPI customOpenApi() {
        return new OpenAPI()
                .info(new Info()
                .title("RESTful API With Java 20 and Spring Boot 3.1.5")
                        .version("v1")
                        .description("")
                        .termsOfService("")
                        .license(new License()
                                .name("Apache 2.0")
                                .url("http://www.apache")));
    }

}
