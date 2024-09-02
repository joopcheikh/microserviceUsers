package com.getusers.getusers.config;


import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(
        info = @Info(
                contact = @Contact(
                        name = "Gats",
                        email = "Gats@gmail.com",
                        url = "https://gatsmapping.com"
                ),
                description = "Gats est une entreprise sénégalaise spécialisée dans les technologies",
                title = "Plateform pour l'etat major des forces armées",
                version = "1.0"

        ),
        servers = {
                @Server(
                        description = "server local",
                        url = "http://localhost:8080"
                ),
                @Server(
                        description = "server local de Gats",
                        url = "https://-----------------"
                )
        }

)
@SecurityScheme(
        name = "bearerAuth",
        description = "JWT auth description",
        scheme = "bearer",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER
)
public class OpenApiConfig {
}
