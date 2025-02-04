package com.lead.generation.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(info = @Info(title = "SwaggerPractice", description = "Doing crud operations", summary = "checking the swagger .How swagger works", termsOfService = "T&C", contact = @Contact(name = "sravani", email = "p.devamatha2001@gmail.com"

), license = @License(name = "your Licence No"

), version = "v1"

), servers = { @Server(description = "Dev", url = "http://localhost:8085/"

		), @Server(description = "Test", url = "http://localhost:8085/"

		),
		// @Server(description = "Developement", url = "https://api.smartaihr.com/"),

}, security = @SecurityRequirement(name = "auth")
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                              
)


@SecurityScheme(name = "auth", in = SecuritySchemeIn.HEADER, type = SecuritySchemeType.APIKEY, paramName = "Authorization", description = "Enter the token directly without the 'Bearer' prefix."

)

public class OpenApiConfig {

}