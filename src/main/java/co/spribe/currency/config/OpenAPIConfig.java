package co.spribe.currency.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {

    @Value("${currency.openapi.dev-url}")
    private String devUrl;

    @Bean
    public OpenAPI myOpenAPI() {

        Info info = new Info()
                .title("Currency Management API")
                .version("0.0.1-SNAPSHOT")
                .contact(new Contact().name("Pavel Mihayeu").email("migaev.p@gmail.com"))
                .description("This API exposes endpoints to manage project currencies.")
                .license(new License().name("MIT License").url("https://choosealicense.com/licenses/mit/"));

        Server devServer = new Server()
                .url(devUrl)
                .description("Server URL in Development environment");

        return new OpenAPI().info(info).addServersItem(devServer);
    }
}
