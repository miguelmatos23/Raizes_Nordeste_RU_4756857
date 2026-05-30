package com.raizesnordeste.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Raízes do Nordeste - API",
                version = "1.0",
                description = "API de gerenciamento de pedidos da Rede Raízes do Nordeste",
                contact = @Contact(
                        name = "Miguel de Matos Cavalcante Leite",
                        email = "miguelmatos766@gmail.com"
                )
        )
)
public class SwaggerConfig {
}
